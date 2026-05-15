package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String businessHours;
    private String image;
    private Double latitude;
    private Double longitude;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}