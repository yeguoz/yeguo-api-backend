package icu.yeguo.yeguoapi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order_info
 */
@TableName(value ="order_info")
@Data
public class OrderInfo implements Serializable {
    /**
     * 自增长id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 支付方式（0 wxpay 1 alipay）
     */
    @TableField(value = "pay_type")
    private Integer payType;

    /**
     * 价格
     */
    @TableField(value = "money")
    private BigDecimal money;

    /**
     * 支付状态（0 未支付 1 已失效 2 正在审核 3 已完成）
     */
    @TableField(value = "pay_status")
    private Integer payStatus;

    /**
     * 商品内容
     */
    @TableField(value = "commodity_content")
    private String commodityContent;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 逻辑删除 （0 正常 1 删除）
     */
    @TableLogic
    private Integer idDeleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}