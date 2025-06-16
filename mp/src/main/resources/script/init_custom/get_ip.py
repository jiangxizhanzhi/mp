import psutil
import socket

def get_ips():
    ip = []
    for interface, addrs in psutil.net_if_addrs().items():
for addr in addrs:
if addr.family == socket.AF_INET:
ip.append(addr.address)
    return ",".join(ip)
if __name__ == "__main__":
    print("ip: " + get_ips())