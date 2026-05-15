@echo off
REM 战途汽配商城数据库备份脚本
REM Windows版本

set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=%DB_PASSWORD%
set DB_NAME=autoparts
set BACKUP_DIR=.\backups
set DATE=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set DATE=%DATE: =0%

if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

echo 开始备份数据库 %DB_NAME%...
mysqldump -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% --single-transaction --routines --triggers --events %DB_NAME% > %BACKUP_DIR%\%DB_NAME%_%DATE%.sql

if %errorlevel% equ 0 (
    echo 备份成功！文件: %BACKUP_DIR%\%DB_NAME%_%DATE%.sql
) else (
    echo 备份失败！
)

REM 保留最近30天的备份
forfiles /p %BACKUP_DIR% /s /m *.sql /d -30 /c "cmd /c del @path" 2>nul

pause
