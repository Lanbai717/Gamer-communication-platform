package com.example.badminton_team.service;

import com.example.badminton_team.dto.VenueDto;
import com.example.badminton_team.entity.Venue;
import java.util.List;

public interface VenueService {
    void add(VenueDto dto, Integer mid);
    void update(VenueDto dto, Integer mid);
    void delete(Integer vid, Integer mid);
    List<Venue> getByMerchant(Integer mid);
    List<Venue> listForUser(String keyword, Double lng, Double lat);
    Venue getDetail(Integer vid);
    Venue getById(Integer vid);                     // 新增
}
