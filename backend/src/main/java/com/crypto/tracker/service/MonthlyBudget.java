package com.crypto.tracker.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Tracks monthly API usage against Twelve Data's 500 requests/month budget.
 *
 * Uses Redis to persist the counter across application restarts.
 * Automatically resets at the start of each calendar month.
 *
 * Key format in Redis: "api:usage:YYYY-MM" (e.g., "api:usage:2024-11")
 */
@Component
public class MonthlyBudget {

    private static final int MONTHLY_REQUEST_LIMIT = 500;
    private static final String REDIS_KEY_PREFIX = "api:usage:";

    private final StringRedisTemplate redisTemplate;

    public MonthlyBudget(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Get the Redis key for the current month.
     *
     * @return key in format "api:usage:YYYY-MM"
     */
    private String getCurrentMonthKey() {
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        return REDIS_KEY_PREFIX + currentMonth.toString();
    }

    /**
     * Increment the usage counter for this month.
     * Sets TTL to auto-delete the key after the month ends.
     */
    public void incrementUsage() {
        String key = getCurrentMonthKey();

        // Increment counter (creates key if it doesn't exist)
        redisTemplate.opsForValue().increment(key, 1);

        // Set expiry to end of next month (ensures cleanup)
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        YearMonth nextMonth = currentMonth.plusMonths(2);
        long daysUntilExpiry = ChronoUnit.DAYS.between(currentMonth.atDay(1), nextMonth.atDay(1));

        redisTemplate.expire(key, daysUntilExpiry, TimeUnit.DAYS);
    }

    /**
     * Get the current usage count for this month.
     *
     * @return number of API requests made this month
     */
    public int getCurrentUsage() {
        String key = getCurrentMonthKey();
        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Get the number of requests remaining in this month's budget.
     *
     * @return remaining requests (can be negative if over budget)
     */
    public int getRemainingBudget() {
        return MONTHLY_REQUEST_LIMIT - getCurrentUsage();
    }

    /**
     * Check if we have budget remaining for this month.
     *
     * @return true if requests are available, false if monthly limit exceeded
     */
    public boolean hasRemainingBudget() {
        return getCurrentUsage() < MONTHLY_REQUEST_LIMIT;
    }

    /**
     * Get the percentage of monthly budget used.
     *
     * @return usage percentage (0-100+)
     */
    public double getUsagePercentage() {
        int current = getCurrentUsage();
        return (current * 100.0) / MONTHLY_REQUEST_LIMIT;
    }

    /**
     * Get the monthly limit.
     *
     * @return maximum requests allowed per month
     */
    public int getMonthlyLimit() {
        return MONTHLY_REQUEST_LIMIT;
    }

    /**
     * Reset the counter (mainly for testing purposes).
     */
    public void resetUsage() {
        String key = getCurrentMonthKey();
        redisTemplate.delete(key);
    }
}
