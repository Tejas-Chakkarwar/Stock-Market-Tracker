package com.crypto.tracker.controller;

import com.crypto.tracker.dto.CryptoHistoryResponse;
import com.crypto.tracker.dto.CryptoIndexResponse;
import com.crypto.tracker.dto.HistoryDataPoint;
import com.crypto.tracker.model.CryptoQuote;
import com.crypto.tracker.model.TimeSeriesResponse;
import com.crypto.tracker.model.TimeSeriesValue;
import com.crypto.tracker.service.TwelveDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller for stock market indices data endpoints.
 *
 * Endpoints:
 * - GET /api/indices - List all tracked stock indices with current prices
 * - GET /api/indices/{symbol}/history - Get 30-day price history for a specific index
 */
@RestController
@RequestMapping("/api/indices")
@CrossOrigin(origins = "${FRONTEND_URL:http://localhost:3000}")
public class CryptoController {

    private static final Logger log = LoggerFactory.getLogger(CryptoController.class);

    private final TwelveDataService twelveDataService;

    public CryptoController(TwelveDataService twelveDataService) {
        this.twelveDataService = twelveDataService;
    }

    /**
     * GET /api/indices
     *
     * Returns a list of all tracked stock market indices with their current prices.
     *
     * Response is cached for 120 seconds to minimize API calls.
     *
     * @return List of stock indices with current prices
     */
    @GetMapping
    public ResponseEntity<?> getAllIndices() {
        log.info("GET /api/indices - Fetching all stock market indices");

        try {
            // Fetch current prices from Twelve Data (or cache)
            Map<String, CryptoQuote> quotes = twelveDataService.getCurrentPrices();

            // Transform to response DTOs
            List<CryptoIndexResponse> response = new ArrayList<>();
            for (Map.Entry<String, CryptoQuote> entry : quotes.entrySet()) {
                CryptoQuote quote = entry.getValue();

                // Convert timestamp from seconds to milliseconds for JavaScript
                Long timestamp = quote.getTimestamp();
                if (timestamp != null && timestamp < 10000000000L) {
                    // If timestamp is less than 10 billion, it's in seconds, convert to milliseconds
                    timestamp = timestamp * 1000;
                }

                CryptoIndexResponse dto = new CryptoIndexResponse(
                    quote.getSymbol(),
                    quote.getName(),
                    quote.getCurrentPrice(),
                    quote.getPercentChangeValue(),
                    quote.getExchange(),
                    timestamp
                );
                response.add(dto);
            }

            log.info("Successfully returned {} stock market indices", response.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching stock market indices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/indices/{symbol}/history
     *
     * Returns 30-day historical price data for a specific stock market index.
     * Includes OHLC data for charting and calculated min/max/avg statistics.
     *
     * Symbol format: Use URL-safe format (e.g., "SPX", "DJI", "IXIC")
     *
     * Response is cached for 300 seconds (historical data doesn't change).
     *
     * @param symbol Stock index symbol (e.g., "SPX", "DJI", "IXIC", "FTSE")
     * @return Historical price data with statistics
     */
    @GetMapping("/{symbol}/history")
    public ResponseEntity<?> getHistory(@PathVariable String symbol) {
        // Convert URL-safe symbol format back to API format if needed
        // For simple symbols like SPX, DJI, this does nothing
        String apiSymbol = symbol.replace("-", "/");

        log.info("GET /api/indices/{}/history - Fetching historical data for index", symbol);

        try {
            // Fetch historical data from Twelve Data (or cache)
            TimeSeriesResponse timeSeriesResponse = twelveDataService.getHistoricalPrices(apiSymbol);

            if (timeSeriesResponse == null || timeSeriesResponse.getValues() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No historical data found for " + symbol));
            }

            // Transform to response DTOs
            List<HistoryDataPoint> historyPoints = new ArrayList<>();
            double minPrice = Double.MAX_VALUE;
            double maxPrice = Double.MIN_VALUE;
            double sumPrice = 0.0;
            int count = 0;

            for (TimeSeriesValue value : timeSeriesResponse.getValues()) {
                Double closePrice = value.getClosePrice();

                // Build data point for chart
                HistoryDataPoint point = new HistoryDataPoint(
                    value.getDatetime(),
                    value.getClosePrice(),  // Using close as open for simplicity
                    value.getHighPrice(),
                    value.getLowPrice(),
                    closePrice,
                    value.getVolume()
                );
                historyPoints.add(point);

                // Calculate statistics
                if (closePrice != null) {
                    minPrice = Math.min(minPrice, closePrice);
                    maxPrice = Math.max(maxPrice, closePrice);
                    sumPrice += closePrice;
                    count++;
                }
            }

            double avgPrice = count > 0 ? sumPrice / count : 0.0;

            // Build response
            CryptoHistoryResponse response = new CryptoHistoryResponse(
                apiSymbol,
                timeSeriesResponse.getMeta() != null ? timeSeriesResponse.getMeta().getSymbol() : apiSymbol,
                historyPoints,
                minPrice == Double.MAX_VALUE ? null : minPrice,
                maxPrice == Double.MIN_VALUE ? null : maxPrice,
                avgPrice
            );

            log.info("Successfully returned {} days of history for {}", historyPoints.size(), symbol);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching history for {}", symbol, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
