package com.zhantu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhantu.common.Result;
import com.zhantu.entity.*;
import com.zhantu.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "管理员-扩展功能管理")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminExtendedController {

    private final StoreService storeService;
    private final InvoiceService invoiceService;
    private final InquiryService inquiryService;
    private final InsuranceServiceService insuranceServiceService;
    private final WarehouseService warehouseService;

    @GetMapping("/store/list")
    @Operation(summary = "门店列表")
    public Result<List<Store>> storeList() {
        return Result.success(storeService.list());
    }

    @PostMapping("/store")
    @Operation(summary = "添加门店")
    public Result<Void> addStore(@RequestBody Store store) {
        storeService.save(store);
        return Result.success();
    }

    @PutMapping("/store/{id}")
    @Operation(summary = "编辑门店")
    public Result<Void> updateStore(@PathVariable Long id, @RequestBody Store store) {
        store.setId(id);
        storeService.updateById(store);
        return Result.success();
    }

    @DeleteMapping("/store/{id}")
    @Operation(summary = "删除门店")
    public Result<Void> deleteStore(@PathVariable Long id) {
        storeService.removeById(id);
        return Result.success();
    }

    @GetMapping("/invoice/list")
    @Operation(summary = "发票列表")
    public Result<List<Invoice>> invoiceList() {
        return Result.success(invoiceService.list(
                new LambdaQueryWrapper<Invoice>().orderByDesc(Invoice::getCreateTime)));
    }

    @PutMapping("/invoice/{id}/process")
    @Operation(summary = "处理开票")
    public Result<Void> processInvoice(@PathVariable Long id, @RequestBody Map<String, String> params) {
        invoiceService.processInvoice(id, params.get("invoiceNo"), params.get("invoiceUrl"));
        return Result.success();
    }

    @GetMapping("/inquiry/list")
    @Operation(summary = "询价列表")
    public Result<List<Inquiry>> inquiryList() {
        return Result.success(inquiryService.list(
                new LambdaQueryWrapper<Inquiry>().orderByDesc(Inquiry::getCreateTime)));
    }

    @PutMapping("/inquiry/{id}/reply")
    @Operation(summary = "回复询价")
    public Result<Void> replyInquiry(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        java.math.BigDecimal price = new java.math.BigDecimal(params.get("quotedPrice").toString());
        String reply = (String) params.get("reply");
        inquiryService.replyInquiry(id, price, reply);
        return Result.success();
    }

    @GetMapping("/insurance/list")
    @Operation(summary = "保险服务列表")
    public Result<List<InsuranceService>> insuranceList() {
        return Result.success(insuranceServiceService.list(
                new LambdaQueryWrapper<InsuranceService>().orderByDesc(InsuranceService::getCreateTime)));
    }

    @PutMapping("/insurance/{id}/process")
    @Operation(summary = "处理保险服务")
    public Result<Void> processInsurance(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        InsuranceService service = insuranceServiceService.getById(id);
        if (service != null) {
            service.setStatus(1);
            if (params.containsKey("quotedPrice")) {
                service.setQuotedPrice(new java.math.BigDecimal(params.get("quotedPrice").toString()));
            }
            insuranceServiceService.updateById(service);
        }
        return Result.success();
    }

    @GetMapping("/warehouse/list")
    @Operation(summary = "仓库列表")
    public Result<List<Warehouse>> warehouseList() {
        return Result.success(warehouseService.list());
    }

    @PostMapping("/warehouse")
    @Operation(summary = "添加仓库")
    public Result<Void> addWarehouse(@RequestBody Warehouse warehouse) {
        warehouseService.save(warehouse);
        return Result.success();
    }

    @PutMapping("/warehouse/{id}")
    @Operation(summary = "编辑仓库")
    public Result<Void> updateWarehouse(@PathVariable Long id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        warehouseService.updateById(warehouse);
        return Result.success();
    }

    @DeleteMapping("/warehouse/{id}")
    @Operation(summary = "删除仓库")
    public Result<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.removeById(id);
        return Result.success();
    }
}