package com.example.badminton_team.controller.merchant;

import com.example.badminton_team.dto.*;
import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.entity.Venue;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.MerchantService;
import com.example.badminton_team.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private VenueService venueService;

    @PostMapping("/login")
    public Result<LoginRespDto> login(@Valid @RequestBody LoginDto loginDto) {
        LoginRespDto resp = merchantService.login(loginDto);
        return Result.success("登录成功", resp);
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody MerchantRegisterDto registerDto) {
        merchantService.register(registerDto);
        return Result.success("注册成功，请等待管理员审核", null);
    }

    @GetMapping("/info")
    public Result<Merchant> info(@RequestAttribute("mid") Integer mid) {
        Merchant merchant = merchantService.getMerchantInfo(mid);
        return Result.success(merchant);
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Merchant merchant, @RequestAttribute("mid") Integer mid) {
        merchant.setMid(mid);
        merchantService.updateMerchant(merchant);
        return Result.success("更新成功", null);
    }


    @GetMapping("/user/list")
    public Result<List<Merchant>> listForUser() {
        List<Merchant> list = merchantService.findByStatus(1); // 只返回审核通过的商家
        list.forEach(m -> m.setPassword(null)); // 清除密码字段
        return Result.success(list);
    }

    @GetMapping("/user/detail/{mid}")
    public Result<MerchantDetailVo> detailForUser(@PathVariable Integer mid) {
        Merchant merchant = merchantService.getMerchantInfo(mid);
        if (merchant == null || merchant.getStatus() != 1) {
            throw new BusinessException("商家不存在");
        }
        List<Venue> venues = venueService.getByMerchant(mid);
        MerchantDetailVo vo = new MerchantDetailVo();
        vo.setMerchant(merchant);
        vo.setVenues(venues);
        return Result.success(vo);
    }
}