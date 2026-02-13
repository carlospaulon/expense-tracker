package com.carlos.expensetracker.security;

import com.carlos.expensetracker.exception.RateLimitingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RateLimitServiceTest {
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
    }

    @Test
    @DisplayName("Should allow requests under the rate limit")
    void testAllowRequestsUnderLimit() {
        String userId = "user1";

        for (int i = 0; i < 50; i++) {
            rateLimitService.checkRateLimit(userId);
        }
    }

    @Test
    @DisplayName("Should throw exception when per-minute limit exceeded")
    void testPerMinuteLimitExceeded() {
        String userId = "user2";

        for (int i = 0; i < 60; i++) {
            rateLimitService.checkRateLimit(userId);
        }

        assertThatThrownBy(() -> rateLimitService.checkRateLimit(userId))
                .isInstanceOf(RateLimitingException.class)
                .hasMessageContaining("Too many requests");
    }

    @Test
    @DisplayName("Should track rate limit info correctly")
    void testGetRateLimitInfo() {

        String userId = "user3";

        for (int i = 0; i < 10; i++) {
            rateLimitService.checkRateLimit(userId);
        }

        RateLimitService.RateLimitInfo info = rateLimitService.getRateLimitInfo(userId);

        assertThat(info.limitPerMinute()).isEqualTo(60);
        assertThat(info.remainingPerMinute()).isEqualTo(50);
        assertThat(info.limitPerHour()).isEqualTo(1000);
        assertThat(info.remainingPerHour()).isEqualTo(990);
    }

    @Test
    @DisplayName("Should return full limits for new user")
    void testGetRateLimitInfoForNewUser() {
        String userId = "newUser";

        RateLimitService.RateLimitInfo info = rateLimitService.getRateLimitInfo(userId);

        assertThat(info.limitPerMinute()).isEqualTo(60);
        assertThat(info.remainingPerMinute()).isEqualTo(60);
        assertThat(info.limitPerHour()).isEqualTo(1000);
        assertThat(info.remainingPerHour()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Should clear rate limit for user")
    void testClearRateLimit() {
        String userId = "user4";

        for (int i = 0; i < 30; i++) {
            rateLimitService.checkRateLimit(userId);
        }

        RateLimitService.RateLimitInfo infoBefore = rateLimitService.getRateLimitInfo(userId);
        assertThat(infoBefore.remainingPerMinute()).isEqualTo(30);

        rateLimitService.clearRate(userId);

        RateLimitService.RateLimitInfo infoAfter = rateLimitService.getRateLimitInfo(userId);
        assertThat(infoAfter.remainingPerMinute()).isEqualTo(60);
    }

    @Test
    @DisplayName("Should remove users that don't consume nothing")
    void testEvictInactiveBuckets() {
        String userId = "inactiveUser";

        rateLimitService.checkRateLimit(userId);

        rateLimitService.clearRate(userId);

        rateLimitService.evictInactiveBuckets();

        RateLimitService.RateLimitInfo info =
                rateLimitService.getRateLimitInfo(userId);

        assertThat(info.remainingPerMinute()).isEqualTo(60);
    }

    @Test
    @DisplayName("Should isolate rate limits per user")
    void testIsolateRateLimitsPerUser() {
        String user1 = "user5";
        String user2 = "user6";

        for (int i = 0; i < 50; i++) {
            rateLimitService.checkRateLimit(user1);
        }

        for (int i = 0; i < 10; i++) {
            rateLimitService.checkRateLimit(user2);
        }

        RateLimitService.RateLimitInfo info1 = rateLimitService.getRateLimitInfo(user1);
        RateLimitService.RateLimitInfo info2 = rateLimitService.getRateLimitInfo(user2);

        assertThat(info1.remainingPerMinute()).isEqualTo(10);
        assertThat(info2.remainingPerMinute()).isEqualTo(50);
    }
}
