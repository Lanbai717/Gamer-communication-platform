package com.example.badminton_team.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类（使用 BCrypt 强哈希）
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密明文密码
     * @param rawPassword 明文密码
     * @return 加密后的密文
     */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证明文密码与密文是否匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 密文
     * @return 匹配返回 true
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }
}