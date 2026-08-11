package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ActivityParticipant {
    private Integer id;
    private Integer activityId;
    private Integer userId;
    private Integer status;       // 0-待确认，1-已确认，2-已取消
    private Date joinTime;
}