# -*- coding: utf-8 -*-
import os
import sys

def execute_command(command):
    out = os.system(command)
    return out


def main(ip=None):
    command = "ping %s -c 100" % ip
    return execute_command(command)


if __name__ == '__main__':
    # 这里IP修改为要攻击的IP地址，持续时间默认100s
    main(sys.argv[0])
