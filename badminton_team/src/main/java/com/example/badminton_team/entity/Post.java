package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Post {
    private Integer pid;
    private Integer userId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;          // 0-正常，1-待审核，2-已拒绝/删除
    private String authorName;       // 冗余字段，方便显示
    private Date createTime;
    private Date updateTime;
}