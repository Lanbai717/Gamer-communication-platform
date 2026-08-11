package com.example.badminton_team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.util.Date;

@Data
public class UserUpdateDto {
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private String avatar;          // 头像URL
    private String skillLevel;      // 技术水平
    private String gender;          // 性别
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date birthday;          // 生日
    private String signature;       // 个性签名
    private String province;
    private String city;
    private String district;
}