package com.example.badminton_team.entity.vo;

import lombok.Data;
import java.util.List;

@Data
public class ActivityDetailVO {
    private Integer aid;
    private String title;
    private String location;
    private String startTime;
    private String endTime;
    private Integer maxPlayers;
    private Integer currentPlayers;
    private String status;       // 状态文本
    private String description;
    private String levelRequire;

    // 发起者信息
    private Integer creatorUid;
    private String creatorNickname;
    private String creatorAvatar;

    // 报名人员列表
    private List<Participant> participants;

    @Data
    public static class Participant {
        private Integer uid;
        private String nickname;
        private String avatar;
    }
}