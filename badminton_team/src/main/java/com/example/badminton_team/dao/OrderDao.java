package com.example.badminton_team.dao;

import com.example.badminton_team.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Mapper
public interface OrderDao {
    /**
     * 根据ID查询订单
     */
    Order findByOid(@Param("oid") Integer oid);

    /**
     * 根据订单号查询
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询用户的订单列表
     */
    List<Order> findByUserId(@Param("userId") Integer userId);

    /**
     * 插入订单
     */
    int insert(Order order);

    /**
     * 更新订单状态
     */
    int updateStatus(@Param("oid") Integer oid, @Param("status") Integer status);

    /**
     * 查询指定场地列表的所有订单
     */
    List<Order> findByVenueIds(@Param("venueIds") List<Integer> venueIds);

    /**
     * 分页查询商家的订单（根据场地ID列表）
     * @param venueIds 场地ID列表
     * @param status 订单状态（可选）
     * @param offset 偏移量
     * @param size 每页大小
     */
    List<Order> findPageByVenueIds(@Param("venueIds") List<Integer> venueIds,
                                   @Param("status") Integer status,
                                   @Param("offset") int offset,
                                   @Param("size") int size);

    /**
     * 统计商家的订单数量
     */
    long countByVenueIds(@Param("venueIds") List<Integer> venueIds,
                         @Param("status") Integer status);

    /**
     * 检查某个时间段是否已被预订（用于冲突检测）
     */
    int countConflict(@Param("venueId") Integer venueId,
                      @Param("bookDate") LocalDate bookDate,
                      @Param("timeSlot") String timeSlot);

    /**
     * 统计订单总数
     */
    long countAll();

    List<Order> findBookedByVenueAndDate(@Param("venueId") Integer venueId,
                                         @Param("date") LocalDate date);

    int updatePayTime(Order order);

    List<Order> selectByUser(@Param("uid") Integer uid, @Param("status") Integer status);}