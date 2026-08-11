package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Admin {
    private Integer aid;            // 管理员ID
    private String username;        // 登录名
    private String password;        // 加密后的密码
    private String nickname;        // 昵称
    private String avatar;          // 头像
    private Integer status;         // 0-正常，1-禁用
    private Date createTime;
    private Date updateTime;
}
