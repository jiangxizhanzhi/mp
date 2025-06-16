import socket

"""
python environment:
    python version: 2.7.18

depedencies:
    chardet-4.0.0
    psutil-5.9.1
    idna-2.10
    certifi-2021.10.8
    urllib3-1.26.9
    requests-2.27.1
    pywin32-228（for windows）
"""
def get_host_name():
    host_name = socket.gethostname()
    host_info = "hostName:%s" % host_name
    print(host_info)

if __name__ == '__main__':
    get_host_name()
