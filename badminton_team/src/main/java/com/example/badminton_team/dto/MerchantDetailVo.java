package com.example.badminton_team.dto;

import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.entity.Venue;
import lombok.Data;
import java.util.List;

@Data
public class MerchantDetailVo {
    private Merchant merchant;
    private List<Venue> venues;
}