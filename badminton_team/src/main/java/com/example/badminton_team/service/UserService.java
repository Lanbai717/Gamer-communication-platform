package com.example.badminton_team.service;

import com.example.badminton_team.dto.LoginDto;
import com.example.badminton_team.dto.LoginRespDto;
import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.RegisterDto;
import com.example.badminton_team.entity.User;
import java.util.List;

public interface UserService {
    LoginRespDto login(LoginDto loginDto);
    void register(RegisterDto registerDto);
    User getUserInfo(Integer uid);
    void updateUser(User user);
    void updateUserStatus(Integer uid, Integer status);   // 管理员更新用户状态
    List<User> findAll(String keyword);                   // 查询所有用户（支持搜索）
    boolean isEmailExists(String email);
    PageResult<User> findPage(Integer page, Integer size, String keyword);
    long countAll();
}
