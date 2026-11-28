#!/usr/bin/env python3
"""单兵训练批量启动及管理网络验收（BC/MP；仅授权测试账号）。"""
import argparse
import concurrent.futures as cf
from http.client import HTTPException
import ipaddress
import json
import os
import re
import shutil
import subprocess
import sys
import time
from pathlib import Path
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode, urlsplit
from urllib.request import HTTPRedirectHandler, ProxyHandler, Request, build_opener
from xml.etree import ElementTree as ET

SCENE_RUNNING = {2, 7, 10, 13}  # scene-websocket.js
NODE_RUNNING = {2, 3, 5, 7, 10, 11, 13}

# 来源：diagnostics/all_databases.sql（2026-11-26）的组织及用户关系。
# 两次备份中的同名账号 ID 有变化，因此固定用户名，运行时按组织解析当前 ID。
PRESET_ORG_ID = 'a3ff7af0ec1e429c99f7517ee7e41a91'
PRESET_ORG_NAME = '2026届研究生'
PRESET_USERNAMES = (
    'caoran', 'caoyuchen', 'dongyingchao', 'douhancong', 'fuzehui', 'gaozihan',
    'guanjiale', 'heyichao', 'huapengbo', 'jiangchunyi', 'lihan', 'lijiayao',
    'liru', 'liucheng', 'liuxinyu', 'lvranran', 'puyabo', 'qinxinwen',
    'shenziqi', 'shijunfei', 'shiwanhong', 'songchenyu', 'sunsai', 'taotianran',
    'wangwei', 'wangxin', 'wangxuedong', 'weimingyuan', 'weiruotong', 'wuyanpeng',
    'xieyunxiang', 'yuanzengyong', 'zhangyixiang', 'zhaoyuxin', 'zhenglin', 'zhouzixin',
)
DEFAULT_TEST_PASSWORD = 'Axctfoj01'
# 与前端 @cyber/helper 的 encryptPsw 默认参数一致，可由现场环境覆盖。
DEFAULT_LOGIN_DES_KEY = 'B13H089Y'


class CheckError(ValueError):
    """可安全显示的本脚本预检错误。"""


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        return None


def placeholders(values):
    return ','.join(['%s'] * len(values))


def scene_state(event, status):
    if status == 5:
        return 'FAILED'
    if status == 4:
        if event in (0, 1):
            # SceneService.create: 初始化结束后才进入创建，不能把中间状态当成失败。
            return 'STARTING'
        return 'SUCCESS' if event in SCENE_RUNNING else 'FAILED'
    return 'STARTING'


def positive(text):
    value = int(text)
    if value <= 0:
        raise argparse.ArgumentTypeError('必须为正整数')
    return value


def account_count(text):
    value = int(text)
    if not 1 <= value <= len(PRESET_USERNAMES):
        raise argparse.ArgumentTypeError('账号数量必须为 1 到 36 的整数')
    return value


MYSQL_NUMERIC_FIELDS = {'n', 'event', 'event_status', 'stage_percentage', 'create_consume_seconds',
                        'deleted', 'vendor', 'port_order'}


def mysql_sql(sql, params):
    # 与项目现有免密 mysql 脚本一致：所有外部值按 UTF-8 HEX 绑定，不拼接原始字符串。
    parts = sql.split('%s')
    if len(parts) != len(params) + 1:
        raise CheckError('SQL 参数数量不一致')
    return parts[0] + ''.join("CONVERT(X'" + str(value).encode('utf-8').hex() + "' USING utf8mb4)" + part
                              for value, part in zip(params, parts[1:]))


def mysql_rows(output):
    try:
        root = ET.fromstring(output)
        if root.tag != 'resultset':
            raise ValueError()
        rows = []
        for row in root.findall('row'):
            result = {}
            for field in row.findall('field'):
                name = field.attrib['name']
                value = None if field.attrib.get('{http://www.w3.org/2001/XMLSchema-instance}nil') == 'true' else (field.text or '')
                result[name] = int(value) if value is not None and name in MYSQL_NUMERIC_FIELDS else value
            rows.append(result)
        return rows
    except (ET.ParseError, KeyError, ValueError) as exc:
        raise CheckError('mysql XML 查询结果格式异常') from exc


class Platform:
    def __init__(self, args):
        self.args = args
        # 现场内网直接访问平台，不继承终端代理；禁止自动重定向。
        self.http = build_opener(ProxyHandler({}), NoRedirect())
        self.mysql_bin = os.environ.get('MYSQL_BIN', 'mysql')
        if not shutil.which(self.mysql_bin):
            raise CheckError('当前执行环境未找到 mysql；请在 T4 现场可免密登录 MariaDB 的服务器运行')

    def query(self, sql, params=()):
        command = [self.mysql_bin, '--xml', '--skip-force', '--skip-reconnect',
                   '--default-character-set=utf8mb4', '--connect-timeout=10']
        request = 'SET SESSION TRANSACTION READ ONLY;\n' + mysql_sql(sql, params) + ';\n'
        try:
            proc = subprocess.run(command, input=request.encode('utf-8'), capture_output=True,
                                  timeout=30, check=True)
        except (OSError, subprocess.SubprocessError) as exc:
            raise CheckError('mysql 只读查询失败；请检查现场免密连接与查询权限') from exc
        return mysql_rows(proc.stdout)

    def template(self, name):
        rows = self.query('''SELECT resource_id, topo_id FROM bc.cr_business_template
                             WHERE business_type=1 AND BINARY name=BINARY %s''', (name,))
        if len(rows) != 1:
            raise CheckError(f'模板精确匹配数量为 {len(rows)}，要求恰好一条')
        return rows[0]

    def expected_vms(self, topo_id):
        rows = self.query('''SELECT COUNT(*) AS n
                              FROM mp.mp_sceneplugincloud_scene_config_element
                              WHERE scene=%s AND virtual_type=4''', (topo_id,))
        return rows[0]['n']

    def preset_accounts(self, count):
        usernames = PRESET_USERNAMES[:count]
        rows = self.query(f'''SELECT u.id,u.username FROM mp.mp_auth_user u
            WHERE u.username IN ({placeholders(usernames)})
              AND EXISTS (SELECT 1 FROM mp.mp_auth_tenant_user t
                          JOIN mp.mp_auth_org o ON o.id=t.org AND o.tenant=t.tenant
                          WHERE t.user=u.id AND t.tenant=u.tenant
                            AND o.id=%s AND BINARY o.name=BINARY %s)''',
            (*usernames, PRESET_ORG_ID, PRESET_ORG_NAME))
        by_name = {row['username']: row for row in rows}
        if (len(rows) != len(usernames) or set(by_name) != set(usernames)
                or len({row['id'] for row in rows}) != len(usernames)):
            raise CheckError(f'预置账号在“{PRESET_ORG_NAME}”中匹配不完整或不唯一，请核对用户名和组织归属')
        return [by_name[name] for name in usernames]

    def candidates(self, template_id, ids):
        return self.query(f'''SELECT u.id,u.username FROM mp.mp_auth_user u
            WHERE u.id IN ({placeholders(ids)}) AND u.is_enabled=1 AND u.is_audited=1
              AND u.last_login IS NOT NULL
              AND LOWER(u.username) <> 'admin'
              AND NOT EXISTS (SELECT 1 FROM bc.cr_single_room r
                              WHERE r.business_template_id=%s AND r.user_id=u.id)
            ORDER BY u.last_login DESC, u.id ASC''', (*ids, template_id))

    def api(self, token, method, path, success_code, *, params=None, json_data=None):
        # 不重发 POST；禁止重定向，避免将认证信息传给其他地址。
        try:
            url = self.args.base_url.rstrip('/') + path
            if params:
                url += '?' + urlencode(params, doseq=True)
            headers = {'Accept': 'application/json'}
            if token:
                headers['Authorization'] = 'JWT ' + token
            data = None
            if json_data is not None:
                data = json.dumps(json_data, ensure_ascii=False).encode('utf-8')
                headers['Content-Type'] = 'application/json; charset=utf-8'
            request = Request(url, data=data, headers=headers, method=method)
            with self.http.open(request, timeout=self.args.http_timeout) as response:
                if not 200 <= response.status < 300:
                    return False, f'HTTP_{response.status}', None
                body = json.loads(response.read())
            if not isinstance(body, dict) or body.get('code') != success_code:
                code = body.get('code') if isinstance(body, dict) else None
                safe_code = code if isinstance(code, str) and re.fullmatch(r'[A-Z0-9-]{1,40}', code) else 'API_ERROR'
                return False, safe_code, None
            return True, 'OK', body.get('data')
        except HTTPError as exc:
            code = exc.code
            exc.close()
            return False, f'HTTP_{code}', None
        except (URLError, HTTPException, OSError, ValueError):
            return False, 'REQUEST_OR_RESPONSE_ERROR', None

    def login(self, row, password):
        # 使用经正常客户端编码的已知测试账号密码；不读数据库密码摘要、不构造 JWT。
        ok, code, data = self.api(None, 'POST', '/api/mp/auth/login', 'MP-000000',
                                  json_data={'username': row['username'], 'password': password})
        if not ok:
            raise CheckError(f'正常登录失败（{code}）；本批尚未启动训练，请核对网关、账号认证或验证码')
        if not isinstance(data, dict):
            return None
        token = data.get('token')
        return token if isinstance(token, str) and token else None

    def verify(self, user_id, token, template_id):
        ok, _, data = self.api(token, 'GET', '/api/mp/auth/users/current', 'MP-000000')
        if not ok or not isinstance(data, dict) or data.get('id') != user_id:
            return False
        ok, _, data = self.api(token, 'GET', '/api/ojj/cr/single_train/room', 'OJ-000000',
                               params={'resource_id': template_id})
        return ok and isinstance(data, dict) and data.get('resource_id') == template_id and data.get('room_info') is None

    def start(self, token, template_id):
        ok, code, _ = self.api(token, 'POST', '/api/ojj/cr/single_train/room', 'OJ-000000',
                               json_data={'resource_id': template_id})
        return 'ACCEPTED' if ok else code

    def scenes(self, template_id, user_ids):
        return self.query(f'''SELECT r.user_id,r.resource_id AS room_id,
            l.resource_id AS local_scene_id,l.mp_scene_id AS scene_id,s.event,s.event_status,
            s.stage_percentage,s.stage_name,s.create_consume_seconds,s.deleted
            FROM bc.cr_single_room r
            LEFT JOIN bc.local_scene l ON l.resource_id=r.local_scene_id AND l.user_id=r.user_id
            LEFT JOIN mp.mp_scene_scene s ON s.id=l.mp_scene_id
            WHERE r.business_template_id=%s AND r.user_id IN ({placeholders(user_ids)})''',
            (template_id, *user_ids))

    def vms(self, scene_ids):
        return self.query(f'''SELECT c.scene AS scene_id,c.scene_element AS node_id,
            e.name,e.event,e.event_status,e.vendor,c.resource_id AS vm_id
            FROM mp.mp_sceneplugincloud_scene_element c
            LEFT JOIN mp.mp_scene_scene_element e ON e.id=c.scene_element AND e.scene=c.scene
            WHERE c.virtual_type=4 AND c.scene IN ({placeholders(scene_ids)})
            ORDER BY c.scene,c.scene_element''', tuple(scene_ids))

    def external_network(self):
        rows = self.query('''SELECT n.id,s.cidr FROM neutron.networks n
            JOIN neutron.externalnetworks ext ON ext.network_id=n.id
            JOIN neutron.subnets s ON s.network_id=n.id
            WHERE n.name=%s AND s.ip_version=4''', (self.args.external_network,))
        if len(rows) != 1:
            raise CheckError('OpenStack 外部网络 IPv4 子网匹配数量不唯一')
        return rows[0]['id'], ipaddress.ip_network(rows[0]['cidr'])

    def addresses(self, inventory, external_id, local_network, vendor_networks):
        # CPCloud 本地 VM 用 Neutron 的实际固定 IP；MP 端口资源关联也覆盖非 Nova VM。
        local_ids = [r['vm_id'] for r in inventory if r['vendor'] == 0 and r['vm_id']]
        scene_ids = sorted({r['scene_id'] for r in inventory})
        local_rows = []
        if local_ids:
            local_rows = self.query(f'''SELECT p.device_id AS vm_id,p.id AS port_id,a.ip_address
                FROM neutron.ports p JOIN neutron.ipallocations a ON a.port_id=p.id AND a.network_id=p.network_id
                WHERE p.network_id=%s AND p.device_id IN ({placeholders(local_ids)})''',
                (external_id, *local_ids))
        mapped_rows = self.query(f'''SELECT e.scene AS scene_id,e.scene_element AS node_id,
            p.id AS port_id,a.ip_address FROM mp.mp_scene_scene_element_port e
            JOIN mp.mp_sceneplugincloud_scene_element_port c ON c.scene=e.scene AND c.scene_element_port=e.id
            JOIN neutron.ports p ON p.id=c.resource_id AND p.network_id=%s
            JOIN neutron.ipallocations a ON a.port_id=p.id AND a.network_id=p.network_id
            WHERE e.scene IN ({placeholders(scene_ids)})''', (external_id, *scene_ids)) if scene_ids else []
        vendor_ports = self.query(f'''SELECT e.scene AS scene_id,e.scene_element AS node_id,
            e.id AS port_id,e.ip,e.mac_addr,e.`order` AS port_order
            FROM mp.mp_scene_scene_element_port e
            JOIN mp.mp_scene_scene_element n ON n.scene=e.scene AND n.id=e.scene_element
            WHERE n.vendor IN (1,2,3) AND e.scene IN ({placeholders(scene_ids)})
            ORDER BY e.scene,e.scene_element,e.`order`,e.id''', tuple(scene_ids)) if scene_ids else []
        for row in inventory:
            row['management_ip'] = None
            row['address_source'] = None
            row['address_error'] = 'EXTERNAL_ADDRESS_MISSING'
            key = (row['scene_id'], row['node_id'])
            if row['vendor'] == 0:
                candidates = {(r['port_id'], r['ip_address']) for r in local_rows if r['vm_id'] == row['vm_id']}
                candidates.update((r['port_id'], r['ip_address']) for r in mapped_rows
                                  if (r['scene_id'], r['node_id']) == key)
                matches = [(pid, ip) for pid, ip in candidates if in_network(ip, local_network)]
                if len(matches) == 1:
                    row.update(management_ip=matches[0][1], address_source='NEUTRON_EXTERNAL_FIXED_IP', address_error=None)
                elif len(matches) > 1:
                    row['address_error'] = 'MULTIPLE_EXTERNAL_IPS'
            elif row['vendor'] in vendor_networks:
                # CreateHandler 把厂商创建时的网络地址写回第一个实例端口。
                ports = [r for r in vendor_ports if (r['scene_id'], r['node_id']) == key]
                if ports:
                    first = ports[0]
                    if len(ports) > 1 and ports[1]['port_order'] == first['port_order']:
                        row['address_error'] = 'AMBIGUOUS_VENDOR_PORT'
                    elif in_network(first['ip'], vendor_networks[row['vendor']]) and first['mac_addr']:
                        row.update(management_ip=first['ip'], address_source='MP_VENDOR_INSTANCE_PORT', address_error=None)
                    else:
                        row['address_error'] = 'VENDOR_ADDRESS_UNCONFIRMED_OR_OUTSIDE_NETWORK'
            else:
                row['address_error'] = 'VENDOR_NETWORK_NOT_CONFIGURED'
        return inventory

    def close(self):
        pass


def encode_login_passwords(values, password_format):
    if password_format == 'client':
        if any(not re.fullmatch(r'(?:[0-9a-fA-F]{16})+', value) for value in values.values()):
            raise CheckError('客户端编码密码格式异常')
        return values
    # 使用平台正常前端登录协议，不读取后端密钥或数据库摘要。
    key = os.environ.get('TRAIN_LOGIN_DES_KEY', DEFAULT_LOGIN_DES_KEY).encode('utf-8')
    if len(key) < 8:
        raise CheckError('TRAIN_LOGIN_DES_KEY 配置必须至少为 8 个 UTF-8 字节')
    openssl_bin = os.environ.get('OPENSSL_BIN', 'openssl')
    if not shutil.which(openssl_bin):
        raise CheckError('未找到 openssl；原始密码编码需要系统已有的 OpenSSL，可用 OPENSSL_BIN 指定路径')
    # 重复 8 字节密钥的 3DES 等价于既有 DES/ECB/PKCS5Padding 登录编码。
    # 使用系统 OpenSSL 的 3DES，兼容默认不启用单 DES 的 OpenSSL 3。
    command = [openssl_bin, 'enc', '-des-ede3-ecb', '-nosalt', '-K', (key[:8] * 3).hex()]
    encoded = {}
    for password in values.values():
        if password in encoded:
            continue
        data = password.encode('utf-8')
        try:
            proc = subprocess.run(command, input=data, capture_output=True, timeout=10, check=True)
        except (OSError, subprocess.SubprocessError) as exc:
            raise CheckError('openssl 登录密码编码失败；请核对系统 OpenSSL 是否支持 des-ede3-ecb') from exc
        if len(proc.stdout) != (len(data) // 8 + 1) * 8:
            raise CheckError('openssl 登录密码编码结果长度异常')
        encoded[password] = proc.stdout.hex()
    return {uid: encoded[password] for uid, password in values.items()}


def shared_login_password(password=None):
    value = password if password is not None else os.environ.get('TRAIN_TEST_PASSWORD', DEFAULT_TEST_PASSWORD)
    if not value:
        raise CheckError('统一登录密码不能为空；请用 --password 或 TRAIN_TEST_PASSWORD 指定')
    return value


def read_auth_values(path, shared_password_mode=False, execute=False, password=None):
    try:
        data = json.loads(Path(path).read_text(encoding='utf-8'))
    except FileNotFoundError as exc:
        raise CheckError(f'未找到授权测试账号清单 {path}；请准备清单或用 --accounts 指定') from exc
    except (OSError, json.JSONDecodeError) as exc:
        raise CheckError(f'无法读取账号清单 {path}，请核对权限和 JSON 格式') from exc
    if shared_password_mode and isinstance(data, list):
        if not data or any(not isinstance(uid, str) or not uid.strip() for uid in data) or len(set(data)) != len(data):
            raise CheckError('测试账号 ID 列表必须为非空且无重复的字符串数组')
        value = shared_login_password(password) if execute else ''
        return {uid: value for uid in data}
    if not isinstance(data, dict) or not data:
        raise CheckError('账号配置必须为非空 JSON 对象: 测试账号 ID -> 认证信息环境变量名')
    sessions = {}
    for user_id, env_name in data.items():
        if not user_id or not isinstance(env_name, str) or not re.fullmatch(r'[A-Za-z_][A-Za-z0-9_]*', env_name):
            raise CheckError('账号配置中存在无效项')
        value = password if shared_password_mode and password is not None else os.environ.get(env_name, '')
        if not value and (not shared_password_mode or execute):
            raise CheckError(f'缺少测试账号认证信息环境变量 {env_name}')
        sessions[user_id] = value
    return sessions


def load_auth_values(args, platform):
    login_mode = args.sessions is None
    if args.preset_accounts:
        rows = platform.preset_accounts(args.count)
        password = shared_login_password(args.password) if args.execute else ''
        values = {row['id']: password for row in rows}
    else:
        values = read_auth_values(args.accounts or args.sessions, login_mode, args.execute, args.password)
    if login_mode and args.execute:
        values = encode_login_passwords(values, args.login_password_format)
    return values


def in_network(value, network):
    try:
        address = ipaddress.ip_address(value)
        return (address in network and not address.is_unspecified and not address.is_multicast
                and not address.is_loopback and address != network.network_address
                and address != network.broadcast_address)
    except ValueError:
        return False


def probe_vm(row, args):
    result = dict(row)
    result['mp_node_ready'] = (row['event_status'] == 4 and row['event'] in NODE_RUNNING and bool(row['vm_id']))
    result['network'] = 'NOT_CHECKED'
    result['success'] = False
    if row['address_error'] or not row['management_ip']:
        result['network'] = row['address_error'] or 'EXTERNAL_ADDRESS_MISSING'
        return result
    address = ipaddress.ip_address(row['management_ip'])
    begin = time.monotonic()
    try:
        proc = subprocess.run(['ping', '-n', '-c', str(args.ping_count), '-W', str(args.ping_timeout), str(address)],
                              capture_output=True, timeout=args.ping_count * (args.ping_timeout + 1) + 3)
        result['network'] = {0: 'REACHABLE', 1: 'NO_ICMP_REPLY'}.get(proc.returncode, 'PROBE_ERROR')
    except (OSError, subprocess.TimeoutExpired):
        result['network'] = 'PROBE_ERROR'
    result['probe_seconds'] = round(time.monotonic() - begin, 3)
    result['success'] = result['mp_node_ready'] and result['network'] == 'REACHABLE'
    return result


def execute(platform, sessions, args, vendor_networks, login_mode=False):
    template = platform.template(args.template_name)
    tid = template['resource_id']
    expected = platform.expected_vms(template['topo_id'])
    if expected <= 0:
        raise CheckError('模板中没有虚拟机类型节点，无法执行本次验收')
    external_id, local_network = platform.external_network()
    selected = []
    candidates = platform.candidates(tid, list(sessions))
    if getattr(args, 'preset_accounts', False):
        order = {uid: i for i, uid in enumerate(sessions)}
        candidates.sort(key=lambda row: order[row['id']])
    # 密码登录会更新 last_login，先锁定本批账号及顺序；默认预检不触发登录。
    if login_mode:
        chosen = candidates[:args.count]
        if len(chosen) != args.count:
            raise CheckError(f'满足数据库条件的测试账号只有 {len(chosen)} 个，需要 {args.count} 个；'
                             '请核对启用、审核、登录记录及是否已有同模板房间')
        if args.execute:
            tokens = {}
            for row in chosen:
                uid = row['id']
                token = platform.login(row, sessions[uid])
                if not token or not platform.verify(uid, token, tid):
                    raise CheckError('选定测试账号正常登录或认证预检失败；本批尚未启动训练，请核对账号密码、验证码和权限')
                tokens[uid] = token
            sessions = tokens
        selected = [row['id'] for row in chosen]
    else:
        for row in candidates:
            uid = row['id']
            if platform.verify(uid, sessions[uid], tid):
                selected.append(uid)
                if len(selected) == args.count:
                    break
        if len(selected) != args.count:
            raise CheckError(f'无已有房间且通过认证预检的测试账号只有 {len(selected)} 个，需要 {args.count} 个')
    report = {'template_name': args.template_name, 'template_id': tid, 'requested': args.count,
              'expected_vms_per_scene': expected, 'external_network': str(local_network), 'instances': []}
    usernames = {row['id']: row['username'] for row in candidates}
    records = {uid: {'index': i + 1, 'account_id': uid, 'username': usernames[uid],
                     'startup': 'STARTING' if args.execute else 'NOT_STARTED', 'nodes': []}
               for i, uid in enumerate(selected)}
    report['instances'] = list(records.values())
    print(f'预检通过: {args.count} 个账号, 每个拓扑预期 {expected} 台虚拟机'
          + ('（仅数据库条件，尚未登录）' if login_mode and not args.execute else ''), file=sys.stderr)
    if not args.execute:
        report['result'] = 'PREFLIGHT_ONLY'
        return summarize(report)
    try:
        return run_batch(platform, sessions, args, vendor_networks, tid, expected, external_id,
                         local_network, selected, records, report)
    except Exception as exc:
        for record in records.values():
            if record['startup'] == 'STARTING':
                record['startup'] = 'UNKNOWN'
            record.setdefault('network_check', 'NOT_COMPLETED')
        report.update(result='EXECUTION_ERROR', error_type=type(exc).__name__)
        if isinstance(exc, CheckError):
            report['error_detail'] = str(exc)
        print(f'监控或验收中断: {type(exc).__name__}；保留已知实例记录，不自动重新启动', file=sys.stderr)
        return summarize(report)


def run_batch(platform, sessions, args, vendor_networks, tid, expected, external_id,
              local_network, selected, records, report):
    begin = time.monotonic()
    with cf.ThreadPoolExecutor(max_workers=args.workers) as pool:
        futures = {pool.submit(platform.start, sessions[uid], tid): uid for uid in selected}
        outstanding = set(futures)
        try:
            while True:
                for future in list(outstanding):
                    if future.done():
                        records[futures[future]]['submission'] = future.result()
                        outstanding.remove(future)
                rows = platform.scenes(tid, selected)
                grouped = {}
                for row in rows:
                    grouped.setdefault(row['user_id'], []).append(row)
                for uid, record in records.items():
                    if record['startup'] != 'STARTING':
                        continue
                    matches = grouped.get(uid, [])
                    if len(matches) > 1:
                        record.update(startup='FAILED', reason='DUPLICATE_ROOM')
                    elif not matches:
                        if record.get('room_id'):
                            record.update(startup='FAILED', reason='ROOM_DISAPPEARED')
                    else:
                        row = matches[0]
                        if record.get('room_id') and record['room_id'] != row['room_id']:
                            record.update(startup='FAILED', reason='ROOM_CHANGED')
                            continue
                        if record.get('scene_id') and record['scene_id'] != row['scene_id']:
                            record.update(startup='FAILED', reason='SCENE_CHANGED')
                            continue
                        record.update({k: v for k, v in row.items() if k != 'user_id'})
                        if row['event'] is not None and row['event_status'] is not None:
                            record['startup'] = 'FAILED' if row['deleted'] else scene_state(row['event'], row['event_status'])
                    if record['startup'] != 'STARTING':
                        record['startup_seconds'] = round(time.monotonic() - begin, 2)
                pending = sum(r['startup'] == 'STARTING' for r in records.values())
                print(f'启动监控: 成功 {sum(r["startup"] == "SUCCESS" for r in records.values())}, '
                      f'失败 {sum(r["startup"] == "FAILED" for r in records.values())}, 等待 {pending}', file=sys.stderr)
                if not pending and not outstanding:
                    break
                if time.monotonic() - begin >= args.startup_timeout:
                    for future in outstanding:
                        future.cancel()
                    for record in records.values():
                        if record['startup'] == 'STARTING':
                            record.update(startup='TIMEOUT', startup_seconds=round(time.monotonic()-begin, 2))
                    break
                time.sleep(min(args.poll_interval, max(0, args.startup_timeout - (time.monotonic()-begin))))
        except Exception:
            for future in outstanding:
                future.cancel()
            raise
        finally:
            for future in cf.as_completed(futures):
                records[futures[future]]['submission'] = 'NOT_SENT' if future.cancelled() else future.result()
    if any(r['startup'] != 'SUCCESS' for r in records.values()):
        for record in records.values():
            record['network_check'] = 'SKIPPED_STARTUP_GATE'
        report['result'] = 'STARTUP_FAILED'
        return summarize(report)
    scene_ids = [r['scene_id'] for r in records.values()]
    if not all(scene_ids) or len(set(scene_ids)) != len(scene_ids):
        report['result'] = 'SCENE_ID_INCONSISTENT'
        return summarize(report)
    # 通过全局屏障前再核对一次，避免早已完成的实例后来被关闭/替换仍进入探测。
    by_user = {}
    for row in platform.scenes(tid, selected):
        by_user.setdefault(row['user_id'], []).append(row)
    for uid, record in records.items():
        matches = by_user.get(uid, [])
        if (len(matches) != 1 or matches[0]['room_id'] != record['room_id']
                or matches[0]['scene_id'] != record['scene_id'] or matches[0]['deleted']
                or scene_state(matches[0]['event'], matches[0]['event_status']) != 'SUCCESS'):
            record.update(startup='FAILED', reason='SCENE_NOT_READY_BEFORE_NETWORK')
    if any(r['startup'] != 'SUCCESS' for r in records.values()):
        for record in records.values():
            record['network_check'] = 'SKIPPED_STARTUP_GATE'
        report['result'] = 'STARTUP_FAILED'
        return summarize(report)
    inventory = platform.addresses(platform.vms(scene_ids), external_id, local_network, vendor_networks)
    by_scene = {r['scene_id']: r for r in records.values()}
    ips = {}
    for row in inventory:
        try:
            ip = str(ipaddress.ip_address(row['management_ip']))
        except ValueError:
            continue
        ips[ip] = ips.get(ip, 0) + 1
    # 所有已确认地址先探测一轮；只对暂时无 ICMP 应答的同一已分配地址进行限时复查。
    # 按轮次并发，避免一个迟迟未就绪的厂商节点占住工作线程数分钟。
    candidates = []
    for row in inventory:
        try:
            duplicate = ips.get(str(ipaddress.ip_address(row['management_ip'])), 0) > 1
        except ValueError:
            duplicate = False
        if duplicate:
            by_scene[row['scene_id']]['nodes'].append(dict(row, success=False,
                network='DUPLICATE_MANAGEMENT_IP', network_attempts=0))
        else:
            candidates.append(row)
    probe_start = time.monotonic()
    pending = list(range(len(candidates)))
    results = {}
    attempts = {i: 0 for i in pending}
    with cf.ThreadPoolExecutor(max_workers=args.workers) as pool:
        while pending:
            jobs = {pool.submit(probe_vm, candidates[i], args): i for i in pending}
            retry = []
            for job in cf.as_completed(jobs):
                i = jobs[job]
                result = job.result()
                if result['network'] in ('REACHABLE', 'NO_ICMP_REPLY', 'PROBE_ERROR'):
                    attempts[i] += 1
                result['network_attempts'] = attempts[i]
                result['network_elapsed_seconds'] = round(time.monotonic() - probe_start, 2)
                results[i] = result
                if result['network'] == 'NO_ICMP_REPLY':
                    retry.append(i)
            if not retry:
                break
            remaining = args.network_timeout - (time.monotonic() - probe_start)
            print(f'网络就绪监控: 已应答 {sum(r["network"] == "REACHABLE" for r in results.values())}, '
                  f'等待应答 {len(retry)}, 剩余等待 {max(0, remaining):.0f} 秒', file=sys.stderr)
            if remaining <= 0:
                break
            time.sleep(min(args.network_poll_interval, remaining))
            if time.monotonic() - probe_start >= args.network_timeout:
                break
            pending = retry
    for i, result in results.items():
        if result['network'] == 'NO_ICMP_REPLY':
            result['network_timeout'] = True
        by_scene[candidates[i]['scene_id']]['nodes'].append(result)
    for record in records.values():
        nodes = record['nodes']
        nodes.sort(key=lambda n: n['node_id'])
        unique_nodes = len({n['node_id'] for n in nodes})
        record['network_check'] = ('SUCCESS' if len(nodes) == expected == unique_nodes and all(n['success'] for n in nodes)
                                   else 'FAILED')
    report['result'] = 'SUCCESS' if all(r['network_check'] == 'SUCCESS' for r in records.values()) else 'NETWORK_FAILED'
    return summarize(report)


def summarize(report):
    instances = report['instances']
    nodes = [node for instance in instances for node in instance['nodes']]
    report['summary'] = {
        'startup_success': sum(r['startup'] == 'SUCCESS' for r in instances),
        'startup_failed': sum(r['startup'] == 'FAILED' for r in instances),
        'startup_timeout': sum(r['startup'] == 'TIMEOUT' for r in instances),
        'startup_unknown': sum(r['startup'] == 'UNKNOWN' for r in instances),
        'startup_not_started': sum(r['startup'] == 'NOT_STARTED' for r in instances),
        'scene_network_success': sum(r.get('network_check') == 'SUCCESS' for r in instances),
        'scene_network_failed': sum(r.get('network_check') == 'FAILED' for r in instances),
        'scene_network_not_completed': sum(r.get('network_check') not in ('SUCCESS','FAILED') for r in instances),
        'expected_vm_count': report['requested'] * report['expected_vms_per_scene'],
        'observed_vm_count': len(nodes),
        'vm_network_reachable': sum(n['network'] == 'REACHABLE' for n in nodes),
        'vm_success': sum(n['success'] for n in nodes),
    }
    return report


def parse_args(argv=None):
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument('--template-name', required=True)
    p.add_argument('--base-url', default=os.environ.get('TRAIN_BASE_URL', 'http://127.0.0.1'),
                   help='正常平台网关 URL（默认 http://127.0.0.1，或 TRAIN_BASE_URL）')
    auth = p.add_mutually_exclusive_group()
    auth.add_argument('--sessions', help='会话 JSON 文件路径（授权测试账号 ID -> JWT 环境变量名）')
    auth.add_argument('--accounts', help='自定义账号 JSON 文件路径（账号 ID 数组或密码环境变量映射）；默认使用内置的 36 个“2026届研究生”账号，TRAIN_ACCOUNTS_FILE 可覆盖')
    p.add_argument('--password', help='统一登录密码；优先于 TRAIN_TEST_PASSWORD，默认使用内置密码；仅用于密码登录模式')
    p.add_argument('--login-password-format', choices=('plain','client'), default='plain',
                   help='密码登录格式；plain 为已知原始密码，client 为正常客户端已编码的密码')
    p.add_argument('--external-network', default='oj_ext', help='Neutron 外部网络名称（现场历史为 oj_ext）')
    p.add_argument('--vendor-cidr', action='append', default=[], metavar='VENDOR=CIDR',
                   help='覆盖厂商创建网络网段；默认 1/3 为 192.168.141.0/24，2 为 192.168.140.0/24（历史现场）')
    p.add_argument('--count', type=account_count, default=36,
                   help='账号数量，1 到 36，默认 36；内置模式使用固定顺序的前 N 个账号')
    p.add_argument('--workers', type=positive, default=12)
    p.add_argument('--startup-timeout', type=positive, default=1800)
    p.add_argument('--poll-interval', type=positive, default=5)
    p.add_argument('--http-timeout', type=positive, default=30)
    p.add_argument('--ping-count', type=positive, default=3)
    p.add_argument('--ping-timeout', type=positive, default=2)
    p.add_argument('--network-timeout', type=positive, default=180, help='全部拓扑启动成功后的网络就绪等待窗口（秒）')
    p.add_argument('--network-poll-interval', type=positive, default=5, help='未应答地址的 ICMP 复查间隔（秒）')
    p.add_argument('--execute', action='store_true', help='实际启动训练；默认仅预检')
    args = p.parse_args(argv)
    if args.accounts is None and args.sessions is None:
        args.accounts = os.environ.get('TRAIN_ACCOUNTS_FILE') or None
    args.preset_accounts = args.accounts is None and args.sessions is None
    if args.sessions is not None and args.password is not None:
        p.error('--password 仅用于密码登录模式，不能与 --sessions 同时使用')
    if args.password == '':
        p.error('--password 不能为空')
    return args


def main(argv=None):
    args = parse_args(argv)
    platform = None
    try:
        url = urlsplit(args.base_url)
        if url.scheme not in ('http', 'https') or not url.hostname or url.username or url.password or url.query or url.fragment:
            raise CheckError('base-url 必须为正常平台 HTTP(S) 网关地址')
        vendor_networks = {1: ipaddress.ip_network('192.168.141.0/24'),
                           2: ipaddress.ip_network('192.168.140.0/24'),
                           3: ipaddress.ip_network('192.168.141.0/24')}
        for override in args.vendor_cidr:
            vendor, cidr = override.split('=', 1)
            vendor_networks[int(vendor)] = ipaddress.ip_network(cidr, strict=True)
        if args.execute and not shutil.which('ping'):
            raise CheckError('未找到 ping 命令')
        platform = Platform(args)
        auth_values = load_auth_values(args, platform)
        result = execute(platform, auth_values, args, vendor_networks, login_mode=args.sessions is None)
        print(json.dumps(result, ensure_ascii=False, indent=2, default=str))
        if result['result'] == 'EXECUTION_ERROR':
            return 2
        return 0 if result['result'] in ('SUCCESS', 'PREFLIGHT_ONLY') else 1
    except CheckError as exc:
        print(f'配置或预检失败: {exc}', file=sys.stderr)
        return 2
    except (ValueError, KeyError, ImportError) as exc:
        print(f'配置或预检失败: {type(exc).__name__}', file=sys.stderr)
        # 仅显示自身明确产生的校验错误，避免第三方异常打印敏感信息。
        return 2
    except Exception as exc:
        print(f'执行中断: {type(exc).__name__}；请按模板核对现场状态，勿自动重发启动请求', file=sys.stderr)
        return 2
    finally:
        if platform:
            platform.close()


if __name__ == '__main__':
    sys.exit(main())
