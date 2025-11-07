package com.crypto.tracker.dto;

import java.util.List;

/**
 * Response DTO for historical price data.
 *
 * This is what the frontend receives when calling GET /api/indices/{symbol}/history
 * Includes the price history and calculated statistics.
 */
public class CryptoHistoryResponse {

    private String symbol;                      // e.g., "BTC/USD"
    private String name;                        // e.g., "Bitcoin/USD"
    private List<HistoryDataPoint> history;     // Array of 30 daily data points

    // Calculated statistics for the 30-day period
    private Double minPrice;                    // Lowest price
    private Double maxPrice;                    // Highest price
    private Double avgPrice;                    // Average closing price

    public CryptoHistoryResponse() {
    }

    public CryptoHistoryResponse(String symbol, String name, List<HistoryDataPoint> history,
                                 Double minPrice, Double maxPrice, Double avgPrice) {
        this.symbol = symbol;
        this.name = name;
        this.history = history;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.avgPrice = avgPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HistoryDataPoint> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryDataPoint> history) {
        this.history = history;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(Double avgPrice) {
        this.avgPrice = avgPrice;
    }
}
