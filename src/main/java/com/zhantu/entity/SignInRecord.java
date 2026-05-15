package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sign_in_record")
public class SignInRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDate signDate;
    private Integer points;
    private Integer consecutiveDays;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}