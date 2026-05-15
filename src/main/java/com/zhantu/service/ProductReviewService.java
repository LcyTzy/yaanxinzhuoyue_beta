package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.ProductReview;

public interface ProductReviewService extends IService<ProductReview> {
    void addReview(Long userId, Long productId, Long orderId, Integer rating, String content, String images);
    void followUpReview(Long userId, Long reviewId, String content);
    IPage<ProductReview> getReviewPage(Long productId, Integer pageNum, Integer pageSize);
    double getAverageRating(Long productId);
}
