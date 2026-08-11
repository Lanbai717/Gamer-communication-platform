package com.example.badminton_team.controller.admin;

import com.example.badminton_team.dto.MerchantAuditDto;
import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取待审核商家列表（状态为0）
     */
    @GetMapping("/pending")
    public Result<List<Merchant>> pending() {
        List<Merchant> list = merchantService.findPending();  // 调用此方法
        list.forEach(m -> m.setPassword(null));
        return Result.success(list);
    }

    /**
     * 查询商家列表（分页、支持搜索和状态筛选）
     */
    @GetMapping("/list")
    public Result<PageResult<Merchant>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        PageResult<Merchant> pageResult = merchantService.findPage(page, size, keyword, status);
        pageResult.getList().forEach(m -> m.setPassword(null));
        return Result.success(pageResult);
    }

    /**
     * 审核商家（通过/拒绝）
     */
    @PutMapping("/audit")
    public Result<?> audit(@RequestBody MerchantAuditDto dto) {
        merchantService.audit(dto);
        return Result.success("审核完成", null);
    }

    /**
     * 更新商家状态（启用/禁用）
     */
    @PutMapping("/status/{mid}")
    public Result<?> updateStatus(@PathVariable Integer mid, @RequestParam Integer status) {
        merchantService.updateStatus(mid, status);
        return Result.success("操作成功", null);
    }
}