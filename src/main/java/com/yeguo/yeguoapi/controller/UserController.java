package com.yeguo.yeguoapi.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeguo.yeguoapi.common.ResponseCode;
import com.yeguo.yeguoapi.common.Result;
import com.yeguo.yeguoapi.common.ResultUtils;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.dto.user.*;
import com.yeguo.yeguoapi.model.vo.UserVO;
import com.yeguo.yeguoapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static com.yeguo.yeguoapi.utils.IsAdminUtil.isAdmin;


/**
 * 用户 Controller
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
        long id = userServiceImpl.userRegister(username,userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }




    /*
     *  注册
     * */
    @PostMapping("emailRegister")
    public Result<Long> userEmailRegister(@RequestBody UserEmailRegisterRequest userEmailRegisterRequest) {
        if (userEmailRegisterRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        String email = userEmailRegisterRequest.getEmail(); // username 可以为空
        String verifyCode = userEmailRegisterRequest.getVerifyCode();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(email, verifyCode)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        long id = userServiceImpl.userRegister(username,userAccount, userPassword, checkPassword);
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
    * 查询当前用户
    * */
    @GetMapping("current")
    public Result<UserVO> getCurrentUser(HttpServletRequest req) {
        UserVO userVO =  userServiceImpl.getCurrentUser(req);
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
        HttpSession session = req.getSession();
        ArrayList<UserVO> userVOList = null;
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
    @PutMapping("/update")
    public Result<Integer> updateById(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 更新成功返回值为 1
        int result = userServiceImpl.upById(userUpdateRequest);
        return ResultUtils.success(result);
    }
}
