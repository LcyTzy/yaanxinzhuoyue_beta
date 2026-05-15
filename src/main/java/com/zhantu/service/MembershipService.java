package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.MembershipLevel;

import java.math.BigDecimal;
import java.util.List;

public interface MembershipService extends IService<MembershipLevel> {

    List<MembershipLevel> getAllLevels();

    MembershipLevel getLevelByPoints(Integer points);

    MembershipLevel getUserLevel(Long userId);

    BigDecimal getDiscount(Long userId);

    void upgradeUserLevel(Long userId);
}