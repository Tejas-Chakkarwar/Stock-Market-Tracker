package com.crypto.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeriesValue implements Serializable {

    @JsonProperty("datetime")
    private String datetime;

    @JsonProperty("open")
    private String open;

    @JsonProperty("high")
    private String high;

    @JsonProperty("low")
    private String low;

    @JsonProperty("close")
    private String close;

    @JsonProperty("volume")
    private String volume;

    /**
     * Get close price as double for calculations.
     */
    public Double getClosePrice() {
        try {
            return close != null ? Double.parseDouble(close) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getHighPrice() {
        try {
            return high != null ? Double.parseDouble(high) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getLowPrice() {
        try {
            return low != null ? Double.parseDouble(low) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Getters and Setters
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
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
}
