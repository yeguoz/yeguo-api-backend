package icu.yeguo.yeguoapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.yeguoapi.model.dto.orderInfo.CreateOrderInfoRequest;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoQueryRequest;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 * @description 针对表【order_info】的数据库操作Service实现
 * @createDate 2024-07-15 17:02:51
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public List<OrderInfoVO> getUserAllOrders(Long userId) {
        List<OrderInfoVO> orderInfoVOList;
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getUserId, userId);
            List<OrderInfo> orders = orderInfoMapper.selectList(lambdaQueryWrapper);

            orderInfoVOList = orders.stream().map(orderInfo -> {
                OrderInfoVO orderVO = new OrderInfoVO();
                orderVO.setOrderId(orderInfo.getOrderId());
                orderVO.setUserId(orderInfo.getUserId());
                orderVO.setPayType(orderInfo.getPayType());
                orderVO.setMoney(orderInfo.getMoney());
                orderVO.setPayStatus(orderInfo.getPayStatus());
                orderVO.setCommodityContent(orderInfo.getCommodityContent());
                orderVO.setCreateTime(orderInfo.getCreateTime());
                orderVO.setUpdateTime(orderInfo.getUpdateTime());
                return orderVO;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoVOList;
    }

    @Override
    public Integer cancelOrderInfo(String orderId) {
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getOrderId, orderId);
            OrderInfo orderInfo = orderInfoMapper.selectOne(lambdaQueryWrapper);
            if (orderInfo.getPayStatus() == 1) {
                return 2;
            }
            orderInfo.setPayStatus(1);
            orderInfoMapper.updateById(orderInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    @Override
    public String createOrderInfo(CreateOrderInfoRequest createOrderInfoRequest) {
        OrderInfo orderInfo = new OrderInfo();
        try {
            orderInfo.setUserId(createOrderInfoRequest.getUserId());
            orderInfo.setPayType(createOrderInfoRequest.getPayType());
            orderInfo.setMoney(createOrderInfoRequest.getMoney());
            orderInfo.setCommodityContent(createOrderInfoRequest.getCommodityContent());
            orderInfoMapper.oderInfoInsert(orderInfo);
            // 设置orderId
            orderInfo.setOrderId(getFormattedDateTime() + orderInfo.getId());
            orderInfoMapper.updateById(orderInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfo.getOrderId();
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

    private String getFormattedDateTime() {
        // 创建SimpleDateFormat对象，并设置日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 获取当前时间的毫秒时间戳 将毫秒时间戳转换为Date对象
        Date date = new Date(System.currentTimeMillis());
        // 使用SimpleDateFormat将Date对象格式化为字符串
        return sdf.format(date);
    }
}




