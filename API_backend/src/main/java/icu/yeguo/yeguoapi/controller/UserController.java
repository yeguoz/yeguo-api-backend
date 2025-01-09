package icu.yeguo.yeguoapi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.constant.UserConstant;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.user.*;
import icu.yeguo.yeguoapi.model.vo.ASKeyVO;
import icu.yeguo.yeguoapi.model.vo.UserVO;
import icu.yeguo.yeguoapi.service.UserService;
import icu.yeguo.yeguoapi.utils.EmailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static icu.yeguo.yeguoapi.utils.IsAdminUtil.isAdmin;


/**
 * 用户 Controller
 *
 * @author yeguo
 * 2024/5/8
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userServiceImpl;

    /*
     *  注册
     * */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String username = userRegisterRequest.getUsername(); // username 可以为空
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        long id = userServiceImpl.userRegister(username, userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }

    /*
     *  登录
     * */
    @PostMapping("/login")
    public Result<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest req) {
        if (userLoginRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        UserVO userVO = userServiceImpl.userLogin(userAccount, userPassword, req);
        return ResultUtils.success(userVO);
    }


    /*
     * 发送验证码
     * */
    @PostMapping("/verifyCode")
    public Result<Integer> userEmailVerifyCode(@RequestBody VerifyCodeEmail verifyCodeEmail, HttpServletRequest req) {
        String email = verifyCodeEmail.getEmail();
        Integer result = sendVerificationEmail(email, req);
        return ResultUtils.success(result);
    }

    /*
     *  邮箱注册
     * */
    @PostMapping("/emailRegister")
    public Result<Long> userEmailRegister(@RequestBody UserEmailRegisterLoginRequest userEmailRegisterLoginRequest, HttpServletRequest req) {
        if (userEmailRegisterLoginRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String email = userEmailRegisterLoginRequest.getEmail();
        String verifyCode = userEmailRegisterLoginRequest.getVerifyCode();
        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(email, verifyCode))
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据");

        long id = userServiceImpl.userEmailRegister(email, verifyCode, req);
        return ResultUtils.success(id);
    }

    /*
     *  邮箱登录
     * */
    @PostMapping("/emailLogin")
    public Result<UserVO> userEmailLogin(@RequestBody UserEmailRegisterLoginRequest userEmailRegisterLoginRequest, HttpServletRequest req) {
        if (userEmailRegisterLoginRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String email = userEmailRegisterLoginRequest.getEmail();
        String verifyCode = userEmailRegisterLoginRequest.getVerifyCode();
        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(email, verifyCode))
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据");

        UserVO userVO = userServiceImpl.userEmailLogin(email, verifyCode, req);
        return ResultUtils.success(userVO);
    }

    /*
     * 查询当前用户
     * */
    @GetMapping("/current")
    public Result<UserVO> getCurrentUser(HttpServletRequest req) {
        UserVO userVO = userServiceImpl.getCurrentUser(req);
        return ResultUtils.success(userVO);
    }

    /*
     * 退出登录
     * */
    @PostMapping("/logout")
    public Result<Integer> logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
        return ResultUtils.success(1);
    }

    /*
     * 查询所有用户
     * */
    @GetMapping("/dynamicQuery")
    public Result<ArrayList<UserVO>> dynamicQuery(UserQueryRequest userQueryRequest, HttpServletRequest req) {
        ArrayList<UserVO> userVOList;
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户，无权限执行此操作");
        }
        // hutool BeanUtil 属性都为空
        if (BeanUtil.isEmpty(userQueryRequest)) {
            userVOList = userServiceImpl.selectAll();
        } else {
            userVOList = userServiceImpl.dynamicQuery(userQueryRequest);
        }
        return ResultUtils.success(userVOList);
    }

    /*
     * 删除用户
     * */
    @DeleteMapping("/{id}")
    public Result<Integer> removeById(@PathVariable("id") Long id, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 删除成功返回值为 1
        int result = userServiceImpl.rmByid(id);
        return ResultUtils.success(result);
    }

    /*
     * 改
     * */
    @PutMapping("/update")
    public Result<Integer> updateById(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 更新成功返回值为 1
        int result = userServiceImpl.upById(userUpdateRequest);
        return ResultUtils.success(result);
    }

    // 更新个人信息
    @PutMapping("/personInfoUpdate")
    public Result<Integer> personInfoUpdate(@RequestBody UserPersonUpdateParams userPersonUpdateParams) {
        // 更新成功返回值为 1
        int result = userServiceImpl.upPersonInfo(userPersonUpdateParams);
        if (result != 1) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(result);
    }

    // 更新个人密钥
    @PutMapping("/{id}")
    public Result<ASKeyVO> personKeysUpdate(@PathVariable("id") Long id) {
        ASKeyVO result = userServiceImpl.upASKey(id);
        return ResultUtils.success(result);
    }

    // 用户充值果币
    @PutMapping("/recharge/{userId}/{goldCoin}")
    public Result<Integer> recharge(@PathVariable("userId") Long userId, @PathVariable("goldCoin") Long goldCoin) {
        Integer result = userServiceImpl.recharge(userId, goldCoin);
        return ResultUtils.success(result);
    }

    // 忘记密码 验证邮箱
    @PostMapping("/forgetPwd/verifyCode")
    public Result<Integer> forgetPasswordVerifyCode(@RequestBody VerifyCodeEmail verifyCodeEmail,
                                                    HttpServletRequest req) {
        String email = verifyCodeEmail.getEmail();
        // 邮箱是否存在判断
        try {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getEmail, email);
            User user = userServiceImpl.getOne(lambdaQueryWrapper);
            if (user == null) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱不存在");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        // 发送验证邮件
        Integer result = sendVerificationEmail(email, req);
        return ResultUtils.success(result);
    }

    // 忘记密码 提交验证
    @PutMapping("/forgetPwd")
    public Result<Integer> forgetPassword(@RequestBody ForgetPasswordParams forgetPasswordParams,
                                          HttpServletRequest req) {
        if (forgetPasswordParams == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String email = forgetPasswordParams.getEmail();
        String newPassword = forgetPasswordParams.getNewPassword();
        String checkNewPassword = forgetPasswordParams.getCheckNewPassword();
        String verifyCode = forgetPasswordParams.getVerifyCode();
        if (BeanUtil.isEmpty(forgetPasswordParams)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        if (!newPassword.equals(checkNewPassword))
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "两次密码不相同");
        Integer result = userServiceImpl.forgetPassword(email, newPassword, checkNewPassword, verifyCode, req);
        return ResultUtils.success(result);
    }

    public static Integer sendVerificationEmail(String email, HttpServletRequest req) throws BusinessException {
        // 检查邮箱是否为空
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据！");
        }

        // 邮箱格式验证的正则表达式
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        // 验证邮箱格式是否正确
        if (!matcher.matches()) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱格式错误！");
        }

        // 发送验证邮件
        Integer result = EmailUtil.sendMail(email);

        // 将验证码和过期时间存入用户的session中
        HttpSession session = req.getSession();
        session.setAttribute(UserConstant.VERIFY_CODE, EmailUtil.verifyCode);
        long expirationTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5分钟后
        session.setAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME, expirationTime);

        // 检查邮件是否发送成功
        if (result != 1) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "发送失败");
        }
        return result;
    }
}

