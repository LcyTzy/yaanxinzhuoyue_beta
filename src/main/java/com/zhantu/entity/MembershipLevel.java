package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("membership_level")
public class MembershipLevel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer level;
    private Integer minPoints;
    private BigDecimal discount;
    private String icon;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}