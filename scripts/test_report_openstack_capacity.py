"""Offline capacity-report tests: no site credentials, SSH or cloud calls."""
import contextlib
import datetime
import io
import json
import os
from pathlib import Path
import shlex
import socket
import sqlite3
import subprocess
import tempfile
import types
import unittest
from unittest.mock import Mock, patch

SCRIPT = Path(__file__).with_name('report_openstack_capacity.sh')
SOURCE = SCRIPT.read_text().split("<<'PY'\n",1)[1].rsplit('\nPY',1)[0]
mod = types.ModuleType('capacity_report')
exec(compile(SOURCE,str(SCRIPT),'exec'),mod.__dict__)


def inventory(after_cleanup=False):
    return {
        'controller01': {'VCPU':mod.inventory_row(56,14,2,0),
                         'MEMORY_MB':mod.inventory_row(385599,157696,1,0),
                         'DISK_GB':mod.inventory_row(1784,1534,1,0)},
        'compute01': {'VCPU':mod.inventory_row(56,8,2,0 if after_cleanup else 29),
                      'MEMORY_MB':mod.inventory_row(257583,12000,1,37888),
                      'DISK_GB':mod.inventory_row(8938,98,2,0)}}


def states():
    return {n:{'state':'up','status':'enabled','placement_disabled':False} for n in ['controller01','compute01']}


class CapacityTests(unittest.TestCase):
    def setUp(self):
        mod.WARNINGS.clear()
        self.args=mod.parse_args(['--disk-mode','volume'])

    def test_current_inventory_and_post_cleanup_are_recomputed(self):
        before=inventory();after=inventory(True)
        self.assertEqual(151,sum(mod.free_resource(i,'VCPU') for i in before.values()))
        self.assertEqual(180,sum(mod.free_resource(i,'VCPU') for i in after.values()))
        self.assertIsNone(mod.placement(self.args,before,states(),{}, {}, [2,2]))
        example=mod.placement(self.args,after,states(),{}, {}, [2,2])
        self.assertIsNotNone(example)
        self.assertEqual(36,sum(v['kali'] for v in example.values()))
        self.assertEqual(36,sum(v['centos'] for v in example.values()))

    def test_cpu_ratios_three_and_four_are_only_calculations(self):
        inv=inventory()
        rows=mod.analyze(self.args,inv,states(),{}, {})
        by_ratio={tuple(r['ratios'].values()):r for r in rows}
        self.assertEqual(199,by_ratio[(2,3)]['available_vcpu'])
        self.assertEqual(241,by_ratio[(3,3)]['available_vcpu'])
        self.assertEqual(331,by_ratio[(4,4)]['available_vcpu'])
        self.assertIsNotNone(by_ratio[(2,3)]['example_placement'])
        self.assertEqual('2',inv['compute01']['VCPU']['ratio'])

    def test_local_disk_constraints_are_not_cinder_constraints(self):
        inv=inventory()
        self.assertIsNotNone(mod.placement(self.args,inv,states(),{}, {}, [2,3]))
        self.args.disk_mode='local'
        self.assertIsNone(mod.placement(self.args,inv,states(),{}, {}, [2,3]))
        self.assertIsNotNone(mod.placement(self.args,inv,states(),{}, {}, [2,4]))

    def test_disabled_down_or_unknown_nodes_cannot_supply_capacity(self):
        for change in [{'state':'down'},{'status':'disabled'},{'placement_disabled':True},{'placement_disabled':None}]:
            s=states();s['compute01'].update(change)
            row=mod.analyze(self.args,inventory(),s,{}, {})[-1]
            self.assertEqual(168,row['confirmed_available_vcpu'])
            self.assertEqual(None if change.get('placement_disabled','absent') is None else 168,row['available_vcpu'])
            self.assertIsNone(row['example_placement'])

    def test_memory_and_max_unit_are_checked_per_node(self):
        inv=inventory()
        inv['controller01']['MEMORY_MB']=mod.inventory_row(1024,0,1,0)
        self.assertIsNone(mod.placement(self.args,inv,states(),{}, {}, [3,3]))
        inv=inventory();inv['controller01']['VCPU']['max_unit']=2
        inv['compute01']['VCPU']['max_unit']=2
        self.assertIsNone(mod.placement(self.args,inv,states(),{}, {}, [4,4]))

    def test_instance_limit_is_checked_when_filter_enabled(self):
        stats={n:{'config':{'filter_scheduler.enabled_filters':'ComputeFilter,NumInstancesFilter',
                          'filter_scheduler.max_instances_per_host':'4'}} for n in self.args.nodes}
        self.assertIsNone(mod.placement(self.args,inventory(),states(),stats,{'controller01':2,'compute01':2},[4,4]))

    def test_negative_remaining_is_reported_without_creating_available_cpu(self):
        row=mod.inventory_row(8,2,1,9)
        self.assertEqual(-3,row['remaining'])
        self.assertEqual(0,row['available'])
        self.assertEqual(7,mod.inventory_row(10,2,'1.2',2)['remaining'])
        for ratio in ['NaN','Infinity',0,-1]:
            with self.assertRaises(mod.ProbeError):mod.inventory_row(8,0,ratio,1)

    def test_invalid_nodes_are_rejected_before_database_or_ssh(self):
        for argv in [['--nodes',"controller01';DROP"],['--nodes','controller01','controller01'],['--kali-count','-1']]:
            with contextlib.redirect_stderr(io.StringIO()),self.assertRaises(SystemExit):mod.parse_args(argv)

    def test_database_inventory_uses_current_allocations(self):
        rows=[]
        for n,values in inventory().items():
            for rc,row in values.items():
                number=next(k for k,v in mod.RESOURCES.items() if v==rc)
                rows.append([n,str(number),str(row['total']),str(row['reserved']),row['ratio'],str(row['allocated']),'1',str(row['max_unit']),'1'])
        with patch.object(mod,'sql',return_value=rows):
            self.assertEqual(67,mod.inventory_db(self.args)['compute01']['VCPU']['remaining'])
        with patch.object(mod,'sql',return_value=[]),self.assertRaises(mod.ProbeError):mod.inventory_db(self.args)

    def test_api_inventory_accepts_usage_list_and_dict_shapes(self):
        provider='11111111-2222-3333-4444-555555555555'
        def fake(args,*cmd):
            if cmd[:3]==('resource','provider','list'):return [{'name':cmd[-1],'uuid':provider,'parent_provider_uuid':None}]
            if cmd[:4]==('resource','provider','usage','show'):
                return [{'resource_class':'VCPU','usage':29}] if cmd[-1]==provider else {}
            return [{'resource_class':'VCPU','total':56,'reserved':8,'allocation_ratio':2},
                    {'resource_class':'MEMORY_MB','total':257583,'reserved':12000,'allocation_ratio':1}]
        with patch.object(mod,'osc',side_effect=fake):
            self.assertEqual(67,mod.inventory_api(self.args)['compute01']['VCPU']['remaining'])

    def test_database_service_state_includes_placement_disable_trait(self):
        with patch.object(mod,'sql',side_effect=[[
                ['controller01','0','0','10'],['compute01','0','0','10']],[['compute01']]]):
            result=mod.services_db(self.args,{})
        self.assertTrue(mod.eligible(result['controller01']))
        self.assertFalse(mod.eligible(result['compute01']))

    def test_neutron_pool_union_and_allocation_deduplication(self):
        rows=[['pool','subnet','198.51.100.0/24','198.51.100.10','198.51.100.20'],
              ['pool','subnet','198.51.100.0/24','198.51.100.20','198.51.100.30'],
              ['ip','subnet','198.51.100.10','',''],['ip','subnet','198.51.100.10','',''],
              ['ip','subnet','198.51.100.30','',''],['ip','subnet','198.51.100.1','','']]
        with patch.object(mod,'sql',return_value=rows):result=mod.neutron_pools(self.args)[0]
        self.assertEqual(21,result['pool_addresses'])
        self.assertEqual(2,result['allocated_in_pool'])
        self.assertEqual(19,result['remaining'])

    def test_failed_command_does_not_echo_secret_stderr(self):
        failure=subprocess.CompletedProcess(['fake'],1,b'',b'OS_PASSWORD=DO_NOT_PRINT token=DO_NOT_PRINT')
        with patch.object(mod.subprocess,'run',return_value=failure):
            with self.assertRaises(mod.ProbeError) as ctx:mod.run(['fake'],'test query')
        self.assertNotIn('DO_NOT_PRINT',str(ctx.exception))

    def test_empty_tsv_columns_are_preserved_by_command_runner(self):
        value=subprocess.CompletedProcess(['mysql'],0,b'ip\tsubnet\t192.168.1.1\t\t\n',b'')
        with patch.object(mod.subprocess,'run',return_value=value):
            self.assertEqual([['ip','subnet','192.168.1.1','','']],mod.sql(self.args,'SELECT test'))

    def test_remote_paths_are_quoted_data_not_remote_shell_code(self):
        self.args.cinder_path="/data/test ' $(must_not_run)"
        with patch.object(mod.socket,'gethostname',return_value='controller01'),patch.object(mod,'run',return_value='{"host":"compute01"}') as call:
            mod.host_stats(self.args,'compute01')
        command=call.call_args[0][0]
        self.assertIn('StrictHostKeyChecking=yes',command)
        params=shlex.split(command[-1])
        self.assertEqual(['python3','-'],params[:2])
        self.assertEqual(self.args.cinder_path,json.loads(params[2])['cinder_path'])

    def test_skip_ssh_does_not_issue_ssh(self):
        self.args.skip_ssh=True
        with patch.object(mod.socket,'gethostname',return_value='controller01'),patch.object(mod,'run') as call:
            with self.assertRaises(mod.ProbeError):mod.host_stats(self.args,'compute01')
        call.assert_not_called()

    def test_host_helper_has_valid_python_syntax(self):
        compile(mod.HOST_HELPER,'host helper','exec')

    def test_end_to_end_report_preserves_unknown_fields_and_restricts_file_permissions(self):
        def unavailable(*a,**k):raise mod.ProbeError('optional source unavailable')
        with tempfile.TemporaryDirectory() as tmp, contextlib.redirect_stdout(io.StringIO()) as output, \
             patch.object(mod,'inventory_db',return_value=inventory()),patch.object(mod,'host_stats',side_effect=unavailable), \
             patch.object(mod,'services_db',return_value=states()),patch.object(mod,'sql',side_effect=unavailable), \
             patch.object(mod,'cinder_pools',side_effect=unavailable),patch.object(mod,'osc',side_effect=unavailable):
            target=Path(tmp)/'report'
            self.assertEqual(0,mod.main(['--output-dir',str(target)]))
            data=json.loads((target/'report.json').read_text())
            self.assertIsNone(data['cinder_pools'])
            self.assertIsNone(data['cinder_volumes'])
            self.assertIsNone(data['neutron_pools'])
            self.assertTrue(data['warnings'])
            self.assertEqual(0o600,(target/'report.json').stat().st_mode & 0o777)
            self.assertEqual(0o700,target.stat().st_mode & 0o777)
            self.assertIn('allocated',output.getvalue())

    def test_no_duplicate_top_level_function_definitions(self):
        tree=mod.ast.parse(SOURCE)
        names=[n.name for n in tree.body if isinstance(n,mod.ast.FunctionDef)]
        self.assertEqual(len(names),len(set(names)))

    def test_disabled_or_unmapped_cells_cannot_supply_capacity(self):
        for flag in ['cell_disabled','cell_mapping_missing']:
            s=states();s['compute01'][flag]=True
            self.assertEqual(168,mod.analyze(self.args,inventory(),s,{}, {})[-1]['available_vcpu'])

    def test_instance_distribution_after_cleanup_keeps_zero_and_unassigned_distinct(self):
        now=datetime.datetime.now(datetime.timezone.utc).replace(tzinfo=None).isoformat(' ')
        rows=[['id1','compute01','active','','4','4096','30','0',now,now],
              ['id2','','error','scheduling','1','1024','30','0',now,'']]
        with patch.object(mod,'sql',return_value=rows):result=mod.instances_db(self.args)
        self.assertEqual(0,result['nodes']['controller01']['total'])
        self.assertEqual(1,result['nodes']['compute01']['recent_launched'])
        self.assertEqual(4096,result['nodes']['compute01']['memory_mb_sum'])
        self.assertEqual({'error':1},result['unassigned']['states'])
        self.assertEqual({'scheduling':1},result['unassigned']['tasks'])

    def test_api_instance_distribution_requests_all_projects_and_all_pages(self):
        with patch.object(mod,'osc',return_value=[{'ID':'vm','Status':'ACTIVE','Networks':'secret'}]) as call:
            result=mod.instances_api(self.args)
        self.assertEqual(1,result['nodes']['controller01']['total'])
        self.assertIsNone(result['nodes']['controller01']['recent_created'])
        self.assertNotIn('secret',json.dumps(result))
        command=call.call_args_list[0][0]
        self.assertIn('--all-projects',command);self.assertIn('-1',command)

    def test_scheduler_limits_override_the_compute_host_file(self):
        stats={n:{'config':{'filter_scheduler.enabled_filters':'ComputeFilter','filter_scheduler.max_instances_per_host':'1000'}} for n in self.args.nodes}
        stats['controller01']['nova_processes']=[{'binary':'nova-scheduler','config':{
            'filter_scheduler.enabled_filters':'ComputeFilter, NumInstancesFilter','filter_scheduler.max_instances_per_host':'56'}}]
        scheduler=mod.scheduler_settings(stats)
        effective=mod.calculation_stats(stats,scheduler,[])
        self.assertEqual(56,effective['compute01']['scheduling_max_instances'])
        self.assertIsNone(mod.placement(self.args,inventory(True),states(),effective,{'controller01':50,'compute01':50},[4,4]))
        self.assertEqual('1000',stats['compute01']['config']['filter_scheduler.max_instances_per_host'])

    def test_unused_compute_filter_setting_does_not_impose_a_limit(self):
        stats={n:{'config':{'filter_scheduler.enabled_filters':'NumInstancesFilter','filter_scheduler.max_instances_per_host':'1'}} for n in self.args.nodes}
        scheduler={'config':{'filter_scheduler.enabled_filters':'ComputeFilter'}}
        effective=mod.calculation_stats(stats,scheduler,[])
        self.assertNotIn('scheduling_max_instances',effective['controller01'])
        self.assertIsNotNone(mod.placement(self.args,inventory(True),states(),effective,{},[2,2]))

    def test_omitted_scheduler_maximum_does_not_inherit_compute_maximum(self):
        stats={'compute01':{'config':{'filter_scheduler.max_instances_per_host':'1000'}}}
        scheduler={'config':{'filter_scheduler.enabled_filters':'NumInstancesFilter'}}
        effective=mod.calculation_stats(stats,scheduler,[])
        self.assertEqual(50,effective['compute01']['scheduling_max_instances'])
        self.assertIn('默认50',effective['compute01']['scheduling_limit_source'])

    def test_aggregate_instance_limit_and_scheduler_default_limit(self):
        scheduler={'config':{'filter_scheduler.enabled_filters':'AggregateNumInstancesFilter'}}
        aggs=[{'hosts':['compute01'],'metadata':{'max_instances_per_host':'60'}}]
        effective=mod.calculation_stats({n:{} for n in self.args.nodes},scheduler,aggs)
        self.assertEqual(60,effective['compute01']['scheduling_max_instances'])
        self.assertEqual(50,effective['controller01']['scheduling_max_instances'])

    def test_conflicting_scheduler_settings_are_exposed(self):
        processes=[{'binary':'nova-scheduler','config':{'filter_scheduler.max_instances_per_host':str(n),
                    'filter_scheduler.enabled_filters':'NumInstancesFilter'}} for n in [56,80]]
        result=mod.scheduler_settings({'controller01':{'nova_processes':processes},'compute01':None})
        self.assertEqual('56',result['config']['filter_scheduler.max_instances_per_host'])
        self.assertIn('filter_scheduler.max_instances_per_host',result['conflicting_keys'])

    def test_ratio_drift_and_disk_cache_reservation_are_distinguished(self):
        cfg={'DEFAULT.cpu_allocation_ratio':'4','DEFAULT.reserved_host_cpus':'8',
             'DEFAULT.disk_allocation_ratio':'1','DEFAULT.reserved_host_disk_mb':'100000'}
        result=mod.resource_config_audit(inventory(),{'controller01':{'config':cfg}})
        controller={r['resource']:r for r in result if r['host']=='controller01'}
        self.assertEqual('DIFFERENT',controller['VCPU']['ratio_check'])
        self.assertEqual('DIFFERENT',controller['VCPU']['reservation_check'])
        self.assertEqual('含额外磁盘保护预留',controller['DISK_GB']['reservation_check'])
        cfg['DEFAULT.cpu_allocation_ratio']='0'
        result=mod.resource_config_audit(inventory(),{'controller01':{'config':cfg}})
        self.assertIn('Placement',result[0]['ratio_check'])

    def test_aggregate_queries_use_api_database_and_redact_unrelated_metadata(self):
        rows=[['1','lab','controller01','availability_zone','nova'],['1','lab','compute01','max_instances_per_host','56']]
        with patch.object(mod,'sql',return_value=rows) as call:result=mod.aggregates_db(self.args)
        self.assertEqual(['controller01','compute01'],result[0]['hosts'])
        self.assertIn('nova_api.aggregates',call.call_args[0][1])
        def api(args,*cmd):
            if cmd==('aggregate','list'):return [{'ID':1}]
            return {'id':1,'name':'lab','hosts':"['controller01']",'properties':"{'availability_zone':'nova','password':'do-not-print'}"}
        with patch.object(mod,'osc',side_effect=api):result=mod.aggregates_api(self.args)
        self.assertNotIn('do-not-print',json.dumps(result))

    def test_request_spec_objects_preserve_only_scheduling_constraints(self):
        spec={'nova_object.data':{'force_hosts':['compute01'],'availability_zone':'nova','admin_password':'do-not-print',
            'requested_destination':{'nova_object.data':{'host':'compute01','password':'do-not-print'}},
            'flavor':{'nova_object.data':{'vcpus':4,'extra_specs':{'hw:cpu_policy':'dedicated','secret':'do-not-print'}}}}}
        with patch.object(mod,'sql',return_value=[['id','compute01',json.dumps(spec)]]):result=mod.request_constraints_db(self.args)
        self.assertNotIn('do-not-print',json.dumps(result))
        self.assertIn('dedicated',json.dumps(result))
        self.assertEqual(['compute01'],result[0]['constraints']['force_hosts'])

    def test_compute_stats_returns_scheduler_count_without_unrelated_stats(self):
        row=['compute01','id','compute01','QEMU','56','29','257583','37888','8938','0','8','0',
             '{"num_instances":11,"num_io_ops":2,"secret":"do-not-print"}','2026-11-28 10:00:00']
        with patch.object(mod,'sql',return_value=[row]):result=mod.hypervisors_db(self.args)
        self.assertEqual(11,result['compute01']['num_instances'])
        self.assertNotIn('do-not-print',json.dumps(result))

    def test_config_merge_uses_runtime_files_and_excludes_credentials(self):
        helper={};exec(mod.CONFIG_HELPER,helper)
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);base=root/'base.conf';extra=root/'conf.d';extra.mkdir()
            base.write_text('[DEFAULT]\ncpu_allocation_ratio=2\n[database]\nconnection=mysql://user:do-not-print@host/nova\n')
            (extra/'10-limit.conf').write_text('[DEFAULT]\ncpu_allocation_ratio=3\n[filter_scheduler]\nmax_instances_per_host=56\n')
            (extra/'20-override.conf').write_text('[DEFAULT]\ncpu_allocation_ratio=4\n')
            files=helper['config_paths'](['--config-file',str(base),'--config-dir='+str(extra)])
            result=helper['read_config'](files)
            self.assertFalse(result['config_incomplete'])
            self.assertEqual('4',result['config']['DEFAULT.cpu_allocation_ratio'])
            self.assertNotIn('do-not-print',json.dumps(result))

    def test_running_nova_process_reports_config_changed_after_start(self):
        helper={};exec(mod.CONFIG_HELPER,helper)
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);(root/'stat').write_text('btime 1\n');proc=root/'123';proc.mkdir()
            cfg=root/'nova.conf';cfg.write_text('[DEFAULT]\ncpu_allocation_ratio=3\n')
            (proc/'cmdline').write_bytes(('python3\0/usr/bin/nova-compute\0--config-file\0'+str(cfg)+'\0').encode())
            (proc/'stat').write_text('123 (nova-compute) S '+'0 '*18+'0 '+'0 '*5)
            result=helper['nova_processes'](str(root))
            self.assertEqual(1,len(result))
            self.assertTrue(result[0]['config_newer_than_process'])
            self.assertEqual('3',result[0]['config']['DEFAULT.cpu_allocation_ratio'])

    def test_usage_response_cannot_silently_turn_unknown_shape_into_zero(self):
        provider='11111111-2222-3333-4444-555555555555'
        def fake(args,*cmd):
            if cmd[:3]==('resource','provider','list'):return [{'name':cmd[-1],'uuid':provider,'parent_provider_uuid':None}]
            return {'usages':{'VCPU':29}}
        with patch.object(mod,'osc',side_effect=fake),self.assertRaises(mod.ProbeError):mod.inventory_api(self.args)

    def test_cinder_cli_python_repr_capabilities_are_supported(self):
        with patch.object(mod,'osc',return_value=[{'Name':'nfs#pool','Capabilities':"{'free_capacity_gb':626,'max_over_subscription_ratio':20,'secret':'do-not-print'}"}]):
            result=mod.cinder_pools(self.args)
        self.assertEqual(626,result[0]['free_capacity_gb'])
        self.assertNotIn('do-not-print',json.dumps(result))

    def test_standalone_bash_entrypoint_with_offline_command_fixtures(self):
        now=datetime.datetime.now(datetime.timezone.utc).replace(tzinfo=None).isoformat(' ')
        inv=[]
        for node,resources in inventory().items():
            for number,rc in mod.RESOURCES.items():
                row=resources[rc]
                inv.append([node,number,row['total'],row['reserved'],row['ratio'],row['allocated'],1,row['max_unit'],1])
        spec={'nova_object.data':{'availability_zone':'nova','force_hosts':['compute01'],'admin_password':'site-admin-secret'}}
        data={
            'FROM placement.inventories':inv,
            "`binary`='nova-compute'": [[n,0,0,3] for n in self.args.nodes],
            'FROM placement.resource_provider_traits':[],
            'FROM nova_api.host_mappings':[[n,'cell-uuid','cell1',0] for n in self.args.nodes],
            'FROM nova.instances WHERE deleted=0 ORDER BY':[
                ['vm1','controller01','active','',1,1024,30,0,now,now],
                ['vm2','compute01','active','',4,4096,30,0,now,now]],
            'FROM nova_cell0.instances WHERE deleted=0 ORDER BY':[],
            'FROM nova.compute_nodes':[
                [n,'node-uuid',n,'QEMU',56,0,257583,0,8938,0,1,0,'{"num_instances":1,"num_io_ops":0}',now]
                for n in self.args.nodes],
            'FROM nova_api.aggregates':[],
            'SELECT status,COUNT(*),SUM(size) FROM cinder.volumes':[['in-use',2,60]],
            'SELECT host,status,COUNT(*),SUM(size) FROM cinder.volumes':[['controller01@nfs','in-use',2,60]],
            'FROM cinder.services':[['controller01@nfs','cinder-volume',0,3]],
            'FROM glance.images i':[
                ['kali-image','kali','active','qcow2','bare',0,30,'block_device_mapping',
                 json.dumps([{'boot_index':0,'source_type':'snapshot','destination_type':'volume','volume_size':30,
                              'snapshot_id':'11111111-2222-3333-4444-555555555555'}])],
                ['centos-image','centos8','active','qcow2','bare',1024,0,'','']],
            'FROM cinder.snapshots s':[['11111111-2222-3333-4444-555555555555','source-volume',30,'available',30,'controller01@nfs','controller01:/data/nfs_data/cinder']],
            'LEFT JOIN nova.block_device_mapping b':[['vm2','compute01','snapshot','volume','vol2','snap',30,'controller01@nfs',30,'controller01:/data/nfs_data/cinder']],
            'FROM mp.mp_vendor_second_address_pool':[['FREE','PROBE_CONFIRMED_FREE',198]],
            "`binary` IN ('nova-scheduler','nova-conductor')":[['controller01',b,0,0,3] for b in ['nova-scheduler','nova-conductor']],
            'FROM information_schema.columns':[['consumer_id','varchar']],
            'FROM placement.allocations a JOIN':[['compute01',0,'deleted_instance',1,4]],
            'FROM nova.instance_faults':[['failed-vm','',500,'NoValidHost',now]],
            'FROM nova_cell0.instance_faults':[['failed-cell0-vm','',500,'NoValidHost',now]],
            'JOIN nova_api.request_specs':[['vm2','compute01',json.dumps(spec)]],
            'FROM neutron.ipallocationpools':[['pool','subnet','192.168.10.0/24','192.168.10.100','192.168.10.200'],
                                            ['ip','subnet','192.168.10.100','','']]}
        fixture_code='''#!/usr/bin/env python3
import json,os,sys
from pathlib import Path
kind=Path(sys.argv[0]).name
data=json.loads(Path(os.environ['CAPACITY_TEST_DATA']).read_text())
if kind=='mysql_fixture':
    query=sys.argv[sys.argv.index('--execute')+1]
    if not query.startswith('START TRANSACTION READ ONLY; ') or not query.endswith('; COMMIT'):sys.exit(91)
    matches=[rows for key,rows in data.items() if key in query]
    if len(matches)!=1:sys.exit(92)
    print('\\n'.join('\\t'.join(str(value) for value in row) for row in matches[0]))
elif kind=='ssh':
    sys.stdin.read()
    host=next(a for a in sys.argv if a.startswith('root@')).split('@')[1]
    cfg={'DEFAULT.cpu_allocation_ratio':'2','DEFAULT.ram_allocation_ratio':'1',
         'filter_scheduler.enabled_filters':'ComputeFilter,NumInstancesFilter','filter_scheduler.max_instances_per_host':'56'}
    processes=[{'binary':'nova-compute','pid':123,'config':cfg,'config_files':[],'config_newer_than_process':False}]
    if host=='controller01':processes.append(dict(processes[0],binary='nova-scheduler',pid=124))
    print(json.dumps({'host':host,'config':cfg,'nova_processes':processes,'filesystems':[],
                      'cpu_count':56,'libvirt_domains':{'total':1,'running':1}}))
elif kind=='openstack_fixture':
    if sys.argv[1:6]!=['volume','backend','pool','list','--long']:sys.exit(93)
    print(json.dumps([{'Name':'nfs#pool','Capabilities':{'total_capacity_gb':1784,'free_capacity_gb':626}}]))
else:sys.exit(94)
'''
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);(root/'data.json').write_text(json.dumps(data))
            for name in ['mysql_fixture','ssh','openstack_fixture']:
                p=root/name;p.write_text(fixture_code);p.chmod(0o700)
            env=dict(os.environ,PATH=str(root)+os.pathsep+os.environ.get('PATH',''),
                     MYSQL_BIN=str(root/'mysql_fixture'),OPENSTACK_BIN=str(root/'openstack_fixture'),
                     CAPACITY_TEST_DATA=str(root/'data.json'),OS_AUTH_URL='https://fixture.invalid',OS_PASSWORD='site-admin-secret')
            output=root/'report'
            process=subprocess.run(['bash',str(SCRIPT),'--output-dir',str(output)],env=env,
                                   stdout=subprocess.PIPE,stderr=subprocess.PIPE,timeout=30)
            self.assertEqual(0,process.returncode,process.stderr.decode())
            report=json.loads((output/'report.json').read_text())
            self.assertEqual(100,report['neutron_pools'][0]['remaining'])
            self.assertEqual(1,report['instances']['nodes']['controller01']['total'])
            self.assertEqual(56,report['calculation_physical']['compute01']['scheduling_max_instances'])
            self.assertEqual(9,len(report['ratio_comparison']))
            self.assertEqual('auto',report['disk_mode'])
            self.assertEqual([1080,1080],report['storage_profile']['local_logical_gib_range'])
            self.assertEqual([1080,1080],report['storage_profile']['volume_logical_gib_range'])
            self.assertTrue(any(r['database']=='nova_cell0' for r in report['recent_faults']))
            self.assertTrue(any('NoValidHost' in r['message'] for r in report['scheduling_checks']))
            self.assertTrue(any('deleted_instance' in r['message'] for r in report['scheduling_checks']))
            self.assertNotIn('site-admin-secret',process.stdout.decode()+process.stderr.decode()+(output/'report.json').read_text())
            self.assertFalse(report['warnings'],report['warnings'])

    def test_observed_172_vcpu_inventory_is_not_reported_as_zero_when_services_fail(self):
        inv=inventory(True)
        inv['compute01']['VCPU']=mod.inventory_row(56,8,2,8)
        inv['compute01']['MEMORY_MB']=mod.inventory_row(257583,12000,1,16384)
        summary=mod.capacity_summary(inv,{})
        self.assertEqual(172,summary['inventory_available']['VCPU'])
        self.assertEqual(0,summary['confirmed_available']['VCPU'])
        self.assertFalse(summary['scheduling_verified'])
        self.assertEqual(self.args.nodes,summary['unknown_nodes'])
        rows=mod.analyze(self.args,inv,{}, {},{'controller01':0,'compute01':1})
        self.assertIsNone(rows[0]['available_vcpu'])
        self.assertIsNone(rows[0]['after_request'])
        self.assertEqual(172,rows[0]['inventory_available_vcpu'])
        self.assertEqual(-8,rows[0]['inventory_after_request'])
        self.assertEqual(220,rows[1]['inventory_available_vcpu'])
        self.assertIsNotNone(rows[1]['inventory_example_placement'])

    def test_failed_trait_query_preserves_both_nova_service_states(self):
        ident='11111111-2222-3333-4444-555555555555'
        def cli(args,*cmd):
            if cmd[:3]==('compute','service','list'):
                return [{'Host':n,'State':'up','Status':'enabled'} for n in self.args.nodes]
            if cmd[:3]==('resource','provider','list'):
                return [{'name':cmd[-1],'uuid':ident}]
            raise mod.ProbeError('trait unavailable')
        with patch.object(mod,'osc',side_effect=cli):result=mod.services_api(self.args)
        self.assertEqual(set(self.args.nodes),set(result))
        self.assertEqual('up',result['controller01']['state'])
        self.assertIsNone(result['controller01']['placement_disabled'])
        self.assertEqual('unknown',mod.scheduling_status(result['controller01']))

    def test_failed_db_traits_preserve_nova_heartbeat_state(self):
        with patch.object(mod,'sql',side_effect=[[['controller01','0','0','3']],mod.ProbeError('trait query unavailable')]):
            result=mod.services_db(self.args,{})
        self.assertEqual('up',result['controller01']['state'])
        self.assertIsNone(result['controller01']['placement_disabled'])

    def test_service_queries_quote_the_mariadb_binary_reserved_word(self):
        with patch.object(mod,'sql',return_value=[]) as call:
            mod.services_db(self.args,{})
            mod.control_services_db(self.args,{})
        queries=[c[0][1] for c in call.call_args_list if 'FROM nova.services' in c[0][1]]
        self.assertEqual(2,len(queries))
        for query in queries:
            self.assertIn('`binary`',query)
            self.assertNotRegex(query.replace('`binary`','quoted_service_column'),r'\bbinary\b')

    def test_trait_cli_explicitly_requests_the_required_microversion(self):
        with patch.object(mod,'auth'),patch.object(mod,'run',return_value='[]') as call:
            mod.osc(self.args,'resource','provider','trait','list','provider-id')
        command=call.call_args[0][0]
        self.assertEqual(['--os-placement-api-version','1.6'],command[1:3])

    def test_sql_numeric_diagnostics_are_safe_without_echoing_error_text(self):
        result=subprocess.CompletedProcess(['mysql'],1,b'',b'ERROR 1064 (42000) at line 1: password=DO_NOT_PRINT')
        with patch.object(mod.subprocess,'run',return_value=result),self.assertRaises(mod.ProbeError) as ctx:
            mod.run(['mysql'],'DB')
        self.assertIn('1064',str(ctx.exception))
        self.assertIn('42000',str(ctx.exception))
        self.assertNotIn('DO_NOT_PRINT',str(ctx.exception))

    def test_cinder_old_client_gets_a_volume_v2_read_only_retry(self):
        with patch.object(mod,'osc',side_effect=[mod.ProbeError('command not available'),[{'Name':'nfs','free_capacity_gb':636}]]) as call:
            result=mod.cinder_pools(self.args)
        self.assertEqual(636,result[0]['free_capacity_gb'])
        self.assertEqual(('--os-volume-api-version','2'),call.call_args_list[1][0][1:3])

    def test_consumer_id_uuid_schema_executes_allocation_joins(self):
        # Execute the actual generated joins against UUID consumer_id columns,
        # matching the site's Placement schema; not just command-name mocks.
        with sqlite3.connect(':memory:') as db:
            for schema in ['placement','nova']:db.execute("ATTACH DATABASE ':memory:' AS "+schema)
            db.execute('CREATE TABLE placement.allocations(resource_provider_id int,resource_class_id int,consumer_id varchar(36),used int)')
            db.execute('CREATE TABLE placement.resource_providers(id int,name varchar(200))')
            db.execute('CREATE TABLE nova.instances(uuid varchar(36),deleted int)')
            db.execute('CREATE TABLE nova.migrations(uuid varchar(36),id int)')
            db.execute("INSERT INTO placement.resource_providers VALUES (1,'compute01')")
            db.executemany('INSERT INTO nova.instances VALUES (?,?)',[('existing',0),('deleted',1)])
            db.execute("INSERT INTO nova.migrations VALUES ('migration',1)")
            db.executemany('INSERT INTO placement.allocations VALUES (1,0,?,4)',[(n,) for n in ['existing','deleted','migration','other']])
            def query(args,statement):
                if 'information_schema.columns' in statement:return [['consumer_id','varchar']]
                return [[str(v) for v in row] for row in db.execute(statement)]
            with patch.object(mod,'sql',side_effect=query):result=mod.allocation_audit_db(self.args)
        self.assertEqual({'existing_instance','deleted_instance','migration_consumer','unmatched_consumer'},{r['kind'] for r in result})
        self.assertEqual(16,sum(r['allocated'] for r in result))

    def test_integer_consumer_schema_joins_consumer_uuid_without_guessing(self):
        with patch.object(mod,'sql',side_effect=[[['consumer_id','bigint']],[]]) as call:
            self.assertEqual([],mod.allocation_audit_db(self.args))
        query=call.call_args[0][1]
        self.assertIn('placement.consumers c ON c.id=a.consumer_id',query)
        self.assertIn('i.uuid=c.uuid',query)

    def test_unrecognized_consumer_schema_fails_without_claiming_no_residual(self):
        with patch.object(mod,'sql',return_value=[]),self.assertRaises(mod.ProbeError):
            mod.allocation_audit_db(self.args)

    def test_all_sources_unavailable_is_not_a_successful_zero_capacity_report(self):
        with tempfile.TemporaryDirectory() as tmp,contextlib.redirect_stderr(io.StringIO()), \
             patch.object(mod,'inventory_db',side_effect=mod.ProbeError('DB unavailable')), \
             patch.object(mod,'inventory_api',side_effect=mod.ProbeError('API unavailable')):
            out=Path(tmp)/'none'
            self.assertEqual(2,mod.main(['--output-dir',str(out)]))
            self.assertFalse(out.exists())


class StorageTests(unittest.TestCase):
    SNAPSHOT='11111111-2222-3333-4444-555555555555'

    def setUp(self):
        mod.WARNINGS.clear()
        self.args=mod.parse_args([])

    def image(self,name,mode='volume',size=60,minimum=0):
        row=dict(id=name+'-id',name=name,status='active',disk_format='qcow2',container_format='bare',
                 size=1024 if mode=='local' else 0,min_disk=minimum)
        props={} if mode=='local' else {'block_device_mapping':json.dumps([dict(boot_index=0,source_type='snapshot',
                     destination_type=mode,volume_size=size,snapshot_id=self.SNAPSHOT)])}
        return mod.template_record(row,props)

    def catalog(self):
        return {'images':[self.image('kali',minimum=60),self.image('centos8','local')]}

    def sources(self):
        return {'snapshots':{self.SNAPSHOT:{'size_gib':60,'parent_size_gib':60,'status':'available'}},'volumes':{}}

    def test_default_auto_detects_mixed_disks_and_keeps_actual_snapshot_size(self):
        p=mod.template_plan(self.args,self.catalog(),self.sources())
        self.assertEqual('auto',self.args.disk_mode)
        self.assertEqual('volume',p['groups']['kali']['boot_mode'])
        self.assertEqual([60,60],p['groups']['kali']['root_gib_range'])
        self.assertEqual([2160,2160],p['volume_logical_gib_range'])
        self.assertEqual([1080,1080],p['local_logical_gib_range'])
        self.assertFalse(p['groups']['kali']['local_min_disk_violation'])
        self.assertTrue(p['groups']['kali']['capacity_model_ready'])

    def test_mixed_disk_placement_does_not_put_36_local_centoses_on_250gib_controller(self):
        self.args.storage_profile=mod.template_plan(self.args,self.catalog(),self.sources())
        inv=inventory(True);inv['compute01']['VCPU']=mod.inventory_row(56,8,2,8)
        example=mod.placement(self.args,inv,states(),{}, {'controller01':0,'compute01':1},[2,3])
        self.assertIsNotNone(example)
        self.assertLessEqual(example['controller01']['centos'],8)
        self.assertEqual(36,sum(x['kali'] for x in example.values()))
        self.assertEqual(36,sum(x['centos'] for x in example.values()))
        self.assertFalse(mod.fits(inv['controller01'],12,36,2,'auto',storage_profile=self.args.storage_profile))

    def test_multiple_volume_templates_report_a_size_range_without_picking_one(self):
        catalog=self.catalog();catalog['images'].append(self.image('kali-other',size=50))
        p=mod.template_plan(self.args,catalog,None)
        self.assertEqual('ambiguous',p['groups']['kali']['selection'])
        self.assertEqual('volume',p['groups']['kali']['boot_mode'])
        self.assertEqual([1800,2160],p['volume_logical_gib_range'])
        self.assertEqual(2,len(p['groups']['kali']['candidate_ids']))
        self.assertIsNone(p['groups']['kali']['selector'])

    def test_mixed_template_modes_and_missing_templates_remain_unknown(self):
        catalog=self.catalog();catalog['images'].append(self.image('kali-local','local'))
        p=mod.template_plan(self.args,catalog,None)
        self.assertIn('kali',p['unknown_groups'])
        self.assertIsNone(p['volume_logical_gib_range'])
        self.assertIsNone(p['local_logical_gib_range'])
        self.args.storage_profile=p
        self.assertIsNone(mod.placement(self.args,inventory(),states(),{}, {},[4,4]))
        self.assertEqual(['kali','centos'],mod.template_plan(self.args,None,None)['unknown_groups'])

    def test_exact_template_selector_removes_ambiguity(self):
        catalog=self.catalog();catalog['images'].append(self.image('kali-other',size=50))
        self.args.kali_image='kali-other-id'
        p=mod.template_plan(self.args,catalog,None)
        self.assertEqual('explicit',p['groups']['kali']['selection'])
        self.assertEqual([50,50],p['groups']['kali']['root_gib_range'])

    def test_zero_count_group_does_not_require_a_template(self):
        self.args.kali_count=0
        p=mod.template_plan(self.args,{'images':[self.image('centos8','local')]},None)
        self.assertFalse(p['unknown_groups'])
        self.assertEqual([0,0],p['volume_logical_gib_range'])

    def test_min_disk_is_checked_for_local_boot_but_volume_boot_uses_volume_size(self):
        catalog={'images':[self.image('kali','local',minimum=60),self.image('centos8','local')]}
        p=mod.template_plan(self.args,catalog,None)
        self.assertTrue(p['groups']['kali']['local_min_disk_violation'])
        self.assertFalse(p['groups']['kali']['capacity_model_ready'])
        p=mod.template_plan(self.args,self.catalog(),self.sources())
        self.assertFalse(p['groups']['kali']['local_min_disk_violation'])

    def test_source_volume_size_overrides_stale_template_metadata(self):
        sources=self.sources();sources['snapshots'][self.SNAPSHOT].update(size_gib=80,parent_size_gib=80)
        p=mod.template_plan(self.args,self.catalog(),sources)
        self.assertEqual([80,80],p['groups']['kali']['root_gib_range'])
        self.assertEqual([2880,2880],p['volume_logical_gib_range'])

    def test_manual_disk_mode_is_a_labeled_override(self):
        self.args.disk_mode='volume'
        p=mod.template_plan(self.args,self.catalog(),self.sources())
        self.assertTrue(p['groups']['centos']['manual_mode_conflicts_with_detection'])
        self.assertIn('假设',p['scope'])

    def test_invalid_mapping_does_not_echo_arbitrary_properties(self):
        row={'id':'id','name':'kali','size':0,'min_disk':0}
        image=mod.template_record(row,{'block_device_mapping':'{"password":"DO_NOT_PRINT"}','database_password':'DO_NOT_PRINT'})
        self.assertEqual('unknown',image['boot_mode'])
        self.assertFalse(image['mapping_valid'])
        self.assertNotIn('DO_NOT_PRINT',json.dumps(image))

    def test_template_selector_is_sql_data_and_mapping_properties_are_whitelisted(self):
        self.args.kali_image="kali'; SELECT secret -- $(do_not_run)"
        with patch.object(mod,'sql',return_value=[]) as call:mod.template_catalog_db(self.args)
        statement=call.call_args[0][1]
        self.assertNotIn(self.args.kali_image,statement)
        self.assertIn(self.args.kali_image.encode().hex(),statement)
        self.assertIn("p.name IN ('block_device_mapping'",statement)

    def test_nfs_parser_rejects_credential_uris(self):
        helper={};exec(mod.CONFIG_HELPER+mod.STORAGE_HELPER,helper)
        for value in ['iscsi://user:DO_NOT_PRINT@host/target','https://host/path?token=DO_NOT_PRINT','host:/path with space']:
            self.assertIsNone(mod.parse_nfs_share(value))
            self.assertIsNone(helper['nfs_share'](value))
        self.assertEqual('2001:db8::1',mod.parse_nfs_share('[2001:db8::1]:/data/cinder')['server'])

    def test_nfs_probe_reads_actual_shares_mounts_and_exports_without_credentials(self):
        helper={};exec(mod.CONFIG_HELPER+mod.STORAGE_HELPER,helper)
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);share=root/'shares';conf=root/'cinder.conf';mounts=root/'mountinfo';exports=root/'exports'
            share.write_text('compute01:/data/cinder -o password=DO_NOT_PRINT\n')
            conf.write_text('[DEFAULT]\nenabled_backends=nfs\n[database]\nconnection=mysql://DO_NOT_PRINT\n[nfs]\n'
                'volume_driver=cinder.volume.drivers.nfs.NfsDriver\nvolume_backend_name=misleading-controller-name\nnfs_shares_config='+str(share)+'\n')
            mounts.write_text('1 0 8:1 / / rw - ext4 /dev/sda1 rw\n2 1 0:5 / /opt/cinder/mnt rw - nfs4 compute01:/data/cinder rw,password=DO_NOT_PRINT\n')
            exports.write_text(str(root)+' *(rw,secret=DO_NOT_PRINT)\n')
            result=helper['storage_probe']([str(conf)],str(mounts),[str(exports)])
        self.assertTrue(result['config_readable'])
        self.assertEqual('compute01',result['backends'][0]['shares'][0]['server'])
        self.assertEqual('nfs4',result['nfs_mounts'][0]['fstype'])
        self.assertTrue(result['exported_paths'][0]['filesystem'])
        self.assertNotIn('DO_NOT_PRINT',json.dumps(result))

    def test_storage_server_is_determined_from_share_not_backend_name_or_device_number(self):
        backend={'section':'nfs','config':{'volume_backend_name':'nfs-controller01'},
                 'shares':[{'source':'10.0.0.2:/data/cinder','server':'10.0.0.2','path':'/data/cinder'}]}
        stats={'controller01':{'host_addresses':['10.0.0.1'],'filesystems':[],
                 'storage':{'backends':[backend],'nfs_mounts':[]}},
               'compute01':{'host_addresses':['10.0.0.2'],'filesystems':[],
                 'storage':{'backends':[],'nfs_mounts':[],'exported_paths':[{'filesystem':{
                     'path':'/data/cinder','device':64768,'free_bytes':8000000000}}]}}}
        link=mod.storage_topology(stats)['connections'][0]
        self.assertEqual('controller01',link['manager_node'])
        self.assertEqual('compute01',link['storage_node'])
        self.assertEqual(8000000000,link['filesystem']['free_bytes'])

    def test_cinder_provider_location_only_emits_nfs_share_not_iscsi_credentials(self):
        row=['vm','compute01','snapshot','volume','volume-id','snap-id','60','controller01@nfs','60','iscsi://user:DO_NOT_PRINT@host/target']
        with patch.object(mod,'sql',return_value=[row]):result=mod.instance_root_disks_db(self.args)
        self.assertIsNone(result[0]['nfs_share'])
        self.assertNotIn('DO_NOT_PRINT',json.dumps(result))

    def test_template_sources_fetch_only_referenced_valid_uuids(self):
        row=[self.SNAPSHOT,'parent',60,'available',80,'controller01@nfs','compute01:/data/cinder']
        with patch.object(mod,'sql',return_value=[row]) as call:
            result=mod.template_sources_db(self.args,self.catalog())
        self.assertEqual('compute01',result['snapshots'][self.SNAPSHOT]['nfs_share']['server'])
        self.assertEqual(80,result['snapshots'][self.SNAPSHOT]['parent_size_gib'])
        self.assertIn(self.SNAPSHOT,call.call_args[0][1])


    def test_parent_volume_growth_does_not_enlarge_an_existing_snapshot(self):
        sources=self.sources();sources['snapshots'][self.SNAPSHOT]['parent_size_gib']=120
        p=mod.template_plan(self.args,self.catalog(),sources)
        self.assertEqual([60,60],p['groups']['kali']['root_gib_range'])

    def test_snapshot_error_state_blocks_capacity_example(self):
        sources=self.sources();sources['snapshots'][self.SNAPSHOT]['status']='error'
        p=mod.template_plan(self.args,self.catalog(),sources)
        self.assertFalse(p['groups']['kali']['capacity_model_ready'])
        self.assertEqual('error',p['groups']['kali']['source_not_ready'][0]['status'])


if __name__=='__main__':unittest.main()
