package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer uid;            // 用户ID
    private String username;        // 用户名
    private String password;        // 加密密码
    private String nickname;        // 昵称
    private String email;           // 邮箱
    private String phone;           // 手机号
    private String avatar;          // 头像
    private String skillLevel;      // 技术水平（0-7）
    private String gender;          // 性别
    private Date birthday;          // 生日
    private String signature;       // 个性签名
    private Integer status;         // 状态：0-正常，1-禁用
    private Date createTime;
    private Date updateTime;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区域 */
    private String district;
}