package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Inquiry;
import com.zhantu.mapper.InquiryMapper;
import com.zhantu.service.InquiryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InquiryServiceImpl extends ServiceImpl<InquiryMapper, Inquiry> implements InquiryService {

    @Override
    public void submitInquiry(Long userId, Inquiry inquiry) {
        inquiry.setUserId(userId);
        inquiry.setStatus(0);
        save(inquiry);
    }

    @Override
    public List<Inquiry> getUserInquiries(Long userId) {
        return list(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getUserId, userId)
                .orderByDesc(Inquiry::getCreateTime));
    }

    @Override
    public void replyInquiry(Long inquiryId, BigDecimal quotedPrice, String reply) {
        Inquiry inquiry = getById(inquiryId);
        if (inquiry == null) {
            throw new BusinessException(400, "询价记录不存在");
        }
        inquiry.setStatus(1);
        inquiry.setQuotedPrice(quotedPrice);
        inquiry.setReply(reply);
        updateById(inquiry);
    }
}