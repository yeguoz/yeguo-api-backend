package com.yeguo.yeguoapi.controller;

import cn.hutool.core.util.StrUtil;
import com.yeguo.yeguoapi.common.ErrorCode;
import com.yeguo.yeguoapi.common.Result;
import com.yeguo.yeguoapi.common.ResultUtils;
import com.yeguo.yeguoapi.constant.UserConstant;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.dto.user.UserLoginRequest;
import com.yeguo.yeguoapi.model.dto.user.UserRegisterRequest;
import com.yeguo.yeguoapi.model.entity.User;
import com.yeguo.yeguoapi.model.vo.UserVO;
import com.yeguo.yeguoapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


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
     *  从请求体中读取数据存入
     *  调用service层来处理注册逻辑
     * */
    @PostMapping("register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String username = userRegisterRequest.getUsername(); // username 可以为空
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        long id = userServiceImpl.userRegister(username,userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }

    /*
     *  登录
     *  从请求体中读取数据存入UserLoginDTO
     *  userLoginDTO为空返回null,
     *  userLoginDTO某个属性为空返回null
     *  调用service层来处理登录逻辑
     * */
    @PostMapping("login")
    public Result<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest req) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        UserVO userVO = userServiceImpl.userLogin(userAccount, userPassword, req);
        return ResultUtils.success(userVO);
    }

    // 查询当前用户
    @GetMapping("current")
    public Result<UserVO> getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserVO currentUser = (UserVO) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "您当前未登录");
        }
        // 根据session中用户id查询数据库
        User user = userServiceImpl.selectById(currentUser.getId());

        // 对用户数据脱敏
        UserVO userVO = userServiceImpl.getUserVO(user);
        return ResultUtils.success(userVO);
    }

    // 退出登录
    @PostMapping("logout")
    public Result<Integer> logout(HttpServletRequest req) {
        HttpSession session = req.getSession();
        session.invalidate();
        return ResultUtils.success(1);
    }

    // 查询所有用户
    @GetMapping("selectAll")
    public Result<ArrayList<UserVO>> selectAll(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (!isAdmin(req)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "普通用户，无权限执行此操作");
        }
        ArrayList<UserVO> userList = userServiceImpl.selectAll();
        return ResultUtils.success(userList);
    }

    // 删除用户
    @DeleteMapping("{id}")
    public Result<Integer> remove(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // todo 返回值应该是id
        int result = userServiceImpl.rmByid(id);
        if (result < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(result);
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession();
        UserVO currentUser = (UserVO)session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "您当前未登录");
        return currentUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

}
