package com.portfolio.mayuri.Config;
// <-- CHANGE THIS TO YOUR PACKAGE

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                String deployedUrl = "https://myportfolio-frontend-pi.vercel.app";
                String localUrl = "http://localhost:3000";

                registry.addMapping("/**")
                        .allowedOrigins(localUrl, deployedUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
