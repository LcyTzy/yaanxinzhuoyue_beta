package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("flash_sale")
public class FlashSale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal flashPrice;
    private Integer stock;
    private Integer soldCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}