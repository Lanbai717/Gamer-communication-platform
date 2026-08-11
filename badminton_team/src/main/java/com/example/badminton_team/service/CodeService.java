package com.example.badminton_team.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CodeService {
    // 存储邮箱 -> 验证码
    private final Map<String, String> codeMap = new ConcurrentHashMap<>();

    /**
     * 生成6位数字验证码
     */
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 保存验证码，并设置过期时间（简单起见用线程延迟删除，这里不做复杂处理）
     */
    public void saveCode(String email, String code) {
        codeMap.put(email, code);
        // 5分钟后删除
        new Thread(() -> {
            try {
                Thread.sleep(5 * 60 * 1000);
                codeMap.remove(email);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    /**
     * 验证并删除验证码
     */
    public boolean verifyCode(String email, String code) {
        String stored = codeMap.get(email);
        if (stored != null && stored.equals(code)) {
            codeMap.remove(email);
            return true;
        }
        return false;
    }
}