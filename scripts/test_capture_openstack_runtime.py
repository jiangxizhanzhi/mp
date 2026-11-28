import csv
import json
import os
from pathlib import Path
import subprocess
import sys
import tarfile
import tempfile
import types
import unittest
from unittest.mock import patch


SCRIPT = Path(__file__).with_name('capture_openstack_runtime.sh')
SOURCE = SCRIPT.read_text().split("<<'PY_RUNTIME'\n", 1)[1].rsplit('\nPY_RUNTIME', 1)[0]
runtime = types.ModuleType('capture_runtime')
exec(compile(SOURCE, str(SCRIPT), 'exec'), runtime.__dict__)


class RuntimeCounterTests(unittest.TestCase):
    def test_rates_have_no_invented_initial_or_reset_values(self):
        self.assertIsNone(runtime.rate(None, 100, 2))
        self.assertIsNone(runtime.rate(100, 50, 2))
        self.assertIsNone(runtime.rate(100, 200, 0))
        self.assertEqual(runtime.rate(100, 300, 2), 100)

    def test_cpu_guest_ticks_are_not_counted_twice(self):
        before = runtime.parse_cpu('cpu  100 0 20 500 30 0 0 0 60 0\n')
        after = runtime.parse_cpu('cpu  150 0 20 530 50 0 0 0 100 0\n')
        self.assertEqual(runtime.cpu_percent(before, after), (50, 20))

    def test_cpu_unknown_or_reset_is_not_zero(self):
        self.assertEqual(runtime.cpu_percent(None, [1] * 8), (None, None))
        self.assertEqual(runtime.cpu_percent([10] * 8, [9] * 8), (None, None))
        self.assertEqual(runtime.cpu_percent([10] * 8, [10] * 8), (None, None))

    def test_network_rx_and_tx_are_independent(self):
        text = 'Inter-| Receive | Transmit\n eno1: 1000 1 2 3 0 0 0 0 5000 1 4 5 0 0 0 0\n'
        value = runtime.parse_net(text)['eno1']
        self.assertEqual(value, dict(rx_bytes=1000, tx_bytes=5000, rx_errors=2,
                                     rx_drops=3, tx_errors=4, tx_drops=5))

    def test_missing_memory_is_unknown(self):
        self.assertIsNone(runtime.memory_available_gib(None))
        self.assertIsNone(runtime.memory_available_gib('MemFree: 1024 kB'))
        self.assertEqual(runtime.memory_available_gib('MemAvailable: 1048576 kB'), 1)

    def test_mountstats_contains_only_nfs_sections(self):
        fixture = ('device tmpfs mounted on /run with fstype tmpfs\nignored\n'
                   'device server:/data mounted on /mnt with fstype nfs4 statvers=1.1\nREAD: 1 2\n'
                   'device cifs mounted on /other with fstype cifs\nprivate-options\n'
                   'device server:/old mounted on /old with fstype nfs statvers=1.1\nWRITE: 3 4\n')
        with patch.object(runtime, 'read_text', return_value=fixture):
            selected = runtime.nfs_mountstats()
        self.assertIn('READ: 1 2', selected)
        self.assertIn('WRITE: 3 4', selected)
        self.assertNotIn('private-options', selected)
        self.assertNotIn('tmpfs', selected)

    def test_only_started_diagnostic_process_is_stopped(self):
        process = subprocess.Popen([sys.executable, '-c', 'import time; time.sleep(30)'],
                                   start_new_session=True)
        try:
            result = runtime.stop_monitor(process)
            self.assertEqual(result['status'], 'stopped_by_capture')
            self.assertIsNotNone(process.poll())
        finally:
            if process.poll() is None:
                process.kill()
                process.wait()


class RuntimeIntegrationTests(unittest.TestCase):
    def test_capture_without_optional_tools_and_refuse_overwrite(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            binaries = root / 'bin'
            binaries.mkdir()
            (binaries / 'python3').symlink_to(sys.executable)
            output = root / 'capture'
            env = dict(os.environ, PATH=str(binaries))
            args = ['/bin/bash', str(SCRIPT), '--duration', '1', '--interval', '1',
                    '--output', str(output)]
            result = subprocess.run(args, env=env, capture_output=True, text=True, timeout=20)
            self.assertEqual(result.returncode, 0, result.stderr)
            metadata = json.loads((output / 'metadata.json').read_text())
            self.assertGreaterEqual(metadata['samples'], 2)
            self.assertTrue(all(v['status'] == 'unavailable' for v in metadata['tools'].values()))
            with (output / 'host.csv').open() as stream:
                rows = list(csv.DictReader(stream))
            self.assertEqual(rows[0]['cpu_busy_pct'], '')
            self.assertNotEqual(rows[-1]['cpu_busy_pct'], '')
            archive = Path(str(output) + '.tar.gz')
            with tarfile.open(archive) as bundle:
                self.assertIn('capture/metadata.json', bundle.getnames())
            original = (output / 'metadata.json').read_bytes()
            repeated = subprocess.run(args, env=env, capture_output=True, text=True, timeout=10)
            self.assertNotEqual(repeated.returncode, 0)
            self.assertEqual((output / 'metadata.json').read_bytes(), original)

    def test_failed_virsh_is_not_reported_as_empty_vm_inventory(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            binaries = root / 'bin'
            binaries.mkdir()
            (binaries / 'python3').symlink_to(sys.executable)
            virsh = binaries / 'virsh'
            virsh.write_text('#!' + sys.executable + '\nimport sys\nprint("unsupported")\nsys.exit(2)\n')
            virsh.chmod(0o700)
            output = root / 'capture'
            result = subprocess.run(['/bin/bash', str(SCRIPT), '--duration', '1', '--interval', '1',
                                     '--output', str(output)], env=dict(os.environ, PATH=str(binaries)),
                                    capture_output=True, text=True, timeout=20)
            self.assertEqual(result.returncode, 0, result.stderr)
            metadata = json.loads((output / 'metadata.json').read_text())
            stats = metadata['tools']['virsh-domstats']
            self.assertEqual(stats['status'], 'failed')
            self.assertEqual(stats['successful_samples'], 0)
            self.assertGreaterEqual(stats['failed_samples'], 1)


if __name__ == '__main__':
    unittest.main()
