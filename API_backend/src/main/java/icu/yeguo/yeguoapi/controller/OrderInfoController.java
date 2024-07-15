package icu.yeguo.yeguoapi.controller;

import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
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

    @GetMapping("{userId}/all")
    public Result<List<OrderInfoVO>> getAllOrderInfos(@PathVariable("userId") Long userId){
        List<OrderInfoVO> list= orderInfoService.getAllOrders(userId);
        return ResultUtils.success(list);
    }
}
