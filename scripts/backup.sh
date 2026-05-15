#!/bin/bash
# 战途汽配商城数据库备份脚本
# Linux版本

DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-3306}
DB_USER=${DB_USER:-root}
DB_PASSWORD=${DB_PASSWORD}
DB_NAME=${DB_NAME:-autoparts}
BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

echo "开始备份数据库 $DB_NAME..."
mysqldump -h$DB_HOST -P$DB_PORT -u$DB_USER -p$DB_PASSWORD --single-transaction --routines --triggers --events $DB_NAME > $BACKUP_DIR/${DB_NAME}_${DATE}.sql

if [ $? -eq 0 ]; then
    echo "备份成功！文件: $BACKUP_DIR/${DB_NAME}_${DATE}.sql"
    # 压缩备份文件
    gzip $BACKUP_DIR/${DB_NAME}_${DATE}.sql
    echo "压缩完成: $BACKUP_DIR/${DB_NAME}_${DATE}.sql.gz"
else
    echo "备份失败！"
    exit 1
fi

# 保留最近30天的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "备份任务完成"
