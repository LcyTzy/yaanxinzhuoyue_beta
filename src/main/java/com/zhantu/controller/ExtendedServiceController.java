package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.*;
import com.zhantu.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "门店/保养/发票/询价/保险模块")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExtendedServiceController {

    private final StoreService storeService;
    private final MaintenanceRecordService maintenanceRecordService;
    private final InvoiceService invoiceService;
    private final InquiryService inquiryService;
    private final InsuranceServiceService insuranceServiceService;

    @GetMapping("/stores")
    @Operation(summary = "获取门店列表")
    public Result<List<Store>> getStores() {
        return Result.success(storeService.getActiveStores());
    }

    @GetMapping("/maintenance/records")
    @Operation(summary = "获取保养记录")
    public Result<List<MaintenanceRecord>> getMaintenanceRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(maintenanceRecordService.getUserRecords(userId));
    }

    @PostMapping("/maintenance/record")
    @Operation(summary = "添加保养记录")
    public Result<Void> addMaintenanceRecord(HttpServletRequest request,
                                              @RequestBody MaintenanceRecord record) {
        Long userId = (Long) request.getAttribute("userId");
        maintenanceRecordService.addRecord(userId, record);
        return Result.success();
    }

    @GetMapping("/invoice/list")
    @Operation(summary = "获取发票列表")
    public Result<List<Invoice>> getInvoices(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(invoiceService.getUserInvoices(userId));
    }

    @PostMapping("/invoice/apply")
    @Operation(summary = "申请发票")
    public Result<Void> applyInvoice(HttpServletRequest request,
                                      @RequestBody Invoice invoice) {
        Long userId = (Long) request.getAttribute("userId");
        invoiceService.applyInvoice(userId, invoice);
        return Result.success();
    }

    @GetMapping("/inquiry/list")
    @Operation(summary = "获取询价列表")
    public Result<List<Inquiry>> getInquiries(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(inquiryService.getUserInquiries(userId));
    }

    @PostMapping("/inquiry/submit")
    @Operation(summary = "提交询价")
    public Result<Void> submitInquiry(HttpServletRequest request,
                                       @RequestBody Inquiry inquiry) {
        Long userId = (Long) request.getAttribute("userId");
        inquiryService.submitInquiry(userId, inquiry);
        return Result.success();
    }

    @GetMapping("/insurance/list")
    @Operation(summary = "获取保险服务列表")
    public Result<List<InsuranceService>> getInsuranceServices(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(insuranceServiceService.getUserServices(userId));
    }

    @PostMapping("/insurance/apply")
    @Operation(summary = "申请保险/年检服务")
    public Result<Void> applyInsurance(HttpServletRequest request,
                                        @RequestBody InsuranceService service) {
        Long userId = (Long) request.getAttribute("userId");
        insuranceServiceService.applyService(userId, service);
        return Result.success();
    }
}