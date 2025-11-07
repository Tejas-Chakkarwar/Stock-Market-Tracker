package com.crypto.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Represents a cryptocurrency quote from Twelve Data API.
 *
 * Example API response:
 * {
 *   "symbol": "BTC/USD",
 *   "name": "Bitcoin/USD",
 *   "close": "35200.00",
 *   "percent_change": "0.86",
 *   ...
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignore fields we don't need
public class CryptoQuote implements Serializable {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("open")
    private String open;

    @JsonProperty("high")
    private String high;

    @JsonProperty("low")
    private String low;

    @JsonProperty("close")
    private String close;  // Current price

    @JsonProperty("volume")
    private String volume;

    @JsonProperty("previous_close")
    private String previousClose;

    @JsonProperty("change")
    private String change;

    @JsonProperty("percent_change")
    private String percentChange;

    @JsonProperty("average_volume")
    private String averageVolume;

    /**
     * Get the current price as a double.
     * Twelve Data returns prices as strings, so we need to parse them.
     */
    public Double getCurrentPrice() {
        try {
            return close != null ? Double.parseDouble(close) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get the percentage change as a double.
     */
    public Double getPercentChangeValue() {
        try {
            return percentChange != null ? Double.parseDouble(percentChange) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Getters and Setters
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

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(String previousClose) {
        this.previousClose = previousClose;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(String percentChange) {
        this.percentChange = percentChange;
    }

    public String getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(String averageVolume) {
        this.averageVolume = averageVolume;
    }
}
