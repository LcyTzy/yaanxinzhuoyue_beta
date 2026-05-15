package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("inquiry")
public class Inquiry {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private String productName;
    private String oeNumber;
    private String vinCode;
    private Integer quantity;
    private String description;
    private BigDecimal quotedPrice;
    private Integer status;
    private String reply;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}