package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_log")
public class PointsLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer points;
    private String type;
    private String description;
    private Long orderId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
