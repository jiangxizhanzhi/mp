#!/usr/bin/env bash
# VM ownership audit; optional operator-confirmed cleanup via the OpenStack API.
set -euo pipefail
python3 - "$@" <<'PY'
import argparse
import collections
import json
import csv
import gzip
import ipaddress
import os
import re
import subprocess
import sys
import uuid

HELP = """默认只读审计全部虚拟机。使用 --candidates-only 筛选建议回收及疑似残留。
删除必须指定完整 UUID，经实时数据库及 OpenStack 双重核对，并在终端输入确认。
归属不明或非MP资源、仍在用、正在操作、外部接入或 Nova 已删除的实例不会被脚本删除。
历史快照只支持审计，禁止删除。MySQL 连接参数可放在 -- 之后（兼容原来的 -u root -p）。
环境变量：MYSQL_BIN（默认 mysql），OPENSTACK_BIN（默认 openstack），
LIBVIRT_ACTIVE_UUIDS（可选 UUID 文件，只用于活动实例核对），MP_RESOURCE_PREFIX（默认 MP_SCENE）。
"""
parser = argparse.ArgumentParser(prog='audit_openstack_scene_vms.sh', description=HELP, formatter_class=argparse.RawDescriptionHelpFormatter)
parser.add_argument('--dump', metavar='SQL', help='离线分析 SQL 或 SQL.gz 快照')
parser.add_argument('--candidates-only', action='store_true', help='仅输出建议回收和需人工核对的疑似残留')
parser.add_argument('--delete', nargs='+', metavar='UUID', help='指定待回收的完整 Nova UUID（空格分隔多个）')
parser.add_argument('--allow-reviewed', action='store_true', help='管理员已另行确认归属的 REVIEW 疑似残留')
parser.add_argument('--dry-run', action='store_true', help='核对所选 UUID，不发送删除请求')
options, mysql_args = parser.parse_known_args()
if mysql_args[:1] == ['--']:
    mysql_args = mysql_args[1:]
if options.dump and options.delete:
    parser.error('历史快照不能用于删除或删除预演')
if (options.allow_reviewed or options.dry_run) and not options.delete:
    parser.error('--allow-reviewed / --dry-run 必须配合 --delete')
if options.dump and mysql_args:
    parser.error('--dump 不接受 mysql 连接参数')
# Connection arguments only. Avoid allowing another SQL command to be injected by CLI flags.
for arg in mysql_args:
    if arg in ('-f', '--force', '--reconnect') or arg.startswith(('-e', '--execute', '--init-command', '--init_command')):
        parser.error('不允许传入 SQL 执行或忽略查询错误参数')

# Explicit projection: avoid loading credentials, request payloads or guest metadata into reports.
TABLES = {
    'nova.instances': 'uuid display_name hostname host project_id vm_state task_state power_state memory_mb deleted locked disable_terminate',
    'nova.compute_nodes': 'host memory_mb memory_mb_used free_ram_mb running_vms ram_allocation_ratio deleted',
    'nova.services': 'host binary disabled forced_down last_seen_up deleted',
    'mp.mp_scene_scene': 'id name event event_status deleted',
    'mp.mp_scene_scene_element': 'id scene node_id name event event_status vendor',
    'mp.mp_sceneplugincloud_scene_element': 'scene scene_element resource_id virtual_type external',
    'mp.mp_cscene_cscene_instance': 'id name scene deleted',
    'cpcloud.device_vm': 'device_uuid instance_uuid instance_exist instance_state',
    'neutron.externalnetworks': 'network_id',
    'neutron.networks': 'id name',
    'neutron.subnets': 'id name network_id cidr',
    'neutron.ipallocationpools': 'subnet_id first_ip last_ip',
    'neutron.ipallocations': 'subnet_id ip_address port_id',
    'neutron.ports': 'id device_owner device_id',
}
TABLES = {k: v.split() for k, v in TABLES.items()}
rows = {k: [] for k in TABLES}


def warn(*a):
    print(*a, file=sys.stderr)


def load_live():
    # HEX encoding preserves tabs/newlines and distinguishes NULL from the empty string.
    sql = ["SET TRANSACTION ISOLATION LEVEL REPEATABLE READ", "START TRANSACTION WITH CONSISTENT SNAPSHOT, READ ONLY"]
    for key, cols in TABLES.items():
        db, tab = key.split('.')
        projection = ','.join('HEX(CAST(`%s` AS CHAR))' % col for col in cols)
        sql.append("SELECT '%s',%s FROM `%s`.`%s`" % (key, projection, db, tab))
        # Detect missing/partial SELECT output, even if client-side settings swallow a query error.
        sql.append("SELECT '%s#count',HEX(CAST(COUNT(*) AS CHAR)) FROM `%s`.`%s`" % (key, db, tab))
    sql.append('COMMIT')
    result = subprocess.run([os.environ.get('MYSQL_BIN', 'mysql'), *mysql_args,
                             '--batch', '--raw', '--skip-column-names', '--skip-reconnect',
                             '--default-character-set=utf8mb4', '--execute', ';\n'.join(sql)],
                            stdout=subprocess.PIPE, check=True)
    counts = {}
    for line in result.stdout.splitlines():
        key, *values = line.decode('ascii').split('\t')
        if key.endswith('#count'):
            table = key[:-6]
            if table not in TABLES or table in counts or len(values) != 1:
                raise ValueError('数据库计数校验返回格式不符合预期')
            counts[table] = int(bytes.fromhex(values[0]).decode('ascii'))
            continue
        if key not in TABLES or len(values) != len(TABLES[key]):
            raise ValueError('数据库查询返回格式不符合预期')
        vals = [None if v == 'NULL' else bytes.fromhex(v).decode('utf-8', 'replace') for v in values]
        rows[key].append(dict(zip(TABLES[key], vals)))
    if any(counts.get(key) != len(rows[key]) for key in TABLES):
        raise ValueError('数据库查询结果缺失或不完整，停止审计及回收')


# Parser for standard mysqldump extended INSERT rows, with MySQL string escape handling.
TOKEN = re.compile(r"\s*(?:(?P<quoted>(?:_binary\s*)?'(?:\\.|''|[^'\\])*')|(?P<atom>[^,()\s;]+)|(?P<punct>[,();]))", re.S)
ESCAPES = {'0': '\0', 'b': '\b', 'n': '\n', 'r': '\r', 't': '\t', 'Z': '\x1a'}


def decode(token):
    if token.startswith(('_binary', "'")):
        token = token[token.index("'")+1:-1]
        return re.sub(r"\\(.)|''", lambda m: ESCAPES.get(m[1], m[1]) if m[1] is not None else "'", token, flags=re.S)
    return None if token.upper() == 'NULL' else token


def parse_values(values, total, selected):
    pos = 0
    row = None
    index = 0
    need_value = False
    while pos < len(values):
        match = TOKEN.match(values, pos)
        if match is None:
            if not values[pos:].strip():
                break
            raise ValueError('无法解析 SQL INSERT 数据，停止审计')
        pos = match.end()
        punct = match['punct']
        if punct == '(' and row is None:
            row, index, need_value = {}, 0, True
        elif row is not None:
            if punct == ',' and not need_value:
                need_value = True
            elif punct == ')' and not need_value and index == total:
                yield row
                row = None
            elif not punct and need_value and index < total:
                if index in selected:
                    row[selected[index]] = decode(match['quoted'] or match['atom'])
                index += 1
                need_value = False
            else:
                raise ValueError('SQL INSERT 字段与表结构不符')
        elif punct not in (',', ';'):
            raise ValueError('SQL INSERT 行格式不符')
    if row is not None:
        raise ValueError('SQL INSERT 不完整')


def load_dump(path):
    opener = gzip.open if path.endswith('.gz') else open
    database = None
    schema = {}
    creating = None
    with opener(path, 'rt', encoding='utf-8', errors='replace') as stream:
        for line in stream:
            if line.startswith('USE `'):
                database = line.split('`')[1]
            elif line.startswith('CREATE TABLE `'):
                key = f"{database}.{line.split('`')[1]}"
                creating = key if key in TABLES else None
                if creating:
                    schema[key] = []
            elif creating and line.startswith('  `'):
                schema[creating].append(line.split('`')[1])
            elif creating and line.startswith(')'):
                creating = None
            elif line.startswith('INSERT INTO `'):
                key = f"{database}.{line.split('`')[1]}"
                if key not in TABLES:
                    continue
                m = re.match(r'INSERT INTO `[^`]+`(?: \(([^)]*)\))? VALUES\s*', line)
                if not m:
                    raise ValueError('不支持的 SQL INSERT 格式: ' + key)
                columns = re.findall(r'`([^`]+)`', m[1]) if m[1] else schema[key]
                selected = {columns.index(c): c for c in TABLES[key]}
                rows[key].extend(parse_values(line[m.end():], len(columns), selected))
    for key, cols in TABLES.items():
        if key not in schema or not set(cols).issubset(schema[key]):
            raise ValueError('SQL 快照缺少所需表或字段: ' + key)


def grouped(records, key):
    result = collections.defaultdict(list)
    for r in records:
        if r[key]:
            result[r[key]].append(r)
    return result


def state(record):
    return (record.get('event'), record.get('event_status')) if record else (None, None)


def valid_uuid(value):
    return str(uuid.UUID(value))


def read_data():
    # Discard all old rows before each revalidation; every live audit is a fresh read-only snapshot.
    for collection in rows.values():
        collection.clear()
    if options.dump:
        load_dump(options.dump)
    else:
        load_live()


def active_uuids():
    path = os.environ.get('LIBVIRT_ACTIVE_UUIDS')
    if not path:
        return set()
    with open(path, encoding='utf-8') as f:
        return {valid_uuid(line.strip()) for line in f if line.strip()}


STABLE_VM_STATES = {'active', 'stopped', 'error', 'paused', 'suspended', 'shelved', 'shelved_offloaded'}
STABLE_OS_STATES = {'ACTIVE', 'SHUTOFF', 'ERROR', 'PAUSED', 'SUSPENDED', 'SHELVED', 'SHELVED_OFFLOADED'}
CANDIDATES = {'ELIGIBLE', 'REVIEW'}

def assess_cleanup(vm, classification, scene, element, linked, business, previews, resource_matches, all_matches, active_scene_reference):
    if vm['deleted'] != '0':
        return 'HYPERVISOR_ONLY', 'Nova已删除；需要现场核对宿主机，不通过脚本强制清理'
    if vm['locked'] != '0' or vm['disable_terminate'] != '0':
        return 'KEEP', '虚拟机受保护或保护状态未知'
    if vm['task_state'] not in (None, '') or vm['vm_state'] not in STABLE_VM_STATES:
        return 'KEEP', 'Nova有未完成任务或处于非稳定状态'
    if all_matches and any(r['external'] != '0' or r['virtual_type'] != '4' for r in all_matches):
        return 'KEEP', 'MP通过外部方式引用的资源，或资源类型/归属不一致'
    if any(r['external'] != '0' for r in linked):
        return 'KEEP', '外部接入资源不属于MP生命周期回收范围'
    if previews and any(r['instance_exist'] != '0' for r in previews):
        return 'KEEP', 'cpcloud仍记录镜像调试实例存在'
    if len(all_matches) > 1 or len(previews) > 1 or classification in ('归属冲突_需人工核对', 'MP归属状态不一致_需人工核对'):
        return 'KEEP', '存在多重或冲突归属，需先核实'
    active_business = any(r['deleted'] != '1' for r in business)
    if scene and element:
        # Explicitly completed scene AND element deletion, with no active business instance.
        if (scene['deleted'] == '1' and state(scene) == ('9', '4') and state(element) == ('9', '4')
                and not active_business and not previews and len(resource_matches) == 1
                and len(linked) == 1 and linked[0]['resource_id'] == vm['uuid'] and element['vendor'] in ('0', None, '')):
            return 'ELIGIBLE', 'MP场景及节点回收完成且无在用业务引用，Nova实例仍存在'
        if active_business:
            return 'KEEP', '仍有未删除的MP业务实例引用'
        if scene['event'] == '9' and scene['event_status'] in ('0', '1', '2', '3'):
            return 'KEEP', 'MP回收任务尚未结束'
        if element['event_status'] in ('0', '1', '2', '3') or scene['event_status'] in ('0', '1', '2', '3'):
            return 'KEEP', 'MP场景或节点事件未完成'
        if classification in ('已回收标记_虚拟机仍存在', '回收异常_虚拟机仍存在', '创建异常_可能残留',
                              '节点记录其他资源ID_疑似旧实例或ID口径不同', '业务实例已删除_拓扑待核查'):
            if element['vendor'] not in ('0', None, ''):
                return 'KEEP', '厂商或归属信息不一致'
            return 'REVIEW', '疑似MP残留；需确认该UUID确无其他用途及非MP引用'
        return 'KEEP', 'MP拓扑仍有效、仅停止或尚未完成回收'
    if active_scene_reference:
        return 'KEEP', '资源仍关联到有效或状态不明的场景，禁止直接回收'
    if classification in ('MP名称前缀匹配_关联记录缺失', 'MP资源关联存在_节点记录缺失或不唯一') and (not previews):
        return 'REVIEW', '疑似MP孤儿资源，归属缺失；须在现场确认后才可回收'
    if classification == '镜像调试已标记不存在_虚拟机仍存在' and len(previews) == 1 and not all_matches:
        return 'REVIEW', 'cpcloud镜像调试状态不一致，须人工确认不存在调试任务及其他用途'
    if classification == '未匹配MP或镜像调试_核对归属':
        return 'UNMANAGED', '未能确认MP归属，不等于可删除；由资源所有者处理'
    return 'KEEP', '证据不足或仍存在资源引用'


def join(records, field):
    return '|'.join(str(r[field] if r[field] is not None else 'NULL') for r in records)


def audit(active):
    nodes = grouped(rows['mp.mp_scene_scene_element'], 'id')
    scenes = grouped(rows['mp.mp_scene_scene'], 'id')
    plugins = [r for r in rows['mp.mp_sceneplugincloud_scene_element'] if r['virtual_type'] == '4']
    # plugin.scene_element is the topology node_id, not the server-side element id (EID).
    # It is only unique within one scene.
    node_by_scene = collections.defaultdict(list)
    for e in rows['mp.mp_scene_scene_element']:
        node_by_scene[(e['scene'], e['node_id'])].append(e)
    by_node = collections.defaultdict(list)
    for p in plugins:
        by_node[(p['scene'], p['scene_element'])].append(p)
    by_resource = grouped(plugins, 'resource_id')
    all_resource = grouped(rows['mp.mp_sceneplugincloud_scene_element'], 'resource_id')
    business = grouped(rows['mp.mp_cscene_cscene_instance'], 'scene')
    preview = grouped(rows['cpcloud.device_vm'], 'instance_uuid')
    prefix = os.environ.get('MP_RESOURCE_PREFIX', 'MP_SCENE') + '_'

    report = []
    seen = set()
    mp_vm_ids = set()
    def emit(out):
        report.append(out)
    for vm in rows['nova.instances']:
        vid = vm['uuid']
        if vm['deleted'] != '0' and vid not in active:
            continue
        seen.add(vid)
        name = vm['display_name'] or vm['hostname'] or ''
        resource_matches = by_resource.get(vid, [])
        candidates = {e['id'] for p in resource_matches for e in node_by_scene[(p['scene'], p['scene_element'])]}
        unlinked = [p for p in resource_matches if not node_by_scene[(p['scene'], p['scene_element'])]]
        named = set()
        for n in (name, vm['hostname'] or ''):
            if n.startswith(prefix):
                element_id = n[len(prefix):].split('_', 1)[0]
                if element_id in nodes:
                    named.add(element_id)
        owners = candidates | named
        previews = preview.get(vid, [])
        association = '+'.join(x for x, c in [('resource_uuid', resource_matches), ('name', named), ('preview_uuid', previews)] if c) or 'none'
        e = s = None
        linked = b = []
        if len(owners) > 1 or (owners and previews) or (owners and unlinked) or len(all_resource.get(vid, [])) > len(resource_matches):
            classification = '归属冲突_需人工核对'
        elif owners:
            eid = next(iter(owners))
            matches = nodes.get(eid, [])
            if len(matches) != 1:
                classification = 'MP资源关联存在_节点记录缺失或不唯一'
            else:
                e = matches[0]
                linked = by_node[(e['scene'], e['node_id'])]
                ss = scenes.get(e['scene'], [])
                b = business.get(e['scene'], [])
                if len(ss) != 1 or e['vendor'] not in ('0', None, '') or len(linked) > 1:
                    classification = 'MP归属状态不一致_需人工核对'
                else:
                    s = ss[0]
                    mp_vm_ids.add(vid)
                    resource_ids = {r['resource_id'] for r in linked if r['resource_id']}
                    # A different nonempty resource UUID requires review even if the scene has been deleted.
                    if resource_ids and vid not in resource_ids:
                        classification = '节点记录其他资源ID_疑似旧实例或ID口径不同'
                    elif state(s) == ('9', '5') or state(e) == ('9', '5'):
                        classification = '回收异常_虚拟机仍存在'
                    elif state(s) == ('9', '4') or state(e) == ('9', '4') or s['deleted'] == '1':
                        classification = '已回收标记_虚拟机仍存在'
                    elif state(s)[0] == '9' or state(e)[0] == '9':
                        classification = '回收处理中_需复查'
                    elif state(s) == ('2', '5') or state(e) == ('2', '5'):
                        classification = '创建异常_可能残留'
                    elif b and all(r['deleted'] == '1' for r in b):
                        classification = '业务实例已删除_拓扑待核查'
                    elif state(s)[0] == '4' or state(e)[0] == '4':
                        classification = '仅停止_未执行回收'
                    elif None in state(s) or None in state(e):
                        classification = 'MP拓扑状态不完整_需核对'
                    else:
                        classification = 'MP拓扑未回收_当前记录'
        elif unlinked:
            classification = 'MP资源关联存在_节点记录缺失或不唯一'
        elif name.startswith(prefix) or (vm['hostname'] or '').startswith(prefix):
            classification = 'MP名称前缀匹配_关联记录缺失'
        elif previews:
            classification = ('镜像调试已标记不存在_虚拟机仍存在' if all(r['instance_exist'] == '0' for r in previews)
                              else '镜像调试关联_核对状态')
        else:
            classification = '未匹配MP或镜像调试_核对归属'
        if vm['deleted'] != '0' and vid in active:
            classification = 'Nova已删除_libvirt仍活动'
        active_scene_reference = any(
            any(r['deleted'] != '1' for r in business.get(ref['scene'], []))
            or any(rs['deleted'] != '1' and state(rs) not in (('9', '4'), ('9', '5'), ('2', '5'))
                   for rs in scenes.get(ref['scene'], []))
            for ref in all_resource.get(vid, [])
        )
        cleanup_status, cleanup_reason = assess_cleanup(vm, classification, s, e, linked, b, previews, resource_matches,
                                                        all_resource.get(vid, []), active_scene_reference)
        emit(dict(cleanup_status=cleanup_status, cleanup_reason=cleanup_reason, classification=classification, association=association, nova_uuid=vid, vm_name=name,
                  host=vm['host'], project_id=vm['project_id'], vm_state=vm['vm_state'], task_state=vm['task_state'], power_state=vm['power_state'],
                  memory_mib=vm['memory_mb'], nova_deleted=vm['deleted'], nova_locked=vm['locked'], disable_terminate=vm['disable_terminate'], libvirt_active=('yes' if vid in active else 'not_listed') if os.environ.get('LIBVIRT_ACTIVE_UUIDS') else 'not_checked',
                  scene_id=e['scene'] if e else '', scene_name=s['name'] if s else '', scene_event=s['event'] if s else '',
                  scene_status=s['event_status'] if s else '', scene_deleted=s['deleted'] if s else '',
                  element_id=e['id'] if e else '|'.join(sorted(owners)), element_name=e['name'] if e else '',
                  element_event=e['event'] if e else '', element_status=e['event_status'] if e else '',
                  resource_ids=join(linked, 'resource_id'), external_flags=join(linked, 'external'), business_ids=join(b, 'id'), business_deleted_flags=join(b, 'deleted'),
                  preview_device_ids=join(previews, 'device_uuid'), preview_instance_exist=join(previews, 'instance_exist'),
                  preview_instance_state=join(previews, 'instance_state')))

    for vid in sorted(active - seen):
        emit(dict(classification='libvirt活动_UUID在Nova无记录', association='none', nova_uuid=vid, libvirt_active='yes',
                  cleanup_status='HYPERVISOR_ONLY', cleanup_reason='仅在libvirt活动清单中存在，需现场核对'))
    return report, mp_vm_ids


fields = ['cleanup_status', 'cleanup_reason', 'classification', 'association', 'nova_uuid', 'vm_name', 'host', 'project_id', 'vm_state', 'task_state',
          'power_state', 'memory_mib', 'nova_deleted', 'nova_locked', 'disable_terminate', 'libvirt_active', 'scene_id', 'scene_name',
          'scene_event', 'scene_status', 'scene_deleted', 'element_id', 'element_name', 'element_event',
          'element_status', 'resource_ids', 'external_flags', 'business_ids', 'business_deleted_flags', 'preview_device_ids',
          'preview_instance_exist', 'preview_instance_state']


def write_report(report):
    writer = csv.DictWriter(sys.stdout, fieldnames=fields, delimiter='\t', lineterminator='\n')
    writer.writeheader()
    for out in report:
        if options.candidates_only and out['cleanup_status'] not in CANDIDATES:
            continue
        writer.writerow({k: str(v).replace('\r', ' ').replace('\n', ' ').replace('\t', ' ') if v is not None else '' for k, v in out.items()})


def print_summary(report, mp_vm_ids):
    summary = collections.Counter(x['classification'] for x in report)
    ram = collections.Counter()
    for x in report:
        ram[x['classification']] += int(x.get('memory_mib') or 0)
    warn('=== 回收候选统计 ===')
    for status, count in collections.Counter(x['cleanup_status'] for x in report).most_common():
        capacity = sum(int(x.get('memory_mib') or 0) for x in report if x['cleanup_status'] == status)
        warn(f'{status}: {count} 台, {capacity} MiB')
    warn('=== VM 分类（配置内存 MiB，非实际宿主机 RSS） ===')
    for category, count in summary.most_common():
        warn(f'{category}: {count} 台, {ram[category]} MiB')
    warn('=== Nova 计算服务及容量（MiB） ===')
    services = {r['host']: r for r in rows['nova.services'] if r['deleted'] == '0' and r['binary'] == 'nova-compute'}
    for n in rows['nova.compute_nodes']:
        if n['deleted'] != '0':
            continue
        svc = services.get(n['host'], {})
        warn(f"{n['host']}: disabled={svc.get('disabled', '?')} forced_down={svc.get('forced_down', '?')} last_seen_up={svc.get('last_seen_up', '?')} total={n['memory_mb']} used={n['memory_mb_used']} free={n['free_ram_mb']} running_vms={n['running_vms']} ram_ratio={n['ram_allocation_ratio']}")
    warn('=== Neutron 外部网段地址池（只覆盖 Neutron 已登记地址） ===')
    external = {r['network_id'] for r in rows['neutron.externalnetworks']}
    networks = {r['id']: r['name'] for r in rows['neutron.networks']}
    pools = grouped(rows['neutron.ipallocationpools'], 'subnet_id')
    allocations = grouped(rows['neutron.ipallocations'], 'subnet_id')
    ports = {r['id']: r for r in rows['neutron.ports']}
    for subnet in rows['neutron.subnets']:
        if subnet['network_id'] not in external:
            continue
        sid = subnet['id']
        intervals = sorted((int(ipaddress.ip_address(p['first_ip'])), int(ipaddress.ip_address(p['last_ip']))) for p in pools[sid])
        merged = []
        for lo, hi in intervals:
            if merged and lo <= merged[-1][1] + 1:
                merged[-1] = (merged[-1][0], max(hi, merged[-1][1]))
            else:
                merged.append((lo, hi))
        ips = collections.Counter(a['ip_address'] for a in allocations[sid])
        used = sum(any(lo <= int(ipaddress.ip_address(ip)) <= hi for lo, hi in merged) for ip in ips)
        size = sum(hi - lo + 1 for lo, hi in merged)
        types = collections.Counter(ports.get(a['port_id'], {}).get('device_owner') or '(未绑定或无owner)' for a in allocations[sid])
        mp_ext_vms = {ports.get(a['port_id'], {}).get('device_id') for a in allocations[sid]} & mp_vm_ids
        warn(f"{networks.get(subnet['network_id'], '?')} {subnet['cidr']}: 地址池容量={size} 已分配={used} 未登记={size-used} 子网内重复IP={sum(v > 1 for v in ips.values())} 已关联MP虚拟机={len(mp_ext_vms)} 端口归属={dict(types)}")
    warn('注意：未登记地址不等于物理网络可用；REVIEW 需要人工核对，历史结果不可作为删除依据。')



def selected_row(report, vid):
    matches = [r for r in report if r.get('nova_uuid') == vid]
    if len(matches) != 1:
        raise ValueError(f'{vid}: 当前数据库无唯一匹配实例，禁止删除')
    row = matches[0]
    if row['cleanup_status'] == 'ELIGIBLE':
        return row
    if options.allow_reviewed and row['cleanup_status'] == 'REVIEW':
        return row
    raise ValueError(f'{vid}: {row["cleanup_status"]}，{row["cleanup_reason"]}，不满足删除条件')


def fingerprint(row):
    # Ignore the optional libvirt list; it is not live evidence for deletion.
    return tuple(row.get(k) for k in fields if k != 'libvirt_active')


def show_server(row):
    vid = row['nova_uuid']
    cli = os.environ.get('OPENSTACK_BIN', 'openstack')
    result = subprocess.run([cli, 'server', 'show', '-f', 'json', vid], stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=90)
    if result.returncode != 0:
        raise ValueError(f'{vid}: OpenStack 查询失败，确认已加载现场管理员认证环境；不发送删除请求')
    try:
        server = {str(k).lower(): v for k, v in json.loads(result.stdout).items()}
    except (ValueError, TypeError, AttributeError):
        raise ValueError(f'{vid}: OpenStack 返回格式无法核对')
    if (server.get('id') != vid or server.get('name') != row['vm_name']
            or not row.get('project_id') or server.get('project_id') != row['project_id']
            or server.get('status') not in STABLE_OS_STATES
            or 'os-ext-sts:task_state' not in server or server['os-ext-sts:task_state'] not in (None, '', 'None')
            or (row.get('host') and server.get('os-ext-srv-attr:host') != row['host'])):
        raise ValueError(f'{vid}: OpenStack ID/名称/项目/计算节点或任务状态与审计不符，禁止删除')
    if str(server.get('locked', 'false')).lower() in ('true', '1', 'yes'):
        raise ValueError(f'{vid}: 虚拟机已锁定，禁止删除')


def delete_selected(report):
    selected = []
    ids = set()
    for value in options.delete:
        vid = valid_uuid(value)
        if value.lower() != vid:
            raise ValueError('只接受标准格式的完整 UUID，不接受名称、前缀、通配符或全量删除')
        if vid in ids:
            raise ValueError('同一 UUID 不可重复指定')
        ids.add(vid)
        row = selected_row(report, vid)
        show_server(row)
        selected.append(row)
    warn('=== 本次待删除的明确指定实例 ===')
    for row in selected:
        warn(row['nova_uuid'], row['vm_name'], row['cleanup_status'], row['cleanup_reason'])
    if options.dry_run:
        warn('删除预演完成，不发送任何删除请求。')
        return 0
    # The shell heredoc occupies stdin. Use the user's actual terminal for irreversible confirmation.
    phrase = 'DELETE ' + str(len(selected))
    with open('/dev/tty', 'w', encoding='utf-8') as tty_out, open('/dev/tty', 'r', encoding='utf-8') as tty_in:
        tty_out.write('确认已核查归属且需要删除以上实例？输入 ' + phrase + ' 确认（其他输入取消）：')
        tty_out.flush()
        if tty_in.readline().strip() != phrase:
            warn('已取消，未删除任何虚拟机。')
            return 1
    cli = os.environ.get('OPENSTACK_BIN', 'openstack')
    failed = 0
    for old in selected:
        vid = old['nova_uuid']
        try:
            read_data()
            fresh, _ = audit(set())
            row = selected_row(fresh, vid)
            if fingerprint(row) != fingerprint(old):
                raise ValueError('审计信息已变化，请重新查看并选择')
            show_server(row)
            # Normal supported OpenStack lifecycle deletion only. No direct SQL delete, force delete,
            # libvirt destroy/undefine, or automatic retry of an ambiguous request.
            res = subprocess.run([cli, 'server', 'delete', '--wait', vid], stdout=subprocess.PIPE, stderr=subprocess.PIPE, timeout=600)
            if res.returncode != 0:
                raise ValueError('删除接口返回失败或结果未确认，请重新审计')
            read_data()
            if any(v['uuid'] == vid and v['deleted'] == '0' for v in rows['nova.instances']):
                raise ValueError('删除请求已完成，但Nova仍有未删除实例记录，需复查')
            warn(f'{vid}: Nova回收已确认。')
        except (ValueError, OSError, subprocess.CalledProcessError, subprocess.TimeoutExpired) as exc:
            failed += 1
            # Destructive API responses may contain sensitive content; print only our own sanitized summary.
            warn(f'{vid}: 未确认或未执行删除；{str(exc) if isinstance(exc, ValueError) else "现场核对/调用异常"}')
            # Stop if any operation is uncertain rather than continue an unsafe batch.
            break
    return 1 if failed else 0


def main():
    warn('数据来源: 历史 SQL 快照，非实时现场状态' if options.dump else '数据来源: 现场数据库只读一致性查询')
    read_data()
    report, mp_vm_ids = audit(active_uuids())
    write_report(report)
    print_summary(report, mp_vm_ids)
    if options.delete:
        return delete_selected(report)
    return 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except (OSError, ValueError, KeyError, subprocess.CalledProcessError, subprocess.TimeoutExpired) as exc:
        warn('执行失败：' + (str(exc) if isinstance(exc, ValueError) else '查询或调用未成功，请检查现场权限、连接与日志'))
        sys.exit(1)
PY
