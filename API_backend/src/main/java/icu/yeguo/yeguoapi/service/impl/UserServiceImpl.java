package icu.yeguo.yeguoapi.service.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.constant.SecretConstant;
import icu.yeguo.yeguoapi.constant.UserConstant;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.user.UserPersonUpdateParams;
import icu.yeguo.yeguoapi.model.dto.user.UserQueryRequest;
import icu.yeguo.yeguoapi.model.dto.user.UserUpdateRequest;
import icu.yeguo.yeguoapi.model.vo.ASKeyVO;
import icu.yeguo.yeguoapi.model.vo.UserVO;
import icu.yeguo.yeguoapi.service.UserService;
import icu.yeguo.yeguoapi.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author yeguo
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-05-08 18:58:22
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    private static final byte[] passwordSecretKey = SecretConstant.PASSWORD_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    // sm4加密
    private static final SymmetricCrypto sm4 = new SymmetricCrypto("SM4", passwordSecretKey);

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return long
     */
    @Override
    public long userRegister(String username, String userAccount, String userPassword, String checkPassword) {
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求包含空数据");
        }
        // 账号不能包含特殊字符
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!userAccount.matches(regex)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 账号长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号长度过短");
        }
        // 密码不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码长度过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 账号不能重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        Long count;

        try {
            count = userMapper.selectCount(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (count > 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号已被使用");
        }
        // 使用hutool进行密码加密
        String encryptedPassword = sm4.encryptHex(userPassword);

        User user = getASKeyUser();
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);

        // 插入数据库
        int result;
        try {
            result = userMapper.insert(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 插入失败返回 -1
        if (result < 1) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "注册失败，请联系管理员");
        }
        // 插入成功 返回id
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param req
     * @return UserVO
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest req) {
        // 数据不能为空
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求包含空数据");
        }
        // 账号长度不小于4位
        if (userAccount.length() < 4) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号长度过短");
        }
        // 密码不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码长度过短");
        }
        // 账号不能包含特殊字符
        String regex = "^[a-zA-Z0-9_-]{4,16}$";
        if (!userAccount.matches(regex)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号包含特殊字符");
        }

        // 查询该用户
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        // 查询错误
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "该用户不存在");
        }

        String password = user.getUserPassword();
        // 查询成功，对数据库用户密码解密和登录用户密码比较
        String decryptedPassword = sm4.decryptStr(password, CharsetUtil.CHARSET_UTF_8);

        if (!decryptedPassword.equals(userPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码错误");
        }

        // 返回脱敏对象
        UserVO userVO = getUserVO(user);
        // 设置session
        HttpSession session = req.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, userVO);

        return userVO;
    }

    /**
     * 按id查询用户
     *
     * @param id
     * @return User
     */
    @Override
    public User selectById(Long id) {
        User user;
        try {
            user = userMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null)
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "用户不存在");
        return user;
    }

    /**
     * 查询所有用户
     *
     * @return ArrayList<UserVO>
     */
    @Override
    public ArrayList<UserVO> selectAll() {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询所有User
        ArrayList<User> userList;
        try {
            userList = (ArrayList<User>) userMapper.selectList(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (userList == null)
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "查询为空,请检查代码");

        // 对每个用户脱敏 返回安全用户信息
        ArrayList<UserVO> result = new ArrayList<>();
        for (User user : userList) {
            UserVO userVO = getUserVO(user);
            result.add(userVO);
        }
        return result;
    }

    /**
     * 按id删除用户
     *
     * @param id
     * @return int
     */
    @Override
    public int rmByid(Long id) {
        int result;
        try {
            result = userMapper.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (result < 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "删除失败,请检查代码");
        return result;
    }

    /**
     * 按id修改用户信息
     *
     * @param userUpdateRequest
     * @return int
     */
    @Override
    public int upById(UserUpdateRequest userUpdateRequest) {
        User user = new User();
        user.setId(userUpdateRequest.getId());
        user.setUsername(userUpdateRequest.getUsername());
        user.setUserAccount(userUpdateRequest.getUserAccount());
        user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        user.setGender(userUpdateRequest.getGender());
        user.setPhone(userUpdateRequest.getPhone());
        user.setEmail(userUpdateRequest.getEmail());
        user.setGoldCoin(userUpdateRequest.getGoldCoin());
        user.setUserStatus(userUpdateRequest.getUserStatus());
        user.setUserRole(userUpdateRequest.getUserRole());
        int result;
        try {
            result = userMapper.updateById(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (result < 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "更新失败,请检查代码");
        return result;
    }

    /**
     * 获取当前用户信息
     *
     * @param req
     * @return UserVO
     */
    @Override
    public UserVO getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserVO currentUser = (UserVO) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR, "您当前未登录");
        }
        // 根据session中用户id查询数据库
        User user;
        try {
            user = selectById(currentUser.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 对用户数据脱敏
        return getUserVO(user);
    }

    /**
     * 按查询参数动态查询
     *
     * @param userQueryRequest
     * @return ArrayList<UserVO>
     */
    @Override
    public ArrayList<UserVO> dynamicQuery(UserQueryRequest userQueryRequest) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(userQueryRequest.getId() != null, User::getId, userQueryRequest.getId())
                .eq(userQueryRequest.getUsername() != null, User::getUsername, userQueryRequest.getUsername())
                .eq(userQueryRequest.getUserAccount() != null, User::getUserAccount, userQueryRequest.getUserAccount())
                .eq(userQueryRequest.getGender() != null, User::getGender, userQueryRequest.getGender())
                .eq(userQueryRequest.getPhone() != null, User::getPhone, userQueryRequest.getPhone())
                .eq(userQueryRequest.getEmail() != null, User::getEmail, userQueryRequest.getEmail())
                .eq(userQueryRequest.getGoldCoin() != null, User::getGoldCoin, userQueryRequest.getGoldCoin())
                .eq(userQueryRequest.getUserStatus() != null, User::getUserStatus, userQueryRequest.getUserStatus())
                .eq(userQueryRequest.getUserRole() != null, User::getUserRole, userQueryRequest.getUserRole());
        ArrayList<User> userList;
        ArrayList<UserVO> result = new ArrayList<>();
        try {
            userList = (ArrayList<User>) userMapper.selectList(lambdaQueryWrapper);
            // 对每个用户脱敏 返回安全用户信息
            for (User user : userList) {
                UserVO userVO = getUserVO(user);
                result.add(userVO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public long userEmailRegister(String email, String verifyCode, HttpServletRequest req) {
        HttpSession session = req.getSession();
        // 校验邮箱
        checkMailbox(email, session);
        // 查询数据库，该邮箱未被使用
        // 邮箱不能重复
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail, email);
        Long count;

        try {
            count = userMapper.selectCount(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 邮箱被使用 将验证码和过期时间删除
        if (count > 0) {
            session.removeAttribute(UserConstant.VERIFY_CODE);
            session.removeAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME);
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱已被使用");
        }
        // 校验验证码
        checkVerificationCode(verifyCode, session);
        // 生成accessKey和secretKey
        User user = getASKeyUser();
        user.setEmail(email);

        // 验证成功,向数据库插入数据
        // 插入数据库
        int result;
        try {
            result = userMapper.insert(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 插入失败返回 -1
        if (result < 1) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "注册失败，请联系管理员");
        }
        // 插入成功，删除验证码和过期时间 返回id
        session.removeAttribute(UserConstant.VERIFY_CODE);
        session.removeAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME);
        return user.getId();
    }

    @Override
    public UserVO userEmailLogin(String email, String verifyCode, HttpServletRequest req) {
        HttpSession session = req.getSession();
        // 校验邮箱格式
        checkMailbox(email, session);
        // 邮箱是否存在数据库
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        // 查询错误
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "该用户不存在");
        }
        // 校验验证码
        checkVerificationCode(verifyCode, session);
        // 校验成功，返回UserVO对象
        UserVO userVO = getUserVO(user);
        // 设置登录态
        session.setAttribute(UserConstant.USER_LOGIN_STATE, userVO);
        return userVO;
    }

    @Override
    public int upPersonInfo(UserPersonUpdateParams userPersonUpdateParams) {
        Long id = userPersonUpdateParams.getId();
        String username = userPersonUpdateParams.getUsername();
        String email = userPersonUpdateParams.getEmail();
        String phone = userPersonUpdateParams.getPhone();
        String avatarUrl = userPersonUpdateParams.getAvatarUrl();

        // 邮箱规则
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if (email != null && !email.isEmpty() && !email.matches(regex)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱格式错误");
        }

        // 手机规则
        String regexPhone = "^1[3-9]\\d{9}$";
        if (phone != null && !phone.isEmpty() && !phone.matches(regexPhone)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "手机格式错误");
        }

        // 邮箱不能重复
        User user = null;
        if (email != null) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getEmail, email);
            Long count;
            try {
                count = userMapper.selectCount(lambdaQueryWrapper);
                user = selectById(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 不填邮箱默认是传递空字符串
            if (count > 0 && !email.equals(user.getEmail())) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱已被使用");
            }
        }
        // 更新用户信息
        int result = 0;
        if (user != null) {
            user.setUsername(username);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAvatarUrl(avatarUrl);
            try {
                result = userMapper.updateById(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (result < 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "更新失败");
        return 1;
    }

    @Override
    public ASKeyVO upASKey(Long id) {
        Map<String, String> keys = generateAccessAndSecretKeys();
        // 取到id 查询然后更新
        User user;
        int i;
        try {
            user = userMapper.selectById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null)
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "用户不存在");
        user.setAccessKey(keys.get("accessKey"));
        user.setSecretKey(keys.get("secretKey"));
        try {
            i = userMapper.updateById(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (i < 0)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "更新失败");
        return new ASKeyVO(keys.get("accessKey"), keys.get("secretKey"));
    }

    @Override
    public Integer recharge(Long userId, Long goldCoin) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null)
                throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "用户不存在");
            user.setGoldCoin(user.getGoldCoin() + goldCoin);
            int i = userMapper.updateById(user);
            if (i < 0)
                throw new BusinessException(ResponseCode.SYSTEM_ERROR, "失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Transactional
    @Override
    public Integer forgetPassword(String email, String newPassword, String checkNewPassword, String verifyCode,
                                  HttpServletRequest req) {
        // 检查密码和验证密码符合规则 长度
        // 数据不能为空
        if (StrUtil.hasBlank(newPassword, checkNewPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求包含空数据");
        }
        // 密码不小于8位
        if (newPassword.length() < 8) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码长度过短");
        }
        if (!newPassword.equals(checkNewPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "两次密码不一致");
        }
        //  验证验证码是否有效
        checkVerificationCode(verifyCode, req.getSession());
        // 有效正确则更新数据库
        try {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getEmail, email);
            User user = userMapper.selectOne(lambdaQueryWrapper);
            if (user == null) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户不存在");
            }
            // 用户存在密码加密 设置新密码
            String encryptedPassword = sm4.encryptHex(newPassword);
            if (user.getUserPassword().equals(encryptedPassword)) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "新密码不能与旧密码相同");
            }
            user.setUserPassword(encryptedPassword);
            int i = userMapper.updateById(user);
            if (i < 0) {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR, "密码修改失败");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public void checkMailbox(String email, HttpSession session) {
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            session.removeAttribute(UserConstant.VERIFY_CODE);
            session.removeAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME);
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱格式错误！");
        }
    }

    public void checkVerificationCode(String verifyCode, HttpSession session) {
        String serverVerifyCode = (String) session.getAttribute(UserConstant.VERIFY_CODE);
        Long expirationTime = (Long) session.getAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME);
        // session中属性为空
        if (expirationTime == null || serverVerifyCode == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请重新获取验证码！");
        }
        long currentTimeMillis = System.currentTimeMillis();
        // 超时失效，重新发送覆盖验证码和时间戳
        if (currentTimeMillis > expirationTime)
            throw new BusinessException(ResponseCode.TIME_OUT, "验证码超时失效!");
        // 验证失败，重新尝试输入，不正确重新发送验证码
        if (!serverVerifyCode.equals(verifyCode))
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "验证码错误!");
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

    private User getASKeyUser() {
        /*
         *  生成accessKey 和 secretKey
         * */
        Map<String, String> keys = generateAccessAndSecretKeys();
        // 创建用户
        User user = new User();
        user.setAccessKey(keys.get("accessKey"));
        user.setSecretKey(keys.get("secretKey"));
        return user;
    }

    private Map<String, String> generateAccessAndSecretKeys() {
        Map<String, String> resultMap = new HashMap<>();
        // 获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        // 生成6位随机数
        SecureRandom random = new SecureRandom();
        int randomPart = random.nextInt(899999) + 100000; // 生成100000到999999之间的随机数
        // 拼接时间戳和随机数
        String accessKeyInput = String.valueOf(timestamp) + randomPart;
        String secretKeyInput = accessKeyInput + SecretConstant.PASSWORD_SECRET_KEY;// 添加额外的字符串以增加复杂性
        // 使用SHA-256哈希函数生成密钥
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] accessKeyBytes = md.digest(accessKeyInput.getBytes());
            byte[] secretKeyBytes = md.digest(secretKeyInput.getBytes());
            // 将字节数组转换为十六进制字符串
            String md5AccessKey = toMD5(accessKeyBytes);
            String md5SecretKey = toMD5(secretKeyBytes);
            resultMap.put("accessKey", md5AccessKey);
            resultMap.put("secretKey", md5SecretKey);
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        return resultMap;
    }

    private String toMD5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(bytes);

            // 转换字节数组为十六进制大写字符串表示
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}




