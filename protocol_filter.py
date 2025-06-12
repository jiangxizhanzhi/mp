import argparse
import os
from datetime import datetime
import pyshark
from scapy.utils import PcapWriter, PcapReader


def write(src_path, protocols):
    protocol_arr = protocols.split(",")
    time = datetime.now().timestamp()
    name, ext = os.path.splitext(src_path)
    # 生成文件地址：使用源文件地址加上时间戳
    dst_path = name + str(int(time)) + ext
    t_read_pcap = pyshark.FileCapture(src_path)
    read_pcap = PcapReader(src_path)
    write_pcap = PcapWriter(dst_path)
    arr = []
    pre_time = datetime.fromtimestamp(0)
    run_time = 0
    result = {}

    for n, pkt in enumerate(t_read_pcap):
        cur_time = datetime.fromtimestamp(pkt.sniff_time.timestamp())
        # 每个包的运行间隔时间
        if n == 0:
            diff_time = 0
        else:
            diff_time = (cur_time - pre_time).total_seconds()

        for protocol in protocol_arr:
            pro = '<' + str(protocol).lower() + ' '
            if pro in repr(pkt.layers).lower():
                arr.append(n)
                run_time += diff_time
                break
        pre_time = cur_time

    result['duration'] = round(run_time, 2)
    result['count'] = len(arr)
    result['dstPath'] = dst_path

    for n, pkt in enumerate(read_pcap):
        if n in arr:
            write_pcap.write(pkt)
    t_read_pcap.close()
    read_pcap.close()
    write_pcap.close()
    file_size = os.path.getsize(dst_path)
    result['fileSize'] = file_size
    print(result)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("src_path", help="source pcap file path")
    parser.add_argument("protocols", help="list of protocols")
    args = parser.parse_args()
    write(args.src_path, args.protocols)

