import re
import json
import sys
import base64

class NotSupportDockerHostConfigException(Exception):
    """异常类用于表示不支持的 Docker 主机配置"""
    def __init__(self, message="不支持的 Docker 主机配置"):
        self.message = message
        super().__init__(self.message)

def _(message):
    return message

# 拷贝云平台的 apps/device/utils/docker_metadata.py 开始 ---------------------------------

def parse_device(x):
    if x and x != '/dev/net/tun':
        raise NotSupportDockerHostConfigException(message=_("--device only support /dev/net/tun"))
    return x


def parse_device_rate(x):
    path, rate = x.split(":")
    return {"Path": path, "Rate": int(rate)}


def parse_device_request(request_str):
    parts = request_str.strip().split(',')
    result = {}
    for part in parts:
        key, value = part.split('=')
        result[key.strip()] = value.strip()
    return result

SUPPORT_DOCKER_HOST_CONFIG_EX = {
    'cap-add': {"rename": "cap_add", "type": list},
    'cap-drop':  {"rename": "cap_drop", "type": list},
    'device': {"rename": "devices", "type": list, 'parse_func': parse_device},
    'device-read-bps': {"rename": "device_read_bps", "type": list, 'parse_func': parse_device_rate},
    'device-write-bps': {"rename": "device_write_bps", "type": list, 'parse_func': parse_device_rate},
    'device-read-iops': {"rename": "device_read_iops", "type": list, 'parse_func': parse_device_rate},
    'device-write-iops': {"rename": "device_write_iops", "type": list, 'parse_func': parse_device_rate},
    'blkio-weight-device': {"rename": "blkio_weight_device", "type": list, 'parse_func': parse_device_rate},
    'blkio-weight': {"rename": "blkio_weight", "type": int},
    'device-cgroup-rule': {"rename": "device_cgroup_rule", "type": list}, # --device-cgroup-rule 'c 195:0 rwm'
	# 'device-request': {"rename": "device_requests", "type": list, 'parse_func': parse_device_request},
    # 'userns_mode': {"value": 'host'},
    # 'uts_mode': {"value": 'host'},
}


class DockerMetaDataHandler(object):

    host_config_ex_match_patten = re.compile('(--[a-z\-]+\s+.*?\s+)+')
    host_config_ex_findall_patten = re.compile('--([a-z\-]+)\s+(.*?)\s+')

    @classmethod
    def _valid_environment_str(cls, environment_str):
        environment = {}
        if environment_str:
            environment_str = environment_str.strip()
            env_list = environment_str.split(';')
            error_message = _('Not Support Docker Environment {}').format(environment_str)
            for item in env_list:
                try:
                    k, v = item.split('=')
                    k, v = k.strip(), v.strip()
                    if k in environment:
                        raise NotSupportDockerHostConfigException(message=_("Exist Duplicate Environment Item"))
                    environment[k] = v
                except Exception:
                    raise NotSupportDockerHostConfigException(message=error_message)
        return environment

    @classmethod
    def _valid_host_config_ex(cls, host_config_ex_str):
        """校验容器靶标扩展的自定义参数"""
        host_config_ex = {}
        if host_config_ex_str not in (None, ''):
            if '=' in host_config_ex_str:
                raise NotSupportDockerHostConfigException(message=_('Not Support Docker Host Config Ex ='))
            host_config_ex_str += ' ' # 加空格解析正则
            host_config_ex_str = host_config_ex_str.lstrip()
            if not cls.host_config_ex_match_patten.match(host_config_ex_str):
                error_msg = _('Not Support Docker Host Config Ex {}').format(host_config_ex_str)
                raise NotSupportDockerHostConfigException(message=error_msg)

            results = cls.host_config_ex_findall_patten.findall(host_config_ex_str)
            for result in results:
                key, value = result
                if key not in SUPPORT_DOCKER_HOST_CONFIG_EX:
                    error_msg = _('Not Support Docker Host Config Ex {}').format(key)
                    raise NotSupportDockerHostConfigException(message=error_msg)
                key_type = SUPPORT_DOCKER_HOST_CONFIG_EX[key]["type"]
                parse_func = SUPPORT_DOCKER_HOST_CONFIG_EX[key].get('parse_func')
                key = SUPPORT_DOCKER_HOST_CONFIG_EX[key]["rename"]
                if key_type != list:
                    if key in host_config_ex:
                        error_msg = _('Not Support Docker Host Config Duplicate Key {}').format(key)
                        raise NotSupportDockerHostConfigException(message=error_msg)
                    host_config_ex[key] = key_type(value)
                else:
                    value_list = host_config_ex.setdefault(key, [])
                    if value in value_list:
                        error_msg = _('Not Support Docker Host Config Duplicate Value {}').format(value)
                        raise NotSupportDockerHostConfigException(message=error_msg)
                    if parse_func:
                        value = parse_func(value)
                    if value:
                        value_list.append(value)

        return host_config_ex

    @classmethod
    def parse_metadata(cls, meta_data):
        host_config_ex_str = meta_data.get('host_config_ex_str')
        host_config_ex = cls._valid_host_config_ex(host_config_ex_str)
        if host_config_ex:
            meta_data['host_config_ex'] = host_config_ex

        environment_str = meta_data.get('environment_str')
        environment = cls._valid_environment_str(environment_str)
        meta_data['environment'] = environment

        return  json.dumps(meta_data)

    @classmethod
    def metadata_from_yml_dict(cls, yml_dict):
        meta_data = {}
        interactive = yml_dict.get('interactive')
        if interactive:
            meta_data['interactive'] = interactive
        tty  = yml_dict.get('tty')
        if tty:
            meta_data['tty'] = tty
        environment_str = yml_dict.get('environment_str')
        if environment_str:
            meta_data['environment_str'] = environment_str
            meta_data['environment'] = cls._valid_environment_str(environment_str)
        host_config_ex_str = yml_dict.get('host_config_ex_str')
        if host_config_ex_str:
            meta_data['host_config_ex_str'] = host_config_ex_str
            meta_data['host_config_ex'] = cls._valid_host_config_ex(host_config_ex_str)
        return meta_data

# 拷贝云平台的 apps/device/utils/docker_metadata.py 结束 ---------------------------------

if __name__ == '__main__':
    input_str = sys.argv[1]
    decoded_data = base64.b64decode(input_str)
    decoded_data = decoded_data.decode('utf-8')
    splitstr = decoded_data.split("split_str_flag")
    environment_str = splitstr[0]
    host_config_ex_str = splitstr[1]
    if environment_str == 'empty':
        environment_str=""
    if host_config_ex_str == 'empty':
        host_config_ex_str=""
    meta_data = {
        'environment_str': environment_str,
        "host_config_ex_str": host_config_ex_str
    }
    try:
        m = DockerMetaDataHandler.parse_metadata(meta_data)
        print(m)
    except NotSupportDockerHostConfigException as e:
        print(e.message)

