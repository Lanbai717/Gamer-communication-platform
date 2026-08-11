package com.example.badminton_team.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 对应 application.yml 中的 jwt 前缀
 */
@Component
@ConfigurationProperties(prefix = "jwt")

public class JwtProperties {
    /**
     * JWT 密钥（至少 32 位）
     */
    private String secret;

    /**
     * JWT 过期时间（毫秒），默认 24 小时
     */
    private Long expiration = 86400000L;

    // getter 和 setter 方法
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }


}