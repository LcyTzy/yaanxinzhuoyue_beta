<p align="center">
  <h1 align="center">新卓阅汽配商城</h1>
  <p align="center">Auto Parts Mall — 全栈汽车配件电商平台</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.4.x-4FC08D.svg" alt="Vue">
  <img src="https://img.shields.io/badge/MySQL-8.0-blue.svg" alt="MySQL">
  <img src="https://img.shields.io/badge/WeChat-Mini%20Program-07C160.svg" alt="WeChat">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

---

## 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [项目结构](#项目结构)
- [功能模块](#功能模块)
- [快速开始](#快速开始)
- [环境配置](#环境配置)
- [部署指南](#部署指南)
- [API文档](#api文档)
- [安全说明](#安全说明)
- [贡献指南](#贡献指南)

---

## 项目简介

新卓阅汽配商城是一个面向汽车后市场的全栈电商平台，提供**Web端**、**微信小程序**、**Android客户端**三端覆盖。核心能力包括：

- **精准配件匹配**：通过车型选择或 VIN 码查询，实现汽车配件的精准匹配
- **完整电商流程**：商品浏览、搜索、购物车、下单、支付（微信/支付宝）
- **EPC 电子配件目录**：集成 VIN 码解析引擎，支持 OE 号反查
- **进销存管理**：采购订单、库存变动追踪、库存预警
- **增值服务**：服务预约、保养记录、保险年检、在线询价、发票管理
- **会员体系**：积分签到、积分商城、会员等级折扣

---

## 技术架构

```
┌──────────────────────────────────────────────────────┐
│                    前端层 (Frontend)                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐   │
│  │ Web 商城  │  │ 管理后台  │  │ 微信小程序/Android │   │
│  │ Vue 3     │  │ Vue 3    │  │ 原生 + Compose    │   │
│  └─────┬─────┘  └─────┬────┘  └────────┬─────────┘   │
└────────┼──────────────┼────────────────┼─────────────┘
         │              │                │
         └──────────────┼────────────────┘
                        │  HTTPS / REST API
┌───────────────────────┼──────────────────────────────┐
│                       ▼                               │
│                   Nginx (反向代理 + SSL)                │
│                       │                               │
│  ┌────────────────────┼────────────────────────────┐  │
│  │              后端层 (Backend)                     │  │
│  │  Spring Boot 3.2  │  MyBatis-Plus  │  JWT       │  │
│  │  Redis 缓存        │  Swagger/OpenAPI           │  │
│  └────────────────────┼────────────────────────────┘  │
│                       │                               │
│  ┌────────────────────┼────────────────────────────┐  │
│  │              数据层 (Data)                        │  │
│  │  MySQL 8.0  │  Redis  │  阿里云 OSS              │  │
│  └─────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────┘
```

### 技术栈详情

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.2.x | 主框架 |
| **安全认证** | Spring Security + JWT | 6.x | 无状态认证 |
| **ORM** | MyBatis-Plus | 3.5.x | 数据库操作 |
| **数据库** | MySQL | 8.0 | 关系型数据库 |
| **缓存** | Redis | 7.x | 会话/热点数据缓存 |
| **Web商城** | Vue 3 + Vite + Element Plus | 3.4 / 5.x | 用户端 |
| **管理后台** | Vue 3 + Vite + Element Plus | 3.4 / 5.x | 管理员端 |
| **微信小程序** | 原生框架 | - | 移动端 |
| **Android** | Kotlin + Jetpack Compose | - | 原生客户端 |
| **支付** | 微信支付V3 / 支付宝 | - | 双通道支付 |
| **物流** | 快递100 | - | 物流追踪 |
| **对象存储** | 阿里云 OSS | - | 图片/文件存储 |
| **短信** | 阿里云 SMS | - | 验证码发送 |

---

## 项目结构

```
goods/
├── src/main/java/com/zhantu/       # 后端源码
│   ├── controller/                  # 控制器层
│   │   └── admin/                   # 管理员接口
│   ├── service/                     # 业务逻辑层
│   │   └── impl/                    # 实现类
│   ├── entity/                      # 数据实体
│   ├── mapper/                      # MyBatis Mapper
│   ├── config/                      # Spring 配置
│   ├── payment/                     # 支付服务（微信/支付宝）
│   ├── logistics/                   # 物流服务（快递100）
│   ├── filter/                      # JWT 过滤器
│   └── common/                      # 通用类（异常/响应）
├── src/main/resources/
│   ├── sql/schema.sql               # 数据库初始化脚本
│   ├── application.yml              # 主配置
│   ├── application-dev.yml          # 开发环境配置
│   └── application-prod.yml         # 生产环境配置
├── web-mall/                        # Web 用户商城 (Vue 3)
│   └── src/views/                   # 页面组件
├── web-admin/                       # Web 管理后台 (Vue 3)
│   └── src/views/                   # 页面组件
├── wxmall/                          # 微信小程序
│   ├── pages/                       # 页面
│   ├── utils/api.js                 # API 封装
│   └── app.js                       # 小程序入口
├── android-clients/                 # Android 客户端 (Kotlin)
├── scripts/                         # 运维脚本
├── docker-compose.yml               # Docker 编排
├── Dockerfile                       # Docker 镜像
├── nginx.conf                       # Nginx 配置模板
├── server-env.example.sh            # 环境变量示例
└── pom.xml                          # Maven 配置
```

---

## 功能模块

### 用户端

| 模块 | Web | 小程序 | Android | 说明 |
|------|:---:|:------:|:-------:|------|
| 商品浏览/搜索 | ✅ | ✅ | ✅ | 分类筛选、关键词搜索、价格排序 |
| VIN 码查配件 | ✅ | ✅ | ✅ | 17位VIN解析车型，匹配配件 |
| 商品详情 | ✅ | ✅ | ✅ | 图片、规格、OE号、适用车型 |
| 购物车 | ✅ | ✅ | ✅ | 增删改查、批量操作 |
| 订单管理 | ✅ | ✅ | ✅ | 下单、支付、物流追踪、售后 |
| 收货地址 | ✅ | ✅ | ✅ | 增删改查、默认地址 |
| 优惠券 | ✅ | ✅ | ✅ | 领取、使用 |
| 商品收藏 | ✅ | ✅ | ✅ | 收藏/取消收藏 |
| 商品对比 | ✅ | ✅ | - | 多商品横向对比 |
| 消息通知 | ✅ | ✅ | ✅ | 订单/系统/促销通知 |
| 会员中心 | ✅ | ✅ | ✅ | 等级展示、折扣权益 |
| 积分商城 | ✅ | ✅ | ✅ | 签到、积分兑换 |
| 发票管理 | ✅ | ✅ | - | 申请电子/纸质发票 |
| 服务预约 | ✅ | ✅ | ✅ | 在线预约维修保养 |
| 保养记录 | ✅ | ✅ | - | 保养历史记录 |
| 在线询价 | ✅ | ✅ | - | 配件询价表单 |
| 保险/年检 | ✅ | ✅ | - | 车险年检服务申请 |
| 个人资料 | ✅ | ✅ | ✅ | 头像、昵称、性别编辑 |

### 管理端

| 模块 | 说明 |
|------|------|
| 数据仪表盘 | 销售统计、订单概览 |
| 商品管理 | CRUD、分类管理、上下架 |
| 车型管理 | 品牌→车系→车型三级管理 |
| 配件关联 | 车型与配件关联关系 |
| 供应商管理 | 供应商信息维护 |
| 采购订单 | 创建、审核、入库 |
| 库存管理 | 库存日志、预警 |
| 订单管理 | 用户订单、发货处理 |
| 服务预约 | 预约审核、状态管理 |
| 用户管理 | 用户列表、角色权限 |
| Banner管理 | 首页轮播图配置 |
| VIN查询 | 管理员VIN查配件 |

---

## 快速开始

### 环境要求

| 软件 | 最低版本 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 7.0+ |

### 1. 克隆项目

```bash
git clone https://github.com/LcyTzy/yaanxinzhuoyue_beta.git
cd yaanxinzhuoyue_beta
```

### 2. 初始化数据库

```bash
mysql -u root -p -e "CREATE DATABASE autoparts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p autoparts < src/main/resources/sql/schema.sql
```

### 3. 配置环境变量

```bash
# 复制环境变量模板
cp server-env.example.sh server-env.sh

# 编辑 server-env.sh，填入真实配置
vim server-env.sh

# 加载环境变量
source server-env.sh
```

### 4. 启动后端

```bash
mvn spring-boot:run
# 默认端口: 8081
# API文档: http://localhost:8081/swagger-ui.html
```

### 5. 启动前端

```bash
# Web 商城
cd web-mall && npm install && npm run dev
# 访问: http://localhost:5173

# 管理后台
cd web-admin && npm install && npm run dev
# 访问: http://localhost:5174
```

### 6. 微信小程序

使用微信开发者工具打开 `wxmall/` 目录，配置 AppID 后即可编译运行。

---

## 环境配置

项目通过环境变量管理所有敏感配置，**严禁将真实密钥硬编码或提交到仓库**。

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_HOST` | 数据库地址 | `localhost` |
| `DB_PORT` | 数据库端口 | `3306` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `your_password` |
| `REDIS_HOST` | Redis 地址 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `JWT_SECRET` | JWT 签名密钥 | `openssl rand -base64 32` |
| `OSS_ACCESS_KEY_ID` | 阿里云 OSS AK | `LTAI5t...` |
| `OSS_ACCESS_KEY_SECRET` | 阿里云 OSS SK | `WOVHEi...` |
| `WECHATPAY_MCH_ID` | 微信支付商户号 | `1744677835` |
| `WECHATPAY_API_V3_KEY` | 微信支付 V3 密钥 | `79a9a16c...` |
| `ALIPAY_APP_ID` | 支付宝 AppID | `202100615...` |
| `SMS_ACCESS_KEY_ID` | 短信 AK | `LTAI5t...` |
| `VIN_TANSHU_API_KEY` | VIN 解析 API Key | `f25667d2...` |

完整配置见 [server-env.example.sh](server-env.example.sh)。

---

## 部署指南

### Docker 部署

```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f app
```

### 手动部署

```bash
# 1. 打包后端
mvn clean package -DskipTests

# 2. 上传 jar 到服务器
scp target/goods-0.0.1-SNAPSHOT.jar user@server:/opt/autoparts/

# 3. 配置环境变量
scp server-env.sh user@server:/etc/profile.d/autoparts.sh
ssh user@server "source /etc/profile"

# 4. 构建前端
cd web-mall && npm run build    # 输出到 dist/
cd web-admin && npm run build   # 输出到 dist/

# 5. 配置 Nginx（参考 nginx.conf）
# 6. 启动服务
java -jar /opt/autoparts/goods-0.0.1-SNAPSHOT.jar
```

### Nginx 配置要点

- 必须启用 HTTPS（微信小程序要求）
- API 路径 `/api/` 代理到后端 `8081` 端口
- 前端静态文件由 Nginx 直接 serve
- SSL 证书使用 CA 签发证书（不支持自签名）

---

## API 文档

启动后端后访问 Swagger UI：

```
http://localhost:8081/swagger-ui.html
```

### 主要接口模块

| 模块 | 路径前缀 | 认证 |
|------|----------|:----:|
| 用户 | `/api/user/**` | 部分公开 |
| 商品 | `/api/product/**` | 公开 |
| 购物车 | `/api/cart/**` | 需登录 |
| 订单 | `/api/order/**` | 需登录 |
| 支付 | `/api/payment/**` | 需登录 |
| 收藏 | `/api/favorite/**` | 需登录 |
| 通知 | `/api/notification/**` | 需登录 |
| 会员 | `/api/membership/**` | 需登录 |
| 积分 | `/api/points-mall/**` | 需登录 |
| 发票 | `/api/invoice/**` | 需登录 |
| 服务预约 | `/api/service-order/**` | 需登录 |
| 保养 | `/api/maintenance/**` | 需登录 |
| 询价 | `/api/inquiry/**` | 需登录 |
| 保险 | `/api/insurance/**` | 需登录 |
| 管理后台 | `/api/admin/**` | 管理员 |

---

## 安全说明

### 已采取的安全措施

- **密钥管理**：所有密钥通过环境变量注入，不硬编码
- **密码加密**：用户密码使用 BCrypt 加密存储
- **JWT 认证**：无状态 Token 认证，支持过期刷新
- **SQL 注入防护**：MyBatis-Plus 参数化查询
- **CORS 配置**：严格的跨域白名单
- **HTTPS**：全站强制 HTTPS
- **敏感文件**：`.gitignore` 排除 `server-env.sh`、证书、密钥文件

### 提交代码前检查清单

- [ ] 确认 `server-env.sh` 未被提交
- [ ] 确认 `application-dev.yml` 默认值为占位符
- [ ] 确认无 `.pem`、`.key`、`.p12` 等证书文件
- [ ] 确认无真实手机号、密码等测试数据
- [ ] 运行 `git status` 确认敏感文件在 `.gitignore` 中

---

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

---

## 许可证

本项目基于 MIT 许可证开源。详见 [LICENSE](LICENSE) 文件。

---

## 联系方式

- 项目地址：[https://github.com/LcyTzy/yaanxinzhuoyue_beta](https://github.com/LcyTzy/yaanxinzhuoyue_beta)
- 问题反馈：[GitHub Issues](https://github.com/LcyTzy/yaanxinzhuoyue_beta/issues)