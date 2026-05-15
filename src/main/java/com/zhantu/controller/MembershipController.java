package com.zhantu.controller;

import com.zhantu.common.Result;
import com.zhantu.entity.MembershipLevel;
import com.zhantu.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "会员等级模块")
@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/levels")
    @Operation(summary = "获取所有会员等级")
    public Result<List<MembershipLevel>> getAllLevels() {
        return Result.success(membershipService.getAllLevels());
    }

    @GetMapping("/my-level")
    @Operation(summary = "获取当前用户会员等级")
    public Result<Map<String, Object>> getMyLevel(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MembershipLevel level = membershipService.getUserLevel(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("level", level);
        result.put("discount", membershipService.getDiscount(userId));
        return Result.success(result);
    }
}