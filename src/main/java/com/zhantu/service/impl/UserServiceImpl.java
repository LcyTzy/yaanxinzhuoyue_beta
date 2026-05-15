package com.zhantu.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.entity.User;
import com.zhantu.mapper.UserMapper;
import com.zhantu.service.UserService;
import com.zhantu.service.SmsService;
import com.zhantu.util.JwtUtil;
import com.zhantu.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;

    @Override
    public String register(String nickname, String phone, String password) {
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        if (password == null || password.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (this.count(wrapper) > 0) {
            throw new BusinessException(400, "手机号已注册");
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setRole("USER");
        user.setPoints(0);
        user.setStatus(1);
        this.save(user);

        return jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
    }

    @Override
    public java.util.Map<String, Object> login(String phone, String password) {
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BusinessException(400, "手机号格式不正确");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = this.getOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(400, "密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setPhone(PhoneUtil.maskPhone(user.getPhone()));
        safeUser.setNickname(user.getNickname());
        safeUser.setAvatar(user.getAvatar());
        safeUser.setRole(user.getRole());
        safeUser.setCreateTime(user.getCreateTime());
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("token", token);
        result.put("user", safeUser);
        return result;
    }

    @Override
    public User getCurrentUser(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setPassword(null);
            if (user.getPhone() != null) {
                user.setPhone(PhoneUtil.maskPhone(user.getPhone()));
            }
        }
        return user;
    }

    @Override
    public String sendResetCode(String phone) {
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BusinessException(400, "手机号格式不正确");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        if (this.count(wrapper) == 0) {
            throw new BusinessException(400, "手机号未注册");
        }

        String limitKey = "sms:limit:" + phone;
        Long count = redisTemplate.opsForValue().increment(limitKey);
        if (count != null && count == 1) {
            redisTemplate.expire(limitKey, 60, TimeUnit.SECONDS);
        }
        if (count != null && count > 1) {
            throw new BusinessException(400, "请60秒后再发送验证码");
        }

        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set("resetCode:" + phone, code, 5, TimeUnit.MINUTES);
        smsService.sendSms(phone, code);
        return code;
    }

    @Override
    public void resetPassword(String phone, String code, String newPassword) {
        if (!PhoneUtil.isValidPhone(phone)) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new BusinessException(400, "密码长度不能少于6位");
        }
        String storedCode = redisTemplate.opsForValue().get("resetCode:" + phone);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException(400, "验证码错误或已过期");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
        redisTemplate.delete("resetCode:" + phone);
    }
}
