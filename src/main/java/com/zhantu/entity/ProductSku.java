package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_sku")
public class ProductSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String skuCode;
    private String specName;
    private String specValue;
    private BigDecimal price;
    private Integer stock;
    private String image;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}