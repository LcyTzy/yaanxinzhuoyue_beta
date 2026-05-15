package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("vehicle_part_relation")
public class VehiclePartRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long vehicleModelId;
    private Long productId;
    private String position;
}
