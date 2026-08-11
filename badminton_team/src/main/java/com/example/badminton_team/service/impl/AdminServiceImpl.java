package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.AdminDao;
import com.example.badminton_team.dto.LoginDto;
import com.example.badminton_team.dto.LoginRespDto;
import com.example.badminton_team.entity.Admin;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.AdminService;
import com.example.badminton_team.util.JwtUtil;
import com.example.badminton_team.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginRespDto login(LoginDto loginDto) {
        Admin admin = adminDao.findByUsername(loginDto.getUsername());
        if (admin == null || !PasswordUtil.matches(loginDto.getPassword(), admin.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() != 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(admin.getAid(), admin.getUsername(), "admin");
        admin.setPassword(null);
        LoginRespDto resp = new LoginRespDto();
        resp.setToken(token);
        resp.setUserInfo(admin);
        return resp;
    }

    @Override
    public Admin getAdminInfo(Integer aid) {
        Admin admin = adminDao.findByAid(aid);
        if (admin == null) throw new BusinessException("管理员不存在");
        admin.setPassword(null);
        return admin;
    }
}
