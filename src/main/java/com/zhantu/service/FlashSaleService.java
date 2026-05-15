package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.FlashSale;

import java.util.List;

public interface FlashSaleService extends IService<FlashSale> {

    List<FlashSale> getActiveFlashSales();

    boolean purchaseFlashSale(Long userId, Long flashSaleId);
}