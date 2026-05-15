package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("vehicle_model")
public class VehicleModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long seriesId;
    private String name;
    private String year;
    private String displacement;
    private String engine;
    private String transmission;
    private String vinPrefix;
    private Integer sort;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
