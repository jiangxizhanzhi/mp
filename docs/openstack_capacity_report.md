# 现场当前资源与节点调度只读核查

在 **controller01** 上执行 `scripts/report_openstack_capacity.sh`。脚本只查询现状和写入本地报告，不清理资源、不改超分/预留、不重启服务。

```bash
bash report_openstack_capacity.sh
```

推荐使用现场现有的 root 运维账户，需要 Bash、Python 3.7+（现场 Ubuntu 20.04 自带 Python 3.8）。默认优先通过本机 mysql 只读事务取得 Placement 当前库存及分配；失败后尝试 OpenStack API。API 自动加载已有 `/root/admin.openrc`，或沿用已设置的 `OS_AUTH_URL` / `OS_CLOUD`。命令行路径可通过 `MYSQL_BIN`、`OPENSTACK_BIN` 指定。脚本不安装依赖、不询问或保存密码。

脚本使用既有可信 SSH 连接只读采样 compute01 的实际资源，主机密钥校验保持启用。没有已配置的 SSH 时会报告物理资源未确认，仍可统计已取得的 Placement 额度；不会自动改 SSH 或云平台配置。

## 默认报告

- 两节点的 VCPU、MEMORY_MB、DISK_GB 总量、预留、超分比、已分配及可新增额度，查询的是执行时数据，不沿用历史日志数值。
- Nova compute 服务 up/down、是否启用调度、Placement 禁用标记，以及已取得的 Cell 映射/禁用状态；不满足条件的节点不计入可新增合计。DB 模式按 `last_seen_up`（无则 `created_at`）与 `service_down_time` 判断服务状态，并标明来源。
- controller01、compute01 各自当前未删除虚拟机的数量、状态、资源总和、最近 24 小时新建/启动数量和最多 20 个实例样本；另列未分配宿主机实例和 `nova_cell0` 中未调度成功的实例。读取 `compute_nodes.stats.num_instances` 对照调度器实例计数，并通过 `virsh --readonly` 对照宿主机实际域数量。
- nova-scheduler、nova-conductor 服务状态；主机聚合/可用区、实例数限制、白名单资源元数据；最近存活实例的指定宿主机、可用区、规格/NUMA/CPU 策略等调度约束样本，以及近期 `NoValidHost` 等故障分类。故障同时查询 `nova` 与 `nova_cell0`，不导出原始异常详情。
- 从运行中的 nova-compute / nova-scheduler 进程找出 `--config-file`、`--config-dir`，按顺序读取白名单配置，并记录文件是否晚于进程启动。核对 CPU/内存/磁盘超分、资源预留、CPU 绑定集合及 Placement 当前生效值；过滤器、权重、实例数上限以 **scheduler 进程的配置**为准，不拿 compute 自己的同名配置充当调度器配置。
- 清理后 Placement 分配按现存实例、已删除实例、迁移消费者、未匹配消费者分类；只做核查，不删除分配。未匹配消费者可能来自其他 Cell，不能直接认定为残留。
- 逻辑 CPU、1 秒 CPU 忙碌率、系统负载、Linux MemAvailable，以及根目录、Nova 实例目录、Cinder 目录等实际磁盘空间。相同 device 的目录共享文件系统，不能重复累加空闲容量。
- Cinder 后端池、服务心跳、按管理后端分组的卷数量/逻辑 GiB，以及当前实例的根盘 BDM、卷 ID、管理后端和可识别的 NFS 共享。区分管理服务位置与物理数据位置。
- 从运行中 cinder-volume 的配置文件（未确认进程时注明仅文件配置）读取实际 NFS 共享列表；在两节点采集当前 NFS 挂载、导出目录及文件系统余量，将管理后端关联到 NFS 服务器/路径。后端叫 `nfs-controller01` 并不证明文件一定在 controller；compute 没有 cinder.conf 也不能证明它没提供 NFS 存储。无法关联的源保持未知。
- 活跃 Kali/CentOS Glance 模板的 `min_disk`、镜像大小、根盘 `block_device_mapping`、关联 Cinder 快照/源卷大小和位置。保留模板候选列表，多候选时显示模式与已知大小范围，不擅自选择某个 Kali 版本。
- 本机 Neutron 分配池的地址总数、池内已分配数及余量；厂商二 MP 地址台账按 status/reason 单独汇总，不混为同一个地址池。
- 默认申请 **36 台 Kali（4 vCPU、4 GiB、申请 30 GiB）+36 台 CentOS（1 vCPU、1 GiB、申请 30 GiB）**，共 180 vCPU、180 GiB 内存、申请 2160 GiB 磁盘。实际根盘大小和磁盘落点按模板/源卷探测，分别汇总本地盘和卷盘逻辑需求；不能把申请值当成实际卷大小。计算当前比例及两节点 CPU 比例分别为 2/3/4 的九种组合，给出已取得条件下的分配示例。

**默认使用 `--disk-mode auto`**：按活跃模板识别本地盘、Cinder 卷盘或两者混合。普通本地镜像计入 VM 实际落点的 Nova DISK_GB；卷盘单独计入 Cinder，不占用该节点的本地根盘额度。缺少模板、同类候选的启动方式不同，或存在未建模的额外磁盘时，磁盘测算标为未确认，不输出含这些组的可行分配示例。CPU 库存仍正常展示。

`--disk-mode volume` 和 `--disk-mode local` 保留为显式容量假设，报告标明与模板检测是否冲突。这些参数不会改变任何模板、启动方式、NFS 导出或 Cinder 配置。

实例数过滤也参与测算：例如启用 `NumInstancesFilter` 且上限 56，就需要扣除该节点现有实例槽位；启用 `AggregateNumInstancesFilter` 时读取该节点聚合上限。多个调度器配置不一致会提示，并在已知限制中取保守值。显式启用实例数过滤而未填写上限时，注明依 Nova 默认值 50 测算。

报告中的配置文件值与运行中实际已加载值有区别：文件晚于进程启动会提示待确认；显式 `*_allocation_ratio=0` 或未设置时，可由 Placement 管理，`initial_*` 仅影响初始库存。资源计算始终使用本次查询的 Placement 库存。磁盘预留可能另含镜像缓存保护，不能只按 `reserved_host_disk_mb` 判断异常。

模板原卷大于 30 GiB、精简卷实际增长、项目配额、可用区及亲和性等仍需结合实际启动请求核对，示例不是实际启动成功承诺。更高 CPU 超分只是调度额度变化，不增加真实计算能力。

## 模板和物理存储的判读

例如，检测到 Kali 为 60 GiB 快照卷、CentOS 为普通本地盘镜像，申请各 36 台、各填 30 GiB 时，报告应显示 **2160 GiB Cinder 卷逻辑需求 + 1080 GiB 本地盘逻辑需求**。Kali 根卷不因申请值 30 而缩小；本地 CentOS 则按各节点实际分配分别检查，不能把 36 台都放在仅余 250 GiB 本地调度额度的节点。若 Kali 候选含 50/60 GiB 多个版本，只能显示区间，并提示指定实际模板。

Nova 对卷启动按卷大小检查 `min_disk`，对本地盘启动按 flavor 的本地根盘大小检查。脚本分别处理这两种情况；已知源快照/卷状态非 available 时阻止该组的分配示例。源对象查询失败则保留缺失标记，已知元数据的大小区间不能当成完整上界。原卷后来扩容也不自动改变旧快照的大小。

当前模板探测针对 **Glance 活跃镜像及其中引用的 Cinder 对象**。自动匹配名称含 kali/centos 的候选；用参数可指定其他名称或镜像 UUID。只有 Cinder 快照别名而没有 Glance 镜像的业务模板，本版不会擅自猜测，需结合业务模板资料确认。

`physical.*.storage` 记录进程配置来源、NFS 共享、实际挂载与导出目录；`storage_topology.connections` 关联后端管理节点和物理服务器。不同主机可能有相同 device 编号，不能据此认定共用磁盘；同一共享多次挂载也不能重复累加容量。拓扑结果来自配置与观察，未创建测试卷，不能单凭“已配置”确认后端已经可写或已通过调度验收。

## 启动和操作卡顿时采集运行数据

容量报告表示采集时的库存与条件，不是满载性能测试。2026-11-28 06:34 的报告只有 3 台虚拟机，无法据此解释 70 多台启动、登录和桌面操作时的卡顿。累计 `virsh domstats --block` 字节数也不能替代瞬时延迟、NFS 流量或物理盘吞吐。

新增 `scripts/capture_openstack_runtime.sh`，使用 Bash 与 Python 3.7+，在 **controller01、compute01 两台宿主机上同时运行**。先启动采集，再重现启动、登录或多人操作过程，默认采集 5 分钟；若 Guacamole/guacd 部署在其他机器，也需要在对应主机采集。该脚本只在本机采样，不自动 SSH 到另一台服务器。

```bash
# 在两个节点上分别执行。建议使用 root，以读取 libvirt 和进程 I/O 指标。
bash capture_openstack_runtime.sh --duration 300 --interval 2
```

输出 `runtime_capture_主机名_UTC时间/` 和同名 `.tar.gz`。返回两个节点各自的压缩包，并记录实际触发启动、开始操作、出现卡顿的大致时间与当时实例数量。现存同名目录和压缩包不会覆盖。

- `host.csv`：每 2 秒的 CPU 忙碌率、iowait、系统负载和可用内存。
- `network.csv`：每接口 RX/TX **分别**换算为十进制 MB/s，附累计错误和丢包。首个样本、计数器重置处保留未知值；物理接口、bridge、bond、虚拟口可能重复承载流量，不能把所有行相加。
- `iostat.log`、`nfsiostat.log`：磁盘 IOPS、延迟、队列、吞吐及 NFS RPC 延迟、排队和重传。不能只凭 SSD/RAID 的 `%util` 判定饱和；NFS 首份输出可能是挂载以来的平均值，使用后续间隔数据。
- `vmstat.log`、`pidstat.log`：CPU 等待、换页及活跃进程 CPU、内存和 I/O，帮助关联 QEMU、guacd 和平台服务。vmstat 首份输出可能为开机以来均值。
- `virsh-domstats.log`、`container-stats.log`：约每 10 秒采集运行域的 CPU/vCPU/磁盘累计计数及容器资源使用。virsh 计数需比较相邻样本的增量，不能直接当作 NFS 网卡字节；不支持的命令会记录失败。
- `proc.jsonl`、`nfs-mountstats.jsonl`：内核原始计数和可用的 PSI 信息，供无可选工具时复核。`metadata.json` 记录实际主机、时间、采样数和工具可用/失败状态。可选工具缺失不自动安装，也不把缺失数据当成零负载或正常。

脚本不重启服务、不改超分/挂载/缓存配置、不修改或登录虚拟机、不执行磁盘压测；不收集进程完整命令行、环境变量、数据库凭据或容器 inspect。采集和写日志仍有少量开销，临近磁盘满时应通过 `--output` 选择有空间的新目录。

判读时优先区分：NFS/物理盘排队、链路繁忙、CPU 竞争与换页，以及远程桌面代理。若 SSH 和系统内同一操作正常而网页桌面延迟明显，继续检查 guacd/VNC/noVNC 与浏览器网络。若所有访问方式均变慢，结合磁盘、NFS、CPU 与内存指标定位。分批启动缓解启动高峰，72 台持续并发操作是否流畅仍须在实际负载下验证。

## 如何判断两节点都正常参与调度

先看报告开头的 `节点调度核查`，再看两个节点的当前实例状态、近期启动记录和 libvirt 域数量。两节点都有现存实例说明当前分布覆盖两节点；如果有近期启动记录，还能确认相应时间内实例已落到节点。节点没有实例可能是刚完成清理，不单凭 0 台认定异常。

服务 `up/enabled`、Placement 未禁用、Cell 未禁用且映射存在，是参与调度的必要条件。还需核对 scheduler/conductor、过滤器及实例数槽位、可用区/聚合、请求的指定宿主机、CPU 策略及错误记录。报告使用 `info` / `check` / `unknown` 列出证据与缺失项；**退出码 0 表示采集完成，不表示全部检查正常**。本脚本不创建测试虚拟机；最终新建成功须由实际业务启动结果验收。

## 其他用法

```bash
# 不采集远端物理资源，但仍读取两节点调度库存
bash report_openstack_capacity.sh --skip-ssh

# 现场已有管理员环境、希望只用API读取Placement库存
bash report_openstack_capacity.sh --source api

# 改变这批虚拟机数量，或按本地盘启动测算
bash report_openstack_capacity.sh --kali-count 36 --centos-count 36 --disk-mode local

# 默认auto先列候选；随后指定实际使用的镜像名称或UUID，消除多模板歧义
bash report_openstack_capacity.sh --kali-image '实际Kali镜像名称或UUID' --centos-image 'centos8'

# 业务申请不同的根盘大小；这不负责缩小已有快照或卷
bash report_openstack_capacity.sh --kali-disk-gib 60 --centos-disk-gib 30

# 可选，追加指定项目的配额上限；不会把管理员项目当成业务项目
bash report_openstack_capacity.sh --project 项目UUID

# 查看近72小时的启动/故障情况，增加每节点样本数
bash report_openstack_capacity.sh --recent-hours 72 --sample-limit 40

bash report_openstack_capacity.sh --help
```

项目配额参数仅展示所选项目的上限，不等同于已扣除当前项目使用量的剩余配额。默认不指定项目，避免误把管理员项目额度用于 MP 场景判断。`--source api` 指 Placement 库存来源，其他详细项目仍可读取本机数据库。数据库实例和宿主机统计默认对应现场 `nova` Cell，Cell0 故障另外读取；其他 Cell 不自动连接，Cell 映射不导出数据库连接串。

## 首次现场输出后的兼容修正

2026-11-28 首次回传报告中，两节点 Placement 库存实际剩余 84+88=172 vCPU，但服务查询失败，旧报告将未核实节点排除后的下界 0 当成可新增总量展示，连带使所有超分组合显示为 0。该数值不能用于判断库存枯竭或节点停机。新版增加 `capacity_summary`，分别展示 `inventory_available`（库存余量）、`confirmed_available`（已核实节点余量下界）、`unknown_nodes` 和 `blocked_nodes`。存在状态未知节点时，组合中的 `available_vcpu`、`after_request` 为 `null`，文字显示“未确认”；另提供 `inventory_available_vcpu` / `inventory_after_request` 和明确假设两节点可调度的示例。这些假设不填充或伪造服务状态。

同时修正以下采集问题：

- Nova 服务 SQL 使用反引号转义 MariaDB 的保留字 `binary`；失败时仅保留 SQL 错误码和 SQLSTATE，不输出原始错误文本或认证信息。
- Placement 消费者字段先查列结构，再区分 UUID 形式的 `consumer_id` / `consumer_uuid` 或需要连接 `consumers.uuid` 的整数形式。现场导出结构为 `consumer_id varchar(36)`。
- API 查询 provider traits 显式请求最低需要的 Placement 微版本 1.6。trait 查询失败时保留已取得的 Nova 服务 up/enabled 状态，并将禁用标记置为未知，继续查询另一个节点。
- Cinder 后端池 CLI 失败时增加 Volume v2 模式的只读兼容重试；仍失败就标明缺失，不将其当成空池或空闲空间。
- 去除 scheduler 多个 worker 产生的重复提示；文件晚于进程启动时，将实例数上限明确标为文件值、加载状态未确认。不能仅凭当前文件写着 120 就证明运行中的 scheduler 已采用 120。

旧报告保持原样，修复后的脚本需要再次在现场采集，才能补齐缺失的计算服务、禁用标记和分配核查；离线修正说明不意味着已经取得这些现场数据。

数据库、SSH 采样或 Cinder CLI 插件不可用时，相应内容标记缺失，不当作 0 或空闲；Placement 所有读取方式都失败则退出码为 2。SSH 不可用时无法确认该节点的物理余量和进程配置，应根据报告的缺失项补采。没有采到 scheduler 配置时，实例数限制的测算仍有未确认条件。

默认在当前目录创建唯一的 `capacity_report_主机_日期时间_随机后缀/`，包含 `report.txt` 与 `report.json`。可把两个文件带回用于离线核查。只输出白名单资源数据，不保存管理员认证环境、完整配置、数据库密码、控制台凭据或整库内容；NFS 文件仅输出共享源，不输出挂载选项、导出客户端 ACL 或认证信息；其他驱动的 provider_location 不作为 NFS 字符串输出。自选 `--output-dir` 必须不存在，避免覆盖之前采集结果。

本地验证：

```bash
bash -n scripts/report_openstack_capacity.sh
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s scripts -p 'test_report_openstack_capacity.py' -v
```

相关已有只读 CLI：[Placement 命令及 trait 微版本要求](https://docs.openstack.org/osc-placement/ussuri/cli/index.html)、[Cinder 后端容量查询](https://docs.openstack.org/python-openstackclient/ussuri/cli/command-objects/volume-backend.html)、[按宿主机查询实例](https://docs.openstack.org/python-openstackclient/ussuri/cli/command-objects/server.html)、[主机聚合](https://docs.openstack.org/python-openstackclient/ussuri/cli/command-objects/aggregate.html)、[MariaDB 保留字](https://mariadb.com/docs/server/reference/sql-structure/sql-language-structure/reserved-words)。
