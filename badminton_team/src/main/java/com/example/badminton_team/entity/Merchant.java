package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Merchant {
    private Integer mid;            // 商家ID
    private String username;        // 登录名
    private String password;        // 加密密码
    private String companyName;     // 公司/场馆名称
    private String contactPerson;   // 联系人
    private String contactPhone;    // 联系电话
    private String email;           // 邮箱
    private String address;         // 地址
    private String businessLicense; // 营业执照图片URL
    private Integer isSubmitted;    // 0-未提交，1-已提交
    private Integer status;         // 状态：0-待审核，1-正常，2-禁用
    private String businessHours;
    private String facilities;
    private String detailIntro;
    private String videos;
    private String rules;
    private String venuePhotos;
    private String pendingData;     // 待审核数据
    private Date createTime;
    private Date updateTime;
}
