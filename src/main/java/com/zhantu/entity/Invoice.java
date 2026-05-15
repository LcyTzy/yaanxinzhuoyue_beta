package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("invoice")
public class Invoice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long orderId;
    private String orderNo;
    private Integer type;
    private String title;
    private String taxNo;
    private BigDecimal amount;
    private String email;
    private Integer status;
    private String invoiceNo;
    private String invoiceUrl;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}