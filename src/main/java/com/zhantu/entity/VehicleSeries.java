package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("vehicle_series")
public class VehicleSeries {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long brandId;
    private String name;
    private Integer sort;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(exist = false)
    private List<VehicleModel> models;
}
