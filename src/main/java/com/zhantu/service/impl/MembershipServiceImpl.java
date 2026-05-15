package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.MembershipLevel;
import com.zhantu.entity.User;
import com.zhantu.mapper.MembershipLevelMapper;
import com.zhantu.service.MembershipService;
import com.zhantu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl extends ServiceImpl<MembershipLevelMapper, MembershipLevel> implements MembershipService {

    private final UserService userService;

    @Override
    public List<MembershipLevel> getAllLevels() {
        LambdaQueryWrapper<MembershipLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MembershipLevel::getLevel);
        return list(wrapper);
    }

    @Override
    public MembershipLevel getLevelByPoints(Integer points) {
        LambdaQueryWrapper<MembershipLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(MembershipLevel::getMinPoints, points)
                .orderByDesc(MembershipLevel::getLevel)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public MembershipLevel getUserLevel(Long userId) {
        User user = userService.getById(userId);
        if (user == null || user.getMembershipLevel() == null) {
            return getLevelByPoints(0);
        }
        return getById(user.getMembershipLevel());
    }

    @Override
    public BigDecimal getDiscount(Long userId) {
        MembershipLevel level = getUserLevel(userId);
        if (level == null || level.getDiscount() == null) {
            return BigDecimal.ONE;
        }
        return level.getDiscount();
    }

    @Override
    public void upgradeUserLevel(Long userId) {
        User user = userService.getById(userId);
        if (user == null) return;

        MembershipLevel currentLevel = getLevelByPoints(user.getPoints() != null ? user.getPoints() : 0);
        if (currentLevel != null) {
            if (user.getMembershipLevel() == null || !user.getMembershipLevel().equals(currentLevel.getId())) {
                user.setMembershipLevel(currentLevel.getId().intValue());
                userService.updateById(user);
            }
        }
    }
}