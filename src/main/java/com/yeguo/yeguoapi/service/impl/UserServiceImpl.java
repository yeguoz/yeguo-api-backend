package com.yeguo.yeguoapi.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeguo.yeguoapi.common.ResponseCode;
import com.yeguo.yeguoapi.constant.SecretConstant;
import com.yeguo.yeguoapi.constant.UserConstant;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.entity.User;
import com.yeguo.yeguoapi.model.vo.UserVO;
import com.yeguo.yeguoapi.service.UserService;
import com.yeguo.yeguoapi.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;



/**
* @author Lenovo
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-05-08 18:58:22
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    private static final byte[] passwordSecretKey = SecretConstant.PASSWORD_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    // sm4加密
    private static final SymmetricCrypto sm4 = new SymmetricCrypto("SM4",passwordSecretKey);

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
    public long userRegister(String username,String userAccount, String userPassword, String checkPassword) {
        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"请求包含空数据");
        }
        // 账号不能包含特殊字符
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!userAccount.matches(regex)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 账号长度不小于4位
        if (userAccount.length() < 4){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"账号长度过短");
        }
        // 密码不小于8位
        if (userPassword.length() < 8){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"密码长度过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"两次密码不一致");
        }
        // 账号不能重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount,userAccount);
        Long count = userMapper.selectCount(lambdaQueryWrapper);
        if (count > 0){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"账号已被使用");
        }
        // 使用hutool进行密码加密
        String encryptedPassword= sm4.encryptHex(userPassword);
        /*
        *  生成accessKey 和 secretKey
        * */
        // 获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        // 生成6位随机数
        SecureRandom random = new SecureRandom();
        int randomPart = random.nextInt(999999) + 1000000;
        // 拼接时间戳和随机数
        String accessKeyInput = String.valueOf(timestamp) + String.valueOf(randomPart);
        String secretKeyInput = accessKeyInput + SecretConstant.API_SECRET_KEY;  // 添加额外的字符串以增加复杂性
        String accessKey = "";
        String secretKey = "";
        // 使用SHA-256哈希函数生成密钥
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] accessKeyBytes = md.digest(accessKeyInput.getBytes());
            byte[] secretKeyBytes = md.digest(secretKeyInput.getBytes());
            // Base64编码密钥
            accessKey = Base64.getEncoder().encodeToString(accessKeyBytes);
            secretKey = Base64.getEncoder().encodeToString(secretKeyBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.SYSTEM_ERROR,"密钥生成失败");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        user.setAccessKey(accessKey);
        user.setSecretKey(secretKey);

        // 插入数据库
        int result = userMapper.insert(user);
        // 插入失败返回 -1
        if ( result < 1){
            throw new BusinessException(ResponseCode.SYSTEM_ERROR,"注册失败，请联系管理员");
        }
        // 插入成功 返回id
        return user.getId();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest req) {
        // 数据不能为空
        if (StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"请求包含空数据");
        }
        // 账号长度不小于4位
        if (userAccount.length() < 4){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"账号长度过短");
        }
        // 密码不小于8位
        if (userPassword.length() < 8){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"密码长度过短");
        }
        // 账号不能包含特殊字符
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!userAccount.matches(regex)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        // 查询该用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        // 查询错误
        if (user == null){
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR,"该用户不存在");
        }

        String password = user.getUserPassword();
        // 查询成功，对数据库用户密码解密和登录用户密码比较
        String decryptedPassword = sm4.decryptStr(password, CharsetUtil.CHARSET_UTF_8);

        if (!decryptedPassword.equals(userPassword)){
            throw new BusinessException(ResponseCode.PARAMS_ERROR,"密码错误");
        }

        // 返回脱敏对象
        UserVO userVO = getUserVO(user);
        // 设置session
        HttpSession session = req.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE,userVO);

        return userVO;
    }

    // 根据id查询用户
    @Override
    public User selectById(Long id) {
        User user = userMapper.selectById(id);
        if (user==null) throw new BusinessException(ResponseCode.NOT_FOUND_ERROR,"用户不存在");
        return user;
    }

    // 查询所有用户
    @Override
    public ArrayList<UserVO> selectAll() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询所有User
        ArrayList<User> users = (ArrayList<User>) userMapper.selectList(lambdaQueryWrapper);
        if (users == null) throw new BusinessException(ResponseCode.NOT_FOUND_ERROR,"查询为空,请检查代码");

        // 对每个用户脱敏 返回安全用户信息
        ArrayList<UserVO> result = new ArrayList<>();
        for (User user : users) {
            UserVO userVO = getUserVO(user);
            result.add(userVO);
        }
        return result;
    }

    // 按id删除
    @Override
    public int rmByid(Long id) {
        int result = userMapper.deleteById(id);
        return result;
    }

    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setUserAccount(user.getUserAccount());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setGender(user.getGender());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setGoldCoin(user.getGoldCoin());
        userVO.setAccessKey(user.getAccessKey());
        userVO.setSecretKey(user.getSecretKey());
        userVO.setUserStatus(user.getUserStatus());
        userVO.setUserRole(user.getUserRole());
        userVO.setCreateTime(user.getCreateTime());
        return userVO;
    }

}




