package com.portfolio.mayuri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")  // Matches your API paths like /api/users/login
						.allowedOrigins("https://myportfolio-frontend-pi.vercel.app")  // Your Vercel URL
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Includes preflight OPTIONS
						.allowedHeaders("*")  // Allows all headers
						.allowCredentials(true);  // For cookies/auth if needed
			}
		};
	}
}