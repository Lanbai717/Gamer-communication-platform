package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.LoginDto;
import com.example.badminton_team.dto.LoginRespDto;
import com.example.badminton_team.dto.RegisterDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.dto.UserUpdateDto;   // 新增导入
import com.example.badminton_team.entity.User;
import com.example.badminton_team.service.CodeService;
import com.example.badminton_team.service.UserService;
import com.example.badminton_team.util.EmailUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginRespDto> login(@Valid @RequestBody LoginDto loginDto) {
        LoginRespDto resp = userService.login(loginDto);
        return Result.success("登录成功", resp);
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return Result.success("注册成功", null);
    }

    @GetMapping("/info")
    public Result<User> info(@RequestAttribute("uid") Integer uid) {
        User user = userService.getUserInfo(uid);
        return Result.success(user);
    }

    // 新增：修改当前登录用户信息
    @PutMapping("/update")
    public Result<?> updateUser(@Valid @RequestBody UserUpdateDto updateDto,
                            @RequestAttribute("uid") Integer uid) {
        // 将 DTO 转换为 User 实体，只设置允许修改的字段
        User user = new User();
        user.setUid(uid);
        user.setNickname(updateDto.getNickname());
        user.setEmail(updateDto.getEmail());
        user.setPhone(updateDto.getPhone());
        user.setAvatar(updateDto.getAvatar());
        user.setSkillLevel(updateDto.getSkillLevel());
        user.setGender(updateDto.getGender());
        user.setBirthday(updateDto.getBirthday());
        user.setSignature(updateDto.getSignature());
        user.setProvince(updateDto.getProvince());
        user.setCity(updateDto.getCity());
        user.setDistrict(updateDto.getDistrict());
        userService.updateUser(user);
        return Result.success("修改成功", null);
    }

    @Autowired
    private CodeService codeService;
    @Autowired
    private EmailUtil emailUtil;

    @PostMapping("/sendCode")
    public Result<?> sendCode(@RequestParam String email) {
        //检查邮箱是否已被注册（如果有该功能）
        if (userService.isEmailExists(email)) {
             return Result.error("邮箱已被注册");
         }

        String code = codeService.generateCode();
        codeService.saveCode(email, code);
        try {
            emailUtil.sendVerificationCode(email, code);
            return Result.success("验证码已发送", null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邮件发送失败，请稍后重试");
        }
    }
}