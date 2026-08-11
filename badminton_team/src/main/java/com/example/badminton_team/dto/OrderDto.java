package com.example.badminton_team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class OrderDto {
    @NotNull
    private Integer venueId;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookDate;
    @NotBlank
    private String timeSlot;
    private Integer hours;
    private String contactName;
    private String contactPhone;
    private Boolean needCoach;
    private Integer coachCount;
}