package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.UserDao;
import com.example.badminton_team.dto.*;
import com.example.badminton_team.entity.User;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.CodeService;
import com.example.badminton_team.service.UserService;
import com.example.badminton_team.util.JwtUtil;
import com.example.badminton_team.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginRespDto login(LoginDto loginDto) {
        User user = userDao.findByUsername(loginDto.getUsername());
        if (user == null || !PasswordUtil.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != 0) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getUid(), user.getUsername(), "user");
        user.setPassword(null);
        LoginRespDto resp = new LoginRespDto();
        resp.setToken(token);
        resp.setUserInfo(user);
        return resp;
    }

    @Autowired
    private CodeService codeService;
    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        // 1. 验证码校验
        if (!codeService.verifyCode(registerDto.getEmail(), registerDto.getCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 2. 检查用户名是否存在
        if (userDao.findByUsername(registerDto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 可选：检查邮箱是否已被注册
        if (userDao.findByEmail(registerDto.getEmail()) != null) {
             throw new BusinessException("邮箱已被注册");
         }

        // 4. 创建用户（原有代码）
        if (userDao.findByUsername(registerDto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(PasswordUtil.encode(registerDto.getPassword()));
        user.setNickname(registerDto.getNickname());
        user.setEmail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.insert(user);
    }

    @Override
    public User getUserInfo(Integer uid) {
        User user = userDao.findByUid(uid);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // 1. 检查用户是否存在
        User exist = userDao.findByUid(user.getUid());
        if (exist == null) {
            throw new BusinessException("用户不存在");
        }
        // 2. 只更新允许修改的字段（防止修改用户名、密码等）
        if (user.getNickname() != null) exist.setNickname(user.getNickname());
        if (user.getEmail() != null) exist.setEmail(user.getEmail());
        if (user.getPhone() != null) exist.setPhone(user.getPhone());
        if (user.getAvatar() != null) exist.setAvatar(user.getAvatar());
        if (user.getSkillLevel() != null) exist.setSkillLevel(user.getSkillLevel());
        if (user.getGender() != null) exist.setGender(user.getGender());
        if (user.getBirthday() != null) exist.setBirthday(user.getBirthday());
        if (user.getSignature() != null) exist.setSignature(user.getSignature());
        if (user.getProvince() != null) exist.setProvince(user.getProvince());
        if (user.getCity() != null) exist.setCity(user.getCity());
        if (user.getDistrict() != null) exist.setDistrict(user.getDistrict());
        exist.setUpdateTime(new Date());
        // 3. 执行更新
        userDao.update(exist);
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer uid, Integer status) {
        userDao.updateStatus(uid, status);
    }

    @Override
    public List<User> findAll(String keyword) {
        if (StringUtils.hasText(keyword)) {
            return userDao.search(keyword);
        } else {
            return userDao.findAll();
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        return userDao.findByEmail(email) != null;
    }

    @Override
    public PageResult<User> findPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<User> list = userDao.findPage(offset, size, keyword);
        long total = userDao.count(keyword);
        return new PageResult<>(list, total);
    }

    @Override
    public long countAll() {
        return userDao.countAll();
    }
}