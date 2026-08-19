package com.devmarquinhos.priowl.core;

import com.devmarquinhos.priowl.security.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    public WebConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(
                        "/api/users/login",
                        "/api/users/register",
                        "/api/auth/password/forgot",
                        "/api/auth/password/reset",
                        "/api/users/me/password/request-token"
                );
    }
}