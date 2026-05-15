package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.User;

public interface UserService extends IService<User> {
    String register(String nickname, String phone, String password);
    java.util.Map<String, Object> login(String phone, String password);
    User getCurrentUser(Long userId);
    String sendResetCode(String phone);
    void resetPassword(String phone, String code, String newPassword);
}
