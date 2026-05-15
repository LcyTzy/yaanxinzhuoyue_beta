package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Address;
import com.zhantu.mapper.AddressMapper;
import com.zhantu.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getUserAddresses(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
    }

    @Override
    @Transactional
    public void addAddress(Long userId, Address address) {
        address.setUserId(userId);
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            this.update(new LambdaUpdateWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .set(Address::getIsDefault, 0));
        } else {
            address.setIsDefault(0);
        }
        this.save(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Address address) {
        Address existing = this.getById(address.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            this.update(new LambdaUpdateWrapper<Address>()
                    .eq(Address::getUserId, userId)
                    .set(Address::getIsDefault, 0));
        }
        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long id) {
        Address existing = this.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long id) {
        Address existing = this.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(400, "地址不存在");
        }
        this.update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .set(Address::getIsDefault, 0));
        existing.setIsDefault(1);
        this.updateById(existing);
    }
}
