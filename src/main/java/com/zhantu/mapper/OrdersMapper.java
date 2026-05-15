package com.zhantu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhantu.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
