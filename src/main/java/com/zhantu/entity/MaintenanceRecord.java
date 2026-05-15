package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("maintenance_record")
public class MaintenanceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String vehicleVin;
    private String vehicleInfo;
    private String serviceType;
    private String description;
    private Integer mileage;
    private LocalDate serviceDate;
    private String storeName;
    private java.math.BigDecimal cost;
    private String images;
    private LocalDate nextRemindDate;
    private Integer reminded;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}