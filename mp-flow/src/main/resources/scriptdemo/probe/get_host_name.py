import socket

def get_host_name():
    host_name = socket.gethostname()
    host_info = "hostName:%s" % host_name
    print(host_info)

if __name__ == '__main__':
    get_host_name()
