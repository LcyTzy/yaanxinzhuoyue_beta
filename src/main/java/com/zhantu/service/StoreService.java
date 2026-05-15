package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Store;

import java.util.List;

public interface StoreService extends IService<Store> {
    List<Store> getActiveStores();
}