package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("purchase_order_item")
public class PurchaseOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long purchaseOrderId;
    private Long productId;
    private String productName;
    private String productCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
