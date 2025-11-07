package com.crypto.tracker.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Rate limiter that enforces Twelve Data's 20 requests per minute limit.
 *
 * Uses a sliding window approach:
 * - Stores timestamps of recent API calls in a queue
 * - Before each API call, removes timestamps older than 60 seconds
 * - Allows the call only if fewer than 20 calls in the last minute
 *
 * This prevents hitting the API rate limit and getting temporarily banned.
 */
@Component
public class MinuteLimiter {

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final long WINDOW_SIZE_SECONDS = 60;

    // Thread-safe queue to store timestamps of recent API calls
    private final Queue<Long> requestTimestamps = new ConcurrentLinkedQueue<>();

    /**
     * Check if we can make another API request without exceeding the rate limit.
     *
     * @return true if request is allowed, false if rate limit would be exceeded
     */
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

    /**
     * Get the number of requests made in the current minute window.
     *
     * @return current request count
     */
    public synchronized int getCurrentRequestCount() {
        long currentTime = Instant.now().getEpochSecond();
        long windowStart = currentTime - WINDOW_SIZE_SECONDS;

        // Clean up old timestamps
        while (!requestTimestamps.isEmpty() && requestTimestamps.peek() < windowStart) {
            requestTimestamps.poll();
        }

        return requestTimestamps.size();
    }

    /**
     * Get remaining requests available in the current minute.
     *
     * @return number of requests that can still be made
     */
    public int getRemainingRequests() {
        return MAX_REQUESTS_PER_MINUTE - getCurrentRequestCount();
    }

    /**
     * Get the time in seconds until the next request slot becomes available.
     *
     * @return seconds to wait, or 0 if requests are available now
     */
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
