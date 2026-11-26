"""Offline tests of collection paths and archive safety (no real Docker)."""
import contextlib
import io
import json
import os
import tarfile
import tempfile
import types
import unittest
import zipfile
from pathlib import Path
from unittest.mock import patch


SCRIPT = Path(__file__).with_name("collect_underlying_sources.sh")
mod = types.ModuleType("collect_underlying_sources")
source = SCRIPT.read_text().split("<<'PY'\n", 1)[1].rsplit("\nPY\n", 1)[0]
exec(compile(source, str(SCRIPT), "exec"), mod.__dict__)


def fixture_tar(path):
    files = {
        "./server.py": b"def app(): pass\n",
        "./apps/scenarios.py": b"# source\n",
        "./requirements.txt": b"flask\n",
        "./server.pyc": b"compiled",
        "./.env": b"password=should-not-be-collected\n",
        "./admin.openrc": b"credentials",
        "./logs/service.log": b"runtime log",
        "./disk.qcow2": b"guest disk",
        "./all_databases.sql": b"database dump",
        "./credentials/private.json": b"runtime secret",
        "./guacamole.properties": b"password=not-source",
        "../outside.py": b"unsafe path",
        "/absolute.py": b"unsafe path",
    }
    with tarfile.open(path, "w") as out:
        for name, content in files.items():
            info = tarfile.TarInfo(name)
            info.size = len(content)
            out.addfile(info, io.BytesIO(content))
        symlink = tarfile.TarInfo("./linked.py")
        symlink.type = tarfile.SYMTYPE
        symlink.linkname = "/root/private.py"
        out.addfile(symlink)


# A stand-in Docker executable exercises real tar-stream subprocess handling.
# It has two modes: running container with no tar, or image-only fallback.
FAKE_DOCKER = """#!/usr/bin/env python3
import os, sys
from pathlib import Path
args = sys.argv[1:]
with open(os.environ['FAKE_DOCKER_LOG'], 'a') as log:
    log.write(' '.join(args) + '\\n')
mode = os.environ['FAKE_DOCKER_MODE']
if args[:2] == ['ps', '-a']:
    if mode in ('running', 'tar'):
        print('adaptation\\tsvcloud:v3\\tUp 1 day')
elif args[:2] == ['image', 'ls']:
    print('svcloud\\tv3\\tsha256:local-image')
elif args[0] == 'inspect':
    if '.Mounts' not in args[2]:
        print(('running' if mode in ('running', 'tar') else 'created') + '\\t/opt/svcloud\\tsvcloud:v3')
elif args[0] == 'exec':
    if mode == 'tar':
        sys.stdout.buffer.write(Path(os.environ['FAKE_SOURCE_TAR']).read_bytes())
        sys.exit(0)
    print('fake container has no tar command', file=sys.stderr)
    sys.exit(127)
elif args[0] == 'cp':
    if args[1].endswith(':/opt/svcloud'):
        with open(os.environ['FAKE_SOURCE_TAR'], 'rb') as source:
            while True:
                block = source.read(65536)
                if not block:
                    break
                sys.stdout.buffer.write(block)
    else:
        print('path not found', file=sys.stderr)
        sys.exit(1)
elif args[0] == 'create':
    print('temporary-container-id')
elif args[0] == 'rm':
    print(args[-1])
else:
    sys.exit(1)
"""


class CollectionTests(unittest.TestCase):
    def test_default_services_cover_virtualization_not_unrelated_apps(self):
        args = mod.arguments([])
        self.assertEqual(set(args.services), set(mod.CONTAINERS) | set(mod.PACKAGES))
        self.assertIn("svcloud", args.services)
        self.assertIn("guacamole", args.services)
        self.assertNotIn("mp", args.services)
        self.assertNotIn("bc", args.services)
        self.assertEqual(mod.arguments(["--services", "svcloud,nova,svcloud"]).services,
                         ["svcloud", "nova"])

    def test_stream_excludes_credentials_runtime_and_unsafe_paths(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            fixture_tar(root / "input.tar")
            with open(root / "input.tar", "rb") as input_, \
                    tarfile.open(root / "output.tar.gz", "w:gz") as output:
                archive = mod.Archive(output, 0)
                archive.stream(input_)
            with tarfile.open(root / "output.tar.gz") as saved:
                members = set(saved.getnames())
            self.assertEqual(members, {"source/server.py", "source/apps/scenarios.py",
                                       "source/requirements.txt", "source/server.pyc"})
            self.assertEqual(archive.counts["source"], 2)
            self.assertEqual(archive.counts["artifact"], 1)
            self.assertEqual(archive.skips["link"], 1)

    def test_binary_artifact_is_not_mislabeled_as_source(self):
        self.assertEqual(mod.category(Path("container-web-tty"), ["container-web-tty"]), "artifact")
        self.assertEqual(mod.category(Path("guacamole.war")), "artifact")
        self.assertEqual(mod.category(Path("server.pyc")), "artifact")
        self.assertEqual(mod.category(Path("server.py")), "source")
        self.assertEqual(mod.category(Path("svcloud_source.tar.gz")), "artifact")
        self.assertIsNone(mod.category(Path("svcloud-20261126_backup.tar.gz")))

    def test_installed_package_and_version_metadata(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            site = root / "site-packages"
            source_dir = site / "nova"
            source_dir.mkdir(parents=True)
            (source_dir / "__init__.py").write_text("# actual site package")
            (source_dir / "scheduler.py").write_text("# scheduling")
            (source_dir / "backup").mkdir()
            (source_dir / "backup" / "manager.py").write_text("# source module, not runtime backup")
            (source_dir / "cache").mkdir()
            (source_dir / "cache" / "password.py").write_text("# source module, not credential data")
            meta = site / "nova-20.1.0.dist-info"
            meta.mkdir()
            (meta / "METADATA").write_text("Name: nova\nVersion: 20.1.0\n")
            (site / "unrelated").mkdir()
            (site / "unrelated" / "not_collected.py").write_text("test")
            work = root / "work"
            work.mkdir()
            collector = mod.Collector(mod.arguments(["--services", "nova"]), work)
            with patch.object(collector, "package_roots", return_value=[str(site)]), \
                    contextlib.redirect_stdout(io.StringIO()):
                collector.collect("nova")
            self.assertEqual(len(collector.results["nova"]), 1)
            entry = collector.results["nova"][0]
            with tarfile.open(work / entry["archive"]) as archive:
                self.assertEqual(set(archive.getnames()), {
                    "source/nova/__init__.py", "source/nova/scheduler.py",
                    "source/nova/backup/manager.py", "source/nova/cache/password.py",
                    "source/nova-20.1.0.dist-info/METADATA"})

    def run_fake_docker(self, mode):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            binary = root / "docker"
            binary.write_text(FAKE_DOCKER)
            binary.chmod(0o700)
            fixture = root / "source.tar"
            fixture_tar(fixture)
            log = root / "docker.log"
            output = root / "output"
            env = {"PATH": str(root) + os.pathsep + os.environ["PATH"],
                   "FAKE_DOCKER_LOG": str(log), "FAKE_DOCKER_MODE": mode,
                   "FAKE_SOURCE_TAR": str(fixture)}
            with patch.dict(os.environ, env), contextlib.redirect_stdout(io.StringIO()), \
                    patch.object(mod, "CONTAINERS", dict(mod.CONTAINERS,
                        svcloud=dict(mod.CONTAINERS["svcloud"], host=()))):
                rc = mod.main(["--services", "svcloud", "--output-dir", str(output)])
            self.assertEqual(rc, 0)
            bundles = list(output.glob("*.zip"))
            self.assertEqual(len(bundles), 1)
            with zipfile.ZipFile(bundles[0]) as bundle:
                self.assertIsNone(bundle.testzip())
                manifest = json.loads(bundle.read("manifest.json"))
                entry = manifest["services"]["svcloud"]["archives"][0]
                self.assertEqual(entry["method"], "docker exec tar" if mode == "tar" else "docker cp tar stream")
                self.assertEqual(entry["counts"]["source"], 2)
                with tarfile.open(fileobj=io.BytesIO(bundle.read(entry["archive"])), mode="r:gz") as tar:
                    self.assertIn("source/server.py", tar.getnames())
                    self.assertNotIn("source/.env", tar.getnames())
            commands = log.read_text().splitlines()
            if mode in ("running", "tar"):
                self.assertTrue(any(line.startswith("exec adaptation ") for line in commands))
                self.assertEqual(any(line.startswith("cp adaptation:/opt/svcloud ") for line in commands),
                                 mode == "running")
                self.assertFalse(any(line.startswith("create ") for line in commands))
            else:
                self.assertTrue(any(line.startswith("create ") for line in commands))
                self.assertTrue(any(line.startswith("rm -v temporary-container-id") for line in commands))
            self.assertFalse(any(line.startswith(("start ", "stop ", "restart ", "pull ")) for line in commands))

    def test_running_container_falls_back_to_docker_cp(self):
        self.run_fake_docker("running")

    def test_running_container_prefers_tar_without_docker_cp(self):
        self.run_fake_docker("tar")

    def test_image_only_fallback_creates_removes_never_starts(self):
        self.run_fake_docker("image")

    def test_host_fallback_and_missing_services_still_produce_report(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp)
            project = root / "svcloud"
            project.mkdir()
            (project / "server.py").write_text("# deployed source")
            (project / "credentials").mkdir()
            (project / "credentials" / "private.json").write_text("not source")
            output = root / "out"
            specs = dict(mod.CONTAINERS, svcloud=dict(mod.CONTAINERS["svcloud"], host=(str(project),)),
                         cpcloud=dict(mod.CONTAINERS["cpcloud"], host=()))
            with patch.multiple(mod, CONTAINERS=specs), \
                    patch.object(mod.shutil, "which", return_value=None), \
                    contextlib.redirect_stdout(io.StringIO()):
                result = mod.main(["--services", "svcloud,cpcloud", "--output-dir", str(output)])
            self.assertEqual(result, 0)
            with zipfile.ZipFile(next(output.glob("*.zip"))) as bundle:
                manifest = json.loads(bundle.read("manifest.json"))
                self.assertIn("未找到", manifest["services"]["cpcloud"]["status"])
                entry = manifest["services"]["svcloud"]["archives"][0]
                self.assertEqual(entry["method"], "host files")
                with tarfile.open(fileobj=io.BytesIO(bundle.read(entry["archive"])), mode="r:gz") as tar:
                    self.assertEqual(tar.getnames(), ["source/svcloud/server.py"])


if __name__ == "__main__":
    unittest.main()
