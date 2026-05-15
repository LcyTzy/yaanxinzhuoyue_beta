package com.zhantu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_favorite")
public class ProductFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long userId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
