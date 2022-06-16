import socket

"""
python运行环境:
    python版本2.7.18

第三方包:
    chardet-4.0.0
    psutil-5.9.1
    idna-2.10
    certifi-2021.10.8
    urllib3-1.26.9
    requests-2.27.1
    pywin32-228（windows环境特有）
"""
def get_host_name():
    host_name = socket.gethostname()
    host_info = "hostName:%s" % host_name
    print(host_info)

if __name__ == '__main__':
    get_host_name()
