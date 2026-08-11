package com.example.badminton_team.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Venue {
    private Integer vid;            // 场地ID
    private String code;            //场地编号
    private String description;      // 场地描述
    private String photos;           // 照片URL（多张用逗号分隔）
    private BigDecimal pricePerHour; // 每小时价格
    private Integer ownerId;         // 所属商家用户ID（对应user表的uid）
    private Integer status;          // 状态：0-正常，1-暂停，2-下架
    private Date createTime;
    private Date updateTime;
}
