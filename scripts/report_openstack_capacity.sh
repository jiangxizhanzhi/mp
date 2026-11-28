#!/usr/bin/env bash
# Read-only capacity report. No configuration changes or resource operations.
set -euo pipefail
export PYTHONIOENCODING=utf-8
exec python3 - "$@" <<'PY'
import argparse
import ast
import configparser
import datetime
import ipaddress
import json
import math
import os
from pathlib import Path
import re
import shlex
import shutil
import socket
import subprocess
import sys
import uuid
from decimal import Decimal, ROUND_FLOOR

GIB = 1024 ** 3
RESOURCES = {0: 'VCPU', 1: 'MEMORY_MB', 2: 'DISK_GB'}
WARNINGS = []
ENV = dict(os.environ, LC_ALL='C')


class ProbeError(RuntimeError):
    pass


def warning(section, reason):
    WARNINGS.append({'section': section, 'reason': reason})


def run(args, label, timeout=60, data=None):
    try:
        result = subprocess.run(args, input=data, env=ENV, stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE, timeout=timeout)
    except (OSError, subprocess.SubprocessError):
        raise ProbeError(label + '：命令不可用或超时') from None
    if result.returncode:
        # Neither command stderr nor config-parser messages are safe to print:
        # they can contain authentication URLs, passwords or configuration lines.
        raise ProbeError(label + '：调用失败，退出码=' + str(result.returncode))
    # SQL TSV can end in empty columns; stripping tabs would silently lose them.
    return result.stdout.decode('utf-8', 'replace').rstrip('\r\n')


def optional(section, callback):
    try:
        return callback()
    except Exception as exc:
        warning(section, str(exc) if isinstance(exc, ProbeError) else '读取未完成（' + type(exc).__name__ + '）')
        return None


def normalized(row):
    return {str(k).strip().lower().replace(' ', '_'): v for k, v in row.items()}


def sql(args, statement):
    exe = os.environ.get('MYSQL_BIN', 'mysql')
    raw = run([exe, '--batch', '--raw', '--skip-column-names', '--skip-reconnect',
               '--execute', 'START TRANSACTION READ ONLY; ' + statement + '; COMMIT'],
              '只读数据库查询', args.timeout)
    return [line.split('\t') for line in raw.splitlines()] if raw else []


def auth(args):
    if ENV.get('OS_AUTH_URL') or ENV.get('OS_CLOUD'):
        return
    rc = Path(args.admin_rc)
    if not rc.is_file():
        raise ProbeError('未加载 OpenStack 管理员环境，且指定的 admin.openrc 不存在')
    raw = run(['bash', '--noprofile', '--norc', '-c',
               'set -a; source "$1" >/dev/null 2>&1 || exit $?; env -0', 'bash', str(rc)],
              '加载现有管理员环境', args.timeout)
    for item in raw.split('\0'):
        key, sep, value = item.partition('=')
        if sep and (key.startswith('OS_') or key == 'PATH'):
            ENV[key] = value
    if not (ENV.get('OS_AUTH_URL') or ENV.get('OS_CLOUD')):
        raise ProbeError('管理员环境中未找到 OS_AUTH_URL 或 OS_CLOUD')


def osc(args, *command):
    auth(args)
    exe = os.environ.get('OPENSTACK_BIN') or shutil.which('openstack', path=ENV.get('PATH')) or '/opt/openstack_project/bin/openstack'
    return json.loads(run([exe, *command, '-f', 'json'], 'OpenStack 只读 ' + ' '.join(command[:3]), args.timeout))


def inventory_row(total, reserved, ratio, used, minimum=1, maximum=None, step=1):
    total, reserved, used = int(total), int(reserved), int(used)
    minimum, maximum, step = int(minimum), int(total if maximum is None else maximum), int(step)
    value = Decimal(str(ratio))
    if not value.is_finite() or value <= 0 or min(total, reserved, used) < 0 or reserved > total:
        raise ProbeError('Placement 库存包含非法容量值')
    if minimum<1 or maximum<minimum or step<1:raise ProbeError('Placement 单实例资源限制非法')
    capacity = int((Decimal(total - reserved) * value).to_integral_value(rounding=ROUND_FLOOR))
    return dict(total=total, reserved=reserved, ratio=str(value), allocated=used,
                capacity=capacity, remaining=capacity-used, available=max(0, capacity-used),
                min_unit=minimum, max_unit=maximum, step_size=step)


def inventory_db(args):
    names = ','.join("'" + n + "'" for n in args.nodes)  # names validated by argparse
    rows = sql(args, """SELECT p.name,i.resource_class_id,i.total,i.reserved,i.allocation_ratio,
        COALESCE(a.used,0),i.min_unit,i.max_unit,i.step_size
        FROM placement.inventories i JOIN placement.resource_providers p ON p.id=i.resource_provider_id
        LEFT JOIN (SELECT resource_provider_id,resource_class_id,SUM(used) used FROM placement.allocations
                   GROUP BY resource_provider_id,resource_class_id) a
          ON a.resource_provider_id=i.resource_provider_id AND a.resource_class_id=i.resource_class_id
        WHERE p.name IN (""" + names + ") AND p.parent_provider_id IS NULL AND i.resource_class_id IN (0,1,2)")
    result = {node: {} for node in args.nodes}
    for node, rc, total, reserved, ratio, used, minimum, maximum, step in rows:
        resource = RESOURCES[int(rc)]
        if resource in result[node]:
            raise ProbeError('Placement 节点资源类别重复')
        result[node][resource] = inventory_row(total, reserved, ratio, used, minimum, maximum, step)
    if any('VCPU' not in inv or 'MEMORY_MB' not in inv for inv in result.values()):
        raise ProbeError('所选节点缺少 VCPU/MEMORY_MB 库存；专用 CPU 节点不能套用共享 VCPU 计算')
    return result


def inventory_api(args):
    result = {}
    for node in args.nodes:
        providers = [normalized(r) for r in osc(args, 'resource', 'provider', 'list', '--name', node)]
        matches = [p for p in providers if p.get('name') == node and not p.get('parent_provider_uuid')]
        if len(matches) != 1:
            raise ProbeError(node + '：未找到唯一的根资源提供者')
        provider = str(uuid.UUID(matches[0]['uuid']))
        usage = osc(args, 'resource', 'provider', 'usage', 'show', provider)
        if isinstance(usage, list):
            usage = {normalized(r)['resource_class']: normalized(r)['usage'] for r in usage}
        if not isinstance(usage, dict):
            raise ProbeError(node + '：资源使用量响应无法识别')
        if any(not re.fullmatch(r'[A-Z][A-Z0-9_]*',str(k)) for k in usage):
            raise ProbeError(node+'：资源使用量映射不是资源类别/用量格式')
        usage = {k:int(v) for k,v in usage.items()}
        if any(v<0 for v in usage.values()):raise ProbeError(node+'：资源使用量为负')
        values = {}
        for row in osc(args, 'resource', 'provider', 'inventory', 'list', provider):
            row = normalized(row)
            rc = row['resource_class']
            if rc not in RESOURCES.values():
                continue
            values[rc] = inventory_row(row['total'], row['reserved'], row['allocation_ratio'], usage.get(rc, 0),
                                       row.get('min_unit', 1), row.get('max_unit'), row.get('step_size', 1))
        if 'VCPU' not in values or 'MEMORY_MB' not in values:
            raise ProbeError(node + '：共享 CPU/内存库存不完整')
        result[node] = values
    return result


# Only whitelisted fields leave each node. No credentials or complete config files.
CONFIG_HELPER = r'''
import configparser,datetime,json,os,shutil,socket,subprocess,sys,time
from pathlib import Path
CONFIG_KEYS={
 'DEFAULT':['host','reserved_host_cpus','reserved_host_memory_mb','reserved_host_disk_mb',
 'cpu_allocation_ratio','ram_allocation_ratio','disk_allocation_ratio',
 'initial_cpu_allocation_ratio','initial_ram_allocation_ratio','initial_disk_allocation_ratio',
 'service_down_time','vcpu_pin_set','compute_driver','instances_path','state_path',
 'scheduler_default_filters','scheduler_default_weighers','scheduler_host_subset_size',
 'scheduler_max_attempts','default_availability_zone'],
 'compute':['cpu_shared_set','cpu_dedicated_set'],
 'filter_scheduler':['max_instances_per_host','enabled_filters','weight_classes','host_subset_size',
 'cpu_weight_multiplier','ram_weight_multiplier','disk_weight_multiplier','io_ops_weight_multiplier','max_io_ops_per_host'],
 'scheduler':['driver','max_attempts','discover_hosts_in_cells_interval',
 'query_placement_for_availability_zone','limit_tenants_to_placement_aggregate','enable_isolated_aggregate_filter'],
 'libvirt':['virt_type','cpu_mode','cpu_model','file_backed_memory']}

def config_paths(argv):
    files=[];dirs=[];i=0
    while i<len(argv):
        value=argv[i];i+=1
        for flag,target in [('--config-file',files),('--config-dir',dirs)]:
            if value==flag and i<len(argv):target.append(argv[i]);i+=1;break
            if value.startswith(flag+'='):target.append(value.split('=',1)[1]);break
    paths=files or ['/etc/nova/nova.conf']
    for directory in dirs:paths.extend(str(p) for p in sorted(Path(directory).glob('*.conf')))
    return paths

def read_config(paths):
    c=configparser.ConfigParser(interpolation=None,strict=False)
    loaded=[];failed=False
    for name in paths:
        try:
            with open(name,encoding='utf-8') as f:c.read_file(f)
            loaded.append({'path':name,'modified_epoch':Path(name).stat().st_mtime})
        except Exception:failed=True
    cfg={}
    for section,keys in CONFIG_KEYS.items():
        for key in keys:
            if c.has_option(section,key):cfg[section+'.'+key]=c.get(section,key)
    return {'config':cfg,'config_files':loaded,'config_incomplete':failed}

def nova_processes(proc='/proc'):
    result=[]
    try:
        boot=float(next(x.split()[1] for x in (Path(proc)/'stat').read_text().splitlines() if x.startswith('btime ')))
        ticks=os.sysconf('SC_CLK_TCK')
    except Exception:boot=None
    for path in Path(proc).iterdir():
        if not path.name.isdigit():continue
        try:
            argv=(path/'cmdline').read_bytes().decode('utf-8','replace').strip('\0').split('\0')
            binary=next((Path(v).name for v in argv[:3] if Path(v).name in ['nova-compute','nova-scheduler']),None)
            if not binary:continue
            item={'binary':binary,'pid':int(path.name)}
            item.update(read_config(config_paths(argv)))
            stat=(path/'stat').read_text().rsplit(')',1)[1].split()
            started=boot+int(stat[19])/ticks if boot is not None else None
            item['started_at']=datetime.datetime.fromtimestamp(started,datetime.timezone.utc).isoformat() if started else None
            item['config_newer_than_process']=any(f['modified_epoch']>started for f in item['config_files']) if started else None
            result.append(item)
        except Exception:continue
    return result
'''

HOST_HELPER = CONFIG_HELPER + r'''
opt=json.loads(sys.argv[1]);out={'host':socket.gethostname().split('.')[0],'cpu_count':os.cpu_count(),'filesystems':[],'config':{}}
try:out['nova_processes']=nova_processes()
except Exception:out['nova_processes']=None
compute=next((p for p in (out['nova_processes'] or []) if p['binary']=='nova-compute'),None)
if compute:
    out['config']=compute['config'];out['config_source']='running nova-compute config files'
else:
    fallback=read_config(['/etc/nova/nova.conf']);out['config']=fallback['config']
    out['config_source']='file only; no nova-compute process confirmed';out['nova_config_unavailable']=fallback['config_incomplete']
try:out['load_average']=list(os.getloadavg())
except OSError:pass
def cpu():
    v=list(map(int,Path('/proc/stat').read_text().splitlines()[0].split()[1:9]));return sum(v),v[3]+v[4]
try:
    a=cpu();time.sleep(1);b=cpu();out['cpu_busy_percent']=round(100*(1-(b[1]-a[1])/(b[0]-a[0])),1) if b[0]>a[0] else None
except Exception:pass
try:
    mem={x.split(':',1)[0]:int(x.split()[1])*1024 for x in Path('/proc/meminfo').read_text().splitlines() if len(x.split())>=2 and x.split()[1].isdigit()}
    out['memory_total_bytes']=mem.get('MemTotal');out['memory_available_bytes']=mem.get('MemAvailable')
except Exception:pass
try:
    p=subprocess.run(['lscpu','-J'],env=dict(os.environ,LC_ALL='C'),stdout=subprocess.PIPE,stderr=subprocess.DEVNULL,timeout=5)
    data={r['field'].rstrip(':'):r['data'] for r in json.loads(p.stdout)['lscpu']}
    out['cpu_topology']={k:data[k] for k in ['Architecture','CPU(s)','Thread(s) per core','Core(s) per socket','Socket(s)'] if k in data}
except Exception:pass
try:
    domains={}
    for key,flags in [('all_uuids',['--all']),('running_uuids',[])]:
        p=subprocess.run(['virsh','--readonly','-c','qemu:///system','list',*flags,'--uuid'],stdout=subprocess.PIPE,stderr=subprocess.DEVNULL,timeout=8)
        if p.returncode:raise RuntimeError()
        domains[key]=sorted(x.strip() for x in p.stdout.decode().splitlines() if x.strip())
    out['libvirt_domains']={'total':len(domains['all_uuids']),'running':len(domains['running_uuids']),
        'uuid_samples':domains['all_uuids'][:opt['sample_limit']]}
except Exception:out['libvirt_domains']=None
try:
    c=configparser.ConfigParser(interpolation=None,strict=False)
    with open('/etc/cinder/cinder.conf',encoding='utf-8') as f:c.read_file(f)
    sections=['DEFAULT']+[s.strip() for s in c.defaults().get('enabled_backends','').split(',') if s.strip()]
    out['cinder_config']={s+'.'+k:c.get(s,k) for s in sections
        for k in ['enabled_backends','volume_driver','volume_backend_name','reserved_percentage',
        'max_over_subscription_ratio','nfs_snapshot_support','nfs_qcow2_volumes'] if c.has_option(s,k)}
except Exception:out['cinder_config']=None
paths=['/','/var/log','/var/lib/docker',opt['cinder_path']]
state=out['config'].get('DEFAULT.state_path','/opt/openstack/nova')
paths.append(out['config'].get('DEFAULT.instances_path',state+'/instances').replace('${state_path}',state).replace('$state_path',state))
for path in dict.fromkeys(paths):
    if not Path(path).exists():continue
    try:
        u=shutil.disk_usage(path);out['filesystems'].append({'path':path,'device':os.stat(path).st_dev,'total_bytes':u.total,'used_bytes':u.used,'free_bytes':u.free})
    except OSError:pass
print(json.dumps(out))
'''


def host_stats(args, node):
    options = json.dumps({'cinder_path': args.cinder_path,'sample_limit':args.sample_limit})
    if node.split('.')[0] == socket.gethostname().split('.')[0]:
        raw = run(['python3', '-c', HOST_HELPER, options], '本机物理资源采样', args.timeout)
    else:
        if args.skip_ssh:
            raise ProbeError('已指定 --skip-ssh，未采集远端物理资源')
        raw = run(['ssh', '-T', '-o', 'BatchMode=yes', '-o', 'StrictHostKeyChecking=yes',
                   '-o', 'ConnectTimeout=8', args.ssh_user+'@'+node,
                   'python3 - ' + shlex.quote(options)], node+' SSH 只读采样', args.timeout, HOST_HELPER.encode())
    value = json.loads(raw)
    if value.get('host') != node.split('.')[0]:
        raise ProbeError(node+'：采样返回主机名不匹配')
    return value


def services_db(args, stats):
    rows = sql(args, """SELECT host,disabled,forced_down,
        TIMESTAMPDIFF(SECOND,COALESCE(last_seen_up,created_at),UTC_TIMESTAMP())
        FROM nova.services WHERE deleted=0 AND binary='nova-compute'""")
    traits = sql(args, """SELECT p.name FROM placement.resource_provider_traits a
        JOIN placement.resource_providers p ON p.id=a.resource_provider_id
        JOIN placement.traits t ON t.id=a.trait_id WHERE t.name='COMPUTE_STATUS_DISABLED'""")
    disabled = {r[0] for r in traits}
    result = {}
    for node in args.nodes:
        matches = [r for r in rows if r[0] == node]
        if len(matches) != 1:
            continue
        _, off, forced, age = matches[0]
        threshold = int((stats.get(node) or {}).get('config', {}).get('DEFAULT.service_down_time', 60))
        up = forced == '0' and age != 'NULL' and abs(int(age)) <= threshold
        result[node] = dict(state='up' if up else 'down', status='enabled' if off == '0' else 'disabled',
                            placement_disabled=node in disabled, heartbeat_age_seconds=None if age == 'NULL' else int(age),
                            source='nova DB heartbeat, threshold='+str(threshold)+'s')
    return result


def services_api(args):
    result = {}
    for row in osc(args, 'compute', 'service', 'list', '--service', 'nova-compute'):
        row = normalized(row)
        if row.get('host') not in args.nodes:
            continue
        node = row['host']
        provider = [normalized(r) for r in osc(args, 'resource', 'provider', 'list', '--name', node)]
        if len(provider) != 1:
            continue
        traits = osc(args, 'resource', 'provider', 'trait', 'list', str(uuid.UUID(provider[0]['uuid'])))
        if not isinstance(traits,list) or any('name' not in normalized(t) for t in traits):
            raise ProbeError(node+'：Placement trait 响应字段不完整')
        disabled = any(normalized(t).get('name') == 'COMPUTE_STATUS_DISABLED' for t in traits)
        result[node] = dict(state=str(row.get('state', 'unknown')).lower(), status=str(row.get('status', 'unknown')).lower(),
                            placement_disabled=disabled, source='Nova/Placement API')
    return result


def read_with_fallback(section, primary, fallback):
    value = optional(section+' DB',primary)
    return value if value is not None else optional(section+' API',fallback)


def instances_db(args, database='nova'):
    if database not in ['nova','nova_cell0']:raise ProbeError('不支持的Nova实例库')
    statement = """SELECT uuid,COALESCE(host,''),COALESCE(vm_state,'unknown'),COALESCE(task_state,''),
        vcpus,memory_mb,root_gb,ephemeral_gb,created_at,COALESCE(launched_at,'')
        FROM nova.instances WHERE deleted=0 ORDER BY created_at DESC"""
    rows = sql(args,statement.replace('nova.instances',database+'.instances'))
    result = {'source':database+' DB (未删除记录)', 'nodes':{}, 'unassigned':None}
    for node in [*args.nodes,'<unassigned>']:
        result['nodes'][node] = dict(total=0,states={},tasks={},vcpu_sum=0,memory_mb_sum=0,
                                     root_gib_sum=0,ephemeral_gib_sum=0,recent_created=0,recent_launched=0,samples=[])
    cutoff = datetime.datetime.now(datetime.timezone.utc).replace(tzinfo=None)-datetime.timedelta(hours=args.recent_hours)
    for ident,host,state,task,cpu,mem,root,ephemeral,created,launched in rows:
        host = host or '<unassigned>'
        entry = result['nodes'].setdefault(host,dict(total=0,states={},tasks={},vcpu_sum=0,memory_mb_sum=0,
                    root_gib_sum=0,ephemeral_gib_sum=0,recent_created=0,recent_launched=0,samples=[]))
        entry['total'] += 1
        entry['states'][state] = entry['states'].get(state,0)+1
        if task:entry['tasks'][task] = entry['tasks'].get(task,0)+1
        for key,value in [('vcpu_sum',cpu),('memory_mb_sum',mem),('root_gib_sum',root),('ephemeral_gib_sum',ephemeral)]:
            entry[key] += int(value or 0)
        for key,value in [('recent_created',created),('recent_launched',launched)]:
            if value and value!='NULL' and datetime.datetime.fromisoformat(value)>=cutoff:entry[key] += 1
        if len(entry['samples'])<args.sample_limit:
            entry['samples'].append(dict(uuid=ident,state=state,task=task,vcpu=int(cpu or 0),memory_mb=int(mem or 0),
                                          created_at=created,launched_at=launched or None))
    result['unassigned'] = result['nodes'].pop('<unassigned>')
    return result


def instances_api(args):
    result = {'source':'Nova API, --all-projects --host --limit -1', 'nodes':{}, 'unassigned':None}
    for node in args.nodes:
        rows = osc(args,'server','list','--all-projects','--host',node,'--long','--no-name-lookup','--limit','-1')
        if not isinstance(rows,list):raise ProbeError('Nova 实例列表响应无法识别')
        entry = dict(total=len(rows),states={},tasks=None,vcpu_sum=None,memory_mb_sum=None,
                     root_gib_sum=None,ephemeral_gib_sum=None,recent_created=None,recent_launched=None,samples=[])
        for item in rows:
            item = normalized(item)
            if 'id' not in item or 'status' not in item:raise ProbeError('Nova 实例列表缺少 ID/status')
            state = str(item['status'])
            entry['states'][state] = entry['states'].get(state,0)+1
            if len(entry['samples'])<args.sample_limit:entry['samples'].append({'uuid':item['id'],'state':state})
        result['nodes'][node] = entry
    return result


def control_services_db(args, stats):
    rows = sql(args,"""SELECT host,binary,disabled,forced_down,
        TIMESTAMPDIFF(SECOND,COALESCE(last_seen_up,created_at),UTC_TIMESTAMP())
        FROM nova.services WHERE deleted=0 AND binary IN ('nova-scheduler','nova-conductor')""")
    result = []
    for host,binary,disabled,forced,age in rows:
        threshold = int((stats.get(host) or {}).get('config',{}).get('DEFAULT.service_down_time',60))
        result.append(dict(host=host,binary=binary,status='enabled' if disabled=='0' else 'disabled',
                           state='up' if forced=='0' and age!='NULL' and abs(int(age))<=threshold else 'down',
                           heartbeat_age_seconds=None if age=='NULL' else int(age),threshold_seconds=threshold,source='nova DB heartbeat'))
    return result


def control_services_api(args):
    result = []
    for row in osc(args,'compute','service','list'):
        row = normalized(row)
        if row.get('binary') not in ['nova-scheduler','nova-conductor']:continue
        result.append({k:row.get(k) for k in ['host','binary','status','state']})
    return result


AGGREGATE_KEYS = ['availability_zone','cpu_allocation_ratio','ram_allocation_ratio','disk_allocation_ratio',
                  'max_instances_per_host','max_io_ops_per_host','ram_weight_multiplier','cpu_weight_multiplier',
                  'disk_weight_multiplier','io_ops_weight_multiplier','filter_tenant_id']


def aggregates_db(args):
    keys = ','.join("'"+k+"'" for k in AGGREGATE_KEYS)
    rows = sql(args,"""SELECT a.id,a.name,COALESCE(h.host,''),COALESCE(m.`key`,''),COALESCE(m.value,'')
        FROM nova_api.aggregates a LEFT JOIN nova_api.aggregate_hosts h ON h.aggregate_id=a.id
        LEFT JOIN nova_api.aggregate_metadata m ON m.aggregate_id=a.id AND m.`key` IN ("""+keys+") ORDER BY a.id")
    values = {}
    for ident,name,host,key,value in rows:
        row = values.setdefault(ident,dict(id=ident,name=name,hosts=[],metadata={}))
        if host and host not in row['hosts']:row['hosts'].append(host)
        if key:row['metadata'][key] = value
    return list(values.values())


def aggregates_api(args):
    result = []
    for entry in osc(args,'aggregate','list'):
        ident = normalized(entry)['id']
        if not str(ident).isdigit():raise ProbeError('Nova aggregate ID 无法识别')
        row = normalized(osc(args,'aggregate','show',str(ident)))
        metadata = row.get('properties',{})
        if isinstance(metadata,str):
            try:metadata = ast.literal_eval(metadata)
            except (ValueError,SyntaxError):raise ProbeError('aggregate metadata 格式无法识别') from None
        if not isinstance(metadata,dict):raise ProbeError('aggregate metadata 不是映射')
        hosts = row.get('hosts',[])
        if isinstance(hosts,str):hosts = ast.literal_eval(hosts)
        result.append(dict(id=ident,name=row.get('name'),hosts=hosts,
                            metadata={k:v for k,v in metadata.items() if k in AGGREGATE_KEYS},
                            availability_zone=row.get('availability_zone')))
    return result


def cells_db(args):
    rows = sql(args,"""SELECT h.host,c.uuid,c.name,c.disabled FROM nova_api.host_mappings h
        JOIN nova_api.cell_mappings c ON c.id=h.cell_id""")
    return {host:dict(uuid=ident,name=name,disabled=off=='1') for host,ident,name,off in rows}


def hypervisors_db(args):
    rows = sql(args,"""SELECT host,uuid,hypervisor_hostname,hypervisor_type,vcpus,vcpus_used,
        memory_mb,memory_mb_used,local_gb,local_gb_used,running_vms,current_workload,stats,updated_at
        FROM nova.compute_nodes WHERE deleted=0""")
    result = {}
    for host,ident,name,kind,cpu,used_cpu,mem,used_mem,disk,used_disk,running,workload,stats,updated in rows:
        if host not in args.nodes:continue
        values = json.loads(stats or '{}')
        result[host] = dict(uuid=ident,hypervisor_hostname=name,hypervisor_type=kind,vcpu_total=int(cpu),
            vcpu_used=int(used_cpu),memory_mb_total=int(mem),memory_mb_used=int(used_mem),
            local_gib_total=int(disk),local_gib_used=int(used_disk),running_vms=int(running),
            current_workload=int(workload),updated_at=updated,
            num_instances=int(values['num_instances']) if 'num_instances' in values else None,
            num_io_ops=int(values['num_io_ops']) if 'num_io_ops' in values else None)
    return result


def allocation_audit_db(args):
    names = ','.join("'"+n+"'" for n in args.nodes)
    rows = sql(args,"""SELECT p.name,a.resource_class_id,
        CASE WHEN i.deleted=0 THEN 'existing_instance'
             WHEN m.id IS NOT NULL THEN 'migration_consumer'
             WHEN i.uuid IS NOT NULL THEN 'deleted_instance'
             ELSE 'unmatched_consumer' END AS kind,COUNT(DISTINCT a.consumer_uuid),SUM(a.used)
        FROM placement.allocations a JOIN placement.resource_providers p ON p.id=a.resource_provider_id
        LEFT JOIN nova.instances i ON i.uuid=a.consumer_uuid
        LEFT JOIN nova.migrations m ON m.uuid=a.consumer_uuid
        WHERE p.name IN ("""+names+") AND a.resource_class_id IN (0,1,2) GROUP BY p.name,a.resource_class_id,kind")
    return [dict(host=host,resource=RESOURCES[int(rc)],kind=kind,consumers=int(count),allocated=int(used))
            for host,rc,kind,count,used in rows]


def recent_faults_db(args, database='nova'):
    if database not in ['nova','nova_cell0']:raise ProbeError('不支持的Nova故障库')
    statement = ("""SELECT f.instance_uuid,COALESCE(i.host,''),f.code,
        CASE WHEN f.message LIKE '%NoValidHost%' OR f.message LIKE '%No valid host%'
                  OR f.details LIKE '%NoValidHost%' THEN 'NoValidHost'
             WHEN f.message LIKE '%BuildAbort%' THEN 'BuildAbort' ELSE 'Other' END AS category,f.created_at
        FROM nova.instance_faults f LEFT JOIN nova.instances i ON i.uuid=f.instance_uuid
        WHERE f.deleted=0 AND f.created_at>=UTC_TIMESTAMP()-INTERVAL """+str(args.recent_hours)+
        " HOUR ORDER BY f.created_at DESC LIMIT "+str(args.sample_limit))
    rows = sql(args,statement.replace('nova.instance_faults',database+'.instance_faults').replace('nova.instances',database+'.instances'))
    return [dict(uuid=ident,host=host or None,code=int(code),category=category,created_at=created,database=database)
            for ident,host,code,category,created in rows]


def recent_faults_both(args):
    rows = []
    found = False
    for database in ['nova','nova_cell0']:
        value = optional(database+' 近期故障',lambda database=database:recent_faults_db(args,database))
        if value is not None:rows.extend(value);found=True
    return sorted(rows,key=lambda r:r['created_at'],reverse=True)[:args.sample_limit] if found else None


def object_data(value):
    if isinstance(value,dict):
        if 'nova_object.data' in value:return object_data(value['nova_object.data'])
        return {k:object_data(v) for k,v in value.items()}
    if isinstance(value,list):return [object_data(v) for v in value]
    return value


def request_constraints_db(args):
    rows = sql(args,"""SELECT i.uuid,COALESCE(i.host,''),r.spec FROM nova.instances i
        JOIN nova_api.request_specs r ON r.instance_uuid=i.uuid WHERE i.deleted=0
        ORDER BY i.created_at DESC LIMIT """+str(args.sample_limit*len(args.nodes)))
    result = []
    for ident,host,raw in rows:
        spec = object_data(json.loads(raw))
        fields = {k:spec[k] for k in ['availability_zone','force_hosts','force_nodes','requested_destination'] if spec.get(k)}
        destination = fields.get('requested_destination')
        if isinstance(destination,dict):
            fields['requested_destination'] = {k:destination[k] for k in ['host','node','aggregates','forbidden_aggregates'] if k in destination}
        flavor = spec.get('flavor') or {}
        fields['flavor'] = {k:flavor[k] for k in ['vcpus','memory_mb','root_gb','ephemeral_gb'] if k in flavor}
        fields['flavor']['scheduling_extra_specs'] = {k:v for k,v in flavor.get('extra_specs',{}).items()
            if k.startswith(('hw:cpu','hw:mem','hw:numa','hw:emulator','hw:machine_type','hw:firmware_type',
                             'aggregate_instance_extra_specs:','trait:','resources:'))}
        fields['scheduler_hint_keys'] = sorted((spec.get('scheduler_hints') or {}).keys())
        result.append(dict(uuid=ident,host=host or None,constraints=fields))
    return result


def scheduler_settings(stats):
    processes = [dict(node=node,**process) for node,sample in stats.items() if sample
                 for process in sample.get('nova_processes') or [] if process['binary']=='nova-scheduler']
    configurations = [p.get('config',{}) for p in processes]
    keys = sorted({k for c in configurations for k in c if k.startswith(('filter_scheduler.','scheduler.','DEFAULT.scheduler_'))})
    conflicts = [k for k in keys if len({c.get(k) for c in configurations})>1]
    config = {k:configurations[0][k] for k in keys if k in configurations[0]} if configurations else {}
    filters = {f.strip() for c in configurations for f in c.get('filter_scheduler.enabled_filters',
                   c.get('DEFAULT.scheduler_default_filters','')).split(',') if f.strip()}
    if filters:config['filter_scheduler.enabled_filters'] = ','.join(sorted(filters))
    limits = [int(c.get('filter_scheduler.max_instances_per_host',50)) for c in configurations]
    if limits and any('filter_scheduler.max_instances_per_host' in c for c in configurations):
        config['filter_scheduler.max_instances_per_host'] = str(min(limits))
    return dict(processes=processes,config=config,conflicting_keys=conflicts,
                inferred_defaults={'filter_scheduler.max_instances_per_host':50} if any('filter_scheduler.max_instances_per_host' not in c for c in configurations) else {},
                source='running scheduler process config files' if processes else 'unknown; scheduler process not sampled')


def calculation_stats(stats, scheduler, aggregates):
    result = {}
    for node,sample in stats.items():
        entry = dict(sample or {})
        cfg = dict(entry.get('config',{}))
        # Limits are scheduler settings, not the nova-compute host's own settings.
        if scheduler.get('processes') or scheduler['config']:
            cfg = {k:v for k,v in cfg.items() if not k.startswith(('filter_scheduler.','scheduler.','DEFAULT.scheduler_'))}
        cfg.update(scheduler['config'])
        filters = {f.strip() for f in cfg.get('filter_scheduler.enabled_filters',cfg.get('DEFAULT.scheduler_default_filters','')).split(',')}
        maximum = int(cfg.get('filter_scheduler.max_instances_per_host',50))
        caps = [maximum] if 'NumInstancesFilter' in filters else []
        if 'AggregateNumInstancesFilter' in filters:
            values = [int(a['metadata']['max_instances_per_host']) for a in aggregates or []
                      if node in a['hosts'] and 'max_instances_per_host' in a['metadata']]
            caps.append(min(values) if values else maximum)
        if caps:
            entry['scheduling_max_instances'] = min(caps)
            entry['scheduling_limit_source'] = ('scheduler配置/聚合' if scheduler.get('processes') or scheduler['config'] else '未确认scheduler；暂按已读文件估算')
            if 'filter_scheduler.max_instances_per_host' not in cfg or scheduler.get('inferred_defaults'):
                entry['scheduling_limit_source'] += '；未显式填写全局上限，按Nova默认50'
        entry['config'] = cfg
        result[node] = entry
    return result


def resource_config_audit(inventory, stats):
    result = []
    for node,inv in inventory.items():
        cfg = (stats.get(node) or {}).get('config',{})
        for rc,prefix,reserve in [('VCPU','cpu','reserved_host_cpus'),('MEMORY_MB','ram','reserved_host_memory_mb'),
                                  ('DISK_GB','disk','reserved_host_disk_mb')]:
            if rc not in inv:continue
            raw = cfg.get('DEFAULT.'+prefix+'_allocation_ratio')
            explicit = Decimal(raw) if raw not in [None,'None',''] else None
            status = 'unknown' if raw is None else ('Placement 管理/初始化值模式' if explicit in [None,Decimal(0)] else
                     'match' if explicit==Decimal(inv[rc]['ratio']) else 'DIFFERENT')
            reserved = cfg.get('DEFAULT.'+reserve)
            converted = None if reserved is None else (int(reserved)+1023)//1024 if rc=='DISK_GB' else int(reserved)
            reserve_check = 'unknown' if converted is None else ('match' if converted==inv[rc]['reserved'] else
                '含额外磁盘保护预留' if rc=='DISK_GB' and inv[rc]['reserved']>=converted else 'DIFFERENT')
            result.append(dict(host=node,resource=rc,configured_ratio=raw,effective_ratio=inv[rc]['ratio'],ratio_check=status,
                               configured_reservation=reserved,placement_reserved=inv[rc]['reserved'],
                               reservation_check=reserve_check,
                               reservation_unit='MiB' if rc=='DISK_GB' else 'MiB' if rc=='MEMORY_MB' else 'logical CPU',
                               note='磁盘 reserved 还可能包含镜像缓存保护；Placement 为 GiB' if rc=='DISK_GB' else 'Placement 为当前调度依据'))
    return result


def scheduling_checks(report):
    checks = []
    for node in report['inventory']:
        state = report['services'].get(node)
        checks.append(dict(level='info' if eligible(state) else 'check',host=node,
            message='Nova/Placement/已取得的Cell条件允许参与调度' if eligible(state) else '节点调度条件未通过或未确认，详见服务/Cell状态'))
        entry = ((report.get('instances') or {}).get('nodes') or {}).get(node)
        if entry is None:
            checks.append(dict(level='unknown',host=node,message='当前实例分布未确认'))
        elif entry['total']:
            checks.append(dict(level='info',host=node,message='当前有 {} 台未删除实例，状态={}; 最近窗口创建={}、启动={}'.format(
                entry['total'],entry['states'],entry['recent_created'],entry['recent_launched'])))
        else:
            checks.append(dict(level='info',host=node,message='当前没有现存实例；清理后空闲不能证明调度异常，也不能证明新建成功'))
        limit = report['calculation_physical'][node].get('scheduling_max_instances')
        if limit is not None:
            used = report['scheduling_instance_counts'].get(node)
            checks.append(dict(level='check' if used is not None and used>=limit else 'info',host=node,
                message='实例数过滤上限={}，当前计数={}，槽位余量={}，依据={}'.format(limit,used,'未知' if used is None else max(0,limit-used),
                    report['calculation_physical'][node].get('scheduling_limit_source','未知'))))
        for proc in (report['physical'].get(node) or {}).get('nova_processes') or []:
            if proc.get('config_newer_than_process'):
                checks.append(dict(level='check',host=node,message=proc['binary']+' 配置文件晚于进程启动，需核查是否已加载；本脚本不重启服务'))
            if proc.get('config_incomplete'):
                checks.append(dict(level='unknown',host=node,message=proc['binary']+' 部分进程配置文件读取失败，配置核对尚不完整'))
    services = report.get('control_services')
    if services is None:checks.append(dict(level='unknown',message='调度器/Conductor 服务状态未确认'))
    else:
        for binary in ['nova-scheduler','nova-conductor']:
            active = [s for s in services if s['binary']==binary and s['state']=='up' and s['status']=='enabled']
            checks.append(dict(level='info' if active else 'check',message=binary+' 在线且启用的服务数='+str(len(active))))
    for row in report.get('resource_config_audit') or []:
        if row['ratio_check']=='DIFFERENT' or row['reservation_check']=='DIFFERENT':
            checks.append(dict(level='check',host=row['host'],message=row['resource']+' 文件配置与 Placement 不一致，以当前库存测算并核查加载/周期上报'))
    if report['scheduler']['conflicting_keys']:
        checks.append(dict(level='check',message='多个调度器的配置有差异：'+','.join(report['scheduler']['conflicting_keys'])))
    if not report['scheduler']['processes']:
        checks.append(dict(level='unknown',message='未采集到运行中的 nova-scheduler 配置；实例数过滤及权重配置仍需确认'))
    cell0 = (report.get('cell0_instances') or {}).get('unassigned')
    if cell0 and cell0['total']:
        checks.append(dict(level='check',message='Cell0 当前有 {} 台未调度成功的实例，最近窗口创建={}，详见状态及故障样本'.format(cell0['total'],cell0['recent_created'])))
    if report.get('cells') is None:
        checks.append(dict(level='unknown',message='Cell 映射/禁用状态未确认；节点合计仅确认服务和 Placement 标记'))
    for row in report.get('allocation_audit') or []:
        if row['kind'] in ['deleted_instance','unmatched_consumer']:
            checks.append(dict(level='check',host=row['host'],message='Placement {}={}，consumer分类={}；需核查残留或其他cell，不自动释放'.format(
                row['resource'],row['allocated'],row['kind'])))
    faults = report.get('recent_faults')
    if faults and any(f['category']=='NoValidHost' for f in faults):
        checks.append(dict(level='check',message='最近窗口内发现 NoValidHost；报告保留实例 UUID 和故障时间供关联'))
    return checks


def cinder_pools(args):
    result = []
    allowed = ['name','total_capacity_gb','free_capacity_gb','allocated_capacity_gb','provisioned_capacity_gb',
               'reserved_percentage','max_over_subscription_ratio','thin_provisioning_support','thick_provisioning_support']
    for row in osc(args, 'volume', 'backend', 'pool', 'list', '--long'):
        row = normalized(row)
        caps = row.get('capabilities', {})
        if isinstance(caps, str):
            try:caps = json.loads(caps)
            except json.JSONDecodeError:caps = ast.literal_eval(caps)
        if isinstance(caps, dict):
            row.update(normalized(caps))
        result.append({k: row[k] for k in allowed if k in row})
    return result


def neutron_pools(args):
    rows = sql(args, """SELECT 'pool',s.id,s.cidr,p.first_ip,p.last_ip FROM neutron.ipallocationpools p
        JOIN neutron.subnets s ON s.id=p.subnet_id;
        SELECT 'ip',subnet_id,ip_address,'','' FROM neutron.ipallocations""")
    grouped = {}
    allocated = {}
    for kind, subnet, a, b, c in rows:
        if kind == 'pool':
            first, last = ipaddress.ip_address(b), ipaddress.ip_address(c)
            if first.version != last.version or int(first) > int(last):
                raise ProbeError('Neutron 地址范围不一致')
            value = grouped.setdefault(subnet, {'subnet_id': subnet, 'cidr': a, 'ranges': []})
            value['ranges'].append([int(first), int(last)])
        elif kind == 'ip':
            allocated.setdefault(subnet, set()).add(int(ipaddress.ip_address(a)))
    result = []
    for subnet, row in grouped.items():
        merged = []
        for first, last in sorted(row.pop('ranges')):
            if merged and first <= merged[-1][1]+1:
                merged[-1][1] = max(last, merged[-1][1])
            else:
                merged.append([first, last])
        row['pool_addresses'] = sum(last-first+1 for first, last in merged)
        row['allocated_in_pool'] = sum(any(first <= ip <= last for first,last in merged) for ip in allocated.get(subnet, set()))
        row['remaining'] = row['pool_addresses']-row['allocated_in_pool']
        result.append(row)
    return result


def eligible(state):
    return bool(state and state.get('state') == 'up' and state.get('status') == 'enabled'
                and state.get('placement_disabled') is False and not state.get('cell_disabled')
                and not state.get('cell_mapping_missing'))


def free_resource(inv, resource, ratio=None):
    row = inv.get(resource)
    if row is None:
        return None
    if ratio is None:
        return row['available']
    return max(0, int(Decimal(row['total']-row['reserved'])*Decimal(str(ratio)))-row['allocated'])


def fits(inv, kali, centos, ratio, disk_mode, physical=None, instances=None):
    for rc, amount in [('VCPU', kali*4+centos), ('MEMORY_MB', (kali*4+centos)*1024)]:
        free = free_resource(inv, rc, ratio if rc == 'VCPU' else None)
        if free is None or amount > free:
            return False
        r = inv[rc]
        for n, size in [(kali, 4 if rc == 'VCPU' else 4096), (centos, 1 if rc == 'VCPU' else 1024)]:
            if n and not (r['min_unit'] <= size <= r['max_unit'] and size % r['step_size'] == 0):
                return False
    if disk_mode == 'local' and (free_resource(inv, 'DISK_GB') is None or (kali+centos)*30 > free_resource(inv, 'DISK_GB')):
        return False
    cfg = (physical or {}).get('config', {})
    filters = {f.strip() for f in cfg.get('filter_scheduler.enabled_filters', '').split(',')}
    maximum = cfg.get('filter_scheduler.max_instances_per_host')
    if 'NumInstancesFilter' in filters and maximum and instances is not None and kali+centos+instances > int(maximum):
        return False
    if (physical or {}).get('scheduling_max_instances') is not None and instances is not None and kali+centos+instances > physical['scheduling_max_instances']:
        return False
    return True


def placement(args, inventory, states, stats, counts, ratios):
    if len(args.nodes) != 2:
        return None
    a,b = args.nodes
    for kali in range(min(args.kali_count, free_resource(inventory[a], 'VCPU', ratios[0])//4)+1):
        for centos in range(min(args.centos_count, free_resource(inventory[a], 'VCPU', ratios[0]))+1):
            distributions = [(a,kali,centos,ratios[0]), (b,args.kali_count-kali,args.centos_count-centos,ratios[1])]
            if all((eligible(states.get(node)) or k+c == 0) and fits(inventory[node],k,c,r,args.disk_mode,stats.get(node),counts.get(node))
                   for node,k,c,r in distributions):
                return {node: {'kali':k,'centos':c} for node,k,c,r in distributions}
    return None


def analyze(args, inventory, states, stats, counts):
    requested = args.kali_count*4+args.centos_count
    rows = []
    if len(args.nodes) == 2:
        for first in (2,3,4):
            for second in (2,3,4):
                ratios = (first,second)
                available = sum(free_resource(inventory[n],'VCPU',r) for n,r in zip(args.nodes,ratios) if eligible(states.get(n)))
                rows.append({'ratios':dict(zip(args.nodes,ratios)), 'available_vcpu':available,
                             'after_request':available-requested,
                             'example_placement':placement(args,inventory,states,stats,counts,ratios)})
    return rows


def human(report):
    lines = ['OpenStack 当前可用资源报告（只读）', '采集时间：'+report['collected_at'],
             '库存来源：'+report['inventory_source'],
             '说明：allocated 是调度分配量，不是 CPU 实时使用率；磁盘调度额度不等于物理空闲。',
             '', '=== 节点调度核查 ===']
    for check in report['scheduling_checks']:
        lines.append('[{}] {}{}'.format(check['level'],check.get('host',''),('：' if check.get('host') else '')+check['message']))
    totals = {'VCPU':0,'MEMORY_MB':0,'DISK_GB':0}
    for node, inv in report['inventory'].items():
        state = report['services'].get(node, {})
        usable = eligible(state)
        lines += ['', '=== '+node+' ===', 'Nova state='+state.get('state','unknown')+', status='+state.get('status','unknown')
                  +', Placement disabled='+str(state.get('placement_disabled','unknown'))+', 计入服务/库存条件允许的合计='+str(usable)]
        if state.get('source'):lines.append('状态来源：'+state['source'])
        lines.append('Cell：'+json.dumps((report['cells'] or {}).get(node),ensure_ascii=False))
        instance = ((report['instances'] or {}).get('nodes') or {}).get(node)
        lines.append('当前实例分布：'+json.dumps(instance,ensure_ascii=False,indent=2))
        lines.append('调度实例计数={}，来源={}'.format(report['scheduling_instance_counts'].get(node),report['scheduling_count_sources'].get(node,'未知')))
        lines.append('计算节点上报：'+json.dumps((report['hypervisors'] or {}).get(node),ensure_ascii=False))
        for rc,row in inv.items():
            lines.append('  {}: total={} reserved={} ratio={} allocated={} capacity={} remaining={}'.format(
                rc,row['total'],row['reserved'],row['ratio'],row['allocated'],row['capacity'],row['remaining']))
            if usable:totals[rc] += row['available']
        sample = report['physical'].get(node)
        if not sample:
            lines.append('  实际 CPU/内存/磁盘：未确认，参见末尾缺失项')
            continue
        lines.append('  实时 CPU 忙碌率={}%，逻辑 CPU={}，load={}'.format(sample.get('cpu_busy_percent','未知'),sample.get('cpu_count','未知'),sample.get('load_average','未知')))
        if sample.get('memory_available_bytes') is not None:
            lines.append('  Linux MemAvailable={:.2f} GiB（不同于 Placement 内存余量）'.format(sample['memory_available_bytes']/GIB))
        lines.append('  CPU 拓扑：'+json.dumps(sample.get('cpu_topology'),ensure_ascii=False))
        lines.append('  libvirt 实际域数量（运行态与全部）：'+json.dumps(sample.get('libvirt_domains'),ensure_ascii=False))
        for fs in sample.get('filesystems',[]):
            lines.append('  {}: total={:.2f} GiB free={:.2f} GiB device={}'.format(fs['path'],fs['total_bytes']/GIB,fs['free_bytes']/GIB,fs['device']))
        root = next((f for f in sample.get('filesystems',[]) if f['path']=='/'),None)
        cinder = next((f for f in sample.get('filesystems',[]) if f['path']==report['cinder_path']),None)
        if cinder:
            lines.append('  Cinder 路径物理空闲={:.2f} GiB，扣除 {} GiB 保护线后={:.2f} GiB；与根盘同文件系统={}'.format(
                cinder['free_bytes']/GIB,report['disk_floor_gib'],max(0,cinder['free_bytes']/GIB-report['disk_floor_gib']),bool(root and root['device']==cinder['device'])))
        cfg = sample.get('config',{})
        lines.append('  Nova 资源配置（白名单）：'+json.dumps(cfg,ensure_ascii=False,sort_keys=True))
        lines.append('  进程配置来源/文件时间：'+json.dumps(sample.get('nova_processes'),ensure_ascii=False,indent=2))
        if sample.get('cinder_config') is not None:
            lines.append('  Cinder 文件配置（白名单）：'+json.dumps(sample['cinder_config'],ensure_ascii=False))
    lines += ['', '=== 服务/Placement 条件允许的节点合计（已取得Cell条件时也校验）===',
              '可新增 VCPU={}，MEMORY={:.2f} GiB，DISK_GB 调度余量={}'.format(totals['VCPU'],totals['MEMORY_MB']/1024,totals['DISK_GB']),
              '=== 本批新增需求 ===',
              'Kali={} 台（4 vCPU/4 GiB/30 GiB），CentOS={} 台（1 vCPU/1 GiB/30 GiB）'.format(report['kali_count'],report['centos_count']),
              '合计 {} vCPU / {} GiB 内存 / {} GiB 逻辑磁盘；磁盘模式假设={}'.format(report['requested_vcpu'],report['requested_vcpu'],report['requested_disk_gib'],report['disk_mode']),
              '按当前比例，CPU额度{}；内存额度{}。'.format('足够' if totals['VCPU']>=report['requested_vcpu'] else '不足', '足够' if totals['MEMORY_MB']>=report['requested_vcpu']*1024 else '不足')]
    if any('DISK_GB' not in inv and eligible(report['services'].get(n)) for n,inv in report['inventory'].items()):
        lines.append('部分节点缺少本地 DISK_GB 库存，上述磁盘仅为已知部分；共享盘需查后端。')
    lines.append('按当前库存及已取得实例数限制的两节点分配示例：'+json.dumps(report['current_example_placement'],ensure_ascii=False))
    lines += ['', '=== 调度器/Conductor 服务 ===',json.dumps(report['control_services'],ensure_ascii=False,indent=2),
              '=== 调度器过滤器、权重和实例数上限（从scheduler进程配置读取）===',json.dumps(report['scheduler'],ensure_ascii=False,indent=2),
              '=== 超分/预留文件配置与当前 Placement 比较 ===',json.dumps(report['resource_config_audit'],ensure_ascii=False,indent=2),
              '=== 主机聚合、可用区及资源限制元数据 ===',json.dumps(report['aggregates'],ensure_ascii=False,indent=2),
              '=== nova中未分配宿主机的当前实例 ===',json.dumps((report['instances'] or {}).get('unassigned'),ensure_ascii=False,indent=2),
              '=== Cell0未调度成功的当前实例 ===',json.dumps(report['cell0_instances'],ensure_ascii=False,indent=2),
              '=== 最近存活实例的定向调度/规格约束样本 ===',json.dumps(report['request_constraints'],ensure_ascii=False,indent=2),
              '=== Placement 分配归属（清理后残留/迁移消费者核查）===',json.dumps(report['allocation_audit'],ensure_ascii=False,indent=2),
              '=== 最近 {} 小时故障样本（含窗口内已删除实例）==='.format(report['recent_hours']),json.dumps(report['recent_faults'],ensure_ascii=False,indent=2)]
    lines += ['', '=== 仅计算 CPU 超分 2/3/4 的假设，不修改任何配置 ===']
    for row in report['ratio_comparison']:
        lines.append('{}: 可新增VCPU={}，本批后余额={}，CPU/内存{}，示例={}'.format(
            row['ratios'],row['available_vcpu'],row['after_request'],'测算可分配' if row['example_placement'] else '未找到可行分配',json.dumps(row['example_placement'],ensure_ascii=False)))
    lines += ['', '=== Cinder 后端上报 ===', json.dumps(report['cinder_pools'],ensure_ascii=False,indent=2),
              '=== Cinder 当前卷数量/逻辑容量（GiB）===', json.dumps(report['cinder_volumes'],ensure_ascii=False),
              '=== Neutron 地址池（仅统计分配池内的分配）===', json.dumps(report['neutron_pools'],ensure_ascii=False,indent=2),
              '=== 厂商二 MP 地址台账状态（与 Neutron 池分别统计）===', json.dumps(report['vendor_second_addresses'],ensure_ascii=False),
              '=== 指定项目配额（未指定项目则未采集）===', json.dumps(report['project_quota'],ensure_ascii=False),
              '', '注意：卷启动消耗 Cinder 后端；不能将 compute01 本地磁盘空闲当作 controller01 NFS 余量。',
              '精简卷/快照克隆的逻辑大小不等于立即实际占用；模板原卷大于30 GiB时实际需求可能更大。',
              '分配示例未验证项目配额、可用区、亲和性及镜像约束，不代表真实启动验收。',
              '未发起测试虚拟机创建：两节点都有现存实例仅证明当前分布，不证明下一次请求一定可调度。',
              'nova.conf 显式 ratio=0/未设置时可由 Placement 管理；initial_* 只影响初始化。当前调度额度以 Placement 为准。',
              '数据库默认查询 nova 与 nova_cell0；分配归属只关联 nova，unmatched_consumer 也可能来自其他cell，不能直接当作垃圾。',
              '各数据先后采集，创建/清理并发时可能变化；无法读取的项目不按0或空闲处理。',
              '', '=== 缺失项/部分采集告警 ===']
    lines += [w['section']+'：'+w['reason'] for w in report['warnings']] or ['无']
    return '\n'.join(lines)+'\n'


def parse_args(argv=None):
    p = argparse.ArgumentParser(prog='report_openstack_capacity.sh',description='只读核查 controller01/compute01 调度、超分与最新资源，默认测算36台Kali+36台CentOS。')
    p.add_argument('--source', choices=['auto','db','api'], default='auto', help='auto优先本机只读MySQL，失败后尝试API')
    p.add_argument('--nodes', nargs='+', default=['controller01','compute01'])
    p.add_argument('--admin-rc', default='/root/admin.openrc')
    p.add_argument('--ssh-user', default='root')
    p.add_argument('--skip-ssh', action='store_true', help='不采集远端物理资源，仍统计其Placement库存')
    p.add_argument('--cinder-path', default='/data/nfs_data/cinder')
    p.add_argument('--disk-floor-gib', type=int, default=300)
    p.add_argument('--kali-count', type=int, default=36)
    p.add_argument('--centos-count', type=int, default=36)
    p.add_argument('--disk-mode', choices=['volume','local'], default='volume', help='volume按Cinder卷启动；local测算额外本地磁盘调度额度')
    p.add_argument('--project', help='可选：追加指定项目的配额上限，不输出认证信息')
    p.add_argument('--recent-hours',type=int,default=24,help='近期实例/故障检查窗口，默认24小时')
    p.add_argument('--sample-limit',type=int,default=20,help='每节点实例样本数、近期故障样本上限，默认20')
    p.add_argument('--timeout', type=int, default=60)
    p.add_argument('--output-dir', help='报告输出目录；必须不存在，避免覆盖旧报告')
    args = p.parse_args(argv)
    if len(set(args.nodes))!=len(args.nodes) or any(not re.fullmatch(r'[A-Za-z0-9][A-Za-z0-9_.-]*',n) for n in args.nodes):
        p.error('节点名重复或包含不合法字符')
    if not re.fullmatch(r'[A-Za-z_][A-Za-z0-9_-]*',args.ssh_user):p.error('SSH用户名不合法')
    if min(args.kali_count,args.centos_count,args.disk_floor_gib)<0 or not 5<=args.timeout<=300:p.error('数量/保护线不能为负，timeout范围5～300秒')
    if not 1<=args.recent_hours<=720 or not 1<=args.sample_limit<=200:p.error('recent-hours范围1～720，sample-limit范围1～200')
    return args


def main(argv=None):
    args = parse_args(argv)
    WARNINGS.clear()
    stamp = datetime.datetime.now(datetime.timezone.utc).isoformat()
    inventory = None
    source = args.source
    if source in ['auto','db']:
        inventory = optional('Placement 数据库',lambda:inventory_db(args))
        if inventory is not None:source='db'
    if inventory is None and args.source in ['auto','api']:
        inventory = optional('Placement API',lambda:inventory_api(args));source='api'
    if inventory is None:
        print('无法取得当前 Placement 库存；没有把读取失败解释为资源空闲。',file=sys.stderr)
        for w in WARNINGS:print(w['section']+'：'+w['reason'],file=sys.stderr)
        return 2
    stats = {node:optional(node+' 物理资源',lambda node=node:host_stats(args,node)) for node in args.nodes}
    states = (read_with_fallback('Nova/Placement 调度状态',lambda:services_db(args,stats),lambda:services_api(args))
              if source=='db' else optional('Nova/Placement 调度状态 API',lambda:services_api(args))) or {}
    if any(node not in states for node in args.nodes):warning('调度状态','部分节点状态未知，不计入已确认可调度合计')
    cells = optional('Nova Cell 映射',lambda:cells_db(args))
    if cells is not None:
        for node,state in states.items():
            state['cell_mapping_missing'] = node not in cells
            state['cell_disabled'] = cells.get(node,{}).get('disabled')
    instances = read_with_fallback('当前实例分布',lambda:instances_db(args),lambda:instances_api(args))
    hypervisors = optional('计算节点上报',lambda:hypervisors_db(args))
    counts = {node:row['total'] for node,row in (instances or {}).get('nodes',{}).items()}
    count_sources = {node:'未删除实例记录（上界估计）' for node in counts}
    for node,row in (hypervisors or {}).items():
        if row['num_instances'] is not None:
            counts[node] = row['num_instances'];count_sources[node] = 'compute_nodes.stats.num_instances（调度HostState来源）'
    scheduler = optional('调度器配置',lambda:scheduler_settings(stats)) or dict(processes=[],config={},conflicting_keys=[],source='unknown')
    aggregates = read_with_fallback('主机聚合/可用区',lambda:aggregates_db(args),lambda:aggregates_api(args))
    calc_stats = optional('实例数限制计算',lambda:calculation_stats(stats,scheduler,aggregates)) or {n:(s or {}) for n,s in stats.items()}
    current = [float(inventory[n]['VCPU']['ratio']) for n in args.nodes]
    volume_counts = optional('Cinder 卷统计',lambda:sql(args,"SELECT status,COUNT(*),SUM(size) FROM cinder.volumes WHERE deleted=0 GROUP BY status"))
    vendor_addresses = optional('MP厂商二地址台账',lambda:sql(args,"SELECT status,reason,COUNT(*) FROM mp.mp_vendor_second_address_pool GROUP BY status,reason"))
    quota = None
    if args.project:
        raw_quota = optional('指定项目配额',lambda:osc(args,'quota','show',args.project))
        if isinstance(raw_quota,dict):
            allowed = {'cores','ram','instances','gigabytes','volumes','snapshots','ports','networks','subnets'}
            quota = {k:v for k,v in normalized(raw_quota).items() if k in allowed}
    report = dict(collected_at=stamp, inventory_source=source, inventory=inventory,services=states,physical=stats,
                  recent_hours=args.recent_hours,sample_limit=args.sample_limit,instances=instances,hypervisors=hypervisors,
                  cell0_instances=optional('Cell0 未调度实例',lambda:instances_db(args,'nova_cell0')),
                  scheduler=scheduler,aggregates=aggregates,cells=cells,calculation_physical=calc_stats,
                  scheduling_instance_counts=counts,scheduling_count_sources=count_sources,
                  control_services=read_with_fallback('调度器/Conductor 状态',lambda:control_services_db(args,stats),lambda:control_services_api(args)),
                  allocation_audit=optional('Placement 分配归属',lambda:allocation_audit_db(args)),
                  recent_faults=recent_faults_both(args),
                  request_constraints=optional('最近实例定向约束',lambda:request_constraints_db(args)),
                  resource_config_audit=optional('Nova 配置与 Placement 核对',lambda:resource_config_audit(inventory,stats)),
                  cinder_path=args.cinder_path,disk_floor_gib=args.disk_floor_gib,
                  kali_count=args.kali_count,centos_count=args.centos_count,requested_vcpu=args.kali_count*4+args.centos_count,
                  requested_disk_gib=(args.kali_count+args.centos_count)*30,disk_mode=args.disk_mode,
                  current_example_placement=placement(args,inventory,states,calc_stats,counts,current),
                  ratio_comparison=analyze(args,inventory,states,calc_stats,counts),
                  cinder_pools=optional('Cinder 后端池',lambda:cinder_pools(args)),cinder_volumes=volume_counts,
                  neutron_pools=optional('Neutron 地址池',lambda:neutron_pools(args)),
                  vendor_second_addresses=vendor_addresses,project_quota=quota,warnings=WARNINGS)
    report['scheduling_checks'] = scheduling_checks(report)
    text = human(report)
    out = Path(args.output_dir or ('capacity_report_'+socket.gethostname().split('.')[0]+'_'+datetime.datetime.now().strftime('%Y%m%d_%H%M%S')+'_'+uuid.uuid4().hex[:6]))
    out.mkdir(mode=0o700,parents=True,exist_ok=False)
    for name,body in [('report.txt',text),('report.json',json.dumps(report,ensure_ascii=False,indent=2)+'\n')]:
        target = out/name
        with target.open('x',encoding='utf-8') as handle:
            os.chmod(target,0o600);handle.write(body)
    print(text,end='')
    print('报告目录：'+str(out.resolve()))
    return 0


if __name__=='__main__':
    try:
        sys.exit(main())
    except Exception as exc:
        print('统计未完成：'+(str(exc) if isinstance(exc,ProbeError) else type(exc).__name__)+'（未修改云资源）',file=sys.stderr)
        sys.exit(2)
PY
