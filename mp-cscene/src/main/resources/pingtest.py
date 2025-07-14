import json
import sys
import paramiko
import re
import time


def check_network_via_ssh(ip, port, username, password, target_ip):
    """
    通过SSH连接到远程主机执行ping测试

    参数:
        ip (str): SSH主机IP
        port (int): SSH端口
        username (str): SSH用户名
        password (str): SSH密码
        target_ip (str): 要ping的目标IP

    返回:
        dict: 包含以下键的字典:
            - connected (bool): 是否可达
            - packet_loss (float): 丢包率(%)
            - delay (float): 平均时延(ms)
            - error (str): 错误信息(成功时为None)
    """
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

    result = {
        'connected': False,
        'packet_loss': 100.0,
        'delay': None,
        'error': None
    }

    try:
        # 连接SSH
        ssh.connect(ip, port, username, password, timeout=10)

        # 执行ping命令 (发送4个ICMP包)
        command = f"ping -c 4 -W 2 {target_ip}"
        stdin, stdout, stderr = ssh.exec_command(command)

        # 等待命令完成并获取输出
        time.sleep(1)
        output = stdout.read().decode().strip()
        error = stderr.read().decode().strip()

        if error:
            raise RuntimeError(f"命令执行错误: {error}")

        # 解析ping结果
        packet_loss_match = re.search(r'(\d+)% packet loss', output)
        rtt_match = re.search(r'rtt min/avg/max/mdev = [\d.]+/([\d.]+)/', output)

        if packet_loss_match:
            result['packet_loss'] = float(packet_loss_match.group(1))
            result['connected'] = result['packet_loss'] < 100

        if rtt_match:
            result['delay'] = float(rtt_match.group(1))

        # 如果无法解析结果但命令成功执行
        if not packet_loss_match:
            if "100% packet loss" in output:
                result['packet_loss'] = 100.0
                result['connected'] = False
            else:
                raise RuntimeError("无法解析ping输出")

    except Exception as e:
        result['error'] = str(e)

    finally:
        ssh.close()

    return result


# 示例用法
if __name__ == "__main__":
    source_ip = sys.argv[1].replace("\"","")
    source_port = sys.argv[2].replace("\"","")
    target_ip = sys.argv[3].replace("\"","")
    username = sys.argv[4].replace("\"","")
    password = sys.argv[5].replace("\"","")
    result = check_network_via_ssh(
        ip=source_ip,
        port=source_port,
        username=username,
        password=password,
        target_ip=target_ip
    )
    print(json.dumps(result))