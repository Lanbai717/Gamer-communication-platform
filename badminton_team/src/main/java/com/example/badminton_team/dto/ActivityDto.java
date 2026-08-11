package com.example.badminton_team.dto;

import com.example.badminton_team.entity.Activity;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ActivityDto {
    private Integer aid;
    @NotBlank
    private String title;
    private Integer venueId;
    private String location;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;
    @NotNull
    private Integer maxPlayers;
    private String levelRequire;
    private String description;
}
