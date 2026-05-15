package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    List<Address> getUserAddresses(Long userId);
    void addAddress(Long userId, Address address);
    void updateAddress(Long userId, Address address);
    void deleteAddress(Long userId, Long id);
    void setDefault(Long userId, Long id);
}
