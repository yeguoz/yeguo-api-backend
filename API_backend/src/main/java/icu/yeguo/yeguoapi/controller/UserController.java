package icu.yeguo.yeguoapi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
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
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    @PostMapping("register")
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
    @PostMapping("login")
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
    @PostMapping("verifyCode")
    public Result<Integer> userEmailVerifyCode(@RequestBody VerifyCodeEmail verifyCodeEmail, HttpServletRequest req) {
        String email = verifyCodeEmail.getEmail();
        if (email == null || email.isEmpty()) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据！");
        }
        String regex = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "邮箱格式错误！");
        }
        Integer result = EmailUtil.sendMail(email);
        // 将 验证码 和 过期时间戳 存入该用户的session中
        HttpSession session = req.getSession();
        session.setAttribute(UserConstant.VERIFY_CODE, EmailUtil.verifyCode);
        long expirationTime = System.currentTimeMillis() + 5 * 60 * 1000; // 5分钟后
        session.setAttribute(UserConstant.VERIFY_CODE_EXPIRATION_TIME, expirationTime);

        System.out.println(EmailUtil.verifyCode);
        if (result != 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "发送失败");

        return ResultUtils.success(result);
    }

    /*
     *  邮箱注册
     * */
    @PostMapping("emailRegister")
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
    @PostMapping("emailLogin")
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
    @GetMapping("current")
    public Result<UserVO> getCurrentUser(HttpServletRequest req) {
        UserVO userVO = userServiceImpl.getCurrentUser(req);
        return ResultUtils.success(userVO);
    }

    /*
     * 退出登录
     * */
    @PostMapping("logout")
    public Result<Integer> logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
        return ResultUtils.success(1);
    }

    /*
     * 查询所有用户
     * */
    @GetMapping("dynamicQuery")
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
    @DeleteMapping("{id}")
    public Result<Integer> removeById(@PathVariable Long id, HttpServletRequest req) {
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
    @PutMapping("update")
    public Result<Integer> updateById(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 更新成功返回值为 1
        int result = userServiceImpl.upById(userUpdateRequest);
        return ResultUtils.success(result);
    }

    @PutMapping("personInfoUpdate")
    public Result<Integer> personInfoUpdate(@RequestBody UserPersonUpdateParams userPersonUpdateParams, HttpServletRequest req) {
        // 更新成功返回值为 1
        int result = userServiceImpl.upPersonInfo(userPersonUpdateParams);
        if (result != 1) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(result);
    }

    @PutMapping("{id}")
    public Result<ASKeyVO> personKeysUpdate(@PathVariable("id") Long id) {
        ASKeyVO result = userServiceImpl.upASKey(id);
        return ResultUtils.success(result);
    }
}

