package com.crypto.tracker.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MinuteLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final long WINDOW_SIZE_SECONDS = 60;

    // Thread-safe queue to store timestamps of recent API calls
    private final Queue<Long> requestTimestamps = new ConcurrentLinkedQueue<>();

    public synchronized boolean allowRequest() {
        long currentTime = Instant.now().getEpochSecond();
        long windowStart = currentTime - WINDOW_SIZE_SECONDS;

        // Remove timestamps older than 60 seconds (outside the sliding window)
        while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < windowStart) {
            requestTimestamps.poll();
        }

        // Check if we're under the limit
        if (requestTimestamps.size() < MAX_REQUESTS_PER_MINUTE) {
            requestTimestamps.offer(currentTime);
            return true;
        }

        return false;  // Rate limit exceeded
    }

    public synchronized int getCurrentRequestCount() {
        long currentTime = Instant.now().getEpochSecond();
        long windowStart = currentTime - WINDOW_SIZE_SECONDS;

        // Clean up old timestamps
        while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < windowStart) {
            requestTimestamps.poll();
        }

        return requestTimestamps.size();
    }

    public int getRemainingRequests() {
        return MAX_REQUESTS_PER_MINUTE - getCurrentRequestCount();
    }

    public long getSecondsUntilReset() {
        if (requestTimestamps.isEmpty()) {
            return 0;
        }

        long currentTime = Instant.now().getEpochSecond();
        long oldestTimestamp = requestTimestamps.peek();
        long timeUntilExpiry = (oldestTimestamp + WINDOW_SIZE_SECONDS) - currentTime;

        return Math.max(0, timeUntilExpiry);
    }
}
