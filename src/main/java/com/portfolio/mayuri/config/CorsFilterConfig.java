package com.portfolio.mayuri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 1. Allow Credentials
        // This is required if your frontend sends cookies or auth headers.
        config.setAllowCredentials(true);

        // 2. Add Allowed Origin Patterns
        // We use patterns here to handle the dynamic Vercel URLs (e.g., https://app-git-main.vercel.app)
        // AND your specific production URL.
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",        // React Localhost
                "http://localhost:5173",        // Vite Localhost// Wildcard for all Vercel deployments
                "https://myportfolio-frontend-pi.vercel.app" // Explicit Prod URL
        ));

        // 3. Allow all Headers
        config.addAllowedHeader("*");

        // 4. Allow all Methods (GET, POST, PUT, DELETE, OPTIONS)
        config.addAllowedMethod("*");

        // 5. Apply this configuration to ALL paths
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}