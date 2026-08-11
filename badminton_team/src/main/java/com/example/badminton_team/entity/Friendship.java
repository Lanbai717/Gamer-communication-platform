package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Friendship {
    private Integer id;
    private Integer userId1;
    private Integer userId2;
    private Date createTime;

    // 扩展字段（非数据库）
    private Integer friendUid;
    private String friendNickname;
    private String friendAvatar;
}