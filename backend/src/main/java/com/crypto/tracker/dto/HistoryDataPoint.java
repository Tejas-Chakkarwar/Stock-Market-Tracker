package com.crypto.tracker.dto;

public class HistoryDataPoint {

    private String date;      // e.g., "2024-11-05"
    private Double open;      // Opening price
    private Double high;      // High price
    private Double low;       // Low price
    private Double close;     // Closing price
    private String volume;    // Trading volume

    public HistoryDataPoint() {
    }

    public HistoryDataPoint(String date, Double open, Double high, Double low, Double close, String volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
