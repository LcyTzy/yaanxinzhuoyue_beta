package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status;
    private LocalDateTime useTime;
    private Long orderId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String couponName;
    @TableField(exist = false)
    private Integer couponType;
    @TableField(exist = false)
    private BigDecimal couponDiscountAmount;
    @TableField(exist = false)
    private BigDecimal couponDiscountRate;
    @TableField(exist = false)
    private BigDecimal couponMinAmount;
    @TableField(exist = false)
    private LocalDateTime couponStartTime;
    @TableField(exist = false)
    private LocalDateTime couponEndTime;
}
