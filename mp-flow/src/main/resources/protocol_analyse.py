import argparse

import pyshark


def read(pcap_path):
    # pcap_path = "/Users/qkjoe/Downloads/trdp.pcap"
    exclude_layers = ['png', 'data', 'image-jfif', 'media', '_ws.malformed', 'data-text-lines', 'urlencoded-form', 'image-gif']
    protocol = {}
    total = 0

    caps = pyshark.FileCapture(pcap_path)
    for n, pkt in enumerate(caps):
        layer_name = pkt.layers[-1].layer_name
        if str(layer_name).lower() in exclude_layers:
            layer_name = pkt.layers[-2].layer_name

        if layer_name in protocol:
            protocol.update({layer_name: protocol.get(layer_name) + 1})
        else:
            protocol.update({layer_name: 1})
        total += 1

    for k, v in protocol.items():
        value = round(v / total * 100, 2)
        if value == 0:
            value = 0.01
        protocol.update({k: value})
    protocol = sorted(protocol.items(), key=lambda x: x[1], reverse=True)
    if len(protocol) > 10:
        i = 0
        max_protocol = {}
        other_protocol = 'other'
        for k, v in protocol:
            if i < 10:
                max_protocol.update({k: v})
            else:
                if other_protocol in max_protocol:
                    max_protocol.update({other_protocol: round(max_protocol.get(other_protocol) + v, 2)})
                else:
                    max_protocol.update({other_protocol: v})
            i += 1
        protocol = sorted(max_protocol.items(), key=lambda x: x[1], reverse=True)

    result = {}
    for k, v in protocol:
        result[k] = v
    value_sum = sum(result.values())
    # 计算百分比和是否为100
    if round(value_sum, 2) != 100:
        last_key = list(result.keys())[len(result) - 1]
        last_value = list(result.values())[len(result) - 1]
        tmp_last_value = round(100 - value_sum + last_value, 2)
        if tmp_last_value == 0:
            first_key = list(result.keys())[0]
            first_value = round(100 - value_sum + list(result.values())[0], 2)
            result.update({first_key: first_value})
        else:
            result.update({last_key: tmp_last_value})

    print(result)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("src_path", help="source pcap file path")
    args = parser.parse_args()
    read(args.src_path)
