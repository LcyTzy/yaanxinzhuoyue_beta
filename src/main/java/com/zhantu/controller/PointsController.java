package com.zhantu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhantu.common.Result;
import com.zhantu.entity.PointsLog;
import com.zhantu.service.PointsLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "积分模块")
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsLogService pointsLogService;

    @GetMapping("/log")
    @Operation(summary = "获取积分记录")
    public Result<IPage<PointsLog>> getPointsLog(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(pointsLogService.getUserPointsLog(userId, pageNum, pageSize));
    }
}
