package com.carlos.expensetracker.security;

import com.carlos.expensetracker.exception.RateLimitingException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
@EnableScheduling
public class RateLimitService {

    private final Map<String, UserRateLimit> buckets = new ConcurrentHashMap<>();

    private static final int LIMIT_PER_MINUTE = 60;
    private static final int LIMIT_PER_HOUR = 1000;


    private UserRateLimit createBucket() {

        Bucket minuteBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(
                        LIMIT_PER_MINUTE,
                        Refill.greedy(LIMIT_PER_MINUTE, Duration.ofMinutes(1))
                ))
                .build();

        Bucket hourBucket = Bucket.builder()
                .addLimit(Bandwidth.classic(
                        LIMIT_PER_HOUR,
                        Refill.greedy(LIMIT_PER_HOUR, Duration.ofHours(1))
                ))
                .build();

        return new UserRateLimit(minuteBucket, hourBucket);

    }

    public void checkRateLimit(String userId) {
        UserRateLimit userLimit = buckets.computeIfAbsent(userId, k -> createBucket());

        boolean minuteAllowed = userLimit.minuteBucket.tryConsume(1);
        boolean hourAllowed = userLimit.hourBucket.tryConsume(1);

        if (!minuteAllowed || !hourAllowed) {
            long minuteWait = userLimit.minuteBucket
                    .estimateAbilityToConsume(1)
                    .getNanosToWaitForRefill();

            long hourWait = userLimit.hourBucket
                    .estimateAbilityToConsume(1)
                    .getNanosToWaitForRefill();

            long secondsToWait = Duration.ofNanos(
                    Math.max(minuteWait, hourWait)
            ).toSeconds();

            log.warn("Rate limit exceed for user: {}", userId);

            throw new RateLimitingException(
                    "Too many requests. Try again in " + secondsToWait + " seconds",
                    secondsToWait
            );
        }
    }

    public void clearRate(String userId) {
        buckets.remove(userId);
    }

    @Scheduled(fixedRate = 600000) //10min
    public void evictInactiveBuckets() {
        buckets.entrySet().removeIf(entry -> {
           UserRateLimit userLimit = entry.getValue();

           boolean minuteFull = userLimit.minuteBucket.getAvailableTokens() == LIMIT_PER_MINUTE;
           boolean hourFull = userLimit.hourBucket.getAvailableTokens() == LIMIT_PER_HOUR;

           return minuteFull && hourFull;
        });
    }

    public RateLimitInfo getRateLimitInfo(String userId) {
        UserRateLimit userLimit = buckets.get(userId);

        if (userLimit == null) {
            return new RateLimitInfo(LIMIT_PER_MINUTE, LIMIT_PER_HOUR, LIMIT_PER_MINUTE, LIMIT_PER_HOUR);
        }

        int remainingMinute = (int) userLimit.minuteBucket.getAvailableTokens();
        int remainingHour = (int) userLimit.hourBucket.getAvailableTokens();

        return new RateLimitInfo(
                LIMIT_PER_MINUTE,
                LIMIT_PER_HOUR,
                remainingMinute,
                remainingHour
        );
    }

    public record RateLimitInfo(
            int limitPerMinute,
            int limitPerHour,
            int remainingPerMinute,
            int remainingPerHour
    ) {
    }

    private static class UserRateLimit {
        private final Bucket minuteBucket;
        private final Bucket hourBucket;

        public UserRateLimit(Bucket minuteBucket, Bucket hourBucket) {
            this.minuteBucket = minuteBucket;
            this.hourBucket = hourBucket;
        }

    }

    //evictInactiveBuckets methods - cleaning the buckets for inactive users @Schedule
}
