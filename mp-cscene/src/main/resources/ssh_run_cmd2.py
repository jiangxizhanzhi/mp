import paramiko
import sys


def download_file_via_ssh(hostname, port, username, password, remote_path, local_path):
    try:
        # 创建 SSH 客户端
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # 连接到 SSH 服务器
        ssh.connect(hostname, port, username, password)

        # 创建 SFTP 会话
        sftp = ssh.open_sftp()

        # 下载文件
        sftp.get(remote_path, local_path)
        print(f"成功下载文件: {remote_path} 到 {local_path}")

        # 关闭 SFTP 会话
        sftp.close()
        ssh.close()

    except Exception as e:
        print(f"发生错误: {e}")


hostname = sys.argv[1]
port = sys.argv[2]
username = sys.argv[3]
password = sys.argv[4]
remote_path = sys.argv[5]
local_path = sys.argv[6]
# 使用示例
download_file_via_ssh(
    hostname=hostname,
    port=port,
    username=username,
    password=password,
    remote_path=remote_path,
    local_path=local_path
)
