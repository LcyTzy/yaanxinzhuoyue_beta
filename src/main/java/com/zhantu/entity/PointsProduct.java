package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_product")
public class PointsProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String image;
    private Integer points;
    private Integer stock;
    private Integer status;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}