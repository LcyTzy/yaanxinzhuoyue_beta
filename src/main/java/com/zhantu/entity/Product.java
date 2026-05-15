package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    private String subName;
    private String oem;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;
    private String series;
    private String qualityGrade;
    private String viscosity;
    private String spec;
    private String unit;
    private String brand;
    private String image;
    private String description;
    private String vinCompatible;
    private String oeNumber;
    private String groupCode;
    private Integer status;
    private Integer sales;
    @TableField(exist = false)
    private Integer favoriteCount;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
