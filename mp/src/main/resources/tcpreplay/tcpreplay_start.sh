#!/bin/bash
# 脚本名称: tcpreplay_by_ip.sh
# 功能: 根据目标IP网段自动选择网卡回放pcap文件
# 参数: <pcap文件路径> <目标IP> <UUID>
# 日志: 所有输出和错误记录到当前目录的<uuid>_log.log文件

# 检查参数数量
if [ $# -ne 3 ]; then
    echo "错误：参数错误！用法: $0 <pcap文件路径> <目标IP> <UUID>" | tee -a log.log
    exit 1
fi

PCAP_URL=$1
TARGET_IP=$2
UUID=$3
LOG_FILE="$(pwd)/${UUID}_log.log"
PCAP_FILE="$(pwd)/${UUID}.pcap"

# 记录开始时间
echo "===== 脚本执行开始: $(date) =====" >> "$LOG_FILE"
{
    if [ ! -f "$PCAP_FILE" ]; then
        # 下载pcap文件
        echo "开始下载pcap文件: $PCAP_URL"
        curl --digest  -u admin:admin "$PCAP_URL" -o "$PCAP_FILE"
        if [ $? -ne 0 ];then
            echo "错误：pcap文件下载失败！"
            exit 2
        fi
    fi

    # 提取目标IP的前三段（如192.168.1）
    IP_PREFIX=$(echo "$TARGET_IP" | awk -F '.' '{printf "%s.%s.%s", $1, $2, $3}')
    if [ -z "$IP_PREFIX" ]; then
        echo "错误：无法解析目标IP格式：$TARGET_IP"
        exit 2
    fi

    # 通过ifconfig过滤匹配IP前缀的网卡，取第一个结果
    NIC_NAME=$(ifconfig | grep -B1 -E "inet $IP_PREFIX\." | awk -F ': ' 'NR==1 {print $1}')

    # 验证网卡是否存在
    if [ -z "$NIC_NAME" ]; then
        echo "错误：未找到与IP段 $IP_PREFIX.* 匹配的网卡"
        exit 3
    fi
    echo "匹配网卡: $NIC_NAME"

    # 执行tcpreplay命令
    echo "开始回放: tcpreplay -i $NIC_NAME $PCAP_FILE"
    sudo tcpreplay -i "$NIC_NAME" "$PCAP_FILE"

    # 检查tcpreplay执行结果
    if [ $? -ne 0 ]; then
        echo "错误：tcpreplay执行失败！"
        exit 4
    fi
} | tee -a "$LOG_FILE" 2>&1  # 同时输出到屏幕和日志

# 记录结束状态
EXIT_CODE=$?
echo "===== 脚本执行结束: $(date) [状态码: $EXIT_CODE] =====" >> "$LOG_FILE"
exit $EXIT_CODE