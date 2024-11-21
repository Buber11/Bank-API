package main.BankApp.service.bucket;

import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.BankApp.common.Loggable;
import main.BankApp.security.JwtService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class BucketServiceImpl implements BucketService{

    private final JwtService jwtService;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket getBucketForUser(String token) {
        return buckets.computeIfAbsent(token, key -> {
            Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
            return Bucket4j.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    @Override
    @Loggable
    public boolean tryConsume(String token) {
        Bucket userBucket = getBucketForUser(token);
        return userBucket.tryConsume(1);
    }

    @Loggable
    @Scheduled(fixedRate = 60000)
    public void invalidateExpiredBucket(){
        buckets.keySet().removeIf(jwtService::isTokenExpired);
    }



}
