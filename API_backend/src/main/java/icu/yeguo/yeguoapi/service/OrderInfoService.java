package icu.yeguo.yeguoapi.service;

import icu.yeguo.yeguoapi.model.dto.orderInfo.CreateOrderInfoRequest;
import icu.yeguo.yeguoapi.model.dto.orderInfo.OrderInfoQueryRequest;
import icu.yeguo.yeguoapi.model.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import icu.yeguo.yeguoapi.model.vo.OrderInfoVO;

import java.util.List;

/**
* @author Lenovo
* @description 针对表【order_info】的数据库操作Service
* @createDate 2024-07-15 17:02:51
*/
public interface OrderInfoService extends IService<OrderInfo> {

    List<OrderInfoVO> getUserAllOrders(Long userId);

    Integer cancelOrderInfo(String orderId);

    OrderInfoVO createOrderInfo(CreateOrderInfoRequest createOrderInfoRequest);

    Integer deleteOrderInfo(String orderId);

    List<OrderInfo> selectAll();

    List<OrderInfo> dynamicQuery(OrderInfoQueryRequest orderInfoQueryRequest);
}
