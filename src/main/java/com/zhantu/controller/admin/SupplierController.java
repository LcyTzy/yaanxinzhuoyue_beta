package com.zhantu.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.Supplier;
import com.zhantu.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/supplier")
@RequiredArgsConstructor
@Tag(name = "供应商管理", description = "供应商管理接口")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/page")
    @Operation(summary = "分页查询供应商")
    public Result<IPage<Supplier>> getSupplierPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(supplierService.getSupplierPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有供应商列表")
    public Result<List<Supplier>> getAllSuppliers() {
        return Result.success(supplierService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取供应商详情")
    public Result<Supplier> getSupplierById(@PathVariable Long id) {
        return Result.success(supplierService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增供应商")
    public Result<Supplier> createSupplier(@RequestBody Supplier supplier) {
        supplier.setStatus(1);
        supplierService.save(supplier);
        return Result.success(supplier);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新供应商")
    public Result<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        supplierService.updateById(supplier);
        return Result.success(supplier);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商")
    public Result<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.removeById(id);
        return Result.success();
    }
}
