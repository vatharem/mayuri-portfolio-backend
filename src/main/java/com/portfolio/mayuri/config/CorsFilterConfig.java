package com.portfolio.mayuri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // Use allowedOriginPatterns instead of allowedOrigins
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://myportfolio-frontend-pi.vercel.app",
                "https://myportfolio-frontend-qjlqimbe0-mayuris-projects-62260ff4.vercel.app"
        ));

        config.addAllowedHeader("*");
        config.addExposedHeader("*");

        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");   // <-- VERY IMPORTANT

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
    @CrossOrigin(origins = {
            "http://localhost:3000",
            "http://localhost:5173",
            "https://myportfolio-frontend-pi.vercel.app",
            "https://myportfolio-frontend-qjlqimbe0-mayuris-projects-62260ff4.vercel.app"
    })
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public void handleOptions() {
        // Empty method, just to allow preflight
    }
}
