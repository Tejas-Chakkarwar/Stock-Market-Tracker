package com.crypto.tracker.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class MonthlyBudget {

    private static final int MONTHLY_REQUEST_LIMIT = 500;
    private static final String REDIS_KEY_PREFIX = "api:usage:";

    private final StringRedisTemplate redisTemplate;

    public MonthlyBudget(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getCurrentMonthKey() {
        YearMonth currentMonth = YearMonth.now(ZoneId.systemDefault());
        return REDIS_KEY_PREFIX + currentMonth.toString();
    }

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

    public int getRemainingBudget() {
        return MONTHLY_REQUEST_LIMIT - getCurrentUsage();
    }

    public boolean hasRemainingBudget() {
        return getCurrentUsage() < MONTHLY_REQUEST_LIMIT;
    }

    public double getUsagePercentage() {
        int current = getCurrentUsage();
        return (current * 100.0) / MONTHLY_REQUEST_LIMIT;
    }

    public int getMonthlyLimit() {
        return MONTHLY_REQUEST_LIMIT;
    }

    public void resetUsage() {
        String key = getCurrentMonthKey();
        redisTemplate.delete(key);
    }
}
