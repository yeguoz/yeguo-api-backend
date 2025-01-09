package icu.yeguo.yeguoapi.controller;

import cn.hutool.core.bean.BeanUtil;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.orderInfo.CreateOrderInfoRequest;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoNotificationRequest;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoQueryRequest;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.utils.EmailUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static icu.yeguo.yeguoapi.utils.IsAdminUtil.isAdmin;

@Slf4j
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController {
    private static final String EMAIL_SENDER = "aidjajd@163.com";
    @Autowired
    private OrderInfoService orderInfoServiceImpl;

    // 创建订单
    @PostMapping
    public Result<OrderInfoVO> createOrderInfo(@RequestBody CreateOrderInfoRequest createOrderInfoRequest) {
        OrderInfoVO orderInfoVO = orderInfoServiceImpl.createOrderInfo(createOrderInfoRequest);
        if (orderInfoVO == null)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        return ResultUtils.success(orderInfoVO);
    }

    // 用户获取自己所有订单
    @GetMapping("/{userId}/dynamicQuery")
    public Result<List<OrderInfoVO>> getUserAllOrderInfos(@PathVariable("userId") Long userId, OrderInfoQueryRequest
            orderInfoQueryRequest) {
        List<OrderInfoVO> orderInfoList;
        // hutool BeanUtil 属性都为空
        if (BeanUtil.isEmpty(orderInfoQueryRequest)) {
            orderInfoList = orderInfoServiceImpl.getUserAllOrderInfos(userId);
        } else {
            orderInfoList = orderInfoServiceImpl.dynamicQueryUserOrderInfos(userId, orderInfoQueryRequest);
        }
        return ResultUtils.success(orderInfoList);
    }

    @GetMapping("/dynamicQuery")
    public Result<List<OrderInfo>> dynamicQuery(OrderInfoQueryRequest orderInfoQueryRequest, HttpServletRequest req) {
        List<OrderInfo> orderInfoList;
        if (!isAdmin(req))
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户，无权限执行此操作");
        // hutool BeanUtil 属性都为空
        if (BeanUtil.isEmpty(orderInfoQueryRequest)) {
            orderInfoList = orderInfoServiceImpl.selectAll();
        } else {
            orderInfoList = orderInfoServiceImpl.dynamicQuery(orderInfoQueryRequest);
        }
        return ResultUtils.success(orderInfoList);
    }

    // 取消订单 使其失效
    @PutMapping("/{orderId}/{payStatus}")
    public Result<Integer> updateOrderInfoStatus(@PathVariable("orderId") String orderId,
                                           @PathVariable("payStatus") Integer payStatus) {
        Integer result = orderInfoServiceImpl.updateOrderInfoStatus(orderId,payStatus);
        if (result != 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        return ResultUtils.success(result);
    }

    // 删除订单
    @DeleteMapping("/{orderId}")
    public Result<Integer> deleteOrderInfo(@PathVariable("orderId") String orderId) {
        Integer result = orderInfoServiceImpl.deleteOrderInfo(orderId);
        if (result != 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        return ResultUtils.success(result);

    }

    @PostMapping("/notifyMail")
    public Result<Integer> sendNotificationMail(@RequestBody OrderInfoNotificationRequest orderInfoNotificationRequest) {
        Integer result = EmailUtil.sendMail(EMAIL_SENDER, orderInfoNotificationRequest);
        if (result != 1)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        return ResultUtils.success(1);
    }

}
