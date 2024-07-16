package icu.yeguo.yeguoapi.controller;

import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.utils.IsAdminUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;

    // 用户获取自己所有订单
    @GetMapping("{userId}/all")
    public Result<List<OrderInfoVO>> getUserAllOrderInfos(@PathVariable("userId") Long userId) {
        List<OrderInfoVO> list = orderInfoService.getUserAllOrders(userId);
        return ResultUtils.success(list);
    }

    // 管理员获取所有订单
    @GetMapping("all")
    public Result<List<OrderInfo>> getAllOrderInfos(HttpServletRequest req) {
        if (!IsAdminUtil.isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "您不是管理员，权限不足");
        }
        List<OrderInfo> list = orderInfoService.getAllOrders();
        return ResultUtils.success(list);
    }

    // 用户取消订单
    @PutMapping("cancel/{orderId}")
    public Result<Integer> cancelOrder(@PathVariable("orderId") String orderId) {
        return orderInfoService.cancelOrder(orderId) == 1 ? ResultUtils.success(1) : ResultUtils.success(-1);
    }
}
