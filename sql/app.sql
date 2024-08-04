-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.12 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 api 的数据库结构
CREATE DATABASE IF NOT EXISTS `api` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `api`;

-- 导出  表 api.interface_info 结构
CREATE TABLE IF NOT EXISTS `interface_info`
(
    `id`                  bigint(20) unsigned                                      NOT NULL AUTO_INCREMENT COMMENT '接口id主键',
    `user_id`             bigint(20) unsigned                                      NOT NULL COMMENT '接口发布人id',
    `name`                varchar(256) CHARACTER SET utf8 COLLATE utf8_unicode_ci           DEFAULT NULL COMMENT '接口名称',
    `description`         varchar(512) COLLATE utf8_unicode_ci                              DEFAULT NULL COMMENT '接口描述',
    `method`              varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci           DEFAULT NULL COMMENT '接口方法',
    `url`                 varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci          DEFAULT NULL COMMENT '接口地址',
    `request_params`      text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '请求参数',
    `response_params`     text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '响应参数',
    `response_format`     varchar(128) CHARACTER SET utf8 COLLATE utf8_unicode_ci           DEFAULT 'JSON' COMMENT '响应格式',
    `request_example`     mediumtext CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '请求示例',
    `response_example`    mediumtext COLLATE utf8_unicode_ci COMMENT '响应示例',
    `interface_status`    tinyint(3) unsigned                                      NOT NULL DEFAULT '0' COMMENT '接口状态 0-关闭 1-开启',
    `invoking_count`      bigint(20) unsigned                                      NOT NULL DEFAULT '0' COMMENT '调用次数',
    `avatar_url`          varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'https://cdn.jsdelivr.net/gh/ye-guo/Images/images/2.jpg' COMMENT '接口头像',
    `required_gold_coins` bigint(20) unsigned                                      NOT NULL DEFAULT '1' COMMENT '调用一次所需金币',
    `request_header`      text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '请求头',
    `response_header`     text CHARACTER SET utf8 COLLATE utf8_unicode_ci COMMENT '响应头',
    `create_time`         datetime                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`          tinyint(3) unsigned                                      NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-正常 1 删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT ='接口信息表';

-- 数据导出被取消选择。

-- 导出  表 api.order_info 结构
CREATE TABLE IF NOT EXISTS `order_info`
(
    `id`                bigint(20) unsigned                                    NOT NULL AUTO_INCREMENT COMMENT '自增长id',
    `order_id`          varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci          DEFAULT NULL COMMENT '订单编号',
    `user_id`           bigint(20)                                             NOT NULL COMMENT '用户id',
    `pay_type`          tinyint(4) unsigned                                    NOT NULL DEFAULT '0' COMMENT '支付方式（0 wxpay 1 alipay）',
    `money`             decimal(10, 2)                                                  DEFAULT NULL COMMENT '价格',
    `pay_status`        tinyint(4) unsigned                                    NOT NULL DEFAULT '0' COMMENT '支付状态（0 未支付 1 已失效 2 正在审核 3 已完成）',
    `commodity_content` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '商品内容',
    `create_time`       datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                                               NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `expire_time`       datetime                                               NOT NULL COMMENT '过期时间',
    `id_deleted`        tinyint(4) unsigned                                    NOT NULL DEFAULT '0' COMMENT '逻辑删除 （0 正常 1 删除）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- 数据导出被取消选择。

-- 导出  表 api.user 结构
CREATE TABLE IF NOT EXISTS `user`
(
    `id`            bigint(20) unsigned                  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`      varchar(128) COLLATE utf8_unicode_ci                     DEFAULT NULL COMMENT '用户名',
    `user_account`  varchar(256) COLLATE utf8_unicode_ci                     DEFAULT NULL COMMENT '用户账号',
    `user_password` varchar(512) CHARACTER SET utf8 COLLATE utf8_unicode_ci  DEFAULT NULL COMMENT '用户密码',
    `avatar_url`    varchar(1024) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT 'https://cdn.jsdelivr.net/gh/ye-guo/Images/images/2.jpg' COMMENT '头像',
    `gender`        tinyint(3) unsigned                                      DEFAULT NULL COMMENT '性别',
    `phone`         varchar(128) COLLATE utf8_unicode_ci                     DEFAULT NULL COMMENT '电话',
    `email`         varchar(512) COLLATE utf8_unicode_ci                     DEFAULT NULL COMMENT '邮箱',
    `gold_coin`     bigint(20) unsigned                  NOT NULL            DEFAULT '100' COMMENT '金币',
    `access_key`    varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'accessKey',
    `secret_key`    varchar(512) COLLATE utf8_unicode_ci NOT NULL COMMENT 'secretKey',
    `user_status`   tinyint(3) unsigned                  NOT NULL            DEFAULT '0' COMMENT '用户状态 0-正常',
    `user_role`     tinyint(3) unsigned                  NOT NULL            DEFAULT '0' COMMENT '用户角色 0-普通用户 1-管理员',
    `create_time`   datetime                             NOT NULL            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime                             NOT NULL            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    tinyint(3) unsigned                  NOT NULL            DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci COMMENT ='用户表';

-- 数据导出被取消选择。

/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
