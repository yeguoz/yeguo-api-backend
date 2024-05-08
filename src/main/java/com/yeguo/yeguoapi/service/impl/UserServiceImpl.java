package com.yeguo.yeguoapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeguo.yeguoapi.common.ErrorCode;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.entity.User;
import com.yeguo.yeguoapi.service.UserService;
import com.yeguo.yeguoapi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-05-08 18:58:22
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private static final String secretKey ="野果是帅比y";
    private static final byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
    // sm4加密
    private static final SymmetricCrypto sm4 = new SymmetricCrypto("SM4",key);

    @Autowired
    private UserMapper userMapper;

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求包含空数据");
        }
        // 账号不能包含特殊字符
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!userAccount.matches(regex)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 账号长度不小于4位
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }
        // 密码不小于8位
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        // 账号不能重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount,userAccount);
        Long count = userMapper.selectCount(lambdaQueryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已被使用");
        }
        // 使用hutool进行密码加密
        String encryptedPassword= sm4.encryptHex(userPassword);
        return 0;
    }
}




