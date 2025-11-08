package com.crypto.tracker.dto;

public class CryptoIndexResponse {

    private String symbol;           // e.g., "BTC/USD"
    private String name;             // e.g., "Bitcoin/USD"
    private Double currentPrice;     // Current price
    private Double percentChange;    // 24h percentage change
    private String exchange;         // Exchange name
    private Long timestamp;          // Last update timestamp

    public CryptoIndexResponse() {
    }

    public CryptoIndexResponse(String symbol, String name, Double currentPrice, Double percentChange,
                               String exchange, Long timestamp) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.percentChange = percentChange;
        this.exchange = exchange;
        this.timestamp = timestamp;
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

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
