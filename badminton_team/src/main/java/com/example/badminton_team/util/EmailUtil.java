package com.example.badminton_team.util;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    @Resource(name = "mailSender")
    private JavaMailSender mailSender;

    /**
     * 发送注册验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     */
    public void sendVerificationCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("1848687180@qq.com");   // 必须与 application.yml 中的 spring.mail.username 一致
            message.setTo(to);
            message.setSubject("球友组队平台注册验证码");
            message.setText("您的注册验证码是：" + code + "，有效期5分钟。");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("邮件发送失败：" + e.getMessage());
        }
    }
}