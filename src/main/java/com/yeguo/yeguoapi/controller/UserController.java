package com.yeguo.yeguoapi.controller;



import cn.hutool.core.util.StrUtil;
import com.yeguo.yeguoapi.common.ErrorCode;
import com.yeguo.yeguoapi.common.Result;
import com.yeguo.yeguoapi.common.ResultUtils;
import com.yeguo.yeguoapi.constant.UserConstant;
import com.yeguo.yeguoapi.exception.BusinessException;
import com.yeguo.yeguoapi.model.dto.user.UserRegisterRequest;
import com.yeguo.yeguoapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        long id = userServiceImpl.userRegister(userAccount, userPassword, checkPassword);
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
    public Result<SafetyUserDTO> userLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest req) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        // 使用hutool工具StrUtil
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数包含空数据");
        }
        SafetyUserDTO safetyUserDTO = userServiceImpl.userLogin(userAccount, userPassword, req);
        return ResultUtils.success(safetyUserDTO);
    }

    // 查询当前用户
    @GetMapping("current")
    public Result<SafetyUserDTO> getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        SafetyUserDTO currentUser = (SafetyUserDTO) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "您当前未登录");
        }
        // 根据session中用户id查询数据库
        User user = userServiceImpl.selectById(currentUser.getId());
        if (user == null) throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未查询到该用户");
        ;
        // 对用户数据脱敏
        SafetyUserDTO safetytUser = userServiceImpl.geSafetytUser(user);
        return ResultUtils.success(safetytUser);
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
    public Result<ArrayList<SafetyUserDTO>> selectAll(HttpServletRequest req) {
        HttpSession session = req.getSession();
        if (!isAdmin(req)) {
            throw new BusinessException(ErrorCode.NOT_AUTHORITY, "无权限执行操作");
        }
        ArrayList<SafetyUserDTO> safetyUserDTOS = userServiceImpl.selectAll();

        return ResultUtils.success(safetyUserDTOS);
    }

    // 删除用户
    @PostMapping("remove")
    public Result<Integer> remove(@RequestBody Long id, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ErrorCode.NOT_AUTHORITY, "无权限执行操作");
        }
        // 1成功 -1失败
        int i = userServiceImpl.removeById(id) ? 1 : -1;
        if (i == -1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        return ResultUtils.success(i);
    }

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession();
        SafetyUserDTO currentUser = (SafetyUserDTO) session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) throw new BusinessException(ErrorCode.NOT_LOGIN, "您没有登录");
        return currentUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

}
