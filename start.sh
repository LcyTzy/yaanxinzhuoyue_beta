#!/bin/bash
# ============================================
# 新卓阅汽配 - 后端启动脚本
# 上传到服务器 /opt/autoparts-mall/start.sh
# 然后执行: chmod +x /opt/autoparts-mall/start.sh
# ============================================

cd /opt/autoparts-mall

echo ">>> 停止旧服务..."
pkill -f autoparts-mall 2>/dev/null
sleep 2

echo ">>> 启动后端服务..."
nohup java -jar autoparts-mall-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &

echo ">>> 等待服务启动..."
sleep 8

if pgrep -f autoparts-mall > /dev/null; then
    echo ">>> 后端启动成功！"
    tail -20 app.log
else
    echo ">>> 启动失败，查看日志："
    tail -30 app.log
fi