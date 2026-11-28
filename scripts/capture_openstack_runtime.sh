#!/usr/bin/env bash
# Read-only runtime capture. Run locally on each affected OpenStack host.
set -euo pipefail
exec python3 - "$@" <<'PY_RUNTIME'
import argparse
import csv
import datetime
import json
import os
from pathlib import Path
import shutil
import signal
import socket
import subprocess
import tarfile
import threading
import time


def utc_now():
    return datetime.datetime.now(datetime.timezone.utc).isoformat()


def rate(previous, current, elapsed):
    if previous is None or current is None or elapsed <= 0 or current < previous:
        return None
    return (current - previous) / elapsed


def parse_cpu(text):
    for line in text.splitlines():
        fields = line.split()
        if fields and fields[0] == 'cpu' and len(fields) >= 9:
            return [int(value) for value in fields[1:9]]
    return None


def cpu_percent(previous, current):
    # guest/guest_nice are already included in user/nice; never add them twice.
    if previous is None or current is None:
        return None, None
    delta = [b - a for a, b in zip(previous, current)]
    total = sum(delta)
    if any(value < 0 for value in delta) or total <= 0:
        return None, None
    return 100 * (total - delta[3] - delta[4]) / total, 100 * delta[4] / total


def parse_net(text):
    result = {}
    for line in text.splitlines():
        if ':' not in line:
            continue
        name, fields = line.rsplit(':', 1)
        values = fields.split()
        if len(values) >= 16:
            result[name.strip()] = dict(rx_bytes=int(values[0]), tx_bytes=int(values[8]),
                                        rx_errors=int(values[2]), rx_drops=int(values[3]),
                                        tx_errors=int(values[10]), tx_drops=int(values[11]))
    return result


def read_text(path):
    try:
        return Path(path).read_text()
    except OSError:
        return None


def nfs_mountstats():
    text = read_text('/proc/self/mountstats')
    if text is None:
        return None
    sections, current, include = [], [], False
    for line in text.splitlines():
        if line.startswith('device '):
            if include:
                sections.extend(current)
            current = []
            include = ' with fstype nfs ' in line or ' with fstype nfs4 ' in line
        current.append(line)
    if include:
        sections.extend(current)
    return '\n'.join(sections)


def proc_snapshot():
    paths = ('stat', 'meminfo', 'vmstat', 'diskstats', 'net/dev', 'loadavg',
             'pressure/cpu', 'pressure/io', 'pressure/memory')
    return {name: read_text('/proc/' + name) for name in paths}


def memory_available_gib(text):
    for line in (text or '').splitlines():
        if line.startswith('MemAvailable:'):
            return int(line.split()[1]) / (1024 * 1024)
    return None


def start_monitor(label, command, output, results):
    if not shutil.which(command[0]):
        results[label] = dict(status='unavailable', command=command)
        return None
    try:
        with (output / (label + '.log')).open('w') as log:
            process = subprocess.Popen(command, stdout=log, stderr=subprocess.STDOUT,
                                       start_new_session=True)
        results[label] = dict(status='running', command=command)
        return process
    except OSError as exc:
        results[label] = dict(status='failed', error=type(exc).__name__, command=command)
        return None


def stop_monitor(process):
    if process.poll() is not None:
        return dict(status='exited_before_capture_end', returncode=process.returncode)
    # Stop only the diagnostic process group created by this script.
    try:
        os.killpg(process.pid, signal.SIGTERM)
    except ProcessLookupError:
        pass
    try:
        process.wait(timeout=3)
    except subprocess.TimeoutExpired:
        try:
            os.killpg(process.pid, signal.SIGKILL)
        except ProcessLookupError:
            pass
        process.wait(timeout=3)
    return dict(status='stopped_by_capture', returncode=process.returncode)


def poll_command(label, command, output, stop, result):
    if not shutil.which(command[0]):
        result['status'] = 'unavailable'
        return
    result['status'] = 'collecting'
    try:
        with (output / (label + '.log')).open('w') as log:
            while not stop.is_set():
                log.write('\n=== ' + utc_now() + ' ===\n')
                log.flush()
                try:
                    completed = subprocess.run(command, stdout=log, stderr=subprocess.STDOUT,
                                               timeout=8)
                    code = completed.returncode
                    result['successful_samples' if code == 0 else 'failed_samples'] += 1
                    log.write('\nreturncode=' + str(code) + '\n')
                except subprocess.TimeoutExpired:
                    result['failed_samples'] += 1
                    log.write('\ncommand_timeout=true\n')
                log.flush()
                stop.wait(10)
        result['status'] = 'completed' if result['successful_samples'] else 'failed'
    except OSError as exc:
        result.update(status='failed', error=type(exc).__name__)


def write_json(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')


def main(argv=None):
    parser = argparse.ArgumentParser(description='Read-only capture during VM boot/desktop slowness. Run on BOTH hosts at the same time.')
    parser.add_argument('--duration', type=int, default=300, help='seconds, 1..3600; default 300')
    parser.add_argument('--interval', type=int, default=2, help='seconds, 1..60; default 2')
    parser.add_argument('--output', help='new output directory; existing directories are refused')
    args = parser.parse_args(argv)
    if not 1 <= args.duration <= 3600 or not 1 <= args.interval <= 60:
        parser.error('duration must be 1..3600 and interval must be 1..60')
    os.umask(0o077)
    hostname = socket.gethostname()
    stamp = datetime.datetime.now(datetime.timezone.utc).strftime('%Y%m%dT%H%M%SZ')
    output = Path(args.output or ('runtime_capture_' + hostname + '_' + stamp)).resolve()
    archive = Path(str(output) + '.tar.gz')
    if output.exists() or archive.exists():
        parser.error('output directory or archive already exists; choose another --output')
    output.mkdir(parents=True)
    stop = threading.Event()
    for signum in (signal.SIGINT, signal.SIGTERM):
        signal.signal(signum, lambda _signum, _frame: stop.set())
    tools, processes, threads = {}, {}, []
    started = time.monotonic()
    metadata = dict(host=hostname, started_at=utc_now(), duration_requested_seconds=args.duration,
                    interval_seconds=args.interval, uid=os.geteuid(),
                    scope='local host only; historical idle capacity reports cannot diagnose busy latency',
                    tools=tools, network_interfaces={})
    for iface in Path('/sys/class/net').iterdir():
        metadata['network_interfaces'][iface.name] = {
            key: (read_text(iface / key) or '').strip() or None
            for key in ('speed', 'duplex', 'operstate')}
    write_json(output / 'metadata.json', metadata)
    print('Recording local host ' + hostname + ' into ' + str(output), flush=True)
    previous_cpu, previous_net, previous_at = None, {}, None
    samples = 0
    try:
        for label, command in (
                ('iostat', ['iostat', '-x', '-y', '-t', str(args.interval)]),
                ('vmstat', ['vmstat', '-w', '-t', str(args.interval)]),
                ('pidstat', ['pidstat', '-u', '-r', '-d', str(args.interval)]),
                ('nfsiostat', ['nfsiostat', str(args.interval)])):
            process = start_monitor(label, command, output, tools)
            if process is not None:
                processes[label] = process
        for label, command in (
                ('virsh-domstats', ['virsh', '-c', 'qemu:///system', 'domstats', '--list-running',
                                   '--state', '--cpu-total', '--vcpu', '--block']),
                ('container-stats', ['docker', 'stats', '--no-stream', '--format',
                                     '{{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.PIDs}}'])):
            tools[label] = dict(status='pending', command=command, successful_samples=0,
                                failed_samples=0, error=None)
            thread = threading.Thread(target=poll_command,
                                      args=(label, command, output, stop, tools[label]))
            thread.start()
            threads.append(thread)
        with (output / 'host.csv').open('w') as host_log, (output / 'network.csv').open('w') as net_log, \
                (output / 'proc.jsonl').open('w') as raw_log, (output / 'nfs-mountstats.jsonl').open('w') as nfs_log:
            host_csv, net_csv = csv.writer(host_log), csv.writer(net_log)
            host_csv.writerow(('utc', 'elapsed_seconds', 'cpu_busy_pct', 'cpu_iowait_pct', 'load1', 'mem_available_gib'))
            net_csv.writerow(('utc', 'interface', 'rx_MBps_decimal', 'tx_MBps_decimal',
                              'rx_errors_total', 'tx_errors_total', 'rx_drops_total', 'tx_drops_total'))
            while not stop.is_set():
                now, timestamp = time.monotonic(), utc_now()
                snapshot = proc_snapshot()
                cpu = parse_cpu(snapshot.get('stat') or '')
                busy, wait = cpu_percent(previous_cpu, cpu)
                net = parse_net(snapshot.get('net/dev') or '')
                elapsed = None if previous_at is None else now - previous_at
                load = (snapshot.get('loadavg') or '').split()
                host_csv.writerow((timestamp, round(now - started, 3), busy, wait,
                                   load[0] if load else None, memory_available_gib(snapshot.get('meminfo'))))
                for iface, counters in sorted(net.items()):
                    before = previous_net.get(iface, {})
                    rx = rate(before.get('rx_bytes'), counters['rx_bytes'], elapsed or 0)
                    tx = rate(before.get('tx_bytes'), counters['tx_bytes'], elapsed or 0)
                    net_csv.writerow((timestamp, iface, None if rx is None else rx / 1e6,
                                      None if tx is None else tx / 1e6, counters['rx_errors'],
                                      counters['tx_errors'], counters['rx_drops'], counters['tx_drops']))
                raw_log.write(json.dumps(dict(utc=timestamp, elapsed_seconds=now-started, proc=snapshot)) + '\n')
                nfs_log.write(json.dumps(dict(utc=timestamp, mountstats=nfs_mountstats())) + '\n')
                for log in (host_log, net_log, raw_log, nfs_log):
                    log.flush()
                previous_cpu, previous_net, previous_at = cpu, net, now
                samples += 1
                remaining = args.duration - (time.monotonic() - started)
                if remaining <= 0:
                    break
                stop.wait(min(args.interval, remaining))
    finally:
        stop.set()
        for label, process in processes.items():
            tools[label].update(stop_monitor(process))
        for thread in threads:
            thread.join(timeout=12)
        metadata.update(ended_at=utc_now(), elapsed_seconds=time.monotonic()-started, samples=samples)
        write_json(output / 'metadata.json', metadata)
    (output / 'READ_ME.txt').write_text(
        'This is a local runtime capture, not an automatic root-cause verdict.\n'
        'Run on controller01 and compute01 concurrently while reproducing the problem.\n'
        'First CSV rates are unknown (blank); counter resets are also unknown.\n'
        'network.csv uses decimal MB/s. RX and TX are separate directions.\n'
        'Do not sum physical NIC, bridge, bond and virtual-interface rows: traffic can repeat.\n'
        'iostat: inspect await, queue, IOPS and throughput together; %util alone cannot establish SSD/RAID saturation.\n'
        'nfsiostat/vmstat first reports can contain since-mount/boot averages; use subsequent intervals.\n'
        'virsh counters are cumulative, not instantaneous rates or NFS wire bytes. Compare successive samples.\n'
        'Missing/failed tools mean unknown, never zero load. See metadata.json.\n'
        'guacd/QEMU/service CPU can be checked in pidstat and container-stats if available.\n'
        'No packages are installed and no OpenStack service, guest or configuration is changed.\n'
        'No process command lines, environment, SQL credentials or container inspect data are collected.\n')
    with tarfile.open(str(archive), 'w:gz') as bundle:
        bundle.add(str(output), arcname=output.name)
    print('Capture complete: ' + str(archive), flush=True)


if __name__ == '__main__':
    main()
PY_RUNTIME
