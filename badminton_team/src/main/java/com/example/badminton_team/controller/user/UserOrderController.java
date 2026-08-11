package com.example.badminton_team.controller.user;

import com.example.badminton_team.dto.OrderDto;
import com.example.badminton_team.dto.Result;
import com.example.badminton_team.dto.TimeSlotDto;
import com.example.badminton_team.entity.Order;
import com.example.badminton_team.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<?> create(@Valid @RequestBody OrderDto dto,
                            @RequestAttribute("uid") Integer uid) {
        orderService.create(dto, uid);
        return Result.success("预约成功", null);
    }

    @GetMapping("/my")
    public Result<List<Order>> myOrders(
            @RequestAttribute("uid") Integer uid,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.getByUser(uid, status));
    }

    @PutMapping("/cancel/{oid}")
    public Result<?> cancel(@PathVariable Integer oid, @RequestAttribute("uid") Integer uid) {
        orderService.cancel(oid, uid);
        return Result.success("已取消", null);
    }

    @GetMapping("/{oid}")
    public Result<Order> detail(@PathVariable Integer oid, @RequestAttribute("uid") Integer uid) {
        return Result.success(orderService.getDetail(oid, uid));
    }

    @GetMapping("/available-slots")
    public Result<List<TimeSlotDto>> getAvailableSlots(@RequestParam Integer venueId,
                                                       @RequestParam String date) {
        return Result.success(orderService.getAvailableSlots(venueId, date));
    }

    @PutMapping("/pay/{oid}")
    public Result<?> pay(@PathVariable Integer oid, @RequestAttribute("uid") Integer uid) {
        orderService.pay(oid, uid);
        return Result.success("支付成功", null);
    }

}
