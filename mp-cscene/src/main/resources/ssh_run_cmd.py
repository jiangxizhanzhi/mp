import json
import sys
import time

import paramiko
import re

result = {
    "success": True,
    "data": "",
    "message": ""
}


def success_result(data):
    return {
        "success": True,
        "data": data
    }


def error_result(message):
    return {
        "success": False,
        "message": message
    }


def execute_ssh_command(hostname, port, username, password, command):
    try:
        # 创建 SSH 客户端
        client = paramiko.SSHClient()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # 连接到服务器
        client.connect(hostname, port=port, username=username, password=password)

        # 执行命令
        stdin, stdout, stderr = client.exec_command(command)

        # 获取命令输出
        output = stdout.read().decode()
        error = stderr.read().decode()

        # 打印输出
        if output:
            return success_result(output)
        if error:
            return error_result(error)

    except Exception as e:
        return error_result(f"发生错误: {e}")
    finally:
        # 关闭连接
        client.close()


def clean_ansi_escape(text):
    """清理 ANSI 转义控制字符"""
    ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
    return ansi_escape.sub('', text)


def parse_result(data):
    # 提取 Load Average
    load_match = re.search(r'load averages:\s+([\d.]+)', data)
    load_average = float(load_match.group(1)) if load_match else None

    # 提取 CPU 核心数（假设为 1 或通过其他方式获取）
    cpu_count = 1  # 可以通过 os.cpu_count() 进行动态获取

    # 估算 CPU 使用率
    cpu_usage = (load_average / cpu_count * 100) if load_average is not None else None

    # 提取内存使用率
    mem_match = re.search(r'Mem:\s+\d+M Active,\s+\d+M Inact,\s+(\d+)M Wired,\s+(\d+)M Free', data)
    if mem_match:
        wired_memory = int(mem_match.group(1))
        free_memory = int(mem_match.group(2))
        total_memory = wired_memory + free_memory  # 假设 Wired + Free = 总内存
        mem_usage = (wired_memory / total_memory) * 100 if total_memory > 0 else None
    else:
        mem_usage = None

    return cpu_usage, mem_usage


hostname = sys.argv[1]
port = sys.argv[2]
username = sys.argv[3]
password = sys.argv[4]
cpu_command = "vmstat 1 2| tail -n 1 | awk '{print 100 * ($14 + $15) / ($14 + $15 + $16)}'"
ram_command = "vmstat 1 2 | tail -n 1 | awk '{print $4/($4+$5)}'"
disk_command = "df / | awk 'NR==2 {print $2 \"_\" $3}'"

cpu_result = execute_ssh_command(hostname, port, username, password, cpu_command)
cpu_percent = cpu_result['data'].replace("\n","")
ram_result = execute_ssh_command(hostname, port, username, password, ram_command)
ram_percent = ram_result['data'].replace("\n","")
disk_result = execute_ssh_command(hostname, port, username, password, disk_command)
disk_str = disk_result['data'].replace("\n","")
disk_arr = disk_str.split("_")
dis_all = disk_arr[0]
disk_used = disk_arr[1]
res = {
    "cpu": float(cpu_percent),
    "ram": float(ram_percent)*100,
    "disk_all": float(dis_all),
    "disk_used": float(disk_used)
}
print(json.dumps(res))
