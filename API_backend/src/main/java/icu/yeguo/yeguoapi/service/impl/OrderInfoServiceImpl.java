package icu.yeguo.yeguoapi.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.orderInfo.CreateOrderInfoRequest;
import icu.yeguo.yeguoapi.model.dto.orderInfo.LeavePay;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoQueryRequest;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.mapper.OrderInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * @author Lenovo
 * @description 针对表【order_info】的数据库操作Service实现
 * @createDate 2024-07-15 17:02:51
 */
@Slf4j
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Value("${pay.address}")
    private String payAddress;
    @Value("${pay.notify.url}")
    private String notifyUrl;
    @Value("${pay.return.url}")
    private String returnUrl;
    @Value("${pay.site.name}")
    private String siteName;
    @Value("${pay.sign.type}")
    private String signType;
    @Value("${pay.pid}")
    private String pid;
    @Value("${pay.key}")
    private String key;

    @Override
    public List<OrderInfoVO> getUserAllOrderInfos(Long userId) {
        List<OrderInfoVO> orderInfoVOList;
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getUserId, userId);
            List<OrderInfo> orderInfos = orderInfoMapper.selectList(lambdaQueryWrapper);

            orderInfoVOList = orderInfos.stream().map(this::getOrderInfoVO
            ).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoVOList;
    }

    @Override
    public OrderInfoVO createOrderInfo(CreateOrderInfoRequest createOrderInfoRequest) {
        OrderInfo orderInfo = new OrderInfo();
        Long userId = createOrderInfoRequest.getUserId();
        Integer payType = createOrderInfoRequest.getPayType();
        BigDecimal money = createOrderInfoRequest.getMoney();
        String commodityContent = createOrderInfoRequest.getCommodityContent();
        String outTradeNo = getFormattedDateTime() + ThreadLocalRandom.current().nextInt(100000, 1000000);
        HashMap<String, Object> paramMap = new HashMap<>();
        try {
            String signStr = "money=" + money +
                    "&name=" + commodityContent +
                    "&notify_url=" + notifyUrl +
                    "&out_trade_no=" + outTradeNo +
                    "&pid=" + pid +
                    "&return_url=" + returnUrl +
                    "&sitename=" + siteName +
                    "&type=" + (payType == 1 ? "alipay" : "wxpay") + key;
            String sign = DigestUtil.md5Hex(signStr);
            // 下单获取订单id
            paramMap.put("pid", pid);
            paramMap.put("type", payType == 1 ? "alipay" : "wxpay");
            paramMap.put("out_trade_no", outTradeNo);
            paramMap.put("notify_url", notifyUrl);
            paramMap.put("return_url", returnUrl);
            paramMap.put("name", commodityContent);
            paramMap.put("money", money);
            paramMap.put("sitename", siteName);
            paramMap.put("sign", sign);
            paramMap.put("sign_type", signType);
            String result = HttpRequest.get(payAddress)
                    .form(paramMap)
                    .timeout(5 * 60 * 1000)//超时，毫秒
                    .execute()
                    .body();
            LeavePay leavePay = JSONUtil.toBean(result, LeavePay.class);
            if (leavePay.getCode() == -1) {
                log.info("下单失败:{}", leavePay.getMsg());
                throw new BusinessException(ResponseCode.SYSTEM_ERROR, "下单失败");
            }
            System.out.println("result:" + result);
            System.out.println("leavePay:" + leavePay);
            String orderId = leavePay.getOrderid();
            System.out.println("leavePay.getOrderid():" + orderId);
            orderInfo.setOrderId(orderId);
            orderInfo.setUserId(userId);
            orderInfo.setPayType(payType);
            orderInfo.setPayStatus(0);
            orderInfo.setMoney(money);
            orderInfo.setCommodityContent(commodityContent);
            orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 3 * 60 * 1000));
            // 插入数据
            orderInfoMapper.insert(orderInfo);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return getOrderInfoVO(orderInfo);
    }

    @Override
    public Integer deleteOrderInfo(String orderId) {
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId);
            orderInfoMapper.delete(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Override
    public List<OrderInfo> selectAll() {
        List<OrderInfo> orderInfoList;
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderInfoList = orderInfoMapper.selectList(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoList;
    }

    @Override
    public List<OrderInfo> dynamicQuery(OrderInfoQueryRequest orderInfoQueryRequest) {
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(orderInfoQueryRequest.getOrderId() != null, OrderInfo::getOrderId, orderInfoQueryRequest.getOrderId())
                .eq(orderInfoQueryRequest.getUserId() != null, OrderInfo::getUserId, orderInfoQueryRequest.getUserId())
                .eq(orderInfoQueryRequest.getPayType() != null, OrderInfo::getPayType, orderInfoQueryRequest.getPayType())
                .eq(orderInfoQueryRequest.getMoney() != null, OrderInfo::getMoney, orderInfoQueryRequest.getMoney())
                .eq(orderInfoQueryRequest.getPayStatus() != null, OrderInfo::getPayStatus, orderInfoQueryRequest.getPayStatus());
        List<OrderInfo> orderInfoList;
        try {
            orderInfoList = orderInfoMapper.selectList(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoList;
    }

    @Override
    public List<OrderInfoVO> dynamicQueryUserOrderInfos(Long userId, OrderInfoQueryRequest orderInfoQueryRequest) {
        LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(OrderInfo::getUserId, userId)
                .eq(orderInfoQueryRequest.getOrderId() != null, OrderInfo::getOrderId, orderInfoQueryRequest.getOrderId())
                .eq(orderInfoQueryRequest.getPayType() != null, OrderInfo::getPayType, orderInfoQueryRequest.getPayType())
                .eq(orderInfoQueryRequest.getMoney() != null, OrderInfo::getMoney, orderInfoQueryRequest.getMoney())
                .eq(orderInfoQueryRequest.getPayStatus() != null, OrderInfo::getPayStatus, orderInfoQueryRequest.getPayStatus());
        List<OrderInfoVO> orderInfoVOList;
        try {
            orderInfoVOList = orderInfoMapper.selectList(lambdaQueryWrapper).stream().map(this::getOrderInfoVO
            ).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoVOList;
    }

    @Override
    public Integer updateOrderInfoStatus(String orderId, Integer payStatus) {
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId);
            OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);
            if (orderInfo == null) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "订单不存在");
            }
            orderInfo.setPayStatus(payStatus);
            orderInfoMapper.updateById(orderInfo);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    private String getFormattedDateTime() {
        // 创建SimpleDateFormat对象，并设置日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 获取当前时间的毫秒时间戳 将毫秒时间戳转换为Date对象
        Date date = new Date(System.currentTimeMillis());
        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date);
    }

    private OrderInfoVO getOrderInfoVO(OrderInfo orderInfo) {
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        orderInfoVO.setOrderId(orderInfo.getOrderId());
        orderInfoVO.setUserId(orderInfo.getUserId());
        orderInfoVO.setPayType(orderInfo.getPayType());
        orderInfoVO.setMoney(orderInfo.getMoney());
        orderInfoVO.setPayStatus(orderInfo.getPayStatus());
        orderInfoVO.setCommodityContent(orderInfo.getCommodityContent());
        orderInfoVO.setCreateTime(orderInfo.getCreateTime());
        orderInfoVO.setUpdateTime(orderInfo.getUpdateTime());
        orderInfoVO.setExpireTime(orderInfo.getExpireTime());
        return orderInfoVO;
    }
}




