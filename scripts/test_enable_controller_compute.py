"""Offline tests of the controller01 scheduler script; no live OpenStack calls."""
import configparser
import contextlib
import io
import json
import subprocess
import tempfile
import types
import unittest
from decimal import Decimal
from pathlib import Path
from unittest.mock import Mock, mock_open, patch


SCRIPT = Path(__file__).with_name("enable_controller_compute.sh")
source = SCRIPT.read_text().split("<<'PY'\n", 1)[1].rsplit("\nPY\n", 1)[0]
mod = types.ModuleType("controller_compute")
exec(compile(source, str(SCRIPT), "exec"), mod.__dict__)

HISTORICAL_CONFIG = """[DEFAULT]
compute_driver = libvirt.LibvirtDriver
instances_path = /data/nova_instances
reserved_host_cpus = 8
reserved_host_memory_mb = 65536
reserved_host_disk_mb = 100000
cpu_allocation_ratio = 2.0
ram_allocation_ratio = 1
disk_allocation_ratio = 2.0
# preserve this setting
[filter_scheduler]
max_instances_per_host = 56
enabled_filters = AvailabilityZoneFilter,ComputeFilter,ImagePropertiesFilter,NumInstancesFilter
[libvirt]
virt_type = kvm
"""


def config():
    cfg = configparser.ConfigParser(interpolation=None)
    cfg.read_string(HISTORICAL_CONFIG)
    return cfg


def old_inventory():
    return {
        "VCPU": dict(total=56, reserved=8, ratio=Decimal(2), used=0),
        "MEMORY_MB": dict(total=385599, reserved=65536, ratio=Decimal(1), used=0),
        "DISK_GB": dict(total=1784, reserved=98, ratio=Decimal(2), used=0),
    }


class ControllerSchedulingTests(unittest.TestCase):
    def setUp(self):
        self.output = io.StringIO()
        self.redirect = contextlib.redirect_stdout(self.output)
        self.redirect.__enter__()
        self.addCleanup(self.redirect.__exit__, None, None, None)

    def calculate(self, cpu=10.2, ram=48, disk=512, options=(), inventory=None):
        return mod.calculate(inventory or old_inventory(), Decimal(str(cpu)),
                             Decimal(ram * 1024), disk,
                             mod.parse_args(list(options)), config())

    def test_historical_capacity_and_25x_peak(self):
        result = self.calculate()
        self.assertEqual(result, {"VCPU": 26, "MEMORY_MB": 120 * 1024, "DISK_GB": 1580})
        self.assertEqual(1784 - result["DISK_GB"], 204)
        self.assertGreaterEqual(512 - 204, 300)

    def test_does_not_reduce_existing_reservations(self):
        result = self.calculate(cpu=1.5, ram=16)
        self.assertEqual(result["VCPU"], 8)
        self.assertEqual(result["MEMORY_MB"], 65536)

    def test_fails_when_business_reserve_does_not_fit(self):
        for options in (dict(cpu=23), dict(ram=155), dict(disk=300),
                        dict(options=["--strict-disk-factor"])):
            with self.subTest(options=options), self.assertRaises(RuntimeError):
                self.calculate(**options)

    def test_fails_if_existing_vm_allocations_do_not_fit(self):
        inv = old_inventory()
        inv["VCPU"]["used"] = 35
        with self.assertRaises(RuntimeError):
            self.calculate(inventory=inv)

    def test_changes_only_target_keys_in_default_section(self):
        values = dict(zip(mod.KEYS, (26, 122880, 1580 * 1024, 1, 1, 1)))
        updated = mod.update_defaults(HISTORICAL_CONFIG, values)
        cfg = configparser.ConfigParser(interpolation=None)
        cfg.read_string(updated)
        for key, value in values.items():
            self.assertEqual(cfg["DEFAULT"][key], str(value))
        self.assertIn("# preserve this setting", updated)
        self.assertEqual(cfg["filter_scheduler"]["max_instances_per_host"], "56")
        self.assertEqual(cfg["libvirt"]["virt_type"], "kvm")

    def test_placement_verification_requires_non_overcommitted_resources(self):
        inv = old_inventory()
        expected = self.calculate()
        self.assertFalse(mod.inventory_matches(inv, expected))
        for rc in inv:
            inv[rc]["ratio"] = Decimal(1)
            inv[rc]["reserved"] = expected[rc]
        self.assertTrue(mod.inventory_matches(inv, expected))
        inv["DISK_GB"]["reserved"] -= 1
        self.assertFalse(mod.inventory_matches(inv, expected))

    def test_placement_disabled_trait_must_be_cleared_before_success(self):
        expected = self.calculate()
        inv = old_inventory()
        for rc in inv:
            inv[rc]["ratio"] = Decimal(1)
            inv[rc]["reserved"] = expected[rc]
        with patch.multiple(mod, run=Mock(), inventory=Mock(return_value=inv),
                            service=Mock(return_value={"State": "up", "Status": "enabled"}),
                            disabled_trait=Mock(return_value=True)), \
                patch.object(mod.time, "monotonic", side_effect=[0, 1, 5]), \
                patch.object(mod.time, "sleep"):
            with self.assertRaises(RuntimeError):
                mod.wait_for_inventory(expected, 2, enabled=True)
        with patch.multiple(mod, run=Mock(), inventory=Mock(return_value=inv),
                            service=Mock(return_value={"State": "up", "Status": "enabled"}),
                            disabled_trait=Mock(return_value=False)):
            mod.wait_for_inventory(expected, 2, enabled=True)

    def network_query(self, config=None, age=2, segments=None):
        mapping = json.dumps({"bridge_mappings": mod.EXPECTED_MAPPINGS,
                              "tunnel_types": []}).encode().hex()
        local = json.dumps(config).encode().hex() if config is not None else mapping
        return Mock(side_effect=[
            [["controller01", "1", str(age), local], ["compute01", "1", "2", mapping]],
            segments if segments is not None else [["flat", "ex_provider"], ["vlan", "vlanprovider"]],
            [["DHCP agent", "1", "3"], ["L3 agent", "1", "3"], ["Metadata agent", "1", "3"]],
        ])

    @staticmethod
    def network_run(*argv, **kwargs):
        if "list-ifaces" in argv:
            return "physical\npatch-port"
        if "get" in argv:
            return '""\n2\nup' if "physical" in argv else '"patch"\n3\n[]'
        return ""

    def test_network_mapping_and_live_uplinks(self):
        with patch.multiple(mod, query=self.network_query(), run=self.network_run,
                            external_pool_report=Mock()):
            mod.check_network()
        self.assertIn("br-ex: 有效物理上联", self.output.getvalue())
        self.assertIn("br-vlan: 有效物理上联", self.output.getvalue())

    def test_network_mismatch_stale_agent_or_bad_link_blocks(self):
        for query, run in (
            (self.network_query(config={"bridge_mappings": {"ex_provider": "wrong"}}), self.network_run),
            (self.network_query(age=76), self.network_run),
            (self.network_query(segments=[["vxlan", ""]]), self.network_run),
            (self.network_query(), lambda *a, **kw: "down"),
        ):
            with self.subTest(query=query), patch.multiple(mod, query=query, run=run), \
                    self.assertRaises(RuntimeError):
                mod.check_network()

    def test_external_ip_pool_counts_only_ips_inside_allocation_pool(self):
        pools = [["192.0.2.0/24", "subnet", "192.0.2.10", "192.0.2.110"]]
        ips = [["subnet", "192.0.2." + str(i)] for i in range(10, 62)]
        ips += [["subnet", "192.0.2.1"]]
        with patch.object(mod, "query", side_effect=[pools, ips]):
            mod.external_pool_report()
        self.assertIn("总量=101, 已分配=52, 剩余=49", self.output.getvalue())

    def test_default_auto_mode_and_sample_duration(self):
        args = mod.parse_args([])
        self.assertEqual(args.mode, "auto")
        self.assertEqual(args.sample_seconds, 300)
        self.assertEqual(mod.parse_args(["check"]).mode, "check")
        self.assertEqual(mod.parse_args(["apply"]).sample_seconds, 300)
        self.assertEqual(mod.parse_args(["--sample-seconds", "30"]).sample_seconds, 30)

    def test_auto_runs_check_then_apply_and_passes_sample(self):
        observed = (Decimal("10.2"), Decimal(48 * 1024))
        events = []
        check = Mock(side_effect=lambda *a: (events.append("check"), observed)[1])
        apply = Mock(side_effect=lambda *a, **kw: events.append("apply"))
        with tempfile.TemporaryDirectory() as temp, \
                patch.multiple(mod, CONF=Path(temp) / "nova.conf", check=check, apply=apply), \
                patch.object(mod.socket, "gethostname", return_value=mod.HOST), \
                patch.object(mod.os, "geteuid", return_value=0), \
                patch.object(mod.fcntl, "flock"), patch("builtins.open", mock_open()):
            mod.main([])
        self.assertEqual(events, ["check", "apply"])
        self.assertEqual(apply.call_args.kwargs["observed"], observed)
        self.assertEqual(check.call_args.args[0].sample_seconds, 300)

    def test_auto_check_failure_never_applies_and_explicit_check_is_read_only(self):
        apply = Mock()
        check = Mock(side_effect=RuntimeError("simulated check failure"))
        with tempfile.TemporaryDirectory() as temp, \
                patch.multiple(mod, CONF=Path(temp) / "nova.conf", check=check, apply=apply), \
                patch.object(mod.socket, "gethostname", return_value=mod.HOST), \
                patch.object(mod.os, "geteuid", return_value=0), \
                patch.object(mod.fcntl, "flock"), patch("builtins.open", mock_open()):
            with self.assertRaises(RuntimeError):
                mod.main([])
            check.side_effect = None
            mod.main(["check"])
        apply.assert_not_called()

    def test_plan_reuses_sample_without_waiting_again(self):
        observed = (Decimal("10.2"), Decimal(48 * 1024))
        sample = Mock()
        with tempfile.TemporaryDirectory() as temp, \
                patch.multiple(mod, registered=Mock(return_value=0),
                               inventory=Mock(return_value=old_inventory()), sample=sample), \
                patch.object(mod.shutil, "disk_usage", return_value=types.SimpleNamespace(free=512 * mod.GIB)):
            result, returned = mod.plan(mod.parse_args([]), config(), Path(temp), observed=observed)
        self.assertEqual(returned, observed)
        self.assertEqual(result["VCPU"], 26)
        sample.assert_not_called()

    def test_check_reports_independent_failures_with_exit_code_and_output(self):
        error = subprocess.CalledProcessError(
            3, ["systemctl", "is-active", mod.UNIT],
            output="inactive\n", stderr="unit failed\n")
        output = io.StringIO()
        plan = Mock()
        with patch.multiple(
            mod, run=Mock(side_effect=error), read_conf=Mock(return_value=config()),
            instance_path=Mock(return_value=Path("/data/nova_instances")),
            service=Mock(return_value={"State": "up", "Status": "disabled"}),
            registered=Mock(return_value=0), inventory=Mock(return_value=old_inventory()),
            check_network=Mock(side_effect=RuntimeError("br-vlan missing")), plan=plan,
        ), contextlib.redirect_stderr(output):
            with self.assertRaisesRegex(RuntimeError, "共 2 项"):
                mod.check(mod.parse_args(["check"]), mod.CONF)
        details = output.getvalue()
        self.assertIn("systemctl is-active nova-compute.service", details)
        self.assertIn("退出码：3", details)
        self.assertIn("inactive", details)
        self.assertIn("unit failed", details)
        self.assertIn("br-vlan missing", details)
        plan.assert_not_called()

    def test_failure_output_redacts_environment_credentials(self):
        output = io.StringIO()
        with patch.dict(mod.os.environ, {"OS_PASSWORD": "private-password-value"}), \
                contextlib.redirect_stderr(output):
            mod.report_failure(subprocess.CalledProcessError(
                1, ["openstack", "compute", "service", "list"],
                stderr="failed private-password-value mysql://u:database-secret@db/nova"))
        self.assertNotIn("private-password-value", output.getvalue())
        self.assertNotIn("database-secret", output.getvalue())
        self.assertIn("[REDACTED]", output.getvalue())

    def run_apply(self, verification_failure=None):
        events = []
        expected = {"VCPU": 26, "MEMORY_MB": 122880, "DISK_GB": 1580}
        inv = old_inventory()
        for rc in inv:
            inv[rc]["reserved"] = expected[rc]
            inv[rc]["ratio"] = Decimal(1)

        def wait(*args, enabled=False):
            events.append("verify-enabled" if enabled else "verify-disabled")
            if verification_failure == enabled:
                raise RuntimeError("simulated verification failure")

        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp) / "nova.conf"
            path.write_text(HISTORICAL_CONFIG)
            with patch.multiple(
                mod, service=Mock(),
                disable=Mock(side_effect=lambda: events.append("disable")),
                instance_path=Mock(return_value=path.parent),
                check_network=Mock(side_effect=lambda: events.append("network")),
                plan=Mock(return_value=(expected, (Decimal("10.2"), Decimal(48 * 1024)))),
                backup_config=Mock(side_effect=lambda p: (events.append("backup"), p)[1]),
                atomic_write=Mock(side_effect=lambda *a: events.append("write")),
                run=Mock(side_effect=lambda *a, **kw: events.append("restart")),
                wait_for_inventory=wait, inventory=Mock(return_value=inv),
                disabled_trait=Mock(return_value=True),
                osc=Mock(side_effect=lambda *a: events.append("enable")),
            ), patch.object(mod.shutil, "disk_usage", return_value=types.SimpleNamespace(free=512 * mod.GIB)):
                if verification_failure is None:
                    mod.apply(mod.parse_args(["apply"]), path)
                else:
                    with contextlib.redirect_stderr(io.StringIO()), self.assertRaises(RuntimeError):
                        mod.apply(mod.parse_args(["apply"]), path)
        return events

    def test_apply_sequence_verifies_before_and_after_enabling(self):
        self.assertEqual(self.run_apply(), ["disable", "network", "backup", "write", "restart",
                                            "verify-disabled", "network", "enable", "verify-enabled"])

    def test_failed_apply_never_leaves_controller_enabled(self):
        self.assertEqual(self.run_apply(False)[-2:], ["verify-disabled", "disable"])
        self.assertEqual(self.run_apply(True)[-3:], ["enable", "verify-enabled", "disable"])


if __name__ == "__main__":
    unittest.main()
