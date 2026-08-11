package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Comment {
    private Integer cid;
    private Integer postId;         // 所属帖子
    private Integer userId;         // 评论者
    private String userName;        // 非数据库字段，用于显示评论者昵称
    private String content;         // 评论内容
    private Integer parentId;       // 父评论ID（用于回复，0表示根评论）
    private Date createTime;
}
