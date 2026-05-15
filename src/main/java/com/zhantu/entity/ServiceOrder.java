package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("service_order")
public class ServiceOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private String userPhone;
    private String serviceType;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleYear;
    private String licensePlate;
    private LocalDateTime appointmentTime;
    private String status;
    private String remark;
    private Long relatedProductId;
    private Long relatedOrderId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
