package com.example.badminton_team.entity;

import lombok.Data;
import java.util.Date;

@Data
public class MatchWeight {
    private Integer id;
    private String featureName;
    private Double weight;
    private Date updateTime;
}