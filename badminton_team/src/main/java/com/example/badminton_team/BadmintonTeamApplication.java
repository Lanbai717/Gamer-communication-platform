package com.example.badminton_team;

import com.example.badminton_team.controller.ChatEndpoint;
import com.example.badminton_team.dao.ChatMessageDao;
import com.example.badminton_team.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;

@SpringBootApplication
public class BadmintonTeamApplication {

    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        ChatEndpoint.setJwtUtil(jwtUtil);
    }

    @Autowired
    public void setChatMessageDao(ChatMessageDao dao) {
        ChatEndpoint.setChatMessageDao(dao);
    }

    public static void main(String[] args) {
        SpringApplication.run(BadmintonTeamApplication.class, args);
    }


//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize(DataSize.ofMegabytes(10));
//        factory.setMaxRequestSize(DataSize.ofMegabytes(10));
//        return factory.createMultipartConfig();
//    }
}