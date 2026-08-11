package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class FriendRequest {
    private Integer id;
    private Integer fromUid;
    private Integer toUid;
    private String message;
    private Integer status; // 0待处理 1同意 2拒绝
    private Date createTime;

    // 扩展字段（非数据库）
    private String fromNickname;
    private String fromAvatar;
}