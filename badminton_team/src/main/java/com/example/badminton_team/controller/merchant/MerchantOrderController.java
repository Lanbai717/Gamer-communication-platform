package com.example.badminton_team.controller.merchant;

import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.entity.Merchant;
import com.example.badminton_team.entity.Order;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.MerchantService;
import com.example.badminton_team.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/merchant/order")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantService merchantService;   // 新增


    @GetMapping("/list")
    public Result<PageResult<Order>> list(@RequestAttribute("mid") Integer mid,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        //merchantService.checkMerchantStatus(mid);     // 权限检查
        PageResult<Order> pageResult = orderService.findPageByMerchant(mid, status, page, size);
        return Result.success(pageResult);
    }

    @PutMapping("/confirm/{oid}")
    public Result<?> confirm(@PathVariable Integer oid,
                             @RequestAttribute("mid") Integer mid) {
        merchantService.checkMerchantStatus(mid);     // 权限检查
        orderService.confirm(oid, mid);
        return Result.success("已确认", null);
    }

    @PutMapping("/reject/{oid}")
    public Result<?> reject(@PathVariable Integer oid,
                            @RequestAttribute("mid") Integer mid) {
        merchantService.checkMerchantStatus(mid);     // 权限检查
        orderService.reject(oid, mid);
        return Result.success("已拒绝", null);
    }

    @PutMapping("/complete/{oid}")
    public Result<?> complete(@PathVariable Integer oid, @RequestAttribute("mid") Integer mid) {
        orderService.complete(oid, mid);
        return Result.success("订单已完成", null);
    }
}