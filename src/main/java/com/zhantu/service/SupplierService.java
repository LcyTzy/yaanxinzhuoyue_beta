package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Supplier;

public interface SupplierService extends IService<Supplier> {
    IPage<Supplier> getSupplierPage(String keyword, Integer pageNum, Integer pageSize);
}
