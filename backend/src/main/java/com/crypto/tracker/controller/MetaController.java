package com.crypto.tracker.controller;

import com.crypto.tracker.dto.ApiLimitsResponse;
import com.crypto.tracker.service.TwelveDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/meta")
@CrossOrigin(origins = "${FRONTEND_URL:http://localhost:3000}")
public class MetaController {

    private static final Logger log = LoggerFactory.getLogger(MetaController.class);

    private final TwelveDataService twelveDataService;

    public MetaController(TwelveDataService twelveDataService) {
        this.twelveDataService = twelveDataService;
    }

    @GetMapping("/limits")
    public ResponseEntity<ApiLimitsResponse> getLimits() {
        log.debug("GET /api/meta/limits - Fetching API usage statistics");

        try {
            // Get usage stats from service
            Map<String, Object> stats = twelveDataService.getUsageStats();

            // Build response DTO
            ApiLimitsResponse response = new ApiLimitsResponse();
            response.setMonthlyUsed((Integer) stats.get("monthlyUsed"));
            response.setMonthlyLimit((Integer) stats.get("monthlyLimit"));
            response.setMonthlyRemaining((Integer) stats.get("monthlyRemaining"));
            response.setMonthlyPercentage((Double) stats.get("monthlyPercentage"));

            response.setMinuteUsed((Integer) stats.get("minuteUsed"));
            response.setMinuteLimit((Integer) stats.get("minuteLimit"));
            response.setMinuteRemaining((Integer) stats.get("minuteRemaining"));

            // Set warning flag if > 80% of monthly budget used
            double percentage = response.getMonthlyPercentage();
            response.setWarningLevel(percentage > 80.0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching API limits", e);
            // Return default/safe values on error
            ApiLimitsResponse errorResponse = new ApiLimitsResponse(
                0, 500, 500, 0.0,
                0, 20, 20,
                false
            );
            return ResponseEntity.ok(errorResponse);
        }
    }
}
