# -*- coding: utf-8 -*-
# 代码中的参数名，需要和可配置参数列表的参数名字保持一致，前面加上$符号
import os
import sys

def execute_command(command):
    out = os.system(command)
    return out


def main():
    command = "ping $IP -c 100"
    return execute_command(command)


if __name__ == '__main__':
    # 这里IP修改为要攻击的IP地址，持续时间默认100s
    main()
