package icu.yeguo.yeguoapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.yeguoapi.model.entity.Order;
import icu.yeguo.yeguoapi.service.OrderService;
import icu.yeguo.yeguoapi.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Lenovo
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-07-15 14:42:03
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService{

}




