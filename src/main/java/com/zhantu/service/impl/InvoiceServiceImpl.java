package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.Invoice;
import com.zhantu.mapper.InvoiceMapper;
import com.zhantu.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements InvoiceService {

    @Override
    public void applyInvoice(Long userId, Invoice invoice) {
        invoice.setUserId(userId);
        invoice.setStatus(0);
        save(invoice);
    }

    @Override
    public List<Invoice> getUserInvoices(Long userId) {
        return list(new LambdaQueryWrapper<Invoice>()
                .eq(Invoice::getUserId, userId)
                .orderByDesc(Invoice::getCreateTime));
    }

    @Override
    public void processInvoice(Long invoiceId, String invoiceNo, String invoiceUrl) {
        Invoice invoice = getById(invoiceId);
        if (invoice == null) {
            throw new BusinessException(400, "发票申请不存在");
        }
        invoice.setStatus(1);
        invoice.setInvoiceNo(invoiceNo);
        invoice.setInvoiceUrl(invoiceUrl);
        updateById(invoice);
    }
}