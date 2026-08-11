package com.example.badminton_team.controller.admin;

import com.example.badminton_team.dto.LoginDto;
import com.example.badminton_team.dto.LoginRespDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Admin;
import com.example.badminton_team.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.badminton_team.service.UserService;
import com.example.badminton_team.service.MerchantService;
import com.example.badminton_team.service.PostService;
import com.example.badminton_team.service.OrderService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private PostService postService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/login")
    public Result<LoginRespDto> login(@Valid @RequestBody LoginDto loginDto) {
        LoginRespDto resp = adminService.login(loginDto);
        return Result.success("登录成功", resp);
    }

    @GetMapping("/info")
    public Result<Admin> info(@RequestAttribute("aid") Integer aid) {
        Admin admin = adminService.getAdminInfo(aid);
        return Result.success(admin);
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", userService.countAll());
        stats.put("merchantCount", merchantService.countAll());
        stats.put("postCount", postService.countAll());
        stats.put("orderCount", orderService.countAll());
        return Result.success(stats);
    }
}
