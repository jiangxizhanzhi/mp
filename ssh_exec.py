import argparse
import paramiko
import sys
import base64
import json


def ssh_execute_command(ip, port, username, password, cmd):
    # 创建SSH客户端实例
    ssh = paramiko.SSHClient()

    try:
        # 自动添加主机密钥（生产环境应考虑更安全的方式）
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # 建立SSH连接
        ssh.connect(ip, port=int(port), username=username, password=password)

        # 构建后台执行命令（使用nohup确保进程不会随SSH断开而终止）
        # 将标准输出和错误输出重定向到/dev/null（可根据需要修改）
        background_cmd = f"nohup {cmd} > /dev/null 2>&1 &"

        # 执行命令
        stdin, stdout, stderr = ssh.exec_command(background_cmd)

        # 获取命令输出（主要是后台命令的启动信息）
        output = stdout.read().decode()
        error = stderr.read().decode()
        print(output)

    except Exception as e:
        print(f"SSH connection failed: {str(e)}", file=sys.stderr)
        sys.exit(1)
    finally:
        # 确保关闭连接
        ssh.close()


if __name__ == "__main__":
    # 设置命令行参数解析
    input_str = sys.argv[1]
    decoded_data = base64.b64decode(input_str)
    decoded_data = decoded_data.decode('utf-8')
    args = json.loads(decoded_data)

    # 执行SSH命令
    ssh_execute_command(args['ip'], args['port'], args['username'], args['password'], args['cmd'])