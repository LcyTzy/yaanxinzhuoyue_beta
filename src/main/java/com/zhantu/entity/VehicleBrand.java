package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("vehicle_brand")
public class VehicleBrand {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String initial;
    private Integer sort;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(exist = false)
    private List<VehicleSeries> seriesList;
}
