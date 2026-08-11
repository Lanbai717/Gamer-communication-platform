package com.example.badminton_team.service;

import com.example.badminton_team.dto.OrderDto;
import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.TimeSlotDto;
import com.example.badminton_team.entity.Order;
import java.util.List;

public interface OrderService {
    void create(OrderDto dto, Integer uid);
    List<Order> getByUser(Integer uid);
    void cancel(Integer oid, Integer uid);
    Order getDetail(Integer oid, Integer uid);
    List<Order> getByMerchant(Integer mid, Integer status);
    void confirm(Integer oid, Integer mid);
    void reject(Integer oid, Integer mid);
    /**
     * 分页获取商家的订单
     * @param mid 商家ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<Order> findPageByMerchant(Integer mid, Integer status, Integer page, Integer size);
    long countAll();
    List<TimeSlotDto> getAvailableSlots(Integer venueId, String date);
    void pay(Integer oid, Integer uid);
    void complete(Integer oid, Integer mid);
    /**
     * 查询用户订单列表，可按状态筛选
     * @param uid 用户ID
     * @param status 订单状态（可选，为 null 时不筛选）
     */
    List<Order> getByUser(Integer uid, Integer status);
}
