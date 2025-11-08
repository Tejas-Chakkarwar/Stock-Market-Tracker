package com.crypto.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeriesResponse implements Serializable {

    @JsonProperty("meta")
    private TimeSeriesMeta meta;

    @JsonProperty("values")
    private List<TimeSeriesValue> values;

    @JsonProperty("status")
    private String status;

    // Getters and Setters
    public TimeSeriesMeta getMeta() {
        return meta;
    }

    public void setMeta(TimeSeriesMeta meta) {
        this.meta = meta;
    }

    public List<TimeSeriesValue> getValues() {
        return values;
    }

    public void setValues(List<TimeSeriesValue> values) {
        this.values = values;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeSeriesMeta implements Serializable {
        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("interval")
        private String interval;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("exchange_timezone")
        private String exchangeTimezone;

        @JsonProperty("exchange")
        private String exchange;

        @JsonProperty("type")
        private String type;

        // Getters and Setters
        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getExchangeTimezone() {
            return exchangeTimezone;
        }

        public void setExchangeTimezone(String exchangeTimezone) {
            this.exchangeTimezone = exchangeTimezone;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
