package com.example.badminton_team.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class VenueDto {
    private Integer vid;
    @NotBlank(message = "场地编号不能为空")
    private String code;
    private String description;
    @NotNull(message = "每小时价格不能为空")
    private BigDecimal pricePerHour;
    private String photos;
    private String facilities;     // 新增
    private String detailIntro;    // 新增
    private String videos;         // 新增
    private String rules;          // 新增
}