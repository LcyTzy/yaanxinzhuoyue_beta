package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Invoice;

import java.util.List;

public interface InvoiceService extends IService<Invoice> {
    void applyInvoice(Long userId, Invoice invoice);
    List<Invoice> getUserInvoices(Long userId);
    void processInvoice(Long invoiceId, String invoiceNo, String invoiceUrl);
}