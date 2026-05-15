package com.zhantu.dto;

import lombok.Data;

@Data
public class CartVO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productSubName;
    private String productImage;
    private java.math.BigDecimal price;
    private Integer quantity;
    private Integer checked;
}
