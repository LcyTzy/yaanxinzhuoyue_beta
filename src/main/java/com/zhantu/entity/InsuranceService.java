package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insurance_service")
public class InsuranceService {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String vehicleVin;
    private String vehicleInfo;
    private Integer serviceType;
    private String contactName;
    private String contactPhone;
    private BigDecimal quotedPrice;
    private Integer status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}