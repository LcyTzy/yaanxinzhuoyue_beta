package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.Address;
import com.zhantu.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "地址管理模块")
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/list")
    @Operation(summary = "获取地址列表")
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(addressService.getUserAddresses(userId));
    }

    @PostMapping("/add")
    @Operation(summary = "添加地址")
    public Result<Void> addAddress(HttpServletRequest request,
                                    @RequestBody Address address) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.addAddress(userId, address);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "更新地址")
    public Result<Void> updateAddress(HttpServletRequest request,
                                       @RequestBody Address address) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.updateAddress(userId, address);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址")
    public Result<Void> deleteAddress(HttpServletRequest request,
                                       @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.deleteAddress(userId, id);
        return Result.success();
    }

    @PutMapping("/default/{id}")
    @Operation(summary = "设置默认地址")
    public Result<Void> setDefault(HttpServletRequest request,
                                    @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        addressService.setDefault(userId, id);
        return Result.success();
    }
}
