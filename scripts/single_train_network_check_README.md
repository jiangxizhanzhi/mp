# 单兵训练跨云网络验收

`single_train_network_check.py` 是独立脚本，不修改 BC、MP、CPCloud 代码。默认使用内置的 36 个“2026届研究生”账号，`--count` 可设为 1 到 36，默认 36。预检不会创建训练；加 `--execute` 后通过 BC 正常认证入口启动，每个账号只发一次创建请求。未结束的请求会持续观察数据库关联记录，不会自动重试创建或关闭房间。

## 依赖与准备

在能够访问现场跨云网络的 Linux 服务器执行。T4 项目既有脚本已确认现场 root 可直接免密执行 `mysql` 登录 MariaDB；本脚本沿用该方式，只执行只读查询，不需要配置数据库主机、账号、密码，也不需要 PyMySQL。`MYSQL_BIN` 可覆盖 mysql 客户端路径。

脚本面向离线环境，Python 部分只使用标准库，**无需安装任何 Python 第三方包，不运行 pip，不下载依赖**。HTTP 请求使用标准库 `urllib`；原始密码编码调用系统已有的 `openssl`，与平台正常客户端编码一致。

需要 Python 3.7 及以上，以及系统已有的 `mysql`、`ping`、`openssl` 命令。在现场脚本目录先检查：

```bash
python3 --version
command -v python3 mysql ping openssl
```

第二条命令应打印四个可执行文件路径。预检的密码模式只需 Python 和 mysql；实际启动及网络验收需要 ping，原始密码模式还需要 openssl。使用 `--sessions` 或 `--login-password-format client` 时不调用 openssl。`OPENSSL_BIN` 可覆盖 openssl 路径，例如 `export OPENSSL_BIN=/usr/bin/openssl`。脚本直接访问平台网关，不使用终端中的 HTTP 代理配置，不需要服务器连接互联网。

### 账号与认证

默认账号已预置在脚本的 `PRESET_USERNAMES` 中，无需准备 `accounts.json`，也无需传 `--accounts` 或预先准备 JWT。来源是 2026-11-26 的 `diagnostics/all_databases.sql`：通过 `mp_auth_org` → `mp_auth_tenant_user` → `mp_auth_user` 确认组织“2026届研究生”（用户所说的“2026级研究生”），组织 ID 为 `a3ff7af0ec1e429c99f7517ee7e41a91`，恰好 36 个账号，备份中均已启用、已审核且有登录记录。

这 36 个用户名按字母顺序固定，`--count N` 使用前 N 个；首个是 `caoran`，末个是 `zhouzixin`。11 月 4 日与 11 月 26 日备份中的同名账号 ID 不同，因此脚本运行时按固定用户名及组织归属查询当前 ID，不沿用历史 ID。只核对选中的前 N 个账号；如果账号缺失、组织不符、禁用、未审核、无登录记录或已有同模板房间，本批停止，不自动换用其他账号。JSON 报告包含账号 ID 和用户名。

统一原始密码按本次要求预置为 `Axctfoj01`。可用 `--password '其他密码'` 或环境变量 `TRAIN_TEST_PASSWORD` 覆盖，优先级为命令行、环境变量、内置密码。预检不需要密码。正常前端登录使用的编码参数 `B13H089Y` 已作为默认值内置，与前端 `@cyber/helper` 依赖包中的 `encryptPsw` 一致，**正常版本不再需要手动 export TRAIN_LOGIN_DES_KEY**；现场定制过该参数时仍可通过 `TRAIN_LOGIN_DES_KEY` 覆盖。脚本不从数据库或后端私有凭据读取参数。也可使用 `--login-password-format client --password '正常客户端已编码的密码'`，此时不需要 openssl 或编码参数配置。

如需覆盖内置账号，可通过 `--accounts /path/to/accounts.json` 指定自定义账号 ID 数组，或用 `TRAIN_ACCOUNTS_FILE` 指定文件；此时统一密码仍按上述优先级选择。脚本同目录的 `accounts.json` 不再自动读取。例如文件内容：

```json
["test-user-id-001", "test-user-id-002"]
```

若不同测试账号使用不同密码，文件可用 `{"test-user-id-001":"TRAIN_PASSWORD_001"}` 的映射格式，分别读取对应环境变量；显式传入 `--password` 会统一覆盖这些密码。映射模式不使用 `TRAIN_TEST_PASSWORD` 或内置密码作为缺失环境变量的替代值。

内置模式使用固定顺序；自定义账号模式保留按数据库 `mp_auth_user.last_login` 排序选择的行为。两种模式均按用户名排除 `admin`（不按角色表判断），并排除禁用、未审核、无登录记录及已有同模板房间的账号。密码模式下通过平台正常登录入口获取 JWT，校验登录用户及模板访问权限，全部通过后才开始启动。登录失败或出现验证码要求时停止本批，不重试、不绕过认证。实际登录可能更新账号最近登录时间；本批选定后不因更新重新排序。数据库 `password` 为 PBKDF2 摘要，不能直接作为原始密码使用，脚本不读取该字段或 `token_secret`。若已有正常认证会话，仍可使用 `--sessions 文件路径`（JSON 文件内容为账号 ID -> JWT 环境变量名的映射）；该参数与 `--accounts`、`--password` 互斥。

## 运行示例

默认使用 `http://127.0.0.1` 作为平台网关（T4 现场历史记录存在 Nginx 监听 80 端口，具体 `/api/mp`、`/api/ojj` 路由仍需现场确认），账号为内置的“2026届研究生”36 人。可通过 `TRAIN_BASE_URL`、`TRAIN_ACCOUNTS_FILE` 覆盖默认值，或者使用 `--base-url`、`--accounts` 显式覆盖。

先预检（密码模式仅查询数据库，不进行登录、不启动）：

```bash
python3 single_train_network_check.py --template-name '实验模板名称'
```

执行全部 36 个账号的批量验收（使用内置密码和正常客户端编码参数）：

```bash
python3 single_train_network_check.py --template-name '实验模板名称' --execute > report.json
```

仅启动固定顺序的前 1 个或前 12 个账号，并可指定其他统一密码：

```bash
python3 single_train_network_check.py --template-name '实验模板名称' --count 1 --execute > report-1.json
python3 single_train_network_check.py --template-name '实验模板名称' --count 12 --password '其他密码' --execute > report-12.json
```

使用自定义清单（示例文件有两个账号，数量显式设为 2）：

```bash
python3 single_train_network_check.py --template-name '实验模板名称' --accounts ./accounts.json --count 2 --execute > report-custom.json
```

监控进度输出到 stderr，报告输出到 stdout。`--workers` 默认 12，`--startup-timeout` 默认 1800 秒，`--poll-interval` 默认 5 秒，`--ping-count` 默认 3。`--network-timeout` 默认 180 秒，`--network-poll-interval` 默认 5 秒，控制拓扑全部成功后的网络就绪复查。退出码：0（成功或预检通过），1（验收失败），2（预检或运行异常）。实例由平台正常创建，脚本不会自动销毁实例。

## 管理网口径（T4）

- 这里的管理网是服务器所在网络与不同厂商创建的虚拟机互联的网络，**不是 MP 的 `mgmt_ip` 字段**。
- 本地云：使用 `neutron.networks`、`externalnetworks`、`subnets` 定位实际外部网络。历史名称 `oj_ext`（可用 `--external-network` 修改）。使用实际 `neutron.ports` / `ipallocations` 中的固定 IP，按实例资源 ID 或对应 MP 端口资源 ID 关联。
- 外部厂商：按 `CreateHandler.createVirtualMachine` 的写回逻辑使用实例第一个端口（按 `order` 排序）的 IP，并核查 MAC 与厂商网段。当前代码对厂商二/三采取实际地址回读后写回逻辑；部署版本若比源码旧，地址可能缺失或陈旧。脚本只核验 MP 当前记录的地址和所在网段，不能独立证明厂商返回的地址与当前虚拟机完全一致。
- 历史现场网段：厂商一/三 `192.168.141.0/24`，厂商二 `192.168.140.0/24`。如实际现场配置已变，可用 `--vendor-cidr 1=实际CIDR` 等参数覆盖。OpenStack 网段从运行时 Neutron 读取，不使用历史固定网段。
- 同批实例重复 IP、无 IP、网段不匹配、虚拟机节点数量异常、启动失败都不会判为通过。

## 验收条件

1. 模板精确唯一；预期 VM 数量来自 MP 模板插件节点 `virtual_type=4`。
2. 按每批实例的 BC 房间 → 本地场景 → MP 场景链路跟踪 `event` / `event_status`。初始化完成（`1,4`）仍需等待；成功终态取前端 `getSceneStatus` 使用的组合；失败、删除、超时不通过。全部成功且在探测前二次核对仍正常，才进入网络探测。
3. 逐个节点核对 MP 生命周期状态与实际管理网地址，向该批实例已知地址发送 ICMP；无地址、地址重复或不属于对应网段时不发送。首轮无 ICMP 应答不立即失败，按批次轮询已分配的同一地址，直到应答或 180 秒网络就绪窗口耗尽。所有节点至少完成首轮探测；已在执行的探测可能略超等待窗口。
4. JSON 汇总每批场景、节点及探测结果。`mp_node_ready` 指 MP 状态正常，不代表虚拟机内的应用完成启动；`REACHABLE` 指 ICMP 已从当前服务器获得应答，不代表完整网络配置、SSH 或业务服务验证成功。`network_attempts` 为实际探测轮数，`network_elapsed_seconds` 为从网络验收开始至最后一次返回的累计时间，`network_timeout=true` 为就绪窗口内始终未应答。至少一个 ICMP 应答即为可达，不代表无丢包，且正常虚拟机可能禁用 ICMP。同批重复 IP 会直接失败，但目前不核验全量其他拓扑或物理网络上的 IP 冲突。`startup_seconds` 为从批量提交开始累计至观测到终态的时间，`create_consume_seconds` 为 MP 记录的实际创建耗时。监控出错后取消尚未发送的请求并保留部分报告（未知状态标 `UNKNOWN`），已经提交的请求不会自动重发或回收。

## 流程伪代码

```text
读取参数（数量 1 到 36，默认 36）
未指定自定义账号或会话文件时：
    取内置“2026届研究生”固定用户名的前 N 个，按组织查询当前 ID
    选择统一密码（命令行 > 环境变量 > 内置密码）
否则读取指定账号或会话清单
通过现场 mysql 只读查询：
    精确查找模板；统计模板 VM 数量；确认外部网络；核对本批账号条件
如果仅预检：
    输出待测试账号数量和模板节点数，不登录、不启动，结束
通过正常账号认证取得会话并验证模板权限
如果任何选中账号认证失败：
    不创建训练，结束
并发提交本批训练创建请求（默认 12 个工作线程，每账号只提交一次）
同时每 5 秒查询本批房间 → BC 本地场景 → MP 场景：
    初始化完成仍继续等待
    记录 MP 成功、失败、删除和未知状态
    直到本批全部终态或达到超时时间
如果监控异常：
    取消尚未发送的请求，输出已有的部分报告，结束
如果存在失败或超时，或再次核对时实例已改变：
    全批网络检查标记跳过，输出启动报告，结束
对于本批各成功场景：
    找出 VM 节点，按实际提供方获取服务器可达网络地址
    若缺少地址、地址歧义或本批 IP 重复：标记失败，不探测
    否则仅检查此次实例所分配地址的 ICMP 可达性
    在网络就绪窗口内只复查尚未应答的已分配地址（默认 180 秒）
    结合 MP 节点生命周期状态、节点数量判定场景结果
输出账号/房间/场景/节点对应关系与汇总报告
```

## 验证范围

源码依据：BC `SingleRoomController` / `SingleRoomServiceImpl`，MP `BaseCloudPluginSceneElementModel.VirtualType` / `CreateHandler` / `HandlerCommon`，前端 `scene-websocket.js`，历史备份 `diagnostics/all_databases.sql` 与 `mp-project-session-history.md`。36 个内置用户名及组织归属已与 11 月 26 日备份核对；该备份不能证明当前账号状态或给定密码仍然有效。当前开发机没有 mysql 客户端，现场服务器无法直连。尚未验证真实接口、现场 mysql 连接、部署版本以及现场 ICMP。

使用 `-S` 禁止加载第三方 site-packages 后运行测试，验证脚本没有 Python 第三方包依赖；密码密文与原实现的空串、边界长度、中文测试向量逐字节一致。标准库 HTTP 已通过本机回环接口验证登录、会话校验、模板权限、创建请求、禁止重定向、不重试 POST 和错误信息处理。其余测试覆盖内置账号解析、1 到 36 的数量限制、固定顺序、密码覆盖、数据库 CLI 输入输出、批量状态及地址识别，不访问真实服务器：

```bash
python3 -S -m unittest -v test_single_train_network_check.py
```
