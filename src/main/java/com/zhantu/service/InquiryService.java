package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Inquiry;

import java.util.List;

public interface InquiryService extends IService<Inquiry> {
    void submitInquiry(Long userId, Inquiry inquiry);
    List<Inquiry> getUserInquiries(Long userId);
    void replyInquiry(Long inquiryId, java.math.BigDecimal quotedPrice, String reply);
}