# 新卓阅汽配商城 — 阿里云 Workbench 远程部署完整指南

---

## 目录

1. [前置准备](#一前置准备)
2. [通过 Workbench 远程连接阿里云 ECS](#二通过-workbench-远程连接阿里云-ecs)
3. [服务器环境安装](#三服务器环境安装)
4. [数据库初始化](#四数据库初始化)
5. [项目构建与上传](#五项目构建与上传)
6. [SSL 证书配置](#六ssl-证书配置)
7. [Nginx 反向代理配置](#七nginx-反向代理配置)
8. [环境变量与启动服务](#八环境变量与启动服务)
9. [验证部署结果](#九验证部署结果)
10. [小程序端配置](#十小程序端配置)
11. [常见问题排查](#十一常见问题排查)

---

## 一、前置准备

在开始之前，请确保你已具备以下条件：

| 序号 | 准备项 | 说明 |
|------|--------|------|
| 1 | 阿里云 ECS 实例 | 已购买并运行中的云服务器（推荐 CentOS 7.9 / Alibaba Cloud Linux 3） |
| 2 | ECS 公网 IP | 例如 `47.xx.xx.xx`，在阿里云控制台 → 实例列表 中查看 |
| 3 | ECS root 密码 | 购买时设置的密码，若忘记可在控制台重置 |
| 4 | 安全组规则已开放端口 | 需要开放：`22`（SSH）、`80`（HTTP）、`443`（HTTPS）、`8081`（后端，可选） |
| 5 | 域名（可选） | 如 `yaanxinzhuoyue.top`，已解析到 ECS 公网 IP |
| 6 | 本地已安装 Maven + JDK 17 | 用于本地构建项目 |

### 1.1 配置安全组规则（重要！）

登录阿里云控制台 → 云服务器 ECS → 实例 → 点击实例 ID → 安全组 → 配置规则 → 入方向 → 手动添加：

| 端口 | 协议 | 授权对象 | 用途 |
|------|------|----------|------|
| 22 | TCP | 0.0.0.0/0 | SSH 远程连接 |
| 80 | TCP | 0.0.0.0/0 | HTTP 访问 |
| 443 | TCP | 0.0.0.0/0 | HTTPS 访问 |
| 8081 | TCP | 0.0.0.0/0 | 后端服务（调试用，正式上线后可关闭） |
| 3306 | TCP | 127.0.0.1/32 | MySQL（仅本地访问，不要对外开放！） |

---

## 二、通过 Workbench 远程连接阿里云 ECS

### 2.1 方式一：阿里云控制台直接连接（推荐新手）

这是最简单的方式，无需安装任何软件：

1. 打开浏览器，登录 [阿里云控制台](https://home.console.aliyun.com/)
2. 在顶部搜索框输入 **"云服务器 ECS"**，点击进入
3. 在左侧菜单点击 **"实例"**
4. 找到你的 ECS 实例，在右侧操作栏点击 **"远程连接"**
5. 在弹出的对话框中，选择 **"通过 Workbench 远程连接"**（蓝色按钮）
6. 点击 **"立即登录"**
7. 输入用户名：`root`，认证方式选择 **"密码"**，输入你的 root 密码
8. 点击 **"确定"**，即可进入服务器的命令行终端

> **提示**：首次连接可能需要几秒钟加载，看到 `[root@xxx ~]#` 就表示连接成功了。

### 2.2 方式二：本地 SSH 客户端连接

如果你习惯使用本地终端工具（如 PowerShell、CMD、Xshell、FinalShell 等）：

**Windows PowerShell / CMD：**

```bash
# 打开 PowerShell 或 CMD，输入以下命令
ssh root@你的ECS公网IP

# 示例：
ssh root@47.100.123.456
```

首次连接会提示确认指纹，输入 `yes` 回车，然后输入密码即可。

**Xshell / FinalShell：**

1. 新建会话 → 主机填 ECS 公网 IP → 端口 22
2. 用户名 `root`，密码填你的 root 密码
3. 点击连接

### 2.3 连接成功后的第一步：更新系统

```bash
# 更新系统软件包（首次连接后建议执行，可能需要几分钟）
yum update -y

# 安装常用工具
yum install -y wget curl vim net-tools lsof unzip git
```

---

## 三、服务器环境安装

以下所有命令都在 Workbench 终端中逐条执行。

### 3.1 安装 JDK 17

```bash
# 查看可用的 JDK 版本
yum search java-17

# 安装 JDK 17
yum install -y java-17-openjdk java-17-openjdk-devel

# 验证安装
java -version
# 期望输出类似：openjdk version "17.0.x" ...

# 查看 JDK 安装路径（后续可能用到）
readlink -f $(which java)
# 通常输出：/usr/lib/jvm/java-17-openjdk-xxx/bin/java
# /usr/lib/jvm/java-17-openjdk-17.0.18.0.8-1.0.2.1.al8.x86_64/bin/java
```

### 3.2 安装 MySQL 8.0

```bash
# 添加 MySQL 官方 YUM 仓库
yum install -y https://dev.mysql.com/get/mysql80-community-release-el7-7.noarch.rpm

# 安装 MySQL 8.0 服务器
yum install -y mysql-community-server

# 启动 MySQL 服务
systemctl start mysqld

# 设置开机自启
systemctl enable mysqld

# 查看 MySQL 运行状态
systemctl status mysqld
# 看到 active (running) 表示启动成功

# 获取 MySQL 初始临时密码
grep 'temporary password' /var/log/mysqld.log
# 输出示例：[Note] A temporary password is generated for root@localhost: aB3xYz9!pQrS
# 记下这个临时密码，下一步要用！
```

**修改 MySQL root 密码并配置：**

```bash
# 使用临时密码登录 MySQL
mysql -u root -p
# 输入上面获取的临时密码

# 登录成功后，在 MySQL 命令行中执行以下操作：

# 1. 修改 root 密码（必须包含大小写字母、数字和特殊字符）
ALTER USER 'root'@'localhost' IDENTIFIED BY '330124';

# 2. 刷新权限
FLUSH PRIVILEGES;

# 3. 创建数据库
CREATE DATABASE autoparts DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 4. 验证数据库创建成功
SHOW DATABASES;
# 应该能看到 autoparts 数据库

# 5. 退出 MySQL
EXIT;
```

> **重要**：请把 `YourNewPassword123!` 换成你自己的强密码，并**务必记住**！

### 3.3 安装 Redis

```bash
# 安装 Redis
yum install -y redis

# 启动 Redis
systemctl start redis

# 设置开机自启
systemctl enable redis

# 查看状态
systemctl status redis

# 验证 Redis 是否正常工作
redis-cli ping
# 期望输出：PONG
```

**（可选）为 Redis 设置密码：**

```bash
# 编辑 Redis 配置文件
vim /etc/redis.conf

# 找到 # requirepass foobared 这一行（约在第 500 行附近）
# 按 i 进入编辑模式，修改为：
requirepass 你的Redis密码

# 按 Esc 退出编辑模式，输入 :wq 保存退出

# 重启 Redis 使配置生效
systemctl restart redis

# 验证密码是否生效
redis-cli -a 你的Redis密码 ping
# 期望输出：PONG
```

### 3.4 安装 Nginx

```bash
# 安装 Nginx
yum install -y nginx

# 启动 Nginx
systemctl start nginx

# 设置开机自启
systemctl enable nginx

# 查看状态
systemctl status nginx

# 此时在浏览器访问 http://你的ECS公网IP
# 应该能看到 Nginx 的欢迎页面
```

---

## 四、数据库初始化

### 4.1 上传 SQL 文件到服务器

**方式一：在 Workbench 中直接创建文件（推荐）**

由于 Workbench 终端支持粘贴，你可以：

1. 在本地用记事本/VS Code 打开 `src/main/resources/sql/schema.sql`
2. 全选复制内容
3. 在 Workbench 终端中执行：

```bash
# 创建 SQL 目录
mkdir -p /opt/autoparts-mall/sql

# 创建 schema.sql 文件
vim /opt/autoparts-mall/sql/schema.sql
# 按 i 进入编辑模式，粘贴内容，按 Esc，输入 :wq 保存
```

**方式二：使用 SCP 从本地上传（如果你用本地 SSH 客户端）**

在**本地** PowerShell 中执行：

```bash
# 先创建服务器目录
ssh root@你的ECS公网IP "mkdir -p /opt/autoparts-mall/sql"

# 上传所有 SQL 文件（在项目根目录 g:\code\spring\goods 执行）
scp src\main\resources\sql\*.sql root@你的ECS公网IP:/opt/autoparts-mall/sql/
```

### 4.2 按顺序导入 SQL 文件

在 Workbench 终端中执行以下命令（**注意替换密码**）：

```bash
# 设置 MySQL 密码变量（替换成你自己的密码）
MYSQL_PWD="330124"

# 进入 SQL 目录
cd /opt/autoparts-mall/sql

# 按顺序导入（必须按此顺序！）
mysql -u root -p"${MYSQL_PWD}" autoparts < schema.sql
echo "✅ schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < banner_schema.sql
echo "✅ banner_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < coupon_schema.sql
echo "✅ coupon_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < epc_schema.sql
echo "✅ epc_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < extended_schema.sql
echo "✅ extended_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < inventory_schema.sql
echo "✅ inventory_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < membership_schema.sql
echo "✅ membership_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < notification_schema.sql
echo "✅ notification_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < points_schema.sql
echo "✅ points_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < points_mall_schema.sql
echo "✅ points_mall_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < promotion_schema.sql
echo "✅ promotion_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < service_schema.sql
echo "✅ service_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < sku_share_schema.sql
echo "✅ sku_share_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < vehicle_schema.sql
echo "✅ vehicle_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < vin_product_schema.sql
echo "✅ vin_product_schema.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < init_data.sql
echo "✅ init_data.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < security_upgrade.sql
echo "✅ security_upgrade.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < add_order_coupon_fields.sql
echo "✅ add_order_coupon_fields.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < add_vin_compatible.sql
echo "✅ add_vin_compatible.sql 导入完成"

mysql -u root -p"${MYSQL_PWD}" autoparts < alter_orders_add_logistics.sql
echo "✅ alter_orders_add_logistics.sql 导入完成"

# 导入商品数据（如果文件存在）
if [ -f import_product_data.sql ]; then
    mysql -u root -p"${MYSQL_PWD}" autoparts < import_product_data.sql
    echo "✅ import_product_data.sql 导入完成"
fi

if [ -f import_transmission_oil.sql ]; then
    mysql -u root -p"${MYSQL_PWD}" autoparts < import_transmission_oil.sql
    echo "✅ import_transmission_oil.sql 导入完成"
fi

if [ -f import_ztb_data.sql ]; then
    mysql -u root -p"${MYSQL_PWD}" autoparts < import_ztb_data.sql
    echo "✅ import_ztb_data.sql 导入完成"
fi

echo ""
echo "========================================="
echo "  所有 SQL 文件导入完成！"
echo "========================================="
```

### 4.3 验证数据库

```bash
# 登录 MySQL 查看表
mysql -u root -p"${MYSQL_PWD}" autoparts -e "SHOW TABLES;"

# 查看商品数量
mysql -u root -p"${MYSQL_PWD}" autoparts -e "SELECT COUNT(*) AS product_count FROM product;"

# 查看用户
mysql -u root -p"${MYSQL_PWD}" autoparts -e "SELECT id, phone, nickname, role FROM user;"

# 验证管理员账号是否存在（admin / admin123）
mysql -u root -p"${MYSQL_PWD}" autoparts -e "SELECT id, phone, role FROM user WHERE role='ADMIN';"
```

---

## 五、项目构建与上传

### 5.1 本地构建项目

在**本地电脑**的 PowerShell 中执行（项目根目录 `g:\code\spring\goods`）：

```bash
# 进入项目目录
cd g:\code\spring\goods

# 清理并构建（跳过测试，加快构建速度）
mvn clean package -DskipTests

# 构建成功后，查看生成的 JAR 文件
dir target\*.jar
# 应该看到：autoparts-mall-1.0.0.jar
```

### 5.2 上传 JAR 包到服务器

**方式一：使用 SCP 上传（本地 PowerShell）**

```bash
# 上传 JAR 包
scp target\autoparts-mall-1.0.0.jar root@47.108.235.57IP:/opt/autoparts-mall/

# 上传微信支付私钥（如果存在）
scp src\main\resources\wechatpay_private_key.pem root@你的ECS公网IP:/opt/autoparts-mall/
```

**方式二：使用 Workbench 的文件上传功能**

1. 在 Workbench 终端右上角，点击 **"上传文件"** 图标
2. 选择本地 `g:\code\spring\goods\target\autoparts-mall-1.0.0.jar`
3. 上传路径填 `/opt/autoparts-mall/`
4. 点击确认上传

### 5.3 在服务器上验证 JAR 包

```bash
# 查看文件是否上传成功
ls -la /opt/autoparts-mall/

# 期望看到：
# autoparts-mall-1.0.0.jar
# wechatpay_private_key.pem（如果有的话）

# 测试 JAR 是否能正常启动（临时启动，Ctrl+C 停止）
cd /opt/autoparts-mall
java -jar autoparts-mall-1.0.0.jar
# 看到 "Started Application" 字样表示启动成功
# 按 Ctrl+C 停止
```

---

## 六、SSL 证书配置

> **重要**：微信小程序要求所有 API 请求必须使用 HTTPS，所以 SSL 证书是**必须的**。

### 6.1 方式一：阿里云免费 SSL 证书（推荐）

1. 登录 [阿里云控制台](https://home.console.aliyun.com/)
2. 搜索 **"SSL 证书"** → 进入 **"SSL 证书（应用安全）"**
3. 点击 **"免费证书"** → **"立即购买"** → **"立即购买"**（0 元）
4. 购买后回到 SSL 证书页面，点击 **"创建证书"**
5. 填写域名（如 `yaanxinzhuoyue.top`），点击 **"提交审核"**
6. 审核通过后，点击 **"下载"** → 选择 **"Nginx"** 格式
7. 下载得到一个 ZIP 文件，解压后包含两个文件：
   - `xxx.pem`（证书文件）
   - `xxx.key`（私钥文件）

**上传证书到服务器：**

```bash
# 在服务器上创建 SSL 目录
mkdir -p /etc/nginx/ssl

# 使用 Workbench 上传功能，将 .pem 和 .key 文件上传到 /etc/nginx/ssl/
# 或者用 SCP：
# scp xxx.pem root@你的ECS公网IP:/etc/nginx/ssl/yaanxinzhuoyue.top.pem
# scp xxx.key root@你的ECS公网IP:/etc/nginx/ssl/yaanxinzhuoyue.top.key
```

### 6.2 方式二：Let's Encrypt 免费证书

```bash
# 安装 certbot
yum install -y epel-release
yum install -y certbot python3-certbot-nginx

# 确保 Nginx 已启动
systemctl start nginx

# 申请证书（替换成你的域名）
certbot --nginx -d yaanxinzhuoyue.top

# 按提示操作：
# 1. 输入邮箱地址
# 2. 同意服务条款（输入 Y）
# 3. 是否分享邮箱（输入 N）
# 证书会自动安装到 Nginx 配置中

# 设置自动续期
echo "0 3 * * * /usr/bin/certbot renew --quiet && nginx -s reload" | crontab -
```

---

## 七、Nginx 反向代理配置

### 7.1 创建 Nginx 配置文件

```bash
# 创建配置文件
vim /etc/nginx/conf.d/autoparts-mall.conf
```

按 `i` 进入编辑模式，粘贴以下完整配置：

```nginx
# HTTP → HTTPS 自动跳转
server {
    listen 80;
    server_name yaanxinzhuoyue.top;  # 改成你的域名，如果没有域名就填ECS公网IP

    # 如果是用 IP 访问，不需要跳转 HTTPS（因为没有 SSL 证书）
    # 有域名的话取消下面这行的注释：
    # return 301 https://$host$request_uri;
    
    # 没有域名时，直接代理到后端（仅开发测试用）
    location / {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# HTTPS 配置（有 SSL 证书后启用）
server {
    listen 443 ssl http2;
    server_name yaanxinzhuoyue.top;  # 改成你的域名

    # SSL 证书路径（根据实际路径修改）
    ssl_certificate     /etc/nginx/ssl/yaanxinzhuoyue.top.pem;
    ssl_certificate_key /etc/nginx/ssl/yaanxinzhuoyue.top.key;

    # SSL 安全配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 日志
    access_log /var/log/nginx/autoparts-mall.access.log;
    error_log  /var/log/nginx/autoparts-mall.error.log;

    # 上传文件大小限制
    client_max_body_size 10M;

    # 反向代理到 Spring Boot 后端
    location / {
        proxy_pass http://127.0.0.1:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 支持（聊天功能需要）
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # 静态资源缓存
    location ~* \.(jpg|jpeg|png|gif|ico|css|js|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://127.0.0.1:8081;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

按 `Esc` 退出编辑模式，输入 `:wq` 保存退出。

### 7.2 测试并重载 Nginx

```bash
# 测试配置文件语法是否正确
nginx -t
# 期望输出：nginx: configuration file ... test is successful

# 如果测试通过，重载 Nginx
nginx -s reload

# 如果重载失败，查看错误日志
tail -50 /var/log/nginx/error.log
```

---

## 八、环境变量与启动服务

### 8.1 创建环境变量文件

```bash
# 创建环境变量文件
vim /etc/systemd/system/autoparts-mall.env
```

按 `i` 进入编辑模式，粘贴以下内容（**务必修改密码和密钥！**）：

```bash
# ========== 数据库配置 ==========
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=YourNewPassword123!     # ← 改成你的 MySQL root 密码

# ========== Redis 配置 ==========
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=                     # 如果设置了 Redis 密码就填这里

# ========== JWT 密钥（必须改成强密码！）==========
# 生成随机密钥的命令：openssl rand -base64 48
JWT_SECRET=zhantu-prod-jwt-secret-change-me-to-random-string-2024

# ========== 激活生产环境 ==========
SPRING_PROFILES_ACTIVE=prod

# ========== 微信支付配置 ==========
WECHATPAY_MCH_ID=1744677835
WECHATPAY_MERCHANT_SERIAL_NUMBER=7DD6D4F1382C6FAF009DF7BBBCAA0F5A01E8A856
WECHATPAY_API_V3_KEY=79a9a16c19dbc60bfc0ff7bd8775cf20
WECHATPAY_APP_ID=wx15b627fecd6aec92
WECHATPAY_NOTIFY_URL=https://yaanxinzhuoyue.top/api/payment/notify/wechat

# ========== 支付宝配置（如需要）==========
ALIPAY_NOTIFY_URL=https://yaanxinzhuoyue.top/api/payment/notify/alipay
ALIPAY_RETURN_URL=https://yaanxinzhuoyue.top

# ========== 短信配置（如需要）==========
SMS_ENABLED=false

# ========== OSS 对象存储（如需要）==========
OSS_ENABLED=false

# ========== VIN 查询 API ==========
VIN_TANSHU_API_KEY=f25667d22ac7cbda025f9734e38c92fd
```

按 `Esc`，输入 `:wq` 保存退出。

### 8.2 生成安全的 JWT 密钥

```bash
# 生成一个安全的随机密钥
openssl rand -base64 48

# 复制输出的密钥，替换上面 JWT_SECRET 的值
# 然后重新编辑环境变量文件
vim /etc/systemd/system/autoparts-mall.env
# 把 JWT_SECRET=... 那行改成刚才生成的密钥
```

### 8.3 创建 systemd 服务

```bash
# 创建服务文件
vim /etc/systemd/system/autoparts-mall.service
```

按 `i` 进入编辑模式，粘贴以下内容：

```ini
[Unit]
Description=新卓阅汽配商城后端服务
Documentation=https://yaanxinzhuoyue.top
After=network.target mysqld.service redis.service
Wants=mysqld.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/autoparts-mall
EnvironmentFile=/etc/systemd/system/autoparts-mall.env
ExecStart=/usr/bin/java -jar \
    -Xms512m \
    -Xmx1024m \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai \
    /opt/autoparts-mall/autoparts-mall-1.0.0.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=autoparts-mall

# 安全加固
NoNewPrivileges=yes
ProtectSystem=strict
ProtectHome=yes
ReadWritePaths=/opt/autoparts-mall/logs

[Install]
WantedBy=multi-user.target
```

按 `Esc`，输入 `:wq` 保存退出。

### 8.4 启动服务

```bash
# 重载 systemd 配置
systemctl daemon-reload

# 启动服务
systemctl start autoparts-mall

# 设置开机自启
systemctl enable autoparts-mall

# 查看服务状态
systemctl status autoparts-mall
# 看到 active (running) 表示启动成功

# 查看实时日志（按 Ctrl+C 退出）
journalctl -u autoparts-mall -f

# 查看最近 50 行日志
journalctl -u autoparts-mall -n 50 --no-pager
```

### 8.5 常用服务管理命令

```bash
# 启动服务
systemctl start autoparts-mall

# 停止服务
systemctl stop autoparts-mall

# 重启服务
systemctl restart autoparts-mall

# 查看状态
systemctl status autoparts-mall

# 查看实时日志
journalctl -u autoparts-mall -f

# 查看今天的日志
journalctl -u autoparts-mall --since today --no-pager
```

---

## 九、验证部署结果

### 9.1 基础验证

```bash
# 1. 检查所有服务是否正常运行
systemctl status mysqld redis nginx autoparts-mall

# 2. 检查端口监听情况
netstat -tlnp | grep -E "3306|6379|80|443|8081"
# 期望看到：
# 3306 - MySQL
# 6379 - Redis
# 80   - Nginx
# 443  - Nginx (SSL)
# 8081 - Java 后端

# 3. 检查后端健康状态
curl -s http://127.0.0.1:8081/actuator/health
# 期望输出：{"status":"UP"}

# 4. 测试 API 是否正常
curl -s http://127.0.0.1:8081/api/product/page?pageNum=1\&pageSize=1
# 期望输出：包含 code:200 的 JSON 数据

# 5. 测试分类接口
curl -s http://127.0.0.1:8081/api/product/category/tree
# 期望输出：分类树 JSON 数据

# 6. 测试用户登录
curl -s -X POST http://127.0.0.1:8081/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13194717525","password":"123456789"}'
# 期望输出：包含 token 的 JSON 数据

# 7. 测试管理员登录
curl -s -X POST http://127.0.0.1:8081/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# 期望输出：包含 token 的 JSON 数据
```

### 9.2 外网访问验证

在**本地浏览器**中访问以下地址：

| 测试地址 | 期望结果 |
|----------|----------|
| `http://你的ECS公网IP/actuator/health` | 显示 `{"status":"UP"}` |
| `http://你的ECS公网IP/api/product/category/tree` | 显示分类 JSON 数据 |
| `https://你的域名/api/product/category/tree` | 同上（配置 SSL 后） |

### 9.3 查看后端日志排查问题

```bash
# 查看应用日志
tail -100 /opt/autoparts-mall/logs/autoparts-mall.log

# 实时查看日志
tail -f /opt/autoparts-mall/logs/autoparts-mall.log

# 查看 systemd 日志
journalctl -u autoparts-mall -n 100 --no-pager

# 查看 Nginx 错误日志
tail -50 /var/log/nginx/autoparts-mall.error.log
```

---

## 十、小程序端配置

### 10.1 修改 API 地址

在本地编辑 `wxmall/app.js`，将 `baseUrl` 改为你的服务器地址：

```javascript
// 开发环境（本地测试）
baseUrl: 'http://localhost:8081'

// 生产环境（改为你的域名，必须 HTTPS）
baseUrl: 'https://yaanxinzhuoyue.top'

// 如果没有域名，用 IP（仅开发测试，小程序不允许 HTTP IP）
baseUrl: 'http://你的ECS公网IP:8081'
```

### 10.2 微信公众平台配置

登录 [微信公众平台](https://mp.weixin.qq.com) → 开发管理 → 开发设置 → 服务器域名：

| 配置项 | 填写内容 |
|--------|----------|
| **request合法域名** | `https://yaanxinzhuoyue.top` |
| **socket合法域名** | `wss://yaanxinzhuoyue.top` |
| **uploadFile合法域名** | `https://yaanxinzhuoyue.top` |
| **downloadFile合法域名** | `https://yaanxinzhuoyue.top` |

> **注意**：
> - 必须使用 **HTTPS** 域名，不能用 IP 地址
> - 域名必须已完成 ICP 备案
> - 每月只能修改 5 次，请确认无误后再保存

### 10.3 微信支付回调配置

登录 [微信支付商户平台](https://pay.weixin.qq.com) → 产品中心 → 开发配置：

| 配置项 | 填写内容 |
|--------|----------|
| **支付回调URL** | `https://yaanxinzhuoyue.top/api/payment/notify/wechat` |

---

## 十一、常见问题排查

### 问题 1：MySQL 启动失败

```bash
# 查看 MySQL 错误日志
tail -100 /var/log/mysqld.log

# 常见原因：数据目录权限问题
chown -R mysql:mysql /var/lib/mysql

# 重新初始化（会丢失数据！仅紧急情况使用）
mysqld --initialize-insecure --user=mysql
```

### 问题 2：后端启动后立即退出

```bash
# 查看详细错误
journalctl -u autoparts-mall -n 100 --no-pager

# 常见原因：
# 1. 数据库密码错误 → 检查 autoparts-mall.env 中的 DB_PASSWORD
# 2. MySQL 未启动 → systemctl start mysqld
# 3. Redis 未启动 → systemctl start redis
# 4. 端口被占用 → lsof -i :8081
```

### 问题 3：Nginx 502 Bad Gateway

```bash
# 原因：后端服务未启动或已崩溃
systemctl status autoparts-mall

# 如果服务未运行，启动它
systemctl start autoparts-mall

# 查看 Nginx 错误日志
tail -50 /var/log/nginx/autoparts-mall.error.log
```

### 问题 4：小程序提示"不在以下合法域名列表中"

- 确认微信公众平台中配置的域名是 `https://` 开头
- 确认 SSL 证书有效且未过期
- 确认 Nginx 正确代理了 HTTPS 请求
- 清除小程序缓存：微信开发者工具 → 清缓存 → 全部清除

### 问题 5：图片上传失败

- 需要配置阿里云 OSS 对象存储
- 在环境变量中设置 `OSS_ENABLED=true` 及相关 OSS 配置
- 或者使用本地存储（不推荐生产环境）

### 问题 6：如何更新部署新版本

```bash
# 1. 本地重新构建
cd g:\code\spring\goods
mvn clean package -DskipTests

# 2. 上传新 JAR 到服务器
scp target\autoparts-mall-1.0.0.jar root@你的ECS公网IP:/opt/autoparts-mall/

# 3. 重启服务
ssh root@你的ECS公网IP "systemctl restart autoparts-mall"

# 4. 查看启动日志确认成功
ssh root@你的ECS公网IP "journalctl -u autoparts-mall -f"
```

---

## 附录：一键检查脚本

将以下脚本保存到服务器 `/opt/autoparts-mall/check.sh`，用于快速检查所有服务状态：

```bash
#!/bin/bash
echo "========================================="
echo "  新卓阅汽配商城 — 服务状态检查"
echo "  $(date '+%Y-%m-%d %H:%M:%S')"
echo "========================================="
echo ""

# 检查 MySQL
echo "📌 MySQL 状态："
systemctl is-active mysqld && echo "  ✅ MySQL 运行中" || echo "  ❌ MySQL 未运行"

# 检查 Redis
echo "📌 Redis 状态："
systemctl is-active redis && echo "  ✅ Redis 运行中" || echo "  ❌ Redis 未运行"

# 检查 Nginx
echo "📌 Nginx 状态："
systemctl is-active nginx && echo "  ✅ Nginx 运行中" || echo "  ❌ Nginx 未运行"

# 检查后端服务
echo "📌 后端服务状态："
systemctl is-active autoparts-mall && echo "  ✅ 后端服务运行中" || echo "  ❌ 后端服务未运行"

# 检查端口
echo ""
echo "📌 端口监听："
netstat -tlnp 2>/dev/null | grep -E "3306|6379|80|443|8081" || ss -tlnp | grep -E "3306|6379|80|443|8081"

# 检查 API
echo ""
echo "📌 API 健康检查："
HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8081/actuator/health 2>/dev/null)
if [ "$HEALTH" = "200" ]; then
    echo "  ✅ API 返回 200 OK"
else
    echo "  ❌ API 返回 $HEALTH"
fi

# 磁盘空间
echo ""
echo "📌 磁盘空间："
df -h / | tail -1

# 内存使用
echo ""
echo "📌 内存使用："
free -h | grep Mem

echo ""
echo "========================================="
echo "  检查完成"
echo "========================================="
```

使用方法：

```bash
# 赋予执行权限
chmod +x /opt/autoparts-mall/check.sh

# 运行检查
/opt/autoparts-mall/check.sh
```

---

> **文档版本**：v2.0  
> **最后更新**：2026-05-13  
> **适用系统**：CentOS 7.9+ / Alibaba Cloud Linux 2/3  
> **项目**：新卓阅汽配商城（autoparts-mall）