package com.zhantu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.PointsExchange;
import com.zhantu.mapper.PointsExchangeMapper;
import com.zhantu.service.PointsExchangeService;
import org.springframework.stereotype.Service;

@Service
public class PointsExchangeServiceImpl extends ServiceImpl<PointsExchangeMapper, PointsExchange> implements PointsExchangeService {
}