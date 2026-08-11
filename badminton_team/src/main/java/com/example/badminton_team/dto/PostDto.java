package com.example.badminton_team.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PostDto {
    private Integer pid;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private Integer sportType;
}