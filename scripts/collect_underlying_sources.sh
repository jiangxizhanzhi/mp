#!/usr/bin/env bash
# Collect locally deployed virtualization / console service source files.
# Run on the site controller. No network downloads or service restarts.
set -euo pipefail
umask 077
exec python3 - "$@" <<'PY'
import argparse
import collections
import datetime
import hashlib
import json
import os
import re
import shutil
import socket
import stat
import subprocess
import sys
import tarfile
import tempfile
import threading
import zipfile
from pathlib import Path, PurePosixPath


CONTAINERS = {
    'svcloud': {
        'names': ('adaptation', 'svcloud'),
        'images': ('svcloud',),
        'paths': ('/opt/svcloud', '/opt/svcloud_project/svcloud', '/app/svcloud',
                  '/srv/svcloud', '/app', '/usr/src/app', '/workspace',
                  '/opt/svcloud.tar.gz', '/opt/svcloud_source.tar.gz', '/opt/svcloud.zip'),
        'markers': ('server.py', 'server.pyc', 'gunicorn_config.py', 'svcloud*.tar.gz'),
        'host': ('/opt/svcloud', '/opt/svcloud_project/svcloud', '/opt/svcloud_project/src/svcloud',
                 '/opt/svcloud.tar.gz', '/opt/svcloud_source.tar.gz', '/opt/svcloud.zip'),
        'one': True,
    },
    'cpcloud': {
        'names': ('cpcloud',),
        'images': ('cpcloud',),
        'paths': ('/opt/cpcloud_project/cpcloud', '/opt/cpcloud', '/app/cpcloud', '/app'),
        'markers': ('manager.py', 'manager.pyc', 'gunicorn_config.py'),
        'host': ('/opt/cpcloud_project/cpcloud', '/opt/cpcloud', '/opt/cpcloud_project/src/cpcloud'),
        'one': True,
    },
    'web-tty': {
        'names': ('cpcloud_web-tty-client', 'container-web-tty', 'web-tty'),
        'images': ('container-web-tty', 'web-tty'),
        'paths': ('/opt/container-web-tty', '/opt/web-tty', '/app', '/src', '/workspace',
                  '/usr/src/app', '/usr/local/src/container-web-tty', '/usr/local/bin/container-web-tty',
                  '/usr/bin/container-web-tty', '/container-web-tty'),
        'markers': ('go.mod', 'package.json', 'main.go', 'container-web-tty'),
        'host': ('/opt/container-web-tty', '/opt/web-tty'),
        'binaries': ('container-web-tty', 'web-tty', 'web-tty-client'),
    },
    'guacamole': {
        'names': ('cpcloud_guacamole', 'guacamole'),
        'images': ('guacamole', 'guacamole-client'),
        'paths': ('/opt/guacamole', '/app/guacamole', '/usr/src/guacamole-client',
                  '/usr/local/src/guacamole-client', '/opt/guacamole-client',
                  '/usr/local/tomcat/webapps/guacamole', '/usr/local/tomcat/webapps/guacamole.war',
                  '/var/lib/tomcat9/webapps/guacamole', '/var/lib/tomcat9/webapps/guacamole.war',
                  '/var/lib/tomcat8/webapps/guacamole', '/var/lib/tomcat8/webapps/guacamole.war'),
        'markers': ('pom.xml', 'guacamole.war', 'package.json', 'guacamole-client*.tar.gz'),
        'host': ('/opt/guacamole', '/opt/guacamole-client'),
    },
    'guacd': {
        'names': ('cpcloud_guacd', 'guacd'),
        'images': ('guacd', 'guacamole-server'),
        'paths': ('/opt/guacamole-server', '/opt/guacd', '/usr/src/guacamole-server',
                  '/usr/local/src/guacamole-server', '/guacamole-server', '/opt/guacamole',
                  '/usr/local/sbin/guacd', '/usr/sbin/guacd', '/app'),
        'markers': ('configure.ac', 'guacd.c', 'guacd', 'guacamole-server*.tar.gz'),
        'host': ('/opt/guacamole-server', '/opt/guacd'),
        'binaries': ('guacd',),
    },
    'dynamips': {
        'names': ('dynamips',),
        'images': ('dynamips',),
        'paths': ('/opt/dynamips', '/usr/src/dynamips', '/usr/local/src/dynamips',
                  '/dynamips', '/app', '/usr/local/bin/dynamips', '/usr/bin/dynamips'),
        'markers': ('CMakeLists.txt', 'dynamips.c', 'dynamips'),
        'host': ('/opt/dynamips', '/usr/local/src/dynamips'),
        'binaries': ('dynamips',),
    },
}

PACKAGES = {
    'nova': ('nova',),
    'neutron': ('neutron', 'neutron_lib'),
    'cinder': ('cinder',),
    'glance': ('glance', 'glance_store'),
    'placement': ('placement', 'os_resource_classes', 'os_traits'),
    'keystone': ('keystone',),
    'zun': ('zun',),
    'kuryr': ('kuryr_libnetwork', 'kuryr'),
}
DEFAULT_SERVICES = tuple(CONTAINERS) + tuple(PACKAGES)

SOURCE_EXT = set(('.py', '.pyi', '.pyx', '.pxd', '.java', '.kt', '.scala',
                  '.go', '.rs', '.c', '.h', '.cc', '.cpp', '.hpp', '.cxx',
                  '.js', '.jsx', '.ts', '.tsx', '.vue', '.svelte', '.qml',
                  '.rb', '.php', '.lua', '.cs', '.swift', '.proto', '.thrift'))
SUPPORT_EXT = set(('.sh', '.bash', '.fish', '.ps1', '.bat', '.cmd', '.html', '.htm',
                   '.css', '.scss', '.sass', '.less', '.jinja', '.jinja2', '.j2', '.tmpl',
                   '.xml', '.json', '.yaml', '.yml', '.toml', '.cfg', '.ini', '.conf',
                   '.properties', '.sql', '.md', '.rst', '.txt', '.in', '.po', '.pot',
                   '.mako', '.svg', '.png', '.jpg', '.jpeg', '.gif', '.webp', '.ico',
                   '.woff', '.woff2', '.ttf', '.otf'))
ARTIFACT_EXT = set(('.pyc', '.pyo', '.so', '.class', '.jar', '.war', '.whl', '.egg'))
BUILD_NAMES = set(('Dockerfile', 'Containerfile', 'Makefile', 'CMakeLists.txt',
                   'LICENSE', 'COPYING', 'NOTICE', 'README', 'CHANGELOG', 'AUTHORS',
                   'MANIFEST.in', 'Procfile', 'Gemfile', 'Rakefile', 'go.mod', 'go.sum',
                   'Cargo.lock', 'yarn.lock', 'pnpm-lock.yaml', 'package-lock.json',
                   'METADATA', 'RECORD', 'WHEEL', 'PKG-INFO', 'entry_points.txt',
                   'top_level.txt', 'setup.cfg', 'configure', 'configure.ac', 'Makefile.am'))
EXCLUDED_DIRS = set(('.git', '.svn', '.hg', '.ssh', '.aws', '.kube', '.gnupg',
                     '.venv', 'venv', 'node_modules', '.cache'))
# Root-level runtime folders in a deployed project may contain huge data.
# Do not exclude names such as backup/cache/snapshots recursively: they can
# be real Cinder, Keystone, Nova, or CPCloud source modules.
RUNTIME_ROOT_DIRS = set(('logs', 'uploads', 'downloads', 'recordings', 'pcap',
                         'tmp', 'secrets', 'credentials'))
EXCLUDED_EXT = set(('.pem', '.key', '.p12', '.pfx', '.jks', '.db', '.sqlite',
                    '.sqlite3', '.log', '.qcow2', '.qcow', '.img', '.iso', '.vmdk',
                    '.vdi', '.raw', '.pcap', '.pcapng', '.mp4', '.avi', '.zip',
                    '.tar', '.gz', '.rar', '.7z'))
EXCLUDED_NAMES = set(('admin.openrc', 'openrc', 'clouds.yaml', 'clouds.yml',
                      'secure.yaml', 'secure.yml', 'id_rsa', 'id_dsa', 'id_ed25519',
                      'shadow', 'passwd', 'htpasswd', 'known_hosts', 'authorized_keys',
                      'guacamole.properties', 'user-mapping.xml',
                      'config.local.py', 'local_settings.py', 'application-production.yml',
                      'application-production.yaml', 'application-prod.properties'))
BROAD = set(('/', '/opt', '/app', '/usr', '/usr/local', '/home', '/var',
             '/var/lib', '/etc', '/root', '/tmp', '/bin', '/sbin', '/usr/bin',
             '/usr/sbin', '/usr/local/bin', '/usr/local/sbin', '/usr/local/lib',
             '/usr/src', '/usr/local/src', '/workspace', '/code', '/src',
             '/data', '/mnt', '/srv', '/run'))


def ordered(items):
    return list(dict.fromkeys(x for x in items if x))


def normalize_member(name):
    p = PurePosixPath(name)
    if p.is_absolute() or '..' in p.parts:
        return None
    parts = tuple(s for s in p.parts if s not in ('', '.'))
    return PurePosixPath(*parts) if parts else None


def source_archive(path):
    # Only recognize archives named for the selected application/source
    # distribution, not arbitrary backups or runtime ZIP files.
    if re.search(r'backup|dump|snapshot', path.name.lower()):
        return False
    return bool(re.fullmatch(
        r'(?:svcloud|cpcloud|guacamole-client|guacamole-server|container-web-tty|dynamips)'
        r'(?:(?:[-_.](?:source|src|v?[0-9][A-Za-z0-9_.-]*)))?'
        r'\.(?:tar\.gz|tar\.bz2|tgz|zip)', path.name.lower()))


def excluded(path):
    if any(p.lower() in EXCLUDED_DIRS for p in path.parts):
        return True
    name = path.name.lower()
    if (name in EXCLUDED_NAMES or name.startswith(('.env', 'id_rsa', 'id_ed25519'))
            or name.endswith('.openrc')
            or (path.suffix.lower() in EXCLUDED_EXT and not source_archive(path))):
        return True
    if (path.suffix.lower() not in SOURCE_EXT
            and re.search(r'(^|[._-])(credentials?|passwords?|secrets?|private[_-]?key)([._-]|$)', name)):
        return True
    if path.suffix.lower() == '.sql' and re.search(r'dump|backup|all_databases', name):
        return True
    return False


def category(path, binaries=()):
    if excluded(path):
        return None
    ext = path.suffix.lower()
    if ext in SOURCE_EXT:
        return 'source'
    if ext in ARTIFACT_EXT or '.so.' in path.name or path.name in binaries or source_archive(path):
        return 'artifact'
    if ext in SUPPORT_EXT or path.name in BUILD_NAMES or path.name.startswith(('Dockerfile.', 'README.')):
        return 'support'
    return None


def safe_name(value):
    return re.sub(r'[^A-Za-z0-9_.-]+', '_', value).strip('._')[:100] or 'source'


def digest(path):
    sha = hashlib.sha256()
    with path.open('rb') as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b''):
            sha.update(block)
    return sha.hexdigest()


class Archive:
    def __init__(self, tar, max_size, binaries=()):
        self.tar = tar
        self.max_size = max_size
        self.binaries = binaries
        self.counts = collections.Counter()
        self.languages = collections.Counter()
        self.skips = collections.Counter()
        self.large = []
        self.seen = set()
        self.bytes = 0

    def accept(self, path, size):
        kind = category(path, self.binaries)
        if kind is None:
            self.skips['excluded or not source/artifact'] += 1
            return None
        if self.max_size and size > self.max_size:
            self.skips['file size limit'] += 1
            if len(self.large) < 50:
                self.large.append(str(path))
            return None
        if str(path) in self.seen:
            self.skips['duplicate'] += 1
            return None
        self.seen.add(str(path))
        return kind

    def add(self, path, size, mode, mtime, fileobj, kind):
        # A fresh TarInfo avoids carrying uid/gid, unsafe links or setuid bits.
        info = tarfile.TarInfo('source/' + str(path))
        info.size = size
        info.mode = 0o755 if mode & 0o111 else 0o644
        info.mtime = int(mtime or 0)
        self.tar.addfile(info, fileobj)
        self.counts[kind] += 1
        self.languages[path.suffix.lower() or '(no suffix)'] += 1
        self.bytes += size

    def stream(self, incoming):
        with tarfile.open(fileobj=incoming, mode='r|*') as source:
            archive_root = None
            first = True
            for member in source:
                path = normalize_member(member.name)
                if first:
                    first = False
                    # docker cp includes the requested directory as a first
                    # entry; docker exec tar usually begins with ".".
                    if member.isdir() and path is not None and len(path.parts) == 1:
                        archive_root = path.parts[0]
                if path is None or not member.isfile():
                    if member.issym() or member.islnk():
                        self.skips['link'] += 1
                    continue
                relative = path.parts[1:] if archive_root == path.parts[0] else path.parts
                if len(relative) > 1 and relative[0].lower() in RUNTIME_ROOT_DIRS:
                    self.skips['root runtime directory'] += 1
                    continue
                kind = self.accept(path, member.size)
                if kind:
                    with source.extractfile(member) as stream:
                        self.add(path, member.size, member.mode, member.mtime, stream, kind)

    def host_file(self, file, name):
        try:
            st = file.lstat()
            if not stat.S_ISREG(st.st_mode):
                self.skips['link/non-regular file'] += 1
                return
            path = normalize_member(name)
            if path is None:
                return
            kind = self.accept(path, st.st_size)
            if not kind:
                return
            stream = file.open('rb')
        except OSError:
            self.skips['unreadable file'] += 1
            return
        # Once an archive header has been written, a short read must invalidate
        # the candidate rather than silently produce a corrupt tar.
        with stream:
            self.add(path, st.st_size, st.st_mode, st.st_mtime, stream, kind)

    def host_tree(self, root, skip_runtime=True):
        root = root.resolve()
        prefix = root.name
        if root.is_file():
            self.host_file(root, prefix)
            return
        for current, dirs, files in os.walk(root, followlinks=False):
            runtime_at_root = RUNTIME_ROOT_DIRS if skip_runtime and Path(current) == root else set()
            dirs[:] = sorted(d for d in dirs
                             if d.lower() not in EXCLUDED_DIRS | runtime_at_root
                             and not (Path(current) / d).is_symlink())
            for name in sorted(files):
                file = Path(current) / name
                self.host_file(file, prefix + '/' + file.relative_to(root).as_posix())

    def summary(self):
        return dict(counts=dict(self.counts), extensions=dict(self.languages),
                    skipped=dict(self.skips), large_files_skipped=self.large,
                    uncompressed_bytes=self.bytes)


class Collector:
    def __init__(self, args, directory):
        self.args = args
        self.directory = directory
        self.docker = shutil.which('docker')
        self.max_size = args.max_file_mib * 1024 * 1024
        self.events = []
        self.results = collections.defaultdict(list)
        self.container_cache = None
        self.image_cache = None
        self.temp_counter = 0

    def log(self, message):
        self.events.append(message)
        print(message, flush=True)

    def command(self, argv):
        try:
            proc = subprocess.run(argv, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                  text=True, errors='replace', timeout=30, check=False)
            return proc.returncode, proc.stdout, proc.stderr
        except (OSError, subprocess.TimeoutExpired) as exc:
            return 1, '', str(exc)

    def dc(self, *args):
        if not self.docker:
            return 1, '', 'docker CLI unavailable'
        return self.command([self.docker, *args])

    def containers(self):
        if self.container_cache is None:
            rc, out, err = self.dc('ps', '-a', '--format', '{{.Names}}\t{{.Image}}\t{{.Status}}')
            if rc:
                self.log('Docker 容器清单不可用：' + err.strip())
            self.container_cache = [line.split('\t', 2) for line in out.splitlines() if line.count('\t') == 2]
        return self.container_cache

    def images(self):
        if self.image_cache is None:
            rc, out, err = self.dc('image', 'ls', '--no-trunc', '--format', '{{.Repository}}\t{{.Tag}}\t{{.ID}}')
            if rc:
                self.log('Docker 镜像清单不可用：' + err.strip())
            self.image_cache = [line.split('\t', 2) for line in out.splitlines() if line.count('\t') == 2]
        return self.image_cache

    @staticmethod
    def image_name(image):
        return image.split('/')[-1].split(':')[0].lower()

    def meta(self, container):
        fmt = '{{.State.Status}}\t{{.Config.WorkingDir}}\t{{.Config.Image}}'
        rc, out, err = self.dc('inspect', '--format', fmt, container)
        if rc:
            self.log(f'  inspect {container} 失败：{err.strip()}')
            return '', '', ''
        fields = out.rstrip('\r\n').split('\t')
        return tuple(fields) if len(fields) == 3 else ('', '', '')

    def mounts(self, container):
        # Only mount paths are queried; do not inspect environment variables.
        rc, out, _ = self.dc('inspect', '--format',
                             '{{range .Mounts}}{{.Source}}{{"\t"}}{{.Destination}}{{"\n"}}{{end}}',
                             container)
        return [line.split('\t', 1) for line in out.splitlines() if '\t' in line] if not rc else []

    def probe_paths(self, container, markers):
        if not markers:
            return []
        # Restrict fallback search to likely source locations; never scan all of /.
        names = []
        for marker in markers:
            if names:
                names.append('-o')
            names.extend(['-name', marker])
        cmd = ('for d in /opt /app /src /workspace /usr/src /usr/local/src /srv; do '
               '[ -d "$d" ] || continue; find "$d" -maxdepth 4 -type f \\( "$@" \\) -print 2>/dev/null; done')
        _, out, _ = self.dc('exec', container, 'sh', '-c', cmd, 'sh', *names)
        return ordered(line if source_archive(PurePosixPath(line)) else str(PurePosixPath(line).parent)
                       for line in out.splitlines() if line.startswith('/') and '\t' not in line)

    def path_allowed(self, path):
        if not path or not path.startswith('/') or '..' in PurePosixPath(path).parts:
            return False
        # /app, /src, /workspace and /code are typical image workdirs, but
        # broad system directories must never be archived.
        return path.rstrip('/') not in (BROAD - {'/app', '/src', '/workspace', '/code'}) and path != '/'

    def candidate_name(self, service, origin):
        self.temp_counter += 1
        return self.directory / service / f'{self.temp_counter:03d}_{safe_name(origin)}.tar.gz'

    def finalize_candidate(self, service, target, archive, origin, method):
        stats = archive.summary()
        counts = stats['counts']
        if not any(counts.values()):
            if target.exists():
                target.unlink()
            return None
        entry = dict(service=service, archive=str(target.relative_to(self.directory)),
                     origin=origin, method=method, **stats)
        entry['sha256'] = digest(target)
        entry['compressed_bytes'] = target.stat().st_size
        self.results[service].append(entry)
        self.log(f'  已打包 {entry["archive"]}：源文件={counts.get("source", 0)}，'
                 f'编译产物={counts.get("artifact", 0)}，支持文件={counts.get("support", 0)}')
        if stats['large_files_skipped']:
            self.log('  超过单文件大小限制，跳过：' + ', '.join(stats['large_files_skipped']))
        return entry

    def host(self, service, paths, origin, binaries=()):
        paths = [p for p in ordered(paths)
                 if Path(p).exists() and str(Path(p).resolve()) not in BROAD]
        if not paths:
            return None
        target = self.candidate_name(service, origin)
        target.parent.mkdir(parents=True, exist_ok=True)
        try:
            with tarfile.open(target, 'w:gz') as stream:
                archive = Archive(stream, self.max_size, binaries)
                for path in paths:
                    archive.host_tree(Path(path), skip_runtime=service not in PACKAGES)
            return self.finalize_candidate(service, target, archive, origin, 'host files')
        except (OSError, tarfile.TarError) as exc:
            self.log(f'  宿主机打包失败 {origin}：{exc}')
            if target.exists():
                target.unlink()
            return None

    def container_stream(self, service, container, path, argv, method, binaries):
        target = self.candidate_name(service, container + '_' + path)
        target.parent.mkdir(parents=True, exist_ok=True)
        error = None
        with tempfile.TemporaryFile() as stderr:
            try:
                proc = subprocess.Popen(argv, stdout=subprocess.PIPE, stderr=stderr)
            except OSError as exc:
                self.log(f'  {method} 失败：{exc}')
                return None, False
            timer = threading.Timer(self.args.timeout, proc.kill)
            timer.daemon = True
            timer.start()
            try:
                with proc.stdout, tarfile.open(target, 'w:gz') as stream:
                    archive = Archive(stream, self.max_size, binaries)
                    archive.stream(proc.stdout)
                    while proc.stdout.read(1024 * 1024):
                        pass
            except (OSError, tarfile.TarError, EOFError) as exc:
                error = str(exc)
                if proc.poll() is None:
                    proc.kill()
            finally:
                timer.cancel()
                try:
                    proc.wait(timeout=15)
                except subprocess.TimeoutExpired:
                    proc.kill()
                    proc.wait()
            stderr.seek(0)
            detail = stderr.read(8192).decode('utf-8', errors='replace').strip()
        if error or proc.returncode:
            self.log(f'  {method} {container}:{path} 失败（rc={proc.returncode}）：'
                     f'{detail or error or "无错误输出"}')
            if target.exists():
                target.unlink()
            return None, False
        return self.finalize_candidate(service, target, archive,
                                       f'{container}:{path}', method), True

    def container_path(self, service, container, state, path, binaries):
        if not self.path_allowed(path):
            return None
        self.log(f'  尝试容器 {container}:{path}')
        if state == 'running':
            # Filter common runtime directories in the container before streaming.
            # The host-side archive filter additionally rejects unsafe links and
            # non-source files. If tar or sh is absent, fall back to docker cp.
            shell = ('p="$1"; shift; if [ -d "$p" ]; then cd "$p" && exec tar -cf - "$@" .; '
                     'elif [ -f "$p" ]; then cd "$(dirname "$p")" && '
                     'exec tar -cf - "$@" "$(basename "$p")"; else exit 3; fi')
            excludes = (['--exclude=' + name for name in sorted(EXCLUDED_DIRS)] +
                        ['--exclude=./' + name for name in sorted(RUNTIME_ROOT_DIRS)])
            entry, success = self.container_stream(
                service, container, path,
                [self.docker, 'exec', container, 'sh', '-c', shell, 'sh', path, *excludes],
                'docker exec tar', binaries)
            if success:
                return entry
        # docker cp works for stopped containers and images with no shell/tar.
        entry, _ = self.container_stream(service, container, path,
                                         [self.docker, 'cp', f'{container}:{path}', '-'],
                                         'docker cp tar stream', binaries)
        return entry

    def has_source(self, service):
        return any(x['counts'].get('source', 0) for x in self.results[service])

    def has_artifact(self, service):
        return any(x['counts'].get('artifact', 0) for x in self.results[service])

    def collect_container(self, service, name, spec):
        state, workdir, image = self.meta(name)
        if not state:
            return
        self.log(f'  容器 {name}：镜像={image}，状态={state}，工作目录={workdir or "(未设置)"}')
        paths = list(spec['paths'])
        if service == 'svcloud' and self.args.svcloud_path:
            paths.insert(0, self.args.svcloud_path)
        paths = ordered(paths[:1] + [workdir] + paths[1:])
        accepted = []
        mounts = self.mounts(name)

        def attempt(path):
            if not self.path_allowed(path):
                return
            if any(path == parent or path.startswith(parent.rstrip('/') + '/') for parent in accepted):
                return
            entry = self.container_path(service, name, state, path, spec.get('binaries', ()))
            if not entry:
                # Bind-mounted project directory may be easier to read from host.
                for source, destination in mounts:
                    if path == destination or path.startswith(destination.rstrip('/') + '/'):
                        relative = path[len(destination):].lstrip('/')
                        host_path = str(Path(source) / relative)
                        if Path(host_path).exists() and host_path not in BROAD:
                            self.log(f'  尝试挂载对应的宿主机源码：{host_path}')
                            entry = self.host(service, [host_path], f'{name}:{path} (bind mount)',
                                              spec.get('binaries', ()))
                            if entry:
                                break
            if entry:
                accepted.append(path)

        for path in paths:
            attempt(path)
            if spec.get('one') and self.has_source(service):
                return
        if state == 'running' and not self.has_source(service):
            for path in self.probe_paths(name, spec.get('markers', ())):
                attempt(path)
                if spec.get('one') and self.has_source(service):
                    return

    def collect_image(self, service, spec):
        if self.args.no_image_fallback:
            return
        images = [(repo, tag, image_id) for repo, tag, image_id in self.images()
                  if self.image_name(repo) in spec['images']]
        for repo, tag, image_id in images:
            # Create an isolated temporary container from a LOCAL image ID.
            # It is never started and only that temporary container is removed.
            name = 'source-collect-' + safe_name(service) + '-' + os.urandom(5).hex()
            self.log(f'  尝试本地镜像 {repo}:{tag}（创建临时容器，不启动）')
            rc, out, err = self.dc('create', '--name', name, '--entrypoint', '/bin/false', image_id)
            if rc:
                self.log('  创建临时容器失败：' + err.strip())
                continue
            container_id = out.strip()
            try:
                self.collect_container(service, container_id or name, spec)
            finally:
                rc, _, err = self.dc('rm', '-v', container_id or name)
                if rc:
                    self.log(f'  临时容器清理失败 {name}：{err.strip()}')
            if self.has_source(service):
                break

    def project(self, service, spec):
        # CPCloud's known host project is authoritative; SVCloud is container-first.
        if service == 'cpcloud':
            for path in spec['host']:
                self.host(service, [path], path)
                if self.has_source(service):
                    return
        names = ordered(row[0] for row in self.containers()
                        if row[0] in spec['names'] or self.image_name(row[1]) in spec['images'])
        names.sort(key=lambda name: (name not in spec['names'],
                                    spec['names'].index(name) if name in spec['names'] else 999))
        if service == 'svcloud' and self.args.svcloud_container:
            names = ordered([self.args.svcloud_container] + names)
        for name in names:
            self.collect_container(service, name, spec)
            if self.has_source(service):
                break
        if not self.has_source(service) and service != 'cpcloud':
            paths = list(spec['host'])
            if service == 'svcloud' and self.args.svcloud_path:
                paths.insert(0, self.args.svcloud_path)
            for path in ordered(paths):
                if self.path_allowed(path) and Path(path).exists():
                    self.log('  尝试宿主机源码：' + path)
                    self.host(service, [path], path, spec.get('binaries', ()))
                    if self.has_source(service) and spec.get('one'):
                        return
        if not self.has_source(service):
            self.collect_image(service, spec)

    @staticmethod
    def package_roots():
        roots = []
        # Prefer the historical active virtual environment over system packages.
        for prefix in ('/opt/openstack_project', '/opt/openstack', '/opt/stack',
                       '/usr/local', '/usr'):
            for lib in ('lib', 'lib64'):
                for pattern in ('python*/site-packages', 'python*/dist-packages'):
                    roots.extend(sorted(Path(prefix, lib).glob(pattern)))
        for command in ('nova-compute', 'neutron-server', 'cinder-volume', 'glance-api',
                        'placement-api', 'keystone-manage', 'zun-compute', 'kuryr-libnetwork'):
            binary = shutil.which(command)
            if binary:
                prefix = Path(binary).parent.parent
                for pattern in ('lib/python*/site-packages', 'lib/python*/dist-packages'):
                    roots.extend(sorted(prefix.glob(pattern)))
        return ordered(str(path.resolve()) for path in roots if path.is_dir())

    @staticmethod
    def package_candidates(root, package):
        p = Path(root)
        result = [p / package, p / (package + '.py')]
        norm = re.sub(r'[-_.]+', '-', package).lower()
        try:
            for child in p.iterdir():
                normalized = re.sub(r'[-_.]+', '-', child.name).lower()
                if (re.match(re.escape(norm) + r'-[0-9]', normalized)
                        and child.name.endswith(('.dist-info', '.egg-info', '.egg', '.whl'))):
                    result.append(child)
        except OSError:
            pass
        return [str(child) for child in result if child.exists()]

    def openstack(self, service, packages):
        roots = self.package_roots()
        for package in packages:
            found = False
            for root in roots:
                paths = self.package_candidates(root, package)
                if not paths:
                    continue
                self.log(f'  尝试实际安装的 Python 模块 {package}：{root}')
                entry = self.host(service, paths, f'{package}@{root}')
                if entry and (entry['counts'].get('source') or entry['counts'].get('artifact')):
                    found = True
                    break
            if not found:
                for path in (f'/opt/openstack_project/src/{package}', f'/opt/stack/{package}',
                             f'/opt/openstack/{package}', f'/usr/local/src/{package}'):
                    if Path(path).is_dir():
                        entry = self.host(service, [path], path)
                        if entry and entry['counts'].get('source'):
                            found = True
                            break
            if not found:
                self.log(f'  未找到模块 {package} 的源码或已安装产物')

    def collect(self, service):
        self.log(f'========== {service} ==========')
        if service in CONTAINERS:
            self.project(service, CONTAINERS[service])
        else:
            self.openstack(service, PACKAGES[service])
        entries = self.results[service]
        if self.has_source(service):
            status = '找到源文件（不保证镜像构建前的完整仓库）'
        elif self.has_artifact(service):
            status = '仅取得部署编译产物，未找到原始源码'
        elif entries:
            status = '仅取得配置/启动脚本/说明等辅助文件'
        else:
            status = '未找到可打包文件'
        self.log(f'[{service}] {status}')
        return status


def arguments(argv=None):
    parser = argparse.ArgumentParser(prog='collect_underlying_sources.sh',
        description='采集现场虚拟化及控制台相关服务源码/部署产物，按服务汇总 ZIP；不采集 MP、BC 或业务数据。')
    parser.add_argument('--services', default=','.join(DEFAULT_SERVICES),
                        help='逗号分隔服务名；默认采集全部已识别的虚拟化/接入组件')
    parser.add_argument('--output-dir', default='/tmp', help='输出目录，默认 /tmp')
    parser.add_argument('--svcloud-container', help='额外优先尝试的 SVCloud 容器名称')
    parser.add_argument('--svcloud-path', help='额外优先尝试的 SVCloud 绝对源码路径')
    parser.add_argument('--no-image-fallback', action='store_true',
                        help='不从已有本地 Docker 镜像创建临时只读采集容器')
    parser.add_argument('--timeout', type=int, default=300, help='单次容器流式采集超时秒数，默认 300')
    parser.add_argument('--max-file-mib', type=int, default=256,
                        help='单个文件大小上限 MiB，默认 256；0 为不限制')
    parser.add_argument('--list-services', action='store_true', help='列出可选择的服务')
    args = parser.parse_args(argv)
    args.services = ordered(s.strip().lower() for s in args.services.split(',') if s.strip())
    if not args.services or any(s not in DEFAULT_SERVICES for s in args.services):
        parser.error('服务必须来自：' + ', '.join(DEFAULT_SERVICES))
    if args.timeout < 1 or args.max_file_mib < 0:
        parser.error('timeout 必须为正整数，max-file-mib 不能为负数')
    return args


def main(argv=None):
    args = arguments(argv)
    if args.list_services:
        print('\n'.join(DEFAULT_SERVICES))
        return 0
    output = Path(args.output_dir).expanduser().resolve()
    output.mkdir(parents=True, exist_ok=True)
    name = ('underlying_sources_' + safe_name(socket.gethostname()) + '_'
            + datetime.datetime.now().strftime('%Y%m%d_%H%M%S') + '_' + os.urandom(3).hex())
    work = Path(tempfile.mkdtemp(prefix=name + '_', dir=str(output)))
    collector = Collector(args, work)
    collector.log('采集目录：' + str(work))
    collector.log('只采集本机已有源码/部署产物；不拉取镜像、不停止或重启现有服务。')
    statuses = {}
    for service in args.services:
        try:
            statuses[service] = collector.collect(service)
        except Exception as exc:
            statuses[service] = f'采集异常：{type(exc).__name__}: {exc}'
            collector.log(f'[{service}] {statuses[service]}，继续下一项')
    manifest = {
        'host': socket.gethostname(), 'collected_at': datetime.datetime.now().astimezone().isoformat(),
        'services': {s: {'status': statuses[s], 'archives': collector.results[s]} for s in args.services},
        'notes': ['source = source-like files; artifact = compiled/deployed product, not original source',
                  'Excluded known credential/runtime files, symlinks, database dumps and guest disk images.',
                  'Source files themselves may contain embedded credentials; review before forwarding.'],
    }
    (work / 'manifest.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    collector.log('========== 采集结果汇总 ==========')
    for service, status in statuses.items():
        collector.log(f'{service}: {status}')
    (work / 'REPORT.txt').write_text('\n'.join(collector.events) + '\n', encoding='utf-8')
    # Include only successfully finalized candidates, never a partial archive
    # left behind by an unexpected error in an individual service.
    archives = sorted(work / entry['archive'] for entries in collector.results.values()
                      for entry in entries)
    (work / 'SHA256SUMS').write_text(''.join(f'{digest(p)}  {p.relative_to(work)}\n' for p in archives),
                                    encoding='utf-8')
    bundle = output / (name + '.zip')
    partial = output / (name + '.zip.partial')
    try:
        # The service archives are already compressed; do not recompress them.
        with zipfile.ZipFile(partial, 'w', compression=zipfile.ZIP_STORED, allowZip64=True) as z:
            for file in [work / 'REPORT.txt', work / 'manifest.json', work / 'SHA256SUMS', *archives]:
                z.write(file, file.relative_to(work).as_posix())
        with zipfile.ZipFile(partial) as z:
            if z.testzip() is not None:
                raise RuntimeError('ZIP integrity check failed')
        partial.replace(bundle)
        checksum = digest(bundle)
        (output / (name + '.zip.sha256')).write_text(f'{checksum}  {bundle.name}\n', encoding='ascii')
    except Exception as exc:
        print(f'汇总 ZIP 失败：{exc}；已保留各服务源码包及报告：{work}', file=sys.stderr)
        return 2
    shutil.rmtree(work)  # Only the private staging directory created by this process.
    print('\n最终源码包：' + str(bundle), flush=True)
    print('SHA256: ' + checksum, flush=True)
    print(f'大小：{bundle.stat().st_size / 1024**2:.2f} MiB；包含服务归档 {len(archives)} 个', flush=True)
    print('请同时查看 ZIP 内 REPORT.txt 和 manifest.json，确认哪些服务仅有编译产物或未找到。')
    return 0 if archives else 2


if __name__ == '__main__':
    try:
        sys.exit(main())
    except KeyboardInterrupt:
        print('采集已中断；未启动或停止现有服务。', file=sys.stderr)
        sys.exit(130)
PY
