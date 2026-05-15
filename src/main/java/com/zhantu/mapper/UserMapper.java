package com.zhantu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhantu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
