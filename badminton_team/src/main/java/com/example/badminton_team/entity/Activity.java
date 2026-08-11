package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Activity {
    private Integer aid;            // 活动ID
    private String title;           // 活动标题
    private Integer venueId;        // 关联场地ID（可能为null，如果未指定场地）
    private String location;        // 活动地点（如果未指定场地，则手动填写）
    private Date startTime;         // 开始时间
    private Date endTime;           // 结束时间
    private Integer creatorId;      // 创建者用户ID
    private Integer maxPlayers;     // 总需人数
    private Integer currentPlayers; // 当前已报名人数
    private String levelRequire;    // 水平要求（初级/中级/不限）
    private String description;     // 活动描述
    private Integer status;         // 状态：0-招募中，1-已满员，2-已取消，3-已结束
    private Date createTime;
    private Date updateTime;
}
