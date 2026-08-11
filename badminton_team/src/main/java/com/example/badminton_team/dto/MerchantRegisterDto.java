package com.example.badminton_team.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class MerchantRegisterDto {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "公司/场馆名称不能为空")
    private String companyName;

    @NotBlank(message = "联系人姓名不能为空")
    private String contactPerson;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String address;

    private String businessLicense;  // 营业执照图片URL
}
