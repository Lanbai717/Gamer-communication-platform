package com.example.badminton_team.service;

import com.example.badminton_team.dto.LoginDto;
import com.example.badminton_team.dto.LoginRespDto;
import com.example.badminton_team.entity.Admin;

public interface AdminService {
    LoginRespDto login(LoginDto loginDto);
    Admin getAdminInfo(Integer aid);
}
