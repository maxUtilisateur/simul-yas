package com.maxime.smul_yas.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class KeepAliveService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Runs every 30 seconds to print a heartbeat log.
     * Also triggers an asynchronous self-ping request to the Render public URL
     * to prevent the free tier instance from spinning down.
     */
    @Scheduled(fixedRate = 30000)
    public void keepAlive() {
        System.out.println("[HEARTBEAT] Server is active. Timestamp: " + LocalDateTime.now());

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://simul-yas-2.onrender.com/api/offres/categories"))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        System.out.println("[SELF-PING] Status received: " + response.statusCode());
                    })
                    .exceptionally(ex -> {
                        System.out.println("[SELF-PING] Request dispatched (offline/dns resolution expected locally): " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("[SELF-PING] Error preparing request: " + e.getMessage());
        }
    }
}
