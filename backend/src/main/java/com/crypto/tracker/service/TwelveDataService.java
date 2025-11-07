package com.crypto.tracker.service;

import com.crypto.tracker.model.CryptoQuote;
import com.crypto.tracker.model.TimeSeriesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.crypto.tracker.config.RedisCacheConfig.CRYPTO_HISTORY_CACHE;
import static com.crypto.tracker.config.RedisCacheConfig.PRICE_LIST_CACHE;

/**
 * Service for interacting with Twelve Data API to fetch stock market indices data.
 *
 * Integrates with rate limiters and caching to respect API limits:
 * - 20 requests per minute (MinuteLimiter)
 * - 500 requests per month (MonthlyBudget)
 *
 * Endpoints used:
 * - /quote: Get current prices for multiple symbols (SPY, DIA, QQQ, IWM)
 * - /time_series: Get historical daily prices (30 days)
 */
@Service
public class TwelveDataService {

    private static final Logger log = LoggerFactory.getLogger(TwelveDataService.class);

    @Value("${twelve.data.api.key}")
    private String apiKey;

    @Value("${twelve.data.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final MinuteLimiter minuteLimiter;
    private final MonthlyBudget monthlyBudget;
    private final ObjectMapper objectMapper;

    // The 4 major stock market index ETFs we're tracking
    // ETFs are used because they're more reliably available in the API
    private static final String[] INDEX_SYMBOLS = {
        "SPY",   // SPDR S&P 500 ETF (tracks S&P 500 index)
        "DIA",   // SPDR Dow Jones ETF (tracks Dow Jones Industrial Average)
        "QQQ",   // Invesco QQQ ETF (tracks NASDAQ-100)
        "IWM"    // iShares Russell 2000 ETF (tracks Russell 2000 small-cap index)
    };

    public TwelveDataService(
            MinuteLimiter minuteLimiter,
            MonthlyBudget monthlyBudget,
            ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.minuteLimiter = minuteLimiter;
        this.monthlyBudget = monthlyBudget;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetch current prices for all tracked stock market indices.
     *
     * Uses the /quote endpoint which can fetch multiple symbols in one call.
     * Cached for 120 seconds to minimize API calls.
     *
     * @return Map of symbol -> CryptoQuote (data structure reused for indices)
     * @throws Exception if API call fails or rate limit exceeded
     */
    @Cacheable(value = PRICE_LIST_CACHE, key = "'all-quotes'")
    public Map<String, CryptoQuote> getCurrentPrices() throws Exception {
        log.info("Fetching current stock index prices from Twelve Data API");

        // Check rate limits before making API call
        if (!minuteLimiter.allowRequest()) {
            long waitSeconds = minuteLimiter.getSecondsUntilReset();
            throw new Exception("Rate limit exceeded. Try again in " + waitSeconds + " seconds.");
        }

        if (!monthlyBudget.hasRemainingBudget()) {
            throw new Exception("Monthly API budget exhausted (" + monthlyBudget.getCurrentUsage() + "/500)");
        }

        // Build the API URL with all symbols
        String symbolsParam = String.join(",", INDEX_SYMBOLS);
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/quote")
                .queryParam("symbol", symbolsParam)
                .queryParam("apikey", apiKey)
                .toUriString();

        try {
            // Call the API
            String response = restTemplate.getForObject(url, String.class);

            // Increment usage counter
            monthlyBudget.incrementUsage();

            log.info("Successfully fetched prices. Monthly usage: {}/500, Minute remaining: {}/20",
                    monthlyBudget.getCurrentUsage(),
                    minuteLimiter.getRemainingRequests());

            // Parse the response
            // Twelve Data returns either a single object (1 symbol) or an object with symbol keys (multiple symbols)
            return parseQuoteResponse(response);

        } catch (Exception e) {
            log.error("Failed to fetch prices from Twelve Data API", e);
            throw new Exception("API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse the quote response which can have varying structure.
     */
    @SuppressWarnings("unchecked")
    private Map<String, CryptoQuote> parseQuoteResponse(String response) throws Exception {
        Map<String, CryptoQuote> result = new HashMap<>();

        // Parse as generic map first
        Map<String, Object> rawData = objectMapper.readValue(response, Map.class);

        // The response contains one entry per symbol
        for (Map.Entry<String, Object> entry : rawData.entrySet()) {
            String symbol = entry.getKey();

            // Convert the nested map to CryptoQuote object
            CryptoQuote quote = objectMapper.convertValue(entry.getValue(), CryptoQuote.class);
            result.put(symbol, quote);
        }

        return result;
    }

    /**
     * Fetch 30-day historical price data for a specific stock market index.
     *
     * Uses the /time_series endpoint with 1day interval.
     * Cached for 300 seconds (historical data doesn't change).
     *
     * @param symbol Stock index symbol (e.g., "SPX", "DJI")
     * @return TimeSeriesResponse with historical data
     * @throws Exception if API call fails or rate limit exceeded
     */
    @Cacheable(value = CRYPTO_HISTORY_CACHE, key = "#symbol")
    public TimeSeriesResponse getHistoricalPrices(String symbol) throws Exception {
        log.info("Fetching 30-day history for index {} from Twelve Data API", symbol);

        // Check rate limits
        if (!minuteLimiter.allowRequest()) {
            long waitSeconds = minuteLimiter.getSecondsUntilReset();
            throw new Exception("Rate limit exceeded. Try again in " + waitSeconds + " seconds.");
        }

        if (!monthlyBudget.hasRemainingBudget()) {
            throw new Exception("Monthly API budget exhausted (" + monthlyBudget.getCurrentUsage() + "/500)");
        }

        // Build the API URL
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/time_series")
                .queryParam("symbol", symbol)
                .queryParam("interval", "1day")
                .queryParam("outputsize", "30")  // Last 30 days
                .queryParam("apikey", apiKey)
                .toUriString();

        try {
            // Call the API
            TimeSeriesResponse response = restTemplate.getForObject(url, TimeSeriesResponse.class);

            // Increment usage counter
            monthlyBudget.incrementUsage();

            log.info("Successfully fetched history for {}. Monthly usage: {}/500, Minute remaining: {}/20",
                    symbol,
                    monthlyBudget.getCurrentUsage(),
                    minuteLimiter.getRemainingRequests());

            return response;

        } catch (Exception e) {
            log.error("Failed to fetch historical data for {}", symbol, e);
            throw new Exception("API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get current API usage statistics.
     *
     * @return Map with usage information
     */
    public Map<String, Object> getUsageStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("monthlyUsed", monthlyBudget.getCurrentUsage());
        stats.put("monthlyLimit", monthlyBudget.getMonthlyLimit());
        stats.put("monthlyRemaining", monthlyBudget.getRemainingBudget());
        stats.put("monthlyPercentage", monthlyBudget.getUsagePercentage());
        stats.put("minuteUsed", minuteLimiter.getCurrentRequestCount());
        stats.put("minuteLimit", 20);
        stats.put("minuteRemaining", minuteLimiter.getRemainingRequests());
        return stats;
    }
}
