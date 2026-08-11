package com.example.badminton_team.dto;

import lombok.Data;

@Data
public class TimeSlotDto {
    private String startTime;
    private String endTime;
    private boolean available;
}