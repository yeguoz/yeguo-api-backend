package icu.yeguo.yeguoapi.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 接口信息表
 * @TableName interface_info
 */
@TableName(value ="interface_info")
@Data
public class InterfaceInfo implements Serializable {
    /**
     * 接口id主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口发布人id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 接口名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 接口描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 接口方法
     */
    @TableField(value = "method")
    private String method;

    /**
     * 接口地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 请求参数
     */
    @TableField(value = "request_params")
    private String requestParams;

    /**
     * 响应参数
     */
    @TableField(value = "response_params")
    private String responseParams;

    /**
     * 响应格式
     */
    @TableField(value = "response_format")
    private String responseFormat;

    /**
     * 请求示例
     */
    @TableField(value = "request_example")
    private String requestExample;

    /**
     * 响应示例
     */
    @TableField(value = "response_example")
    private String responseExample;

    /**
     * 接口状态 0-关闭 1-开启
     */
    @TableField(value = "interface_status")
    private Integer interfaceStatus;

    /**
     * 调用次数
     */
    @TableField(value = "invoking_count")
    private Long invokingCount;

    /**
     * 接口头像
     */
    @TableField(value = "avatar_url")
    private String avatarUrl;

    /**
     * 调用一次所需金币
     */
    @TableField(value = "required_gold_coins")
    private Long requiredGoldCoins;

    /**
     * 请求头
     */
    @TableField(value = "request_header")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField(value = "response_header")
    private String responseHeader;

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
     * 逻辑删除 0-正常 1 删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}