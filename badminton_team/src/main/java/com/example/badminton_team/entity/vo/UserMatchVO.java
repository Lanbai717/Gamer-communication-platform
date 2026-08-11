package com.example.badminton_team.entity.vo;

import lombok.Data;

@Data
public class UserMatchVO {
    private Integer uid;
    private String nickname;
    private String avatar;
    private String levelText;   // 等级中文
    private String location;    // 省/市/区
    private Double score;       // 匹配得分
}