package com.example.badminton_team.dto;

import lombok.Data;

@Data
public class ActivityUpdateDto {
    private Integer aid;
    private String title;
    private String location;
    private String startTime;
    private String endTime;
    private Integer maxPlayers;
    private String levelRequire;
    private String description;
}