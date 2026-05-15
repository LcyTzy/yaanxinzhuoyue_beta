package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.Supplier;
import com.zhantu.mapper.SupplierMapper;
import com.zhantu.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public IPage<Supplier> getSupplierPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Supplier::getName, keyword)
                    .or().like(Supplier::getContactPerson, keyword);
        }
        
        wrapper.eq(Supplier::getStatus, 1);
        wrapper.orderByDesc(Supplier::getCreateTime);
        
        return this.page(page, wrapper);
    }
}
