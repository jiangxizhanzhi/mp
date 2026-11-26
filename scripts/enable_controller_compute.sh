#!/usr/bin/env bash
# controller01 / Ubuntu 20.04 / site OpenStack systemd deployment.
# The embedded Python uses only the standard library (Python >= 3.7).
set -euo pipefail
exec python3 - "$@" <<'PY'
import argparse
import configparser
import fcntl
import ipaddress
import json
import os
import re
import shlex
import shutil
import socket
import stat
import subprocess
import sys
import tempfile
import time
import traceback
from decimal import Decimal, ROUND_CEILING, ROUND_FLOOR
from pathlib import Path

HOST = 'controller01'
OTHER = 'compute01'
UNIT = 'nova-compute.service'
CONF = Path('/etc/nova/nova.conf')
GIB = 1024 ** 3
RC = {0: 'VCPU', 1: 'MEMORY_MB', 2: 'DISK_GB'}
EXPECTED_MAPPINGS = {'ex_provider': 'br-ex', 'vlanprovider': 'br-vlan'}
KEYS = ('reserved_host_cpus', 'reserved_host_memory_mb', 'reserved_host_disk_mb',
        'cpu_allocation_ratio', 'ram_allocation_ratio', 'disk_allocation_ratio')
ENV = dict(os.environ, LC_ALL='C')


def fail(message):
    raise RuntimeError(message)


def safe_text(value):
    if isinstance(value, bytes):
        value = value.decode('utf-8', errors='replace')
    value = str(value)
    for key in ('OS_PASSWORD', 'OS_TOKEN', 'OS_AUTH_TOKEN',
                'OS_APPLICATION_CREDENTIAL_SECRET', 'MYSQL_PWD'):
        secret = os.environ.get(key)
        if secret:
            value = value.replace(secret, '[REDACTED]')
    value = re.sub(r'([A-Za-z][A-Za-z0-9+.-]*://)[^/\s@]+@',
                   r'\1[REDACTED]@', value)
    return re.sub(r'(?i)(\b(?:password|token|secret)\s*=\s*)[^\s,;]+',
                  r'\1[REDACTED]', value)


def report_failure(exc):
    print(f'  异常类型：{type(exc).__name__}', file=sys.stderr)
    if isinstance(exc, configparser.Error):
        # ConfigParser exceptions can include a complete sensitive config line.
        location = getattr(exc, 'source', str(CONF))
        lines = [n for n, _ in getattr(exc, 'errors', [])]
        if getattr(exc, 'lineno', None):
            lines.append(exc.lineno)
        print(f'  Nova 配置解析失败：文件={location}，行号={lines or "未知"}'
              '（不输出可能包含凭据的原始配置行）', file=sys.stderr)
    else:
        print('  详细原因：' + safe_text(exc), file=sys.stderr)
    if isinstance(exc, (subprocess.CalledProcessError, subprocess.TimeoutExpired)):
        command = exc.cmd
        if isinstance(command, (list, tuple)):
            command = ' '.join(shlex.quote(str(x)) for x in command)
        print('  执行命令：' + safe_text(command), file=sys.stderr)
        if isinstance(exc, subprocess.CalledProcessError):
            print(f'  退出码：{exc.returncode}', file=sys.stderr)
        else:
            print(f'  超时时间：{exc.timeout} 秒', file=sys.stderr)
        for label, output in (('stdout', exc.stdout), ('stderr', exc.stderr)):
            print(f'  {label}:\n{safe_text(output) if output else "(无输出)"}', file=sys.stderr)
    elif isinstance(exc, OSError):
        print(f'  errno：{exc.errno}，文件：{safe_text(exc.filename or "(无)")}', file=sys.stderr)
    frames = traceback.extract_tb(exc.__traceback__)
    if frames:
        print('  脚本定位：' + ' -> '.join(f'{f.name}:{f.lineno}' for f in frames),
              file=sys.stderr, flush=True)


def run(*argv, timeout=60, check=True):
    result = subprocess.run(list(map(str, argv)), stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE, text=True, timeout=timeout,
                            env=ENV, check=check)
    if result.stderr:
        print(safe_text(result.stderr), end='', file=sys.stderr, flush=True)
    return result.stdout.strip()


def osc(*argv):
    client = os.environ.get('OPENSTACK_BIN') or shutil.which('openstack')
    if not client:
        client = '/opt/openstack_project/bin/openstack'
    return run(client, *argv)


def query(sql):
    # No database mutations, credentials in code, or direct updates to Nova state.
    result = run(os.environ.get('MYSQL_BIN', 'mysql'), '--batch', '--raw',
                 '--skip-column-names', '--skip-reconnect',
                 '--execute', 'START TRANSACTION READ ONLY; ' + sql + '; COMMIT')
    return [line.split('\t') for line in result.splitlines()] if result else []


def service():
    rows = json.loads(osc('compute', 'service', 'list', '--host', HOST,
                          '--service', 'nova-compute', '-f', 'json'))
    if len(rows) != 1 or rows[0]['Host'] != HOST or rows[0]['Binary'] != 'nova-compute':
        fail(f'controller01 未唯一注册 nova-compute，实际返回：{rows!r}')
    return rows[0]


def disable():
    osc('compute', 'service', 'set', '--disable', '--disable-reason',
        'controller01 service resource protection', HOST, 'nova-compute')
    if service()['Status'] != 'disabled':
        fail('无法确认 controller01 已关闭调度')
    print('controller01 调度已关闭（不停止现有虚拟机）', flush=True)


def inventory():
    rows = query("""
        SELECT i.resource_class_id,i.total,i.reserved,i.allocation_ratio,COALESCE(a.used,0)
        FROM placement.inventories i
        JOIN placement.resource_providers p ON p.id=i.resource_provider_id
        LEFT JOIN (
            SELECT resource_provider_id,resource_class_id,SUM(used) AS used
            FROM placement.allocations GROUP BY resource_provider_id,resource_class_id
        ) a ON a.resource_provider_id=i.resource_provider_id
            AND a.resource_class_id=i.resource_class_id
        WHERE p.name='controller01' AND p.root_provider_id=p.id
            AND p.parent_provider_id IS NULL
    """)
    if len(rows) != 3 or {int(r[0]) for r in rows} != set(RC):
        fail(f'Placement 资源类型与历史现场不一致，需检查计算节点配置；实际 inventory={rows!r}')
    return {RC[int(rc)]: dict(total=int(total), reserved=int(reserved),
                              ratio=Decimal(ratio), used=int(used))
            for rc, total, reserved, ratio, used in rows}


def disabled_trait():
    rows = query("""
        SELECT COUNT(*) FROM placement.resource_provider_traits rpt
        JOIN placement.resource_providers p ON p.id=rpt.resource_provider_id
        JOIN placement.traits t ON t.id=rpt.trait_id
        WHERE p.name='controller01' AND t.name='COMPUTE_STATUS_DISABLED'
    """)
    if rows not in ([['0']], [['1']]):
        fail('Placement COMPUTE_STATUS_DISABLED 状态异常')
    return rows == [['1']]


def registered():
    rows = query("""
        SELECT COUNT(*) FROM nova_api.host_mappings m
        JOIN nova.compute_nodes n ON m.host=n.host
        WHERE m.host='controller01' AND n.hypervisor_hostname='controller01'
            AND n.deleted=0 AND n.mapped=1
    """)
    if rows != [['1']]:
        fail(f'controller01 Nova Cell/计算节点映射异常，期望 1 条，实际={rows!r}')
    return int(query("SELECT COUNT(*) FROM nova.instances WHERE host='controller01' AND deleted=0")[0][0])


def read_conf(path):
    cfg = configparser.ConfigParser(interpolation=None, strict=False)
    with path.open(encoding='utf-8') as f:
        cfg.read_file(f)
    return cfg


def instance_path(cfg):
    if cfg['DEFAULT'].get('compute_driver', '').strip() != 'libvirt.LibvirtDriver':
        fail('当前 compute_driver 与历史 libvirt 配置不一致')
    for section, key in [('DEFAULT', 'vcpu_pin_set'), ('compute', 'cpu_shared_set'),
                         ('compute', 'cpu_dedicated_set')]:
        if cfg.get(section, key, fallback='').strip():
            fail('发现 CPU 绑定/专用 CPU 池配置：' + key)
    if cfg.get('libvirt', 'images_type', fallback='default') not in ('default', 'qcow2', 'flat'):
        fail('不是现场已记录的本地文件型 Nova 磁盘')
    state = cfg['DEFAULT'].get('state_path', '/opt/openstack/nova')
    path = Path(cfg['DEFAULT'].get('instances_path', state + '/instances')
                .replace('$state_path', state).replace('$' + '{state_path}', state))
    if not path.is_dir():
        fail('Nova instances_path 不存在：' + str(path))
    if path.stat().st_dev != Path('/').stat().st_dev:
        fail('Nova 磁盘已不在根文件系统，需重新确认存储布局')
    return path


def check_network():
    rows = query("""
        SELECT host,admin_state_up,
            TIMESTAMPDIFF(SECOND,heartbeat_timestamp,UTC_TIMESTAMP()),HEX(configurations)
        FROM neutron.agents WHERE agent_type='Open vSwitch agent'
            AND host IN ('controller01','compute01')
    """)
    agents = {}
    for host, enabled, age, hex_config in rows:
        if host in agents:
            fail('OVS agent 注册重复：' + host)
        agents[host] = (enabled, int(age), json.loads(bytes.fromhex(hex_config).decode()))
    if set(agents) != {HOST, OTHER}:
        fail(f'未找到两台节点的 OVS agent，实际注册主机：{sorted(agents)}')
    for host, (enabled, age, config) in agents.items():
        print(f'{host}: OVS agent admin={enabled}, 心跳={age}s, 映射={config.get("bridge_mappings")}')
        if config.get('bridge_mappings') != EXPECTED_MAPPINGS or config.get('tunnel_types'):
            fail(f'{host} 网络映射/隧道类型与历史现场不同，期望映射={EXPECTED_MAPPINGS}、无隧道；'
                 f'实际映射={config.get("bridge_mappings")}、隧道={config.get("tunnel_types")}')
        if enabled != '1' or not -10 <= age <= 75:
            if host == HOST:
                fail('controller01 OVS agent 未启用或心跳超时')
            print('警告：compute01 OVS agent 心跳异常，仅比较其最近上报的映射')
    segments = query("SELECT DISTINCT network_type,COALESCE(physical_network,'') FROM neutron.networksegments")
    if not segments or any((kind, phys) not in {('flat', 'ex_provider'), ('vlan', 'vlanprovider')}
                           for kind, phys in segments):
        fail(f'Neutron 网络段类型与历史现场不同，需要重新确认可达性；实际网络段={segments!r}')
    run('systemctl', 'is-active', 'neutron-openvswitch-agent.service')
    # controller01 also runs the existing cluster-wide DHCP/L3/metadata agents.
    network_units = {'DHCP agent': 'neutron-dhcp-agent.service',
                     'L3 agent': 'neutron-l3-agent.service',
                     'Metadata agent': 'neutron-metadata-agent.service'}
    control_agents = query("""
        SELECT agent_type,admin_state_up,
            TIMESTAMPDIFF(SECOND,heartbeat_timestamp,UTC_TIMESTAMP())
        FROM neutron.agents WHERE host='controller01'
            AND agent_type IN ('DHCP agent','L3 agent','Metadata agent')
    """)
    if len(control_agents) != len(network_units) or {r[0] for r in control_agents} != set(network_units):
        fail(f'controller01 DHCP/L3/Metadata agent 注册异常；实际注册={control_agents!r}')
    for kind, enabled, age in control_agents:
        if enabled != '1' or not -10 <= int(age) <= 75:
            fail(f'controller01 网络服务心跳异常：{kind}, admin={enabled}, 心跳={age}s')
        run('systemctl', 'is-active', network_units[kind])
        print(f'controller01: {kind} 心跳={age}s，服务正常')
    run('ovs-vsctl', '--timeout=10', 'br-exists', 'br-int')
    for bridge in EXPECTED_MAPPINGS.values():
        run('ovs-vsctl', '--timeout=10', 'br-exists', bridge)
        live = []
        for iface in run('ovs-vsctl', '--timeout=10', 'list-ifaces', bridge).splitlines():
            vals = run('ovs-vsctl', '--timeout=10', 'get', 'Interface', iface,
                       'type', 'ofport', 'link_state').splitlines()
            print(f'  {bridge}/{iface}: type/ofport/link_state={vals!r}')
            if len(vals) == 3:
                kind, port, link = [v.strip('"') for v in vals]
                if kind in ('', 'system') and port.isdigit() and int(port) > 0 and link == 'up':
                    live.append(iface)
        if not live:
            fail('物理网桥没有处于 up 状态的有效上联端口：' + bridge)
        print(f'{bridge}: 有效物理上联 {", ".join(live)}')
    print('网络静态/实时检查通过；尚需业务侧跨节点连通性验收。', flush=True)
    external_pool_report()


def external_pool_report():
    # External provider addresses are shared cluster-wide; they are not per compute host.
    where = ("EXISTS (SELECT 1 FROM neutron.externalnetworks e WHERE e.network_id=s.network_id)"
             " OR EXISTS (SELECT 1 FROM neutron.networksegments n WHERE n.network_id=s.network_id"
             " AND n.physical_network='ex_provider')")
    pools = query("SELECT s.cidr,p.subnet_id,p.first_ip,p.last_ip FROM neutron.ipallocationpools p "
                  "JOIN neutron.subnets s ON s.id=p.subnet_id WHERE " + where)
    addresses = query("SELECT a.subnet_id,a.ip_address FROM neutron.ipallocations a "
                      "JOIN neutron.subnets s ON s.id=a.subnet_id WHERE " + where)
    per_subnet = {}
    for cidr, subnet, first, last in pools:
        per_subnet.setdefault((subnet, cidr), []).append(
            (int(ipaddress.ip_address(first)), int(ipaddress.ip_address(last))))
    for (subnet, cidr), ranges in per_subnet.items():
        total = sum(end - start + 1 for start, end in ranges)
        ips = {int(ipaddress.ip_address(ip)) for sid, ip in addresses if sid == subnet}
        used = sum(any(start <= ip <= end for start, end in ranges) for ip in ips)
        print(f'共享外网地址池 {cidr}: 总量={total}, 已分配={used}, 剩余={total-used}')
        if total == used:
            print('警告：外网地址池已满；开放 controller01 并不会增加 IP 数量')


def cpu_ticks():
    with open('/proc/stat') as f:
        ticks = list(map(int, f.readline().split()[1:9]))
    # guest/guest_nice already included in user/nice; iowait is not CPU execution.
    return sum(ticks), sum(ticks) - ticks[3] - ticks[4]


def memory_used():
    info = {}
    with open('/proc/meminfo') as f:
        for line in f:
            key, val = line.split(':', 1)
            if key in ('MemTotal', 'MemAvailable'):
                info[key] = int(val.split()[0])
    return Decimal(info['MemTotal'] - info['MemAvailable']) / 1024


def sample(seconds, interval):
    online = os.sysconf('SC_NPROCESSORS_ONLN')
    peak_cpu = Decimal(0)
    peak_ram = memory_used()
    previous = cpu_ticks()
    deadline = time.monotonic() + seconds
    print(f'采样 {seconds}s（每 {interval}s），统计整个宿主机实际 CPU/内存占用峰值...', flush=True)
    while True:
        remaining = deadline - time.monotonic()
        if remaining <= 0:
            break
        time.sleep(min(remaining, interval))
        current = cpu_ticks()
        delta = current[0] - previous[0]
        if delta <= 0 or current[1] < previous[1]:
            fail('CPU 采样数据无效')
        cores = Decimal(current[1] - previous[1]) * online / delta
        peak_cpu = max(peak_cpu, cores)
        peak_ram = max(peak_ram, memory_used())
        previous = current
        print(f'  CPU: {cores:.2f}/{online} 核, RAM: {peak_ram/1024:.2f} GiB（截至当前峰值）', flush=True)
    return peak_cpu, peak_ram


def ceil(number):
    return int(Decimal(number).to_integral_value(rounding=ROUND_CEILING))


def floor(number):
    return int(Decimal(number).to_integral_value(rounding=ROUND_FLOOR))


def calculate(inv, peak_cpu, peak_ram, free_gib, args, cfg):
    cpu = max(args.min_cpu, inv['VCPU']['reserved'],
              cfg['DEFAULT'].getint('reserved_host_cpus', fallback=0),
              ceil(peak_cpu * args.factor))
    ram = max(args.min_ram_gib * 1024, inv['MEMORY_MB']['reserved'],
              cfg['DEFAULT'].getint('reserved_host_memory_mb', fallback=0),
              ceil(peak_ram * args.factor / 1024) * 1024)
    total = inv['DISK_GB']['total']
    # At the site, ~1.3 TiB is already used on a 1.8 TiB shared root disk.
    # Multiplying that occupancy by 2.5 is physically impossible. By default,
    # allow new local-disk allocations to consume at most floor(free / factor),
    # and leave an absolute free-space floor. Do not give Nova credit for
    # potentially reclaimable data or already committed guest allocations.
    new_budget = min(floor(Decimal(free_gib) / args.factor),
                     free_gib - args.min_disk_free_gib)
    disk = max(inv['DISK_GB']['reserved'],
               ceil(Decimal(cfg['DEFAULT'].getint('reserved_host_disk_mb', fallback=0)) / 1024),
               total - max(new_budget, 0))
    if args.strict_disk_factor:
        disk = max(disk, ceil(Decimal(max(0, total - free_gib)) * args.factor))
    result = dict(VCPU=cpu, MEMORY_MB=ram, DISK_GB=disk)
    print(f'当前磁盘实际剩余 {free_gib} GiB，计算出的本地磁盘账面容量上限 {total-disk} GiB')
    for rc, reserve in result.items():
        row = inv[rc]
        print(f'{rc}: 总量={row["total"]}, 原预留={row["reserved"]}, '
              f'新预留={reserve}, 已分配={row["used"]}, '
              f'1.0 倍分配下剩余={row["total"]-reserve-row["used"]}')
        if reserve < 0 or row['total'] - reserve <= row['used']:
            fail(f'{rc} 无剩余调度容量；不会降低预留值来强制开放')
    return result


def plan(args, cfg, path, observed=None):
    vms = registered()
    print(f'controller01 当前 Nova 实例数: {vms}')
    if vms:
        print(f'注意：采样包含现有虚拟机负载，{args.factor} 倍预留会更保守，不自动减去虚拟机占用。')
    if observed is None:
        inventory()  # Fail fast if Placement is unavailable, before waiting for samples.
        observed = sample(args.sample_seconds, args.interval)
    else:
        print('复用只读检查阶段的 CPU/内存采样，重新读取实时资源容量及分配情况。')
    peak_cpu, peak_ram = observed
    free_gib = shutil.disk_usage(path).free // GIB
    inv = inventory()
    print(f'采样峰值：CPU={peak_cpu:.2f} 核，RAM={peak_ram/1024:.2f} GiB；倍率={args.factor}')
    cinder = Path('/data/nfs_data/cinder')
    if cinder.is_dir() and cinder.stat().st_dev == path.stat().st_dev:
        count, logical = query("SELECT COUNT(*),COALESCE(SUM(size),0) FROM cinder.volumes WHERE deleted=0")[0]
        print(f'注意：Cinder NFS 与 Nova 共用根盘，现有卷 {count} 个，逻辑容量 {logical} GiB。')
        print('Nova DISK_GB 预留不限制 Cinder 卷写入、镜像缓存或其他业务数据增长。')
    return calculate(inv, peak_cpu, peak_ram, free_gib, args, cfg), observed


def check(args, path):
    failures = []

    def step(label, action):
        print('[CHECK] ' + label, flush=True)
        try:
            result = action()
        except Exception as exc:
            failures.append(label)
            print('[FAIL] ' + label, file=sys.stderr, flush=True)
            report_failure(exc)
            return None
        print('[ OK ] ' + label, flush=True)
        return result

    def check_service():
        row = service()
        print(f'Nova: {row["State"]}/{row["Status"]}')
        if row['State'] != 'up':
            fail(f'controller01 Nova Compute 心跳异常，实际 State={row["State"]}, Status={row["Status"]}')
        return row

    def require_success():
        if failures:
            fail(f'只读检查失败，共 {len(failures)} 项：' + '；'.join(failures)
                 + '。未执行 apply，未修改配置或调度状态，请反馈上述完整检查输出。')

    print(f'检查主机={socket.gethostname()}，时间={time.strftime("%Y-%m-%d %H:%M:%S %z")}，'
          f'Nova 配置={path}，采样时长={args.sample_seconds}s，预留系数={args.factor}', flush=True)
    step('本机 Nova Compute systemd 服务', lambda: run('systemctl', 'is-active', UNIT))
    cfg = step('读取 Nova 配置文件', lambda: read_conf(path))
    disk_path = None
    if cfg is not None:
        disk_path = step('Nova 虚拟机磁盘目录与计算驱动', lambda: instance_path(cfg))
    else:
        print('[SKIP] Nova 磁盘目录检查：依赖配置文件读取成功', flush=True)
    step('OpenStack 管理员 API 与 Compute 服务心跳', check_service)
    step('Nova Cell 及计算节点注册', registered)
    step('Placement 资源容量及分配记录', inventory)
    step('Neutron 网络映射、Agent、网桥、上联和 IP 地址池', check_network)
    require_success()
    result = step('CPU/内存采样及资源预留容量计算', lambda: plan(args, cfg, disk_path))
    require_success()
    _, observed = result
    print('只读检查完成，未修改配置或调度状态', flush=True)
    return observed


def update_defaults(original, values):
    # Preserve unrelated sections, credentials, comments and formatting.
    lines = original.splitlines(keepends=True)
    output = []
    found = False
    in_default = False
    keys = set(values)
    for line in lines:
        section = re.match(r'^\s*\[([^\]]+)\]', line)
        if section:
            in_default = section[1] == 'DEFAULT'
            if in_default:
                if found:
                    fail('配置包含多个 DEFAULT 段，不自动修改')
                found = True
                output.append(line.rstrip('\r\n') + '\n')
                output.extend(f'{k} = {v}\n' for k, v in values.items())
                continue
        if in_default:
            match = re.match(r'^\s*([a-zA-Z0-9_]+)\s*[:=]', line)
            if match and match[1].lower() in keys:
                continue
        output.append(line)
    if not found:
        fail('Nova 配置中没有 DEFAULT 段')
    return ''.join(output)


def atomic_write(path, data):
    info = path.stat()
    fd, temp = tempfile.mkstemp(prefix='.nova-controller-', dir=str(path.parent))
    try:
        with os.fdopen(fd, 'wb') as stream:
            stream.write(data)
            os.fchown(stream.fileno(), info.st_uid, info.st_gid)
            os.fchmod(stream.fileno(), stat.S_IMODE(info.st_mode))
            stream.flush()
            os.fsync(stream.fileno())
        os.replace(temp, path)
    finally:
        if os.path.exists(temp):
            os.unlink(temp)


def backup_config(path):
    # Nova config contains credentials; keep backup private on the site host.
    directory = Path('/var/backups/controller01-nova-reserve')
    directory.mkdir(mode=0o700, parents=True, exist_ok=True)
    os.chmod(directory, 0o700)
    fd, filename = tempfile.mkstemp(prefix='nova.conf.', suffix='.bak', dir=str(directory))
    with os.fdopen(fd, 'wb') as stream:
        stream.write(path.read_bytes())
        stream.flush()
        os.fsync(stream.fileno())
    return Path(filename)


def inventory_matches(inv, expected):
    return all(inv[rc]['reserved'] >= want and inv[rc]['ratio'] == 1
               and inv[rc]['total'] - inv[rc]['reserved'] > inv[rc]['used']
               for rc, want in expected.items())


def wait_for_inventory(expected, timeout, enabled=False):
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        try:
            run('systemctl', 'is-active', UNIT)
            row = service()
            if (row['State'] == 'up'
                    and row['Status'] == ('enabled' if enabled else 'disabled')
                    and disabled_trait() != enabled and inventory_matches(inventory(), expected)):
                print('Nova 服务、Placement 资源预留及调度标记验证通过', flush=True)
                return
        except (RuntimeError, subprocess.SubprocessError, ValueError, KeyError):
            pass
        time.sleep(5)
    fail('等待 Placement 新容量/调度标记超时')


def apply(args, path, observed=None):
    service()
    disable()
    success = False
    backup = None
    try:
        cfg = read_conf(path)
        disk_path = instance_path(cfg)
        check_network()
        expected, _ = plan(args, cfg, disk_path, observed=observed)
        values = dict(zip(KEYS, (expected['VCPU'], expected['MEMORY_MB'],
                                  expected['DISK_GB'] * 1024, '1.0', '1.0', '1.0')))
        updated = update_defaults(path.read_text(encoding='utf-8'), values)
        backup = backup_config(path)
        print('原配置备份：' + str(backup), flush=True)
        atomic_write(path, updated.encode('utf-8'))
        run('systemctl', 'restart', UNIT, timeout=120)
        wait_for_inventory(expected, args.verify_timeout)
        check_network()
        # Recheck physical space: Cinder and other services may have used it
        # while the Compute service was restarting.
        free = shutil.disk_usage(disk_path).free // GIB
        current = inventory()
        if not inventory_matches(current, expected) or not disabled_trait():
            fail('启用前 Placement 资源预留或调度禁用标记发生变化')
        local_remaining = current['DISK_GB']['total'] - current['DISK_GB']['reserved'] - current['DISK_GB']['used']
        if free - local_remaining < args.min_disk_free_gib:
            fail('重启期间实际磁盘剩余空间下降，不能满足业务磁盘预留')
        osc('compute', 'service', 'set', '--enable', HOST, 'nova-compute')
        wait_for_inventory(expected, args.verify_timeout, enabled=True)
        success = True
        print('成功：controller01 已开放调度；仅重启本机 nova-compute，未修改网桥/Cinder/其他节点。')
    finally:
        if not success:
            try:
                disable()
            except Exception as error:
                print('无法确认调度已关闭，请立即人工核查：' + str(error), file=sys.stderr)
            if backup:
                print(f'恢复配置：bash enable_controller_compute.sh rollback {backup}', file=sys.stderr)


def parse_args(argv=None):
    p = argparse.ArgumentParser(prog='enable_controller_compute.sh',
                                description='controller01 资源保护与 Nova 调度管理（默认先只读检查，检查通过后自动开放调度）')
    p.add_argument('mode', nargs='?', default='auto',
                   choices=('auto', 'check', 'apply', 'network', 'disable', 'rollback', 'guard'),
                   help='auto（默认）：先 check 后 apply；check：仅检查，不修改配置')
    p.add_argument('backup', nargs='?', help='rollback 使用的原配置备份路径')
    p.add_argument('--factor', type=Decimal, default=Decimal('2.5'), help='CPU/RAM 峰值预留倍数，默认 2.5')
    p.add_argument('--sample-seconds', type=int, default=300, help='CPU/RAM 采样时长，默认 300 秒')
    p.add_argument('--interval', type=int, default=5, help='采样间隔，默认 5 秒')
    p.add_argument('--min-cpu', type=int, default=8, help='CPU 最低预留（历史值 8）')
    p.add_argument('--min-ram-gib', type=int, default=64, help='内存最低预留 GiB（历史值 64）')
    p.add_argument('--min-disk-free-gib', type=int, default=300, help='共享根盘要求保留的实际可用空间 GiB')
    p.add_argument('--strict-disk-factor', action='store_true', help='另外要求已占用磁盘空间的 factor 倍预留；不足则拒绝')
    p.add_argument('--verify-timeout', type=int, default=180, help='等待 Nova 更新 Placement 的秒数')
    args = p.parse_args(argv)
    if not args.factor.is_finite() or args.factor < 1:
        p.error('factor 必须是 >= 1 的有限数值')
    if min(args.sample_seconds, args.interval, args.min_cpu, args.min_ram_gib,
           args.min_disk_free_gib, args.verify_timeout) < 1:
        p.error('资源及秒数参数必须为正整数')
    if (args.mode == 'rollback') != bool(args.backup):
        p.error('只有 rollback 需要且允许提供备份路径')
    return args


def main(argv=None):
    args = parse_args(argv)
    if socket.gethostname().split('.')[0] != HOST or os.geteuid() != 0:
        fail(f'仅允许在 controller01 以 root 执行；实际主机={socket.gethostname()}、UID={os.geteuid()}；'
             '请在现场加载 OpenStack 管理员环境后执行')
    if args.mode in ('auto', 'apply', 'disable', 'rollback', 'guard'):
        lock = open('/run/lock/controller01-nova-scheduling.lock', 'a')
        fcntl.flock(lock, fcntl.LOCK_EX | fcntl.LOCK_NB)
    if args.mode == 'disable':
        disable()
        return
    if args.mode == 'network':
        check_network()
        return
    path = CONF.resolve(strict=args.mode not in ('auto', 'check'))
    if args.mode == 'rollback':
        source = Path(args.backup).resolve(strict=True)
        if source.parent != Path('/var/backups/controller01-nova-reserve'):
            fail('只接受脚本生成的备份文件')
        disable()
        atomic_write(path, source.read_bytes())
        run('systemctl', 'restart', UNIT, timeout=120)
        print('原配置已恢复，controller01 保持关闭调度')
        return
    if args.mode == 'guard':
        free = shutil.disk_usage(instance_path(read_conf(path))).free // GIB
        disk = inventory()['DISK_GB']
        projected = free - max(0, disk['total'] - disk['reserved'] - disk['used'])
        print(f'共享根盘实际剩余 {free} GiB，扣除剩余 Nova 可调度容量后 {projected} GiB，'
              f'阈值 {args.min_disk_free_gib} GiB')
        if projected <= args.min_disk_free_gib:
            disable()
            print('磁盘余量不足，已关闭 controller01 调度；不会停止存量实例或限制 Cinder 写入')
        return
    if args.mode == 'auto':
        print('========== 第 1/2 步：检查执行环境并采样资源 ==========', flush=True)
        observed = check(args, path)
        print('========== 第 2/2 步：检查通过，自动应用配置并开放调度 ==========', flush=True)
        apply(args, path, observed=observed)
    elif args.mode == 'apply':
        run('systemctl', 'is-active', UNIT)
        apply(args, path)
    else:
        check(args, path)


if __name__ == '__main__':
    try:
        main()
    except (Exception, KeyboardInterrupt) as exc:
        print('错误：脚本执行未完成。', file=sys.stderr, flush=True)
        report_failure(exc)
        sys.exit(1)
PY
