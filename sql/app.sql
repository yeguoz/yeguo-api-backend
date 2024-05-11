# 创建数据库
create database if not exists api;

# 使用数据库
use api;

-- 用户表

create table if not exists user
(
    id            bigint unsigned auto_increment comment '主键'  primary key,
    username      varchar(128)                                                                                   null comment '用户名',
    user_account  varchar(256)                                                                                   null comment '用户账号',
    user_password varchar(512)                                                                                   not null comment '用户密码',
    avatar_url    varchar(1024)    default 'https://cdn.jsdelivr.net/gh/ye-guo/Images/images/2.jpg'              null comment '头像',
    gender        tinyint unsigned                                                                               null comment '性别',
    phone         varchar(128)                                                                                   null comment '电话',
    email         varchar(512)                                                                                   null comment '邮箱',
    gold_coin     bigint unsigned  default 40                                                                    not null comment '金币 初始化40个',
    access_key    varchar(512)                                                                                   not null comment 'accessKey',
    secret_key    varchar(512)                                                                                   not null comment 'secretKey',
    user_status   tinyint unsigned default 0                                                                     not null comment '用户状态 0-正常',
    user_role     tinyint unsigned default 0                                                                     not null comment '用户角色 0-普通用户 1-管理员',
    create_time   datetime         default CURRENT_TIMESTAMP                                                     not null comment '创建时间',
    update_time   datetime         default CURRENT_TIMESTAMP                                                     not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint unsigned default 0                                                                     not null comment '逻辑删除 0-未删除 1-删除'
)
    comment '用户表';

-- 接口信息表
-- auto-generated definition
create table interface_info
(
    id                  bigint unsigned auto_increment comment '接口id主键'
        primary key,
    user_id             bigint unsigned                                                                                not null comment '接口发布人id',
    name                varchar(256)                                                                                   not null comment '接口名称',
    description         varchar(512)                                                                                   null comment '接口描述',
    method              varchar(128)                                                                                   not null comment '接口方法',
    url                 varchar(1024)                                                                                  not null comment '接口地址',
    request_params      mediumtext                                                                                     not null comment '请求参数',
    request_header      mediumtext                                                                                     not null comment '请求头',
    response_header     mediumtext                                                                                     not null comment '响应头',
    response_format     varchar(128)     default 'JSON'                                                                not null comment '响应格式',
    request_example     text                                                                                           not null comment '请求示例',
    interface_status    tinyint unsigned default 0                                                                     not null comment '接口状态 0-关闭 1-开启',
    invoking_count      bigint unsigned  default 0                                                                     not null comment '调用次数',
    avatar_url          varchar(1024)    default 'https://cdn.jsdelivr.net/gh/ye-guo/Images/images/20240411203856.png' not null comment '接口头像',
    required_gold_coins bigint unsigned  default 1                                                                     not null comment '调用一次所需金币',
    create_time         datetime         default CURRENT_TIMESTAMP                                                     not null comment '创建时间',
    update_time         datetime         default CURRENT_TIMESTAMP                                                     not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete           tinyint unsigned default 0                                                                     not null comment '逻辑删除 0-正常 1 删除'
)
    comment '接口信息表';


# 用户接口关系表