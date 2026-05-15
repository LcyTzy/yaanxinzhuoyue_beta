package com.zhantu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhantu.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
