package com.yeguo.yeguoapi.service;

import com.yeguo.yeguoapi.model.dto.user.UserUpdateRequest;
import com.yeguo.yeguoapi.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yeguo.yeguoapi.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-05-08 18:58:22
*/
public interface UserService extends IService<User> {
   long  userRegister(String username,String userAccount,String userPassword,String checkPassword);
   UserVO userLogin(String userAccount, String userPassword, HttpServletRequest req);
   UserVO getUserVO(User user);
   User selectById(Long id);
   ArrayList<UserVO> selectAll();
   int rmByid(Long id);

   int upById(UserUpdateRequest userUpdateRequest);
}
