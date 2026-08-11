package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ChatMessage {
    private Long id;
    private Integer senderUid;
    private Integer receiverUid;
    private String content;
    private Integer isRead;
    private Date createTime;
}