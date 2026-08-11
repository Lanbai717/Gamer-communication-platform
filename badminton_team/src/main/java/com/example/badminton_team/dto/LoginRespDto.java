package com.example.badminton_team.dto;

import lombok.Data;

@Data
public class LoginRespDto {
    private String token;
    private Object userInfo;  // 可以是 User、Merchant 或 Admin
}
