#!/bin/bash
# tcpreplay 自动安装脚本
# 工作目录: /root
# 日志路径: /root/tcpreplay_install.log

# 初始化日志文件
LOG_FILE="/root/tcpreplay/tcpreplay_install.log"
{
    echo "===== tcpreplay 安装开始 [$(date)] ====="

    # 1. 检查并创建缺失的 dpkg 目录
    echo "[步骤1] 检查 dpkg 目录状态..."
    dpkg_dirs=("/var/lib/dpkg/updates" "/var/lib/dpkg/info")
    for dir in "${dpkg_dirs[@]}"; do
        if [ ! -d "$dir" ]; then
            echo "创建缺失目录: $dir"
            mkdir -p "$dir"
        fi
    done

    if [ ! -f "/var/lib/dpkg/status" ]; then
        echo "创建缺失文件: /var/lib/dpkg/status"
        touch /var/lib/dpkg/status
    fi

    # 2. 解压依赖包
    echo "[步骤2] 解压依赖包 libpcap-debs.tar..."
    cd /root/tcpreplay || { echo "无法进入 /root/tcpreplay 目录"; exit 1; }
    tar -xvf libpcap-debs.tar >> "$LOG_FILE" 2>&1 || { echo "依赖包解压失败"; exit 2; }

    # 3. 安装依赖
    echo "[步骤3] 安装 libpcap 依赖..."
    dpkg -i /root/tcpreplay/libpcap-debs/*.deb >> "$LOG_FILE" 2>&1 || { echo "依赖安装失败"; exit 3; }

    # 4. 解压 tcpreplay
    echo "[步骤4] 解压 tcpreplay-4.4.4.tar.xz..."
    tar -xvf tcpreplay-4.4.4.tar.xz >> "$LOG_FILE" 2>&1 || { echo "源码解压失败"; exit 4; }

    # 5-8. 编译安装
    echo "[步骤5] 编译安装 tcpreplay..."
    cd tcpreplay-4.4.4/ || { echo "源码目录不存在"; exit 5; }
    ./configure >> "$LOG_FILE" 2>&1 || { echo "configure 失败"; exit 6; }
    make >> "$LOG_FILE" 2>&1 || { echo "make 编译失败"; exit 7; }
    sudo make install >> "$LOG_FILE" 2>&1 || { echo "make install 失败"; exit 8; }

    # 9. 验证安装
    echo "[步骤9] 验证安装..."
    tcpreplay -V 2>> "$LOG_FILE"
    if [ $? -eq 0 ]; then
        echo "tcpreplay 安装成功！版本信息: $(tcpreplay -V 2>&1 | head -n1)"
    else
        echo "安装验证失败！"
        exit 9
    fi

    echo "===== 安装完成 [$(date)] ====="
} | tee -a "$LOG_FILE" 2>&1

exit $?