package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.Banner;
import com.zhantu.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "轮播图模块")
@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/list")
    @Operation(summary = "获取轮播图列表")
    public Result<List<Banner>> getBanners() {
        return Result.success(bannerService.getActiveBanners());
    }
}
