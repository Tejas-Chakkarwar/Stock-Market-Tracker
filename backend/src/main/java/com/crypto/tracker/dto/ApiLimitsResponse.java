package com.crypto.tracker.dto;

public class ApiLimitsResponse {

    // Monthly limits
    private Integer monthlyUsed;         // Requests used this month
    private Integer monthlyLimit;        // Total monthly limit (500)
    private Integer monthlyRemaining;    // Requests remaining
    private Double monthlyPercentage;    // Usage percentage

    // Per-minute limits
    private Integer minuteUsed;          // Requests in current minute
    private Integer minuteLimit;         // Per-minute limit (20)
    private Integer minuteRemaining;     // Requests available

    // Warning flag for UI
    private Boolean warningLevel;        // True if > 80% of monthly budget used

    // Default constructor
    public ApiLimitsResponse() {
    }

    // All-args constructor
    public ApiLimitsResponse(Integer monthlyUsed, Integer monthlyLimit, Integer monthlyRemaining,
                             Double monthlyPercentage, Integer minuteUsed, Integer minuteLimit,
                             Integer minuteRemaining, Boolean warningLevel) {
        this.monthlyUsed = monthlyUsed;
        this.monthlyLimit = monthlyLimit;
        this.monthlyRemaining = monthlyRemaining;
        this.monthlyPercentage = monthlyPercentage;
        this.minuteUsed = minuteUsed;
        this.minuteLimit = minuteLimit;
        this.minuteRemaining = minuteRemaining;
        this.warningLevel = warningLevel;
    }

    // Getters and Setters
    public Integer getMonthlyUsed() {
        return monthlyUsed;
    }

    public void setMonthlyUsed(Integer monthlyUsed) {
        this.monthlyUsed = monthlyUsed;
    }

    public Integer getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Integer monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public Integer getMonthlyRemaining() {
        return monthlyRemaining;
    }

    public void setMonthlyRemaining(Integer monthlyRemaining) {
        this.monthlyRemaining = monthlyRemaining;
    }

    public Double getMonthlyPercentage() {
        return monthlyPercentage;
    }

    public void setMonthlyPercentage(Double monthlyPercentage) {
        this.monthlyPercentage = monthlyPercentage;
    }

    public Integer getMinuteUsed() {
        return minuteUsed;
    }

    public void setMinuteUsed(Integer minuteUsed) {
        this.minuteUsed = minuteUsed;
    }

    public Integer getMinuteLimit() {
        return minuteLimit;
    }

    public void setMinuteLimit(Integer minuteLimit) {
        this.minuteLimit = minuteLimit;
    }

    public Integer getMinuteRemaining() {
        return minuteRemaining;
    }

    public void setMinuteRemaining(Integer minuteRemaining) {
        this.minuteRemaining = minuteRemaining;
    }

    public Boolean getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(Boolean warningLevel) {
        this.warningLevel = warningLevel;
    }
}
