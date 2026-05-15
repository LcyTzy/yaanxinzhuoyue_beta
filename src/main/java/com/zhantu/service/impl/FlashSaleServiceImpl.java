package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.FlashSale;
import com.zhantu.entity.Product;
import com.zhantu.mapper.FlashSaleMapper;
import com.zhantu.service.FlashSaleService;
import com.zhantu.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl extends ServiceImpl<FlashSaleMapper, FlashSale> implements FlashSaleService {

    private final ProductService productService;

    @Override
    public List<FlashSale> getActiveFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<FlashSale> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FlashSale::getStatus, 1)
                .le(FlashSale::getStartTime, now)
                .ge(FlashSale::getEndTime, now)
                .gt(FlashSale::getStock, 0)
                .orderByAsc(FlashSale::getStartTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public boolean purchaseFlashSale(Long userId, Long flashSaleId) {
        FlashSale flashSale = getById(flashSaleId);
        if (flashSale == null || flashSale.getStatus() != 1) {
            throw new BusinessException(400, "秒杀活动不存在或已结束");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(flashSale.getStartTime())) {
            throw new BusinessException(400, "秒杀活动尚未开始");
        }
        if (now.isAfter(flashSale.getEndTime())) {
            throw new BusinessException(400, "秒杀活动已结束");
        }
        if (flashSale.getStock() <= 0) {
            throw new BusinessException(400, "秒杀商品已售罄");
        }

        Product product = productService.getById(flashSale.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(400, "商品不存在或已下架");
        }

        flashSale.setStock(flashSale.getStock() - 1);
        flashSale.setSoldCount((flashSale.getSoldCount() != null ? flashSale.getSoldCount() : 0) + 1);
        updateById(flashSale);

        return true;
    }
}