<div align="center">
  <img data-type="dingtalk" src="https://cdn.jsdelivr.net/gh/ye-guo/Images/images/ygIcon512.png" width="100" alt="logo"/>
  <h2>YGAPI 开发者文档</h2>
  <p>YGAPI 接口开放平台是一个为用户开发者提供稳定、安全、高效的接口调用服务平台</p>
</div>

## 项目介绍 ✨

YGAPI 是一个基于 Ant Design Pro 和 Spring Boot 的接口开放平台，旨在为用户提供稳定、安全、高效的接口调用服务。YGAPI 提供了丰富的接口文档、详细的接口说明、便捷的接口测试功能，帮助用户快速了解和使用接口。YGAPI 还提供了用户管理、权限控制等功能，确保用户数据的安全性和隐私性。YGAPI 是一个功能强大、易于使用的接口开放平台，为用户提供了一个可靠、高效的接口调用解决方案。

## 项目导航 📌

- [YGAPI 前端 🔗 ](https://github.com/ye-guo/yeguo-api-frontend)
- [YGAPI 后端 🔗 ](https://github.com/ye-guo/yeguo-api-backend)
- [YGAPI 接口开放平台在线平台 🔗 ](https://api.yeguo.icu)
- [YGAPI 开发者文档 📚](https://apidocs.yeguo.icu)
- [YGAPI-SDK🧰](https://apidocs.yeguo.icu/guide/getting-started#%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B)

## 工作流程 🔄

![YGAPI 工作流程](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/api%E9%A1%B9%E7%9B%AE%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg)

## 快速启动 ⚡

### 前端

Node.js >= 18

### 安装依赖

使用 npm or yarn

```bash
npm install
or
yarn install
```

### 启动项目

```bash
npm run start:dev
or
yarn start:dev
```

### 构建项目

```bash
npm run build
or
yarn build
```

### 后端

- JDK17
- Maven 3.9.7
- MySQL 8.0.12
- Redis 3.2.12+
- Nacos 2.3.2

创建数据库和表结构，参考 `sql` 文件夹下 app.sql 文件

## 技术选型 🧩

### 前端：

- Ant Design Pro 6.0.0
- Ant Design 5.20
- React 18
- TypeScript 5.4.3
- UmiJS 4.3.11

### 后端

- JDK17
- Spring
- SpringMVC
- SpringBoot 3.3.0
- Mybatis-Plus 3.5.5
- SpringCloud Gateway 4.1.4
- Redis 3.2.12
- Hutool 5.8.27
- mysql 8.0.12
- dubbo 3.2.12
- nacos 2.3.2
- maven 3.9.7
- Swagger + Knife4j 接口文档

## 功能介绍 💡

|                            功能                             | 普通用户 | 管理员 |
| :---------------------------------------------------------: | :------: | :----: |
|      [开发者 API 在线文档](https://apidocs.yeguo.icu)       |    √     |   √    |
| [SDK 使用](https://apidocs.yeguo.icu/guide/getting-started) |    √     |   √    |
|                       登录注册(邮箱)                        |    √     |   √    |
|                     找回密码(需有邮箱)                      |    √     |   √    |
|                          接口市集                           |    √     |   √    |
|                          在线调试                           |    √     |   √    |
|                          钱包充值                           |    √     |   √    |
|                    订单支付、取消、删除                     |    √     |   √    |
|                        更新个人信息                         |    √     |   √    |
|                          更新凭证                           |    √     |   √    |
|                    管理用户、接口、订单                     |    ×     |   √    |

## 功能展示

### 开发者文档

![开发者文档](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093834756.png) ![开发者文档](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094127748.png)

### 首页

![首页](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093020891.png)

### 接口管理

![接口管理](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093229796.png)

### 用户管理

![用户管理](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093438601.png)

### 订单管理

![订单管理](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093511968.png)

### 接口市集

![接口市集](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806093543196.png)

### 在线 API 调试

![在线API调试](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094256719.png) ![在线API调试](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094346032.png)

### 钱包充值

![钱包充值](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094434489.png) ![订单充值](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094534609.png)

### 我的订单

![我的订单](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094614152.png)

### 个人信息

![个人信息](https://cdn.jsdelivr.net/gh/ye-guo/Images/images/image-20240806094725168.png)
