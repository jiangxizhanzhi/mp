import contextlib
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import io
import ipaddress
import json
import os
import tempfile
import threading
from pathlib import Path
import unittest
from types import SimpleNamespace
from unittest.mock import patch
from urllib.parse import parse_qs, urlsplit

import single_train_network_check as check


@contextlib.contextmanager
def local_api(responder):
    calls = []

    class Handler(BaseHTTPRequestHandler):
        def handle_request(self):
            request = SimpleNamespace(method=self.command, path=self.path, headers=self.headers,
                data=self.rfile.read(int(self.headers.get('Content-Length', '0'))))
            calls.append(request)
            status, headers, body = responder(request)
            if not isinstance(body, bytes):
                body = json.dumps(body).encode('utf-8')
            self.send_response(status)
            self.send_header('Content-Length', str(len(body)))
            for name, value in headers.items():
                self.send_header(name, value)
            self.end_headers()
            self.wfile.write(body)

        do_GET = handle_request
        do_POST = handle_request

        def log_message(self, *args):
            pass

    server = ThreadingHTTPServer(('127.0.0.1', 0), Handler)
    thread = threading.Thread(target=lambda: server.serve_forever(poll_interval=0.01), daemon=True)
    thread.start()
    try:
        yield f'http://127.0.0.1:{server.server_port}', calls
    finally:
        server.shutdown()
        server.server_close()
        thread.join(timeout=2)


def http_platform(base_url):
    with patch.object(check.shutil, 'which', return_value='/test/mysql'):
        return check.Platform(SimpleNamespace(base_url=base_url, http_timeout=2))


class FakePlatform:
    def __init__(self, count=36, fail=False, duplicate=False, missing=False):
        self.count = count
        self.fail = fail
        self.duplicate = duplicate
        self.missing = missing
        self.started = []

    def template(self, name):
        return {'resource_id': 'template', 'topo_id': 'topo'}

    def expected_vms(self, topo):
        return 3

    def external_network(self):
        return 'external', ipaddress.ip_network('192.0.2.0/24')

    def candidates(self, tid, ids):
        return [{'id': f'u{i}', 'username': f'user{i}'} for i in range(self.count) if f'u{i}' in ids]

    def preset_accounts(self, count):
        return [{'id': f'u{i}', 'username': name} for i, name in enumerate(check.PRESET_USERNAMES[:count])]

    def login(self, row, password):
        return 'issued-session-' + row['id']

    def verify(self, uid, token, tid):
        return True

    def start(self, token, tid):
        self.started.append(token)
        return 'ACCEPTED'

    def scenes(self, tid, selected):
        return [dict(user_id=uid, room_id='room'+uid, local_scene_id='local'+uid,
                     scene_id='scene'+uid, event=2, event_status=5 if self.fail and i == 0 else 4,
                     deleted=0, stage_percentage=100) for i,uid in enumerate(selected)]

    def vms(self, scene_ids):
        rows = []
        for i, sid in enumerate(scene_ids):
            for j in range(3):
                if self.missing and i == j == 0:
                    continue
                ip = f'192.0.2.{i * 3 + j + 1}'
                if self.duplicate and i == 0 and j == 1:
                    ip = '192.0.2.1'
                rows.append(dict(scene_id=sid, node_id=f'n{j}', vm_id=f'vm{i}-{j}', vendor=0,
                                 event=2, event_status=4, management_ip=ip, address_error=None))
        return rows

    def addresses(self, inventory, *args):
        return inventory


class CheckTests(unittest.TestCase):
    def run_batch(self, provider, count=36, execute=True, login_mode=False):
        args = SimpleNamespace(template_name='test', count=count, workers=8, execute=execute,
                               startup_timeout=5, poll_interval=0.001, ping_count=1, ping_timeout=1,
                               network_timeout=0.05, network_poll_interval=0.001)
        with contextlib.redirect_stderr(io.StringIO()), patch.object(check.subprocess, 'run') as ping:
            ping.return_value.returncode = 0
            result = check.execute(provider, {f'u{i}': f't{i}' for i in range(provider.count)}, args, {}, login_mode=login_mode)
        return result, ping.call_count

    def test_36_by_3_success(self):
        result, calls = self.run_batch(FakePlatform())
        self.assertEqual(result['result'], 'SUCCESS')
        self.assertEqual(result['summary']['vm_success'], 108)
        self.assertEqual(calls, 108)

    def test_startup_failed_gates_all_probes(self):
        result, calls = self.run_batch(FakePlatform(count=2, fail=True), count=2)
        self.assertEqual(result['result'], 'STARTUP_FAILED')
        self.assertEqual(result['summary']['startup_success'], 1)
        self.assertEqual(calls, 0)

    def test_init_complete_is_not_startup_failure(self):
        self.assertEqual(check.scene_state(1, 4), 'STARTING')
        self.assertEqual(check.scene_state(2, 4), 'SUCCESS')
        self.assertEqual(check.scene_state(1, 5), 'FAILED')
        self.assertEqual(check.scene_state(9, 4), 'FAILED')
        platform = FakePlatform(count=1)
        original = platform.scenes('template', ['u0'])
        init = [dict(original[0], event=1, event_status=4)]
        with patch.object(platform, 'scenes', side_effect=[init, original, original, original]):
            result, calls = self.run_batch(platform, count=1)
        self.assertEqual(result['result'], 'SUCCESS')
        self.assertEqual(calls, 3)

    def test_scene_gone_before_probe_gate(self):
        platform = FakePlatform(count=1)
        original = platform.scenes('template', ['u0'])
        # First monitor pass completes. Then the scene is deleted before network inspection.
        def current(tid, users):
            if getattr(current, 'seen', False):
                return [dict(original[0], deleted=1)]
            current.seen = True
            return original
        with patch.object(platform, 'scenes', side_effect=current):
            result, calls = self.run_batch(platform, count=1)
        self.assertEqual(result['result'], 'STARTUP_FAILED')
        self.assertEqual(calls, 0)

    def test_query_error_preserves_partial_report(self):
        platform = FakePlatform(count=2)
        partial = platform.scenes('template', ['u0', 'u1'])[:1]
        with patch.object(platform, 'scenes', side_effect=[partial, check.CheckError('mock db failure')]):
            result, calls = self.run_batch(platform, count=2)
        self.assertEqual(result['result'], 'EXECUTION_ERROR')
        self.assertEqual(result['summary']['startup_success'], 1)
        self.assertEqual(result['summary']['startup_unknown'], 1)
        self.assertEqual(result['instances'][0]['scene_id'], 'sceneu0')
        self.assertEqual(calls, 0)

    def test_delayed_network_ready(self):
        args = SimpleNamespace(template_name='test', count=1, workers=3, execute=True,
            startup_timeout=5, poll_interval=0.001, ping_count=1, ping_timeout=1,
            network_timeout=1, network_poll_interval=0.001)
        attempts = {}
        def delayed_ping(command, **kwargs):
            ip = command[-1]
            attempts[ip] = attempts.get(ip, 0) + 1
            return SimpleNamespace(returncode=1 if ip == '192.0.2.1' and attempts[ip] == 1 else 0)
        with contextlib.redirect_stderr(io.StringIO()), patch.object(check.subprocess, 'run', side_effect=delayed_ping):
            result = check.execute(FakePlatform(count=1), {'u0':'t0'}, args, {})
        self.assertEqual(result['result'], 'SUCCESS')
        self.assertEqual(attempts, {'192.0.2.1':2, '192.0.2.2':1, '192.0.2.3':1})
        self.assertEqual(result['instances'][0]['nodes'][0]['network_attempts'], 2)

    def test_network_timeout(self):
        args = SimpleNamespace(template_name='test', count=1, workers=3, execute=True,
            startup_timeout=5, poll_interval=0.001, ping_count=1, ping_timeout=1,
            network_timeout=0.003, network_poll_interval=0.002)
        with contextlib.redirect_stderr(io.StringIO()), patch.object(check.subprocess, 'run') as ping:
            ping.return_value.returncode = 1
            result = check.execute(FakePlatform(count=1), {'u0':'t0'}, args, {})
        self.assertEqual(result['result'], 'NETWORK_FAILED')
        self.assertTrue(all(n['network_timeout'] for n in result['instances'][0]['nodes']))
        self.assertGreaterEqual(ping.call_count, 3)

    def test_duplicate_ip_not_probed(self):
        result, calls = self.run_batch(FakePlatform(count=2, duplicate=True), count=2)
        self.assertEqual(result['result'], 'NETWORK_FAILED')
        self.assertEqual(calls, 4)
        self.assertEqual(result['instances'][0]['network_check'], 'FAILED')

    def test_missing_vm_fails_topology(self):
        result, calls = self.run_batch(FakePlatform(count=2, missing=True), count=2)
        self.assertEqual(result['result'], 'NETWORK_FAILED')
        self.assertEqual(result['summary']['observed_vm_count'], 5)
        self.assertEqual(calls, 5)

    def test_preflight_does_not_start(self):
        platform = FakePlatform(count=2)
        result, calls = self.run_batch(platform, count=2, execute=False)
        self.assertEqual(result['result'], 'PREFLIGHT_ONLY')
        self.assertEqual(platform.started, [])
        self.assertEqual(calls, 0)

    def test_login_only_selected_accounts_and_uses_issued_sessions(self):
        platform = FakePlatform(count=3)
        with patch.object(platform, 'login', wraps=platform.login) as login:
            result, _ = self.run_batch(platform, count=2, login_mode=True)
        self.assertEqual(result['result'], 'SUCCESS')
        self.assertEqual(login.call_count, 2)
        self.assertEqual(set(platform.started), {'issued-session-u0','issued-session-u1'})

    def test_login_failure_starts_nothing(self):
        platform = FakePlatform(count=2)
        with patch.object(platform, 'login', side_effect=['issued-session-u0', None]):
            with self.assertRaises(check.CheckError):
                self.run_batch(platform, count=2, login_mode=True)
        self.assertEqual(platform.started, [])

    def test_password_preflight_does_not_login(self):
        platform = FakePlatform(count=2)
        with patch.object(platform, 'login') as login:
            result, _ = self.run_batch(platform, count=2, execute=False, login_mode=True)
        self.assertEqual(result['result'], 'PREFLIGHT_ONLY')
        login.assert_not_called()

    def test_candidates_exclude_admin_username(self):
        platform = object.__new__(check.Platform)
        with patch.object(platform, 'query', return_value=[]) as query:
            platform.candidates('template', ['u1','u2'])
        sql, bindings = query.call_args.args
        self.assertIn("LOWER(u.username) <> 'admin'", sql)
        self.assertNotIn('mp_auth_user_role', sql)
        self.assertIn('ORDER BY u.last_login DESC', sql)
        self.assertEqual(bindings, ('u1','u2','template'))

    def test_default_gateway_and_preset_accounts(self):
        with patch.dict(os.environ, {}, clear=True):
            args = check.parse_args(['--template-name', 'test'])
            self.assertEqual(args.base_url, 'http://127.0.0.1')
            self.assertIsNone(args.accounts)
            self.assertTrue(args.preset_accounts)
            self.assertEqual(args.count, 36)
            self.assertFalse(args.execute)
            other = check.parse_args(['--template-name', 'test', '--sessions', 'sessions.json'])
            self.assertIsNone(other.accounts)
            self.assertFalse(other.preset_accounts)
            self.assertEqual(other.sessions, 'sessions.json')

    def test_override_default_configuration(self):
        with patch.dict(os.environ, {'TRAIN_BASE_URL':'https://test.invalid', 'TRAIN_ACCOUNTS_FILE':'approved.json'}):
            args = check.parse_args(['--template-name', 'test'])
            self.assertEqual(args.base_url, 'https://test.invalid')
            self.assertEqual(args.accounts, 'approved.json')
            self.assertFalse(args.preset_accounts)
            explicit = check.parse_args(['--template-name','test','--base-url','http://127.0.0.1:8080','--accounts','other.json'])
            self.assertEqual(explicit.base_url, 'http://127.0.0.1:8080')
            self.assertEqual(explicit.accounts, 'other.json')
            self.assertFalse(explicit.preset_accounts)
            sessions = check.parse_args(['--template-name', 'test', '--sessions', 'sessions.json'])
            self.assertIsNone(sessions.accounts)
            self.assertFalse(sessions.preset_accounts)

    def test_count_boundaries_and_incompatible_password_options(self):
        with patch.dict(os.environ, {}, clear=True):
            for count in (1, 36):
                with self.subTest(count=count):
                    args = check.parse_args(['--template-name', 'test', '--count', str(count)])
                    self.assertEqual(args.count, count)
            for extra in (['--count', '0'], ['--count', '37'], ['--count', '-1'],
                          ['--count', '1.5'], ['--password', ''],
                          ['--sessions', 'sessions.json', '--password', 'dummy']):
                with self.subTest(extra=extra), contextlib.redirect_stderr(io.StringIO()):
                    with self.assertRaises(SystemExit) as exc:
                        check.parse_args(['--template-name', 'test', *extra])
                    self.assertEqual(exc.exception.code, 2)

    def test_preset_lookup_resolves_current_ids_in_fixed_order_and_checks_org(self):
        platform = object.__new__(check.Platform)
        names = check.PRESET_USERNAMES[:2]
        current = [{'id': 'current-1', 'username': names[0]}, {'id': 'current-2', 'username': names[1]}]
        with patch.object(platform, 'query', return_value=list(reversed(current))) as query:
            self.assertEqual(platform.preset_accounts(2), current)
        sql, bindings = query.call_args.args
        self.assertIn('mp.mp_auth_tenant_user', sql)
        self.assertIn('o.id=t.org', sql)
        self.assertIn('t.user=u.id', sql)
        self.assertIn('t.tenant=u.tenant', sql)
        self.assertIn('BINARY o.name=BINARY %s', sql)
        self.assertEqual(bindings, (*names, check.PRESET_ORG_ID, check.PRESET_ORG_NAME))

    def test_preset_lookup_rejects_missing_or_ambiguous_accounts(self):
        platform = object.__new__(check.Platform)
        names = check.PRESET_USERNAMES[:2]
        first = {'id': 'current-1', 'username': names[0]}
        cases = [[], [first], [first, dict(first, id='current-2')],
                 [first, dict(first, username=names[1])],
                 [first, {'id': 'current-2', 'username': 'unlisted-user'}]]
        for rows in cases:
            with self.subTest(rows=rows), patch.object(platform, 'query', return_value=rows):
                with self.assertRaisesRegex(check.CheckError, '组织归属'):
                    platform.preset_accounts(2)

    def test_preset_execution_uses_first_n_accounts_and_normal_login(self):
        with patch.dict(os.environ, {}, clear=True):
            for count in (1, 36):
                with self.subTest(count=count):
                    args = check.parse_args(['--template-name', 'test', '--count', str(count), '--execute'])
                    platform = FakePlatform()
                    candidates = platform.candidates
                    with contextlib.redirect_stderr(io.StringIO()), \
                            patch.object(check, 'read_auth_values', side_effect=AssertionError('不应读取账号文件')), \
                            patch.object(check, 'encode_login_passwords',
                                         side_effect=lambda values, fmt: {uid: 'encoded-password' for uid in values}) as encode, \
                            patch.object(platform, 'candidates', side_effect=lambda tid, ids: list(reversed(candidates(tid, ids)))), \
                            patch.object(platform, 'login', wraps=platform.login) as login, \
                            patch.object(check.subprocess, 'run') as ping:
                        ping.return_value.returncode = 0
                        values = check.load_auth_values(args, platform)
                        result = check.execute(platform, values, args, {}, login_mode=True)
                    expected_ids = [f'u{i}' for i in range(count)]
                    self.assertEqual(list(encode.call_args.args[0]), expected_ids)
                    self.assertEqual(set(encode.call_args.args[0].values()), {check.DEFAULT_TEST_PASSWORD})
                    self.assertEqual([call.args[0]['id'] for call in login.call_args_list], expected_ids)
                    self.assertTrue(all(call.args[1] == 'encoded-password' for call in login.call_args_list))
                    self.assertEqual(set(platform.started), {'issued-session-' + uid for uid in expected_ids})
                    self.assertEqual(result['result'], 'SUCCESS')
                    self.assertEqual([r['account_id'] for r in result['instances']], expected_ids)
                    self.assertEqual([r['username'] for r in result['instances']], [f'user{i}' for i in range(count)])

    def test_preset_preflight_needs_no_password_or_encoding_key(self):
        with patch.dict(os.environ, {'TRAIN_TEST_PASSWORD': ''}, clear=True):
            args = check.parse_args(['--template-name', 'test', '--count', '2'])
            platform = FakePlatform()
            with contextlib.redirect_stderr(io.StringIO()), \
                    patch.object(check, 'encode_login_passwords') as encode, \
                    patch.object(platform, 'login') as login:
                values = check.load_auth_values(args, platform)
                result = check.execute(platform, values, args, {}, login_mode=True)
        self.assertEqual(result['result'], 'PREFLIGHT_ONLY')
        encode.assert_not_called()
        login.assert_not_called()
        self.assertEqual(platform.started, [])

    def test_preset_ineligible_account_aborts_without_substitution(self):
        with patch.dict(os.environ, {}, clear=True):
            args = check.parse_args(['--template-name', 'test', '--count', '2', '--execute'])
            platform = FakePlatform()
            values = {'u0': 'encoded-password', 'u1': 'encoded-password'}
            with patch.object(platform, 'candidates', return_value=[{'id': 'u0', 'username': 'user0'}]), \
                    patch.object(platform, 'login') as login:
                with self.assertRaisesRegex(check.CheckError, '同模板房间'):
                    check.execute(platform, values, args, {}, login_mode=True)
        login.assert_not_called()
        self.assertEqual(platform.started, [])

    def test_missing_default_account_file_has_clear_error(self):
        with tempfile.TemporaryDirectory() as tmp:
            with self.assertRaisesRegex(check.CheckError,'未找到授权测试账号清单'):
                check.read_auth_values(Path(tmp)/'accounts.json', True, False)

    def test_shared_password_account_list(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = Path(tmp) / 'accounts.json'
            p.write_text('["u1","u2"]')
            with patch.dict(os.environ, {'TRAIN_TEST_PASSWORD':'dummy-test-password'}):
                self.assertEqual(check.read_auth_values(p, True, True),
                                 {'u1':'dummy-test-password','u2':'dummy-test-password'})

    def test_shared_password_default_environment_and_cli_precedence(self):
        with tempfile.TemporaryDirectory() as tmp:
            p = Path(tmp) / 'accounts.json'
            p.write_text('["u1","u2"]')
            with patch.dict(os.environ, {}, clear=True):
                self.assertEqual(set(check.read_auth_values(p, True, True).values()), {check.DEFAULT_TEST_PASSWORD})
                with patch.dict(os.environ, {'TRAIN_TEST_PASSWORD': 'environment-password'}):
                    self.assertEqual(set(check.read_auth_values(p, True, True).values()), {'environment-password'})
                    self.assertEqual(set(check.read_auth_values(p, True, True, 'cli-password').values()), {'cli-password'})
                with patch.dict(os.environ, {'TRAIN_TEST_PASSWORD': ''}):
                    with self.assertRaisesRegex(check.CheckError, '统一登录密码不能为空'):
                        check.read_auth_values(p, True, True)

    def test_per_account_password_mapping_and_sessions_remain_explicit(self):
        with tempfile.TemporaryDirectory() as tmp, patch.dict(os.environ, {}, clear=True):
            p = Path(tmp) / 'accounts.json'
            p.write_text('{"u1":"TEST_AUTH_1","u2":"TEST_AUTH_2"}')
            with patch.dict(os.environ, {'TEST_AUTH_1': 'first-password', 'TEST_AUTH_2': 'second-password'}):
                self.assertEqual(check.read_auth_values(p, True, True), {'u1': 'first-password', 'u2': 'second-password'})
                self.assertEqual(check.read_auth_values(p, True, True, 'cli-password'),
                                 {'u1': 'cli-password', 'u2': 'cli-password'})
                self.assertEqual(check.read_auth_values(p), {'u1': 'first-password', 'u2': 'second-password'})
            with self.assertRaisesRegex(check.CheckError, '缺少测试账号认证信息环境变量'):
                check.read_auth_values(p, False, False)

    def test_normal_client_encoding(self):
        # 固定结果来自原 cryptography 实现；新实现须保持填充、UTF-8 和密文完全一致。
        vectors = {
            '': 'feb959b7d4642fcb',
            'a': '2f1518e5843f0f69',
            '1234567': '998a3e058213e353',
            '12345678': '96d0028878d58c89feb959b7d4642fcb',
            '123456789': '96d0028878d58c8928b46a38a7a27092',
            '中文测试密码': 'aa67c44b0fb038eb26722bf80550cff8aeabce60858ceff9',
        }
        with patch.dict(os.environ, {'TRAIN_LOGIN_DES_KEY':'12345678'}):
            for password, expected in vectors.items():
                with self.subTest(password=password):
                    result = check.encode_login_passwords({'u': password}, 'plain')
                    self.assertEqual(result['u'], expected)
        self.assertEqual(check.encode_login_passwords(result, 'client'), result)

    def test_default_login_protocol_key_needs_no_environment_configuration(self):
        with patch.dict(os.environ, {}, clear=True):
            result = check.encode_login_passwords({'u': '12345678'}, 'plain')
        self.assertEqual(result['u'], '6229dfeb09c91ad3cc1dd06df1a8be31')

    def test_openssl_receives_password_on_stdin_and_encodes_shared_password_once(self):
        with patch.dict(os.environ, {}, clear=True), \
                patch.object(check.shutil, 'which', return_value='/test/openssl'), \
                patch.object(check.subprocess, 'run') as run:
            run.return_value.stdout = bytes(16)
            values = check.encode_login_passwords({'u1': 'dummy-secret', 'u2': 'dummy-secret'}, 'plain')
        self.assertEqual(values, {'u1': bytes(16).hex(), 'u2': bytes(16).hex()})
        run.assert_called_once()
        self.assertNotIn('dummy-secret', ' '.join(run.call_args.args[0]))
        self.assertEqual(run.call_args.kwargs['input'], b'dummy-secret')
        self.assertTrue(run.call_args.kwargs['check'])

    def test_openssl_failures_are_clear_and_do_not_expose_passwords(self):
        with patch.object(check.shutil, 'which', return_value=None):
            with self.assertRaisesRegex(check.CheckError, '未找到 openssl'):
                check.encode_login_passwords({'u': 'dummy-secret'}, 'plain')
            self.assertEqual(check.encode_login_passwords({'u': '0' * 16}, 'client'), {'u': '0' * 16})
        failure = check.subprocess.CalledProcessError(1, ['openssl'], stderr=b'dummy-secret')
        with patch.object(check.shutil, 'which', return_value='/test/openssl'), \
                patch.object(check.subprocess, 'run', side_effect=failure):
            with self.assertRaisesRegex(check.CheckError, 'openssl 登录密码编码失败') as exc:
                check.encode_login_passwords({'u': 'dummy-secret'}, 'plain')
        self.assertNotIn('dummy-secret', str(exc.exception))
        with patch.object(check.shutil, 'which', return_value='/test/openssl'), \
                patch.object(check.subprocess, 'run', return_value=SimpleNamespace(stdout=b'bad')):
            with self.assertRaisesRegex(check.CheckError, '长度异常'):
                check.encode_login_passwords({'u': 'dummy-secret'}, 'plain')

    def test_standard_library_http_login_verify_and_start(self):
        template_id = '模板 +/?&'

        def responder(request):
            if request.path == '/api/mp/auth/login':
                return 200, {}, {'code': 'MP-000000', 'data': {'token': 'dummy-session'}}
            if request.path == '/api/mp/auth/users/current':
                return 200, {}, {'code': 'MP-000000', 'data': {'id': 'u0'}}
            return 200, {}, {'code': 'OJ-000000', 'data': {'resource_id': template_id, 'room_info': None}}

        # 即使终端残留代理配置，也应直接访问现场平台。
        with local_api(responder) as (base_url, calls), \
                patch.dict(os.environ, {'http_proxy': 'http://127.0.0.1:1', 'HTTP_PROXY': 'http://127.0.0.1:1',
                                       'no_proxy': '', 'NO_PROXY': ''}):
            platform = http_platform(base_url)
            token = platform.login({'id': 'u0', 'username': 'test-user'}, 'encoded-password')
            self.assertEqual(token, 'dummy-session')
            self.assertTrue(platform.verify('u0', token, template_id))
            self.assertEqual(platform.start(token, template_id), 'ACCEPTED')
        self.assertEqual(len(calls), 4)
        self.assertIsNone(calls[0].headers.get('Authorization'))
        self.assertEqual(json.loads(calls[0].data), {'username': 'test-user', 'password': 'encoded-password'})
        self.assertTrue(all(call.headers['Authorization'] == 'JWT dummy-session' for call in calls[1:]))
        self.assertEqual(parse_qs(urlsplit(calls[2].path).query), {'resource_id': [template_id]})
        self.assertEqual(calls[3].method, 'POST')
        self.assertEqual(json.loads(calls[3].data), {'resource_id': template_id})

    def test_http_redirects_do_not_forward_credentials_or_replay_posts(self):
        def responder(request):
            return int(request.path.rsplit('/', 1)[1]), {'Location': '/unexpected-target'}, {}

        with local_api(responder) as (base_url, calls):
            platform = http_platform(base_url)
            for status in (301, 302, 303, 307, 308):
                with self.subTest(status=status):
                    result = platform.api('dummy-session', 'POST', f'/redirect/{status}', 'OJ-000000',
                                          json_data={'resource_id': 'template'})
                    self.assertEqual(result, (False, f'HTTP_{status}', None))
        self.assertEqual(len(calls), 5)
        self.assertTrue(all(call.method == 'POST' and call.path.startswith('/redirect/') for call in calls))

    def test_http_response_failures_are_sanitized(self):
        replies = {
            '/http-error': (503, {}, {}),
            '/not-json': (200, {}, b'private response'),
            '/not-object': (200, {}, []),
            '/unsafe-code': (200, {}, {'code': 'private response'}),
            '/known-code': (200, {}, {'code': 'MP-110004'}),
        }
        expected = {'/http-error': 'HTTP_503', '/not-json': 'REQUEST_OR_RESPONSE_ERROR',
                    '/not-object': 'API_ERROR', '/unsafe-code': 'API_ERROR', '/known-code': 'MP-110004'}
        with local_api(lambda request: replies[request.path]) as (base_url, calls):
            platform = http_platform(base_url)
            for path, code in expected.items():
                with self.subTest(path=path):
                    self.assertEqual(platform.api(None, 'GET', path, 'MP-000000'), (False, code, None))
        self.assertEqual(len(calls), len(expected))

    def test_http_transport_errors_do_not_retry_creation(self):
        platform = http_platform('http://127.0.0.1')
        with patch.object(platform.http, 'open', side_effect=check.URLError('private response')) as open_request:
            self.assertEqual(platform.start('dummy-session', 'template'), 'REQUEST_OR_RESPONSE_ERROR')
        open_request.assert_called_once()

    def test_mysql_cli_sql_and_xml(self):
        platform = object.__new__(check.Platform)
        platform.mysql_bin = 'mysql'
        xml = b'''<?xml version="1.0"?>
<resultset xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" statement="SELECT ...">
  <row><field name="name">A&amp;B</field><field name="event_status">4</field>
  <field name="vendor" xsi:nil="true"/><field name="scene_id"></field></row>
</resultset>'''
        with patch.object(check.subprocess, 'run') as run:
            run.return_value.stdout = xml
            rows = platform.query('SELECT name WHERE name=%s', ("foo'--",))
        cmd = run.call_args.args[0]
        kw = run.call_args.kwargs
        self.assertEqual(cmd[0], 'mysql')
        self.assertNotIn('foo', kw['input'].decode())
        self.assertIn("CONVERT(X'666f6f272d2d' USING utf8mb4)", kw['input'].decode())
        self.assertIn('READ ONLY', kw['input'].decode())
        self.assertEqual(rows, [{'name':'A&B', 'event_status':4, 'vendor':None, 'scene_id':''}])

    def test_address_sources(self):
        platform = object.__new__(check.Platform)
        inventory = [dict(scene_id='s', node_id='local', vm_id='v0', vendor=0),
                     dict(scene_id='s', node_id='vendor', vm_id='v1', vendor=2),
                     dict(scene_id='s', node_id='bad', vm_id='v2', vendor=3)]
        with patch.object(platform, 'query', side_effect=[
            [{'vm_id':'v0', 'port_id':'p', 'ip_address':'192.0.2.10'}],
            [{'scene_id':'s', 'node_id':'local', 'port_id':'p', 'ip_address':'192.0.2.10'}],
            [{'scene_id':'s', 'node_id':'vendor', 'port_id':'v1p', 'ip':'198.51.100.10', 'mac_addr':'test', 'port_order':0},
             {'scene_id':'s', 'node_id':'bad', 'port_id':'v2p', 'ip':'198.51.100.20', 'mac_addr':'test', 'port_order':0}],
        ]):
            out = platform.addresses(inventory, 'net', ipaddress.ip_network('192.0.2.0/24'),
                                     {2:ipaddress.ip_network('198.51.100.0/24'), 3:ipaddress.ip_network('203.0.113.0/24')})
        self.assertEqual(out[0]['management_ip'], '192.0.2.10')
        self.assertEqual(out[0]['address_source'], 'NEUTRON_EXTERNAL_FIXED_IP')
        self.assertEqual(out[1]['management_ip'], '198.51.100.10')
        self.assertIsNone(out[2]['management_ip'])
        self.assertIn('OUTSIDE_NETWORK', out[2]['address_error'])

    def test_missing_address_never_probed(self):
        with patch.object(check.subprocess, 'run') as ping:
            result = check.probe_vm({'vm_id':'v','event':2,'event_status':4,
                'management_ip':None,'address_error':'EXTERNAL_ADDRESS_MISSING'}, SimpleNamespace())
        self.assertFalse(result['success'])
        ping.assert_not_called()


if __name__ == '__main__':
    unittest.main()
