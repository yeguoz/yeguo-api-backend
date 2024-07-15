package icu.yeguo.yeguoapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;
import icu.yeguo.yeguoapi.service.OrderInfoService;
import icu.yeguo.yeguoapi.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Lenovo
* @description 针对表【order_info】的数据库操作Service实现
* @createDate 2024-07-15 17:02:51
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public List<OrderInfoVO> getAllOrders(Long userId) {
        List<OrderInfoVO> orderInfoVOList = new ArrayList<>();
        try {
            LambdaQueryWrapper<OrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfo::getUserId, userId);
            List<OrderInfo> orders = orderInfoMapper.selectList(lambdaQueryWrapper);

            orderInfoVOList = orders.stream().map(order -> {
                OrderInfoVO orderVO = new OrderInfoVO();
                orderVO.setOrderId(order.getOrderId());
                orderVO.setUserId(order.getUserId());
                orderVO.setPayType(order.getPayType());
                orderVO.setMoney(order.getMoney());
                orderVO.setPayStatus(order.getPayStatus());
                orderVO.setCreateTime(order.getCreateTime());
                return orderVO;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderInfoVOList;
    }
}




