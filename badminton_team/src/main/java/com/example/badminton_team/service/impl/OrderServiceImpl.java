package com.example.badminton_team.service.impl;

import com.example.badminton_team.dao.OrderDao;
import com.example.badminton_team.dao.VenueDao;
import com.example.badminton_team.dto.OrderDto;
import com.example.badminton_team.dto.PageResult;
import com.example.badminton_team.dto.TimeSlotDto;
import com.example.badminton_team.entity.Order;
import com.example.badminton_team.entity.Venue;
import com.example.badminton_team.exception.BusinessException;
import com.example.badminton_team.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private VenueDao venueDao;

    @Override
    @Transactional
    public void create(OrderDto dto, Integer uid) {
        Venue venue = venueDao.findByVid(dto.getVenueId());
        if (venue == null) throw new BusinessException("场地不存在");

        int count = orderDao.countConflict(dto.getVenueId(), dto.getBookDate(), dto.getTimeSlot());
        if (count > 0) throw new BusinessException("该时间段已被预订");

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(uid);
        order.setVenueId(dto.getVenueId());
        order.setBookDate(dto.getBookDate());   // 直接赋值
        order.setTimeSlot(dto.getTimeSlot());
        order.setHours(dto.getHours());
        order.setTotalPrice(venue.getPricePerHour().multiply(BigDecimal.valueOf(dto.getHours())));
        order.setStatus(0);
        order.setCreateTime(new Date());
        order.setContactName(dto.getContactName());
        order.setContactPhone(dto.getContactPhone());
        order.setNeedCoach(dto.getNeedCoach() != null ? dto.getNeedCoach() : false);
        order.setCoachCount(dto.getCoachCount() != null ? dto.getCoachCount() : 0);
        orderDao.insert(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
    }

    @Override
    public List<Order> getByUser(Integer uid) {
        return orderDao.findByUserId(uid);
    }

    @Override
    @Transactional
    public void cancel(Integer oid, Integer uid) {
        Order order = orderDao.findByOid(oid);
        if (order == null || !order.getUserId().equals(uid)) {
            throw new BusinessException("订单不存在或无权限");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("当前订单状态不可取消");
        }
        orderDao.updateStatus(oid, 2); // 已取消
    }

    @Override
    public Order getDetail(Integer oid, Integer uid) {
        Order order = orderDao.findByOid(oid);
        if (order == null || !order.getUserId().equals(uid)) {
            throw new BusinessException("订单不存在或无权限");
        }
        return order;
    }

    @Override
    public List<Order> getByMerchant(Integer mid, Integer status) {
        List<Venue> venues = venueDao.findByOwnerId(mid);
        if (venues.isEmpty()) {
            return List.of();
        }
        List<Integer> venueIds = venues.stream().map(Venue::getVid).collect(Collectors.toList());
        List<Order> orders = orderDao.findByVenueIds(venueIds);
        if (status != null) {
            orders = orders.stream().filter(order -> order.getStatus().equals(status)).collect(Collectors.toList());
        }
        return orders;
    }

    @Override
    public PageResult<Order> findPageByMerchant(Integer mid, Integer status, Integer page, Integer size) {
        List<Venue> venues = venueDao.findByOwnerId(mid);
        if (venues.isEmpty()) {
            return new PageResult<>(List.of(), 0L);
        }
        List<Integer> venueIds = venues.stream().map(Venue::getVid).toList();
        int offset = (page - 1) * size;
        List<Order> list = orderDao.findPageByVenueIds(venueIds, status, offset, size);
        long total = orderDao.countByVenueIds(venueIds, status);
        return new PageResult<>(list, total);
    }

    @Override
    @Transactional
    public void confirm(Integer oid, Integer mid) {
        Order order = orderDao.findByOid(oid);
        if (order == null) throw new BusinessException("订单不存在");
        Venue venue = venueDao.findByVid(order.getVenueId());
        if (venue == null || !venue.getOwnerId().equals(mid)) {
            throw new BusinessException("无权限操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态异常");
        }
        orderDao.updateStatus(oid, 1); // 已确认/已支付
    }

    @Override
    @Transactional
    public void reject(Integer oid, Integer mid) {
        Order order = orderDao.findByOid(oid);
        if (order == null) throw new BusinessException("订单不存在");
        Venue venue = venueDao.findByVid(order.getVenueId());
        if (venue == null || !venue.getOwnerId().equals(mid)) {
            throw new BusinessException("无权限操作此订单");
        }
        orderDao.updateStatus(oid, 2); // 已取消（商家拒绝）
    }

    @Override
    public long countAll() {
        return orderDao.countAll();
    }

    @Override
    public List<TimeSlotDto> getAvailableSlots(Integer venueId, String date) {
        List<TimeSlotDto> allSlots = new ArrayList<>();
        String[] preset = {"08:00-10:00","10:00-12:00","12:00-14:00","14:00-16:00","16:00-18:00","18:00-20:00","20:00-22:00"};
        for (String slot : preset) {
            TimeSlotDto dto = new TimeSlotDto();
            dto.setStartTime(slot.split("-")[0]);
            dto.setEndTime(slot.split("-")[1]);
            dto.setAvailable(true);
            allSlots.add(dto);
        }

        // 关键：将字符串日期转换为 LocalDate
        LocalDate localDate = LocalDate.parse(date);
        List<Order> booked = orderDao.findBookedByVenueAndDate(venueId, localDate);
        if (booked != null && !booked.isEmpty()) {
            Set<String> bookedSet = booked.stream().map(Order::getTimeSlot).collect(Collectors.toSet());
            for (TimeSlotDto dto : allSlots) {
                String full = dto.getStartTime() + "-" + dto.getEndTime();
                if (bookedSet.contains(full)) {
                    dto.setAvailable(false);
                }
            }
        }
        return allSlots;
    }

    @Override
    @Transactional
    public void pay(Integer oid, Integer uid) {
        Order order = orderDao.findByOid(oid);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getUserId().equals(uid)) throw new BusinessException("无权限操作");
        if (order.getStatus() != 0) throw new BusinessException("当前订单状态不可支付");
        orderDao.updateStatus(oid, 1);
        // 可选：更新支付时间
        Order updateOrder = new Order();
        updateOrder.setOid(oid);
        updateOrder.setPayTime(new Date());
        orderDao.updatePayTime(updateOrder); // 需在 Dao 中添加方法
    }

    @Override
    @Transactional
    public void complete(Integer oid, Integer mid) {
        Order order = orderDao.findByOid(oid);
        if (order == null) throw new BusinessException("订单不存在");
        Venue venue = venueDao.findByVid(order.getVenueId());
        if (venue == null || !venue.getOwnerId().equals(mid)) {
            throw new BusinessException("无权限操作");
        }
        if (order.getStatus() != 1) throw new BusinessException("只有已支付的订单才能完成");
        orderDao.updateStatus(oid, 3);
    }

    @Override
    public List<Order> getByUser(Integer uid, Integer status) {
        return orderDao.selectByUser(uid, status);
    }
}