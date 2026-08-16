package com.devmarquinhos.priowl.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        // 5 attempts, resets after 15 min
        Refill refill = Refill.intervally(5, Duration.ofMinutes(15));
        Bandwidth limit = Bandwidth.classic(5, refill);

        return Bucket.builder().addLimit(limit).build();
    }
}