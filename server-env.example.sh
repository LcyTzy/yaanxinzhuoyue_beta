# ============================================
# 新卓阅汽配 - 服务器环境变量配置（示例）
# 复制此文件为 server-env.sh 并填入真实值
# 上传到服务器 /etc/profile.d/autoparts.sh
# 然后执行: source /etc/profile
# ============================================

# --- 运行环境 ---
export SPRING_PROFILES_ACTIVE=prod

# --- 数据库配置 ---
export DB_HOST="localhost"
export DB_PORT="3306"
export DB_USERNAME="root"
export DB_PASSWORD="请填写你的数据库密码"

# --- Redis配置 ---
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export REDIS_PASSWORD=""

# --- JWT密钥 ---
# 在服务器上执行: openssl rand -base64 32
# 把生成的字符串替换下面这行
export JWT_SECRET="请执行openssl rand -base64 32生成随机密钥"

# --- 阿里云OSS配置（图片上传）---
export OSS_ENABLED="true"
export OSS_ENDPOINT="oss-cn-chengdu.aliyuncs.com"
export OSS_ACCESS_KEY_ID="请填写你的AccessKey ID"
export OSS_ACCESS_KEY_SECRET="请填写你的AccessKey Secret"
export OSS_BUCKET_NAME="autoparts-mall-images"
export OSS_URL_PREFIX="https://autoparts-mall-images.oss-cn-chengdu.aliyuncs.com"

# --- 短信配置（可选）---
export SMS_ENABLED="false"
export SMS_ACCESS_KEY_ID=""
export SMS_ACCESS_KEY_SECRET=""
export SMS_SIGN_NAME="新卓阅汽车服务"
export SMS_TEMPLATE_CODE="SMS_506235013"

# --- 微信支付配置 ---
export WECHATPAY_MCH_ID="请填写商户号"
export WECHATPAY_MERCHANT_SERIAL_NUMBER="请填写商户证书序列号"
export WECHATPAY_API_V3_KEY="请填写APIv3密钥"
export WECHATPAY_APP_ID="请填写小程序AppID"
export WECHATPAY_NOTIFY_URL="https://你的域名/api/payment/notify/wechat"

# --- 支付宝配置 ---
export ALIPAY_APP_ID="请填写支付宝AppID"
export ALIPAY_MERCHANT_PRIVATE_KEY="请填写商户私钥"
export ALIPAY_ALIPAY_PUBLIC_KEY="请填写支付宝公钥"
export ALIPAY_NOTIFY_URL="https://你的域名/api/payment/notify/alipay"
export ALIPAY_RETURN_URL="https://你的域名"
export ALIPAY_SIGN_TYPE="RSA2"
export ALIPAY_GATEWAY_URL="https://openapi.alipay.com/gateway.do"

# --- 快递100配置 ---
export KUAIDI100_KEY="请填写快递100 Key"
export KUAIDI100_CUSTOMER="请填写快递100 Customer"
export KUAIDI100_QUERY_URL="https://poll.kuaidi100.com/poll/query.do"

# --- VIN解析API配置 ---
export VIN_TANSHU_API_KEY="请填写探舒VIN API Key"