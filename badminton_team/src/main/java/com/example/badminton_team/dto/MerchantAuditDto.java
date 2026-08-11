package com.example.badminton_team.dto;

import lombok.Data;

@Data
public class MerchantAuditDto {
    private Integer mid;
    private Integer status; // 1-通过，2-拒绝
    private String reason;
}
