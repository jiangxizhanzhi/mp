# 厂商二独占地址池管理

## 行为

- 默认规划池：192.168.140.76～192.168.140.225（150 个地址），掩码 255.255.255.0，网关 192.168.140.254。比原 .126～.225 向左扩展 50 个地址，只管理厂商二，不修改其他云平台。
- 优先使用池内已确认空闲的指定 IP，否则按 IP 数值升序选择其他已确认空闲地址。数据库主键和唯一 owner_key 加上原子条件更新保证同一共享数据库中的并发分配不会重复；地址预占事务独立提交，即使外层拓扑事务失败也保留资源关联。
- 对已预占的合法候选地址进行有界 Java 可达性检查（`InetAddress.isReachable`；运行环境可能使用 ICMP 或 TCP 可达性判断）。有响应则隔离并换候选；无响应**不代表地址空闲**。失败或探测不可用不改变已核对的台账结论。不会对地址池外的节点执行检测。
- 通过已有虚拟机列表回读地址、MAC、资源 UUID 和运行状态。创建请求受理后记录 UUID 和 task_id 日志，并在有限时间内等待虚拟机运行、完整清单中 IP 与台账一致且 MAC 非空。未确认时保留 UUID 和台账、标记创建结果待核查，不把原模板地址或异步受理当作成功。每分钟核对台账；已完成节点的运行端口更新要求场景、节点、端口和厂商 UUID 均匹配。
- 厂商明确返回 IP 冲突、没有任务或资源 ID，且完整列表未发现此次创建名称时才换地址重试；请求超时或结果模糊不盲目重发。同一节点的未确认记录阻止重复创建。
- 关机不释放地址。回收时已关机资源直接删除，运行资源先提交关机并轮询状态，不再固定等待 90 秒；删除后持续核对完整资源列表，确认原 UUID 不存在且曾观察到该资源才允许释放地址。若地址仍有占用响应则隔离。对于从未观察到的异步创建资源，不能仅凭列表没有它就释放 IP。
- FREE 不足时，先从台账取回收中租约的快照，再即时读取厂商清单，核对一批已观察且已确认消失的资源；对这类已释放候选进行现有可达性检查，随后再次原子预占。存在正在回收或待核对的合规候选时，允许有界等待后再核对。真实冲突不会因为无响应而转成 FREE。
- 现场已确认新增 .76～.125 为厂商二专属地址，和原 .126～.225 合计 150 个地址，默认全部纳入 `XD_TWO_IP_ON_DEMAND_PROBE_RANGE`。常规分配和即时回收仍不足时，可以对该范围内的单个候选补充核对：仅限无关联的 INITIAL_UNREVIEWED、完整厂商清单中无占用、未被其他活跃 MP 场景记录占用的地址；先原子预占再执行现有可达性检查。有响应或探测不可用均不提交创建，负响应也不直接批量置 FREE；创建结果按正常 UUID/状态/IP 回读确认。明确拒绝创建后，临时预占的初始地址恢复待核对。显式配置为空则禁用此分支。
- 虚拟机列表按响应的 `next_page_num` 尝试读取后续页（`page_num` 查询参数），结合 `total_size`、`page_num`、唯一 UUID 数量检查完整性。不把不完整或失败的列表当作唯一占用/删除证据，也不认为该清单涵盖非厂商平台资源或未经授权可见的资源。现场需验证分页请求参数的兼容性。

## 首次上线（存量地址核对）

1. 应用新增的数据库迁移 `V202611260900__vendor_second_address_pool.sql`（MariaDB/MySQL 和 DM 均有对应版本）。如果部署环境关闭了 Flyway，请通过该环境现有数据库升级流程应用本次迁移。不要清空现有台账作为重新初始化手段。
2. 所有 MP 实例必须使用同一个地址台账数据库，并停用旧版仍然随机分配的 MP 实例。先维持 `XD_TWO_IP_REVIEWED_FREE` 为空。定时任务或首次请求会建立地址记录，并导入可识别的存量资源/地址证据。
3. 管理员结合厂商管理端资源记录、存量 MP 实例、地址管理记录，核实池内可分配地址。关机、无 ping 响应以及列表未返回的未核实 IP 不应被直接认定为空闲。
4. 配置 `XD_TWO_IP_REVIEWED_FREE`，内容为已经确认空闲的单个地址或闭区间，用逗号分隔。仅初始未核查且没有资源关联的对应记录变为 `FREE`。例如（**示例格式，不能作为现场可用地址证明**）：`192.168.140.126,192.168.140.130-192.168.140.135`。重启或再次核对不会将已分配或被隔离的记录重置为空闲。
5. 用下述只读查询检查结果，再逐步恢复厂商二资源创建。若没有任何已核对的空闲记录，创建会报告地址资源不足，不回退到旧的随机 IP 或固定 IP。

配置项：

| 环境变量 | 默认值 | 用途 |
|---|---|---|
| `XD_TWO_IP_POOL_START` | `192.168.140.76` | 池起始地址 |
| `XD_TWO_IP_POOL_END` | `192.168.140.225` | 池结束地址 |
| `XD_TWO_IP_NETMASK` | `255.255.255.0` | 子网掩码 |
| `XD_TWO_IP_GATEWAY` | `192.168.140.254` | 网关 |
| `XD_TWO_IP_REVIEWED_FREE` | 空 | 首次上线明确核对为空闲的地址/范围 |
| `XD_TWO_IP_ON_DEMAND_PROBE_RANGE` | `192.168.140.76-192.168.140.225` | 紧缺时可逐个核对的专属地址范围；不自动放行真实冲突 |
| `XD_TWO_IP_PROBE_ENABLED` | `true` | 是否启用候选地址辅助可达性检查 |
| `XD_TWO_IP_PROBE_TIMEOUT_MS` | `1000` | 单候选检查超时，合法范围 1～10000 毫秒 |
| `XD_TWO_CREATE_CONFIRM_TIMEOUT_MS` | `300000` | 提交创建后运行及地址确认等待时间 |
| `XD_TWO_CREATE_CONFIRM_POLL_MS` | `3000` | 创建确认轮询间隔 |
| `XD_TWO_RECYCLE_STOP_TIMEOUT_MS` | `120000` | 回收时等待关机确认时间 |
| `XD_TWO_RECYCLE_DELETE_TIMEOUT_MS` | `120000` | 删除结果确认等待时间 |
| `XD_TWO_RECOVERY_POLL_MS` | `3000` | 回收及申请前核对轮询间隔 |
| `XD_TWO_ALLOCATION_RECOVERY_TIMEOUT_MS` | `300000` | FREE 不足时的恢复等待预算（1～300 秒；在已执行的接口调用或候选检查结束后判断是否超时） |

本次向左扩容不修改既有 .126～.225 范围内的租约，定时核对或首次创建会补齐 .76～.125 中缺失的记录（不需要重新执行建表迁移）。若部署文件显式配置了旧的 `XD_TWO_IP_POOL_START`，需要更新该配置。新增地址仍须按上述流程核对空闲；既有 `XD_TWO_IP_REVIEWED_FREE` 不包含新地址时，新地址不会自动成为 FREE。不要在仍有租约时缩小/移动地址池、改变掩码、网关或厂商账号管理范围。

完整 .76～.225 专属地址池已作为按需核验默认值，无需逐一填写 `XD_TWO_IP_REVIEWED_FREE`。实际候选只取按需范围与当前地址池的交集，因此若现场仍显式使用旧的 .126 起点，须先更新池起点才能使用扩容地址。该选项不替代厂商资源清单及 IP 分配回读。

## 地址状态及只读检查

```sql
SELECT status, COUNT(*) AS count
FROM mp_vendor_second_address_pool GROUP BY status;

SELECT status, reason, COUNT(*) AS count
FROM mp_vendor_second_address_pool GROUP BY status, reason
ORDER BY status, reason;

SELECT ip, status, scene_id, element_id, vm_uuid, request_name,
       seen_in_inventory, reason, updated_at
FROM mp_vendor_second_address_pool ORDER BY ip;
```

| 状态 | 说明 |
|---|---|
| `FREE` | 已确认可分配 |
| `RESERVED` | 正在创建；不能分给其他节点 |
| `USED` | 已读取并确认实际资源绑定 |
| `RELEASING` | 已开始回收，等待可靠确认 |
| `VERIFY_PENDING` | 存量、创建或删除不确定、占用响应或其他异常；禁止重复分配 |

`reason` 和 `updated_at` 便于诊断当前状态。主要异常包括 `INITIAL_UNREVIEWED`、`PROBE_OCCUPIED`、`PROVIDER_IP_CONFLICT`、`CREATE_UNCERTAIN`、`CREATED_UNVERIFIED`、`ADDRESS_MISMATCH`、`DELETE_UNCERTAIN` 和 `POST_DELETE_OCCUPIED`。

大量 `VERIFY_PENDING/INITIAL_UNREVIEWED` 通常来自初始空闲范围尚未配置，而不是 ping 失败；初始未核对地址不会进入候选探测。配置 `XD_TWO_IP_REVIEWED_FREE` 后，仅没有关联资源的初始记录才转成 FREE。若是 `CREATED_UNVERIFIED` 或 `LEGACY_RECORD`，完整清单核对一致后可转为 USED；若是冲突或探测占用，不能把它当作初始记录再次放行。每次定时核对日志记录 `inventoryComplete` 以及池内 `status/reason` 计数，便于定位是哪类原因。

如果待核查记录无法通过后续资源清单确认（例如已存在但不可见的旧虚拟机、未知占用、曾出现地址冲突），由管理员通过现场实际资源信息核对后再处理；不要批量清空关联字段或按时间/ping 无响应释放。定时任务不自动删除厂商虚拟机，也不会因超时把待核查 IP 变为空闲。

## 场景虚拟机电源生命周期

- `MultiCloudService` 统一查询、开机、关机、冷重启；场景启动/停止/重启按节点 vendor 调用对应适配器，本地 vendor 0/null 仍使用原 cpcloud 流程。既有资源管理页面独立的连接配置和接口不变。
- 三家厂商均只使用仓库中已有资源管理 API；操作受理后等待确认目标状态。冷重启只有在确认关机后才提交开机，不释放厂商二地址，不删除或重建虚拟机。
- 厂商二运行状态按已有 `status=running` 及电源字段判断。仅明确的停止状态才可继续冷重启，不将未知/过渡状态当作关机。现场仍需核对关机响应状态字段及分页格式；未引入猜测的 task_id 查询或原生重启接口。
- 回收先验证 UUID 和厂商状态。已不存在的资源通过完整清单再次确认，不发送多余关机/删除；HTTP/业务失败且资源仍存在时，不标记回收成功。UUID 缺失时，只允许由场景和节点关联的唯一地址台账 UUID 或完整清单匹配的唯一已记录创建名称恢复资源关联；无可靠关联的历史数据仍需核对，不向厂商发送空 UUID 请求。
- 厂商平台确认 VM 运行且地址绑定完成，不等于客户机已完成操作系统、驱动和网络初始化；不以 ping 响应作为创建成功或失败的唯一依据。

## 验证

```bash
bash gradlew :mp-base:test --tests 'cn.cyberpeace.mp.service.Vendor*Test' \
  :mp-scene-plugin-cloud:test \
  --tests 'cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandlerTest' \
  --tests 'cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.*Test' \
  :mp:compileJava
```

测试使用 H2 事务数据库和模拟厂商响应/可达性结果，不访问现场厂商网络。现场数据库升级、真实资源列表完整性和运行环境的辅助检查能力需在部署时验证。
