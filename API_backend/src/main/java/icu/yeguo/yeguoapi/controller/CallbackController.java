package icu.yeguo.yeguoapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/callback")
public class CallbackController {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserService userService;

    @Transactional
    @GetMapping("/payment/notify")
    public void handlePaymentNotification(@RequestParam("pid") String pid,
                                          @RequestParam("trade_no") String tradeNo,
                                          @RequestParam("out_trade_no") String outTradeNo,
                                          @RequestParam("type") String type,
                                          @RequestParam("name") String name,
                                          @RequestParam("money") BigDecimal money,
                                          @RequestParam("trade_status") String tradeStatus,
                                          @RequestParam("sign") String sign,
                                          @RequestParam("sign_type") String signType) {
        try {
            log.info("收到异步回调通知：");
            log.info("pid：{}", pid);
            log.info("trade_no：{}", tradeNo);
            log.info("out_trade_no：{}", outTradeNo);
            log.info("type：{}", type);
            log.info("name：{}", name);
            log.info("money：{}", money);
            log.info("trade_status：{}", tradeStatus);
            log.info("sign：{}", sign);
            log.info("sign_type：{}", signType);
            if (tradeStatus.equals("TRADE_SUCCESS")) {
                // 更新订单状态为已支付
                Integer i = orderInfoService.updateOrderInfoStatus(tradeNo, 3);
                if (i == 1) {
                    log.info("订单状态更新成功");
                }
                // 通过订单号找到userId
                OrderInfo orderInfoOne = orderInfoService
                        .getOne(new LambdaQueryWrapper<>(OrderInfo.class)
                                .eq(OrderInfo::getOrderId, tradeNo));
                Long userId = orderInfoOne.getUserId();
                User userOne = userService.getOne(new LambdaQueryWrapper<>(User.class).eq(User::getId, userId));

                // 把name中数值提取出来
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(name);
                if (matcher.find()) {
                    Long number = Long.parseLong(matcher.group());
                    userOne.setGoldCoin(userOne.getGoldCoin() + number);
                    // 为用户添加金币
                    userService.updateById(userOne);
                }
            }
        } catch (Exception e) {
            log.error("处理支付回调通知时发生异常:{}", e.getMessage());
        }
    }
}
