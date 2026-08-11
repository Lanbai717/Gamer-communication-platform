package com.example.badminton_team.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                // 允许所有路径
                .allowedOriginPatterns("*")       // 允许所有来源（开发阶段）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 明确允许 OPTIONS
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}