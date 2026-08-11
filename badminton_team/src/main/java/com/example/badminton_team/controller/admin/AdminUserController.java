package com.example.badminton_team.controller.admin;

import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.User;
import com.example.badminton_team.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户列表（支持关键字搜索）
     */
    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<User> pageResult = userService.findPage(page, size, keyword);
        pageResult.getList().forEach(u -> u.setPassword(null));
        return Result.success(pageResult);
    }

    /**
     * 启用/禁用用户
     * @param uid 用户ID
     * @param status 状态：0-正常，1-禁用
     */
    @PutMapping("/status/{uid}")
    public Result<?> updateStatus(@PathVariable Integer uid, @RequestParam Integer status) {
        userService.updateUserStatus(uid, status);
        return Result.success("操作成功", null);
    }

    /**
     * 查看用户详情
     */
    @GetMapping("/{uid}")
    public Result<User> detail(@PathVariable Integer uid) {
        User user = userService.getUserInfo(uid);
        return Result.success(user);
    }
}