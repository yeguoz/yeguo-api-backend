package com.yeguo.yeguoapi.service;

import com.yeguo.yeguoapi.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-05-08 18:58:22
*/
public interface UserService extends IService<User> {
   long  userRegister(String userAccount,String userPassword,String checkPassword);
}
