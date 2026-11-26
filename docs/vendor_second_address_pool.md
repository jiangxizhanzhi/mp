# 厂商二独占地址池管理

## 行为

- 默认授权池：192.168.140.126～192.168.140.225（100 个地址），掩码 255.255.255.0，网关 192.168.140.254。只管理厂商二，不修改其他云平台。
- 优先使用池内已确认空闲的指定 IP，否则按 IP 数值升序选择其他已确认空闲地址。数据库主键和唯一 owner_key 加上原子条件更新保证同一共享数据库中的并发分配不会重复；地址预占事务独立提交，即使外层拓扑事务失败也保留资源关联。
- 对已预占的合法候选地址进行有界 Java 可达性检查（`InetAddress.isReachable`；运行环境可能使用 ICMP 或 TCP 可达性判断）。有响应则隔离并换候选；无响应**不代表地址空闲**。失败或探测不可用不改变已核对的台账结论。不会对地址池外的节点执行检测。
- 通过已有虚拟机列表回读地址和资源 UUID。MP 不将请求 IP 当作已确认的实际 IP；未确认时保留创建返回的 UUID 和台账，运行端口暂不显示旧模板 IP。每五分钟核对未确认资源；运行端口更新要求场景、节点、端口和厂商 UUID 均匹配。
- 厂商明确返回 IP 冲突、没有任务或资源 ID，且完整列表未发现此次创建名称时才换地址重试；请求超时或结果模糊不盲目重发。同一节点的未确认记录阻止重复创建。
- 关机不释放地址。删除后只有在曾观察到该资源、完整有效的资源列表确认它不存在时才确认释放；若地址仍有占用响应则隔离。对于从未观察到的异步创建资源，不能仅凭列表没有它就释放 IP。
- 虚拟机列表完整性按已有返回字段 `total_size`、`page_num` 与实际条数进行核对。不把分页/失败的列表当作删除证据，也不认为该清单涵盖非厂商平台资源或未经授权可见的资源。

## 首次上线（存量地址核对）

1. 应用新增的数据库迁移 `V202611260900__vendor_second_address_pool.sql`（MariaDB/MySQL 和 DM 均有对应版本）。如果部署环境关闭了 Flyway，请通过该环境现有数据库升级流程应用本次迁移。不要清空现有台账作为重新初始化手段。
2. 所有 MP 实例必须使用同一个地址台账数据库，并停用旧版仍然随机分配的 MP 实例。先维持 `XD_TWO_IP_REVIEWED_FREE` 为空。定时任务或首次请求会建立地址记录，并导入可识别的存量资源/地址证据。
3. 管理员结合厂商管理端资源记录、存量 MP 实例、地址管理记录，核实池内可分配地址。关机、无 ping 响应以及列表未返回的未核实 IP 不应被直接认定为空闲。
4. 配置 `XD_TWO_IP_REVIEWED_FREE`，内容为已经确认空闲的单个地址或闭区间，用逗号分隔。仅初始未核查且没有资源关联的对应记录变为 `FREE`。例如（**示例格式，不能作为现场可用地址证明**）：`192.168.140.126,192.168.140.130-192.168.140.135`。重启或再次核对不会将已分配或被隔离的记录重置为空闲。
5. 用下述只读查询检查结果，再逐步恢复厂商二资源创建。若没有任何已核对的空闲记录，创建会报告地址资源不足，不回退到旧的随机 IP 或固定 IP。

配置项：

| 环境变量 | 默认值 | 用途 |
|---|---|---|
| `XD_TWO_IP_POOL_START` | `192.168.140.126` | 池起始地址 |
| `XD_TWO_IP_POOL_END` | `192.168.140.225` | 池结束地址 |
| `XD_TWO_IP_NETMASK` | `255.255.255.0` | 子网掩码 |
| `XD_TWO_IP_GATEWAY` | `192.168.140.254` | 网关 |
| `XD_TWO_IP_REVIEWED_FREE` | 空 | 首次上线明确核对为空闲的地址/范围 |
| `XD_TWO_IP_PROBE_ENABLED` | `true` | 是否启用候选地址辅助可达性检查 |
| `XD_TWO_IP_PROBE_TIMEOUT_MS` | `1000` | 单候选检查超时，合法范围 1～10000 毫秒 |

不要在仍有租约时改变地址池、掩码、网关或厂商账号的管理范围；如果必须更改，需要先核对旧租约并确认新池的使用权。地址台账记录在池变更后仍保留，不自动覆盖或清除。

## 地址状态及只读检查

```sql
SELECT status, COUNT(*) AS count
FROM mp_vendor_second_address_pool GROUP BY status;

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

如果待核查记录无法通过后续资源清单确认（例如已存在但不可见的旧虚拟机、未知占用、曾出现地址冲突），由管理员通过现场实际资源信息核对后再处理；不要批量清空关联字段或按时间/ping 无响应释放。定时任务不自动删除厂商虚拟机，也不会因超时把待核查 IP 变为空闲。

## 验证

```bash
bash gradlew :mp-base:test --tests 'cn.cyberpeace.mp.service.VendorSecond*Test' \
  :mp-scene-plugin-cloud:test \
  --tests 'cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandlerTest' \
  --tests 'cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandlerTest' \
  :mp:compileJava
```

测试使用 H2 事务数据库和模拟厂商响应/可达性结果，不访问现场厂商网络。现场数据库升级、真实资源列表完整性和运行环境的辅助检查能力需在部署时验证。
