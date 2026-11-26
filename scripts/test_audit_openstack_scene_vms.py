#!/usr/bin/env python3
"""Offline tests. All mysql, OpenStack, and confirmation input are mocked."""
import contextlib
import io
import json
import os
from pathlib import Path
import subprocess
import sys
import unittest
from unittest.mock import patch

SCRIPT = Path(__file__).with_name('audit_openstack_scene_vms.sh')
CODE = SCRIPT.read_text(encoding='utf-8').split("<<'PY'\n", 1)[1].rsplit('\nPY', 1)[0]
VID = '11111111-1111-4111-8111-111111111111'
OTHER = '22222222-2222-4222-8222-222222222222'


class AuditTests(unittest.TestCase):
    def setUp(self):
        self.ns = {'__name__': 'audit_test'}
        with patch.object(sys, 'argv', ['audit-test']), patch.dict(os.environ, {'MP_RESOURCE_PREFIX': 'MP_SCENE'}, clear=False):
            exec(compile(CODE, str(SCRIPT), 'exec'), self.ns)
        self.rows = self.ns['rows']
        self.vm = self.add('nova.instances', uuid=VID, display_name='MP_SCENE_element', hostname='MP_SCENE_element',
                           host='compute-fixture', project_id='project-fixture', vm_state='active', task_state=None,
                           power_state='1', memory_mb='1024', deleted='0', locked='0', disable_terminate='0')
        self.scene = self.add('mp.mp_scene_scene', id='scene', name='fixture', event='9', event_status='4', deleted='1')
        self.element = self.add('mp.mp_scene_scene_element', id='element', scene='scene', node_id='node', name='fixture',
                                event='9', event_status='4', vendor='0')
        self.plugin = self.add('mp.mp_sceneplugincloud_scene_element', scene='scene', scene_element='node', resource_id=VID,
                               virtual_type='4', external='0')
        self.business = self.add('mp.mp_cscene_cscene_instance', id='business', name='fixture', scene='scene', deleted='1')
        self.ns['options'].delete = [VID]

    def add(self, table, **kwargs):
        record = dict.fromkeys(self.ns['TABLES'][table])
        record.update(kwargs)
        self.rows[table].append(record)
        return record

    def report(self):
        return self.ns['audit'](set())[0]

    def status(self):
        return self.report()[0]['cleanup_status']

    def test_completed_delete_is_eligible(self):
        self.assertEqual('ELIGIBLE', self.status())

    def test_active_business_and_in_progress_are_protected(self):
        self.business['deleted'] = '0'
        self.assertEqual('KEEP', self.status())
        self.business['deleted'] = '1'
        self.scene.update(event='9', event_status='2', deleted='0')
        self.assertEqual('KEEP', self.status())

    def test_lock_and_external_reference_protected(self):
        for key in ('locked', 'disable_terminate'):
            self.vm[key] = '1'
            self.assertEqual('KEEP', self.status())
            self.vm[key] = '0'
        self.plugin['external'] = '1'
        self.assertEqual('KEEP', self.status())

    def test_failed_delete_requires_explicit_review(self):
        self.scene.update(event='9', event_status='5', deleted='0')
        self.element.update(event='9', event_status='5')
        self.assertEqual('REVIEW', self.status())
        with self.assertRaises(ValueError):
            self.ns['selected_row'](self.report(), VID)
        self.ns['options'].allow_reviewed = True
        self.assertEqual(VID, self.ns['selected_row'](self.report(), VID)['nova_uuid'])

    def test_unlinked_but_live_scene_cannot_be_deleted(self):
        self.rows['mp.mp_scene_scene_element'].clear()
        self.scene.update(event='2', event_status='4', deleted='0')
        self.assertEqual('KEEP', self.status())

    def test_non_mp_is_not_deletable_even_with_review_flag(self):
        self.vm.update(display_name='unrelated', hostname='unrelated')
        self.rows['mp.mp_scene_scene_element'].clear()
        self.rows['mp.mp_sceneplugincloud_scene_element'].clear()
        self.assertEqual('UNMANAGED', self.status())
        self.ns['options'].allow_reviewed = True
        with self.assertRaises(ValueError):
            self.ns['selected_row'](self.report(), VID)

    def test_prefix_only_and_preview_are_review(self):
        self.rows['mp.mp_sceneplugincloud_scene_element'].clear()
        self.rows['mp.mp_scene_scene_element'].clear()
        self.assertEqual('REVIEW', self.status())
        self.vm.update(display_name='preview-fixture', hostname='preview-fixture')
        preview = self.add('cpcloud.device_vm', device_uuid='preview', instance_uuid=VID, instance_exist='0', instance_state='fixture')
        self.assertEqual('REVIEW', self.status())
        preview['instance_exist'] = '1'
        self.assertEqual('KEEP', self.status())

    def test_already_deleted_but_libvirt_active_not_deletable(self):
        self.vm['deleted'] = '1'
        row = self.ns['audit']({VID})[0][0]
        self.assertEqual('HYPERVISOR_ONLY', row['cleanup_status'])

    def cli_show(self, project='project-fixture'):
        return {'id': VID, 'name': self.vm['display_name'], 'project_id': project, 'status': 'ACTIVE',
                'OS-EXT-STS:task_state': None, 'OS-EXT-SRV-ATTR:host': 'compute-fixture', 'locked': False}

    def execute_mocked_delete(self, *, confirm='DELETE 1', dry=False, changed=False, project='project-fixture'):
        self.ns['options'].dry_run = dry
        cmds = []
        report = self.report()
        def cli(cmd, **kwargs):
            cmds.append(cmd)
            if cmd[1:3] == ['server', 'show']:
                return subprocess.CompletedProcess(cmd, 0, json.dumps(self.cli_show(project)).encode(), b'')
            if cmd[1:3] == ['server', 'delete']:
                self.vm['deleted'] = '1'
                return subprocess.CompletedProcess(cmd, 0, b'', b'')
            raise AssertionError(cmd)
        def refresh():
            if changed:
                self.business['deleted'] = '0'
        def tty(path, mode, **kwargs):
            self.assertEqual('/dev/tty', path)
            return io.StringIO(confirm + '\n' if mode == 'r' else '')
        with patch.object(subprocess, 'run', side_effect=cli), patch('builtins.open', side_effect=tty), \
                patch.dict(self.ns, {'read_data': refresh}), contextlib.redirect_stderr(io.StringIO()):
            result = self.ns['delete_selected'](report)
        return result, cmds

    def test_dry_run_never_deletes(self):
        result, cmds = self.execute_mocked_delete(dry=True)
        self.assertEqual(0, result)
        self.assertTrue(all(c[1:3] == ['server', 'show'] for c in cmds))

    def test_confirmation_cancellation(self):
        result, cmds = self.execute_mocked_delete(confirm='NO')
        self.assertEqual(1, result)
        self.assertTrue(all(c[1:3] == ['server', 'show'] for c in cmds))

    def test_changed_business_reference_blocks_delete(self):
        result, cmds = self.execute_mocked_delete(changed=True)
        self.assertEqual(1, result)
        self.assertTrue(all(c[1:3] == ['server', 'show'] for c in cmds))

    def test_wrong_cloud_project_blocks(self):
        with self.assertRaises(ValueError):
            self.execute_mocked_delete(project='different-project')

    def test_selected_instance_normal_delete_and_verify(self):
        result, cmds = self.execute_mocked_delete()
        self.assertEqual(0, result)
        requests = [c[1:] for c in cmds if c[1:3] == ['server', 'delete']]
        self.assertEqual([['server', 'delete', '--wait', VID]], requests)

    def test_historical_snapshot_refuses_delete(self):
        p = subprocess.run(['bash', str(SCRIPT), '--dump', 'not-opened.sql', '--delete', VID], capture_output=True)
        self.assertNotEqual(0, p.returncode)
        self.assertIn('历史快照不能', p.stderr.decode())

    def test_live_queries_are_read_only(self):
        def mysql(argv, **kwargs):
            statements = argv[argv.index('--execute') + 1].split(';\n')
            self.assertEqual('SET TRANSACTION ISOLATION LEVEL REPEATABLE READ', statements[0])
            self.assertEqual('START TRANSACTION WITH CONSISTENT SNAPSHOT, READ ONLY', statements[1])
            self.assertEqual('COMMIT', statements[-1])
            self.assertTrue(all(q.startswith('SELECT ') for q in statements[2:-1]))
            return subprocess.CompletedProcess(argv, 0, b''.join((k + '#count\t30\n').encode() for k in self.ns['TABLES']), b'')
        for records in self.rows.values():
            records.clear()
        with patch.object(subprocess, 'run', side_effect=mysql):
            self.ns['load_live']()
        with patch.object(subprocess, 'run', return_value=subprocess.CompletedProcess([], 0, b'', b'')):
            with self.assertRaises(ValueError):
                self.ns['load_live']()


if __name__ == '__main__':
    unittest.main()
