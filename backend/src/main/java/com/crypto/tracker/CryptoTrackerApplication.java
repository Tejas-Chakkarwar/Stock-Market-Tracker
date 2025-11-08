package com.crypto.tracker;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // Enable Spring's caching support
public class CryptoTrackerApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file (for local development)
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("../")  // Look for .env in project root (one level up from backend/)
                    .ignoreIfMissing()  // Don't fail if .env doesn't exist (e.g., in production)
                    .load();

            // Set system properties so Spring Boot can access them
            dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
            );

            System.out.println("Successfully loaded .env file from project root");
        } catch (Exception e) {
            // If .env loading fails, continue anyway (environment variables might be set differently)
            System.out.println("Note: .env file not loaded, using system environment variables");
        }

        SpringApplication.run(CryptoTrackerApplication.class, args);
    }
}
