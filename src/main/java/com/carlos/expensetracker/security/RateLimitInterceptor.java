package com.carlos.expensetracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimitService rateLimitService;

    @Override
    //analyze if it's right to throw a generic exception
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        String requestURI = request.getRequestURI();

        if (isPublicEndpoint(requestURI)) return true;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            String username = authentication.getName();

            rateLimitService.checkRateLimit(username);

            RateLimitService.RateLimitInfo info = rateLimitService.getRateLimitInfo(username);
            response.setHeader("X-RateLimit-Limit-Minute", String.valueOf(info.limitPerMinute()));
            response.setHeader("X-RateLimit-Remaining-Minute", String.valueOf(info.remainingPerMinute()));
            response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(info.limitPerHour()));
            response.setHeader("X-RateLimit-Remaining-Hour", String.valueOf(info.remainingPerHour()));
        }


        return true;
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/api/auth/")
                || requestURI.startsWith("/actuator/")
                || requestURI.startsWith("/swagger-ui/")
                || requestURI.startsWith("/v3/api-docs/");
    }
}
