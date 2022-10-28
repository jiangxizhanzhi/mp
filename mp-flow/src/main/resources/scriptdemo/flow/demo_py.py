# -*- coding: utf-8 -*-
import os
import sys

def execute_command(command):
    out = os.system(command)
    return out


def main():
    command = "ping $IP -c 100"
    return execute_command(command)


if __name__ == '__main__':
    main()
