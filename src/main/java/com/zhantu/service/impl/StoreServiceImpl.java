package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.Store;
import com.zhantu.mapper.StoreMapper;
import com.zhantu.service.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    @Override
    public List<Store> getActiveStores() {
        return list(new LambdaQueryWrapper<Store>().eq(Store::getStatus, 1));
    }
}