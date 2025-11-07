package com.crypto.tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MinuteLimiter to verify rate limiting behavior.
 */
class MinuteLimiterTest {

    private MinuteLimiter limiter;

    @BeforeEach
    void setUp() {
        limiter = new MinuteLimiter();
    }

    @Test
    void testAllowsRequestsUnderLimit() {
        // Should allow first 20 requests
        for (int i = 0; i < 20; i++) {
            assertTrue(limiter.allowRequest(),
                "Request " + (i + 1) + " should be allowed");
        }

        // 21st request should be blocked
        assertFalse(limiter.allowRequest(),
            "21st request should be blocked");
    }

    @Test
    void testGetCurrentRequestCount() {
        // Make 5 requests
        for (int i = 0; i < 5; i++) {
            limiter.allowRequest();
        }

        assertEquals(5, limiter.getCurrentRequestCount(),
            "Should show 5 requests made");
    }

    @Test
    void testGetRemainingRequests() {
        // Make 15 requests
        for (int i = 0; i < 15; i++) {
            limiter.allowRequest();
        }

        assertEquals(5, limiter.getRemainingRequests(),
            "Should have 5 requests remaining");
    }

    @Test
    void testInitialState() {
        assertEquals(0, limiter.getCurrentRequestCount(),
            "Should start with 0 requests");
        assertEquals(20, limiter.getRemainingRequests(),
            "Should have 20 requests available initially");
    }
}
