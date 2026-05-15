package com.zhantu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhantu.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
