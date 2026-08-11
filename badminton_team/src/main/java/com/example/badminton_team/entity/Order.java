package com.example.badminton_team.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class Order {
    private Integer oid;            // 订单ID
    private String orderNo;         // 订单编号（唯一）
    private Integer userId;         // 下单用户ID
    private Integer venueId;        // 预订场地ID
    private LocalDate bookDate;          // 预订日期
    private String timeSlot;        // 时间段（如 "14:00-16:00"）
    private Integer hours;          // 预订小时数
    private BigDecimal totalPrice;  // 总价
    private Integer status;         // 状态：0-待支付，1-已支付，2-已取消，3-已完成
    private Date createTime;
    private Date payTime;           // 支付时间
    private String contactName;     // 联系人
    private String contactPhone;    // 联系电话
    private String venueName;
    private Boolean needCoach;     // 是否需要陪练
    private Integer coachCount;    // 陪练人数
}
