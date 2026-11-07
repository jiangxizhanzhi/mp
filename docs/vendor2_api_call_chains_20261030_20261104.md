# 厂商二镜像查询与虚拟机创建调用链记录

本报告覆盖“保存的镜像 UUID 在 images 查询中未命中”和“镜像命中后调用 servers，但虚拟机未正常可用”两个问题。按日志文件、追踪号和线程分别关联请求，避免把同一场景并发调用的厂商一、厂商三或本机 cpcloud 请求误接到厂商二链路。

结论：镜像未命中异常由 MP 在遍历厂商二返回列表后本地抛出；两次创建请求均收到 code=0、task_id 和虚拟机 UUID，其中第一次返回的 UUID 随后连续 9 次查询控制台均为 HTTP 404，业务消息为“找不到该云主机”。现有日志能够证明创建任务已受理和后续控制台查询失败，不能证明虚拟机曾成功开机，也不能单凭这些记录判定厂商二的异步创建失败原因。

## 1. 数据来源、完整性与阅读方式

| 标记 | 本地来源 | 日志时间范围（保留原日志时间，时区未标注） | 字节数 / 行数 |
| --- | --- | --- | --- |
| A | `/root/mp_project/mp/mp.log` | 2026-10-30 00:00:00.000 — 2026-10-30 21:38:03.116 | 48,236,301 / 149,513 |
| B | `/root/mp_project/diagnostics/controller01_20261104/latest_mp/mp.log` | 2026-11-04 00:00:00.000 — 2026-11-04 19:53:50.839 | 37,698,601 / 115,419 |

- 日志 A SHA-256：`0a1363feaa61459d21ec0a16d24b6a7f8955f925408b9a24a52276bed9c0fbe0`。

- 日志 B SHA-256：`6fbb5dc56479a3a8f5828ebf20f830c746d1653e26ec119c3371a6565a538f06`。

- 数据库依据：2026-11-04 导出的 all_databases.sql，以及对应 sql_tables/*.jsonl；下文保存的 image_uuid 明确标为数据库快照值，不能当作 10 月 30 日方法实参的直接日志。

- 源码基线：提交 1b81f7ef5e0fd55c3795970dfac63bd576fc0a2d；源码调用方式与日志逐项核对，但当前仓库已包含后加的响应日志改动，不代表历史运行包已具备该日志能力。

- 全量清单共 63 次对厂商二的请求：日志 A 为 58 次，日志 B 为 5 次。每次均独立列出 URL、方法、请求头、请求体、已记录的 HTTP 状态和厂商业务响应；重复请求也逐条保留。

- 所有请求头名称、非认证字段值、请求体和已有业务响应均完整列出，没有用省略号截断。Authorization 中访问键、Signature 及 Cookie 的认证值以带同值指纹的标识脱敏；签名算法、Credential 作用域、SignedHeaders 和追踪号原样保留。

- 请求头来自日志中的 request.getAllHeaders()，不是网络抓包；客户端底层可能自动补充的 Host、Content-Length 等字段没有记录，不补造。所有 HTTP 响应头均未记录。

- 两份日志均没有 Http GET/POST/DELETE response 日志。18 次 images 查询没有响应正文；日志 A 两次 servers 创建正文由业务层记录，13 次控制台失败正文由 CsceneInstanceService 记录，关机/删除正文由厂商适配层记录。业务层 JSON 解析后再打印的内容不等同于原始 HTTP 报文。

- 文中“未记录”表示现有日志不能恢复该信息，不表示响应为空，不代表 HTTP 200，也不以源码模板或本机 cpcloud 的响应代替厂商响应。

## 2. 问题一：保存的镜像 UUID 未在 images 查询中命中

### 2.1 实际调用顺序

调用链为 CreateHandler.createVirtualMachine → cloudService.createVm(cloudElement.getImageUuid()) → VendorSecondCloudServiceImpl.getTargetImage(imageUuid) → GET https://192.168.140.15/janus/20180725/images。请求无查询参数、无请求体。getTargetImage 遍历返回 JSON 的 data.data，按 image.id.equals(imageUuid) 查找；遍历结束仍未匹配时，由 MP 抛出“未找到指定镜像”，本次创建不会继续调用 POST /servers。

12 次失败包括日志 A 的 11 个节点和日志 B 的 1 个节点，分别如下；每行“调用详情”可跳转到完整请求及对应异常原文。

| 调用详情 | 请求时间 | 追踪号 | 节点 / 日志中的 element ID | 数据库快照保存的 image_uuid | MP 异常行 |
| --- | --- | --- | --- | --- | --- |
| [A-78030](#call-a-78030) | 2026-10-30 14:42:43.429 | `17933425684528727865` | Centos7 redis / `ccaefffdb5bc464ca91d0c510810003a` | `a8b3785f-08b4-4da5-bc5e-b7ded3c72ede` | A:L78059 |
| [A-78031](#call-a-78031) | 2026-10-30 14:42:43.429 | `17933425684528727865` | Centos7 redis / `d6635e9843304df3a0e91d3689403f58` | `a8b3785f-08b4-4da5-bc5e-b7ded3c72ede` | A:L78097 |
| [A-101894](#call-a-101894) | 2026-10-30 15:50:12.338 | `17933466155174260506` | 360沙箱_win10 / `4888c550894e4fbeb2cc575b9446d197` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L101954 |
| [A-101895](#call-a-101895) | 2026-10-30 15:50:12.338 | `17933466155174260506` | 360沙箱_win10 / `65154af705fc44c183d4d369358903f1` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L101916 |
| [A-101896](#call-a-101896) | 2026-10-30 15:50:12.338 | `17933466155174260506` | 360沙箱_win10 / `6b12835b1b854699aea56a8beb789bed` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L101992 |
| [A-136895](#call-a-136895) | 2026-10-30 19:47:07.027 | `17933608319932091799` | 360沙箱_win10 / `400c730f614e4729b2f03e44276b6090` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L136917 |
| [A-136896](#call-a-136896) | 2026-10-30 19:47:07.027 | `17933608319932091799` | 360沙箱_win10 / `5b653708399b4610a509acdfb64d3a57` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L136993 |
| [A-136897](#call-a-136897) | 2026-10-30 19:47:07.027 | `17933608319932091799` | 360沙箱_win10 / `2565da066aed44dfacfbacb4c0a98aa7` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L136955 |
| [A-144335](#call-a-144335) | 2026-10-30 20:52:28.065 | `17933647529539207515` | 360沙箱_win10 / `3d92bc8b00c640caac51e5dee27bb410` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L144402 |
| [A-144336](#call-a-144336) | 2026-10-30 20:52:28.066 | `17933647529539207515` | 360沙箱_win10 / `dc9d9433fc8e4a24a3a6fdcf5b4c3fe5` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L144364 |
| [A-144337](#call-a-144337) | 2026-10-30 20:52:28.066 | `17933647529539207515` | 360沙箱_win10 / `cb83fa18d58c4f9887f095b8f5d1c33d` | `014c7b7d-a10e-461a-b6c6-a586299a5417` | A:L144440 |
| [B-84933](#call-b-84933) | 2026-11-04 15:10:19.576 | `17937762237629175628` | 蜜罐诱捕ubuntu / `2417aabe46bf4d4bb1b0d655b34156f9` | `60e41054-14a7-42ed-ab5b-4d0cff2ff7a4` | B:L84953 |

数据库关联关系为 mp_scene_scene_element.id = 日志 element ID，再通过 (scene, node_id) 关联 mp_sceneplugincloud_scene_element.(scene, scene_element)，读取后者 image_uuid。两个表的主键并不直接相等。

### 2.2 能确认和不能确认的事项

“未找到指定镜像”是 MP 本地异常，不是厂商二返回的原始错误 JSON。当前方法只请求一次 images 列表，没有按镜像 ID 查询详情，也没有遍历后续页。日志没有厂商返回的列表、分页信息、租户或资源可见范围，因此尚不能区分旧 UUID 已删除/重建、查询范围不同、默认分页遗漏、厂商业务错误或其他原因。数据库中的 image 字段出现 d8806f6a-7685-4c3a-8274-a9c0a36d3d04快照，它与真正用于厂商二匹配的 image_uuid 是不同字段。

## 3. 问题二：镜像命中后创建任务已受理，但虚拟机未正常可用

### 3.1 两次创建请求与结果

| 链路 | 节点 / element ID | images 请求 | servers 请求 → 业务响应 | 厂商 task_id | 厂商返回虚拟机 UUID |
| --- | --- | --- | --- | --- | --- |
| 2026-10-30 14:44:40.864 | windows2003 / `82031b99a00c4149890ad4fcd7688ed4` | [A-80274](#call-a-80274) | [A-80280](#call-a-80280) → A:L80285 | `ba746e08-d42e-11f1-a270-fefcfe5dad37` | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` |
| 2026-10-30 19:41:52.879 | windows2003 / `1e2749fd54d94e40b1b25ceb8b1e5539` | [A-135286](#call-a-135286) | [A-135296](#call-a-135296) → A:L135300 | `3f665891-d458-11f1-a270-fefcfe5dad37` | `f72a1059-2fbd-4b25-b52c-f5f6a4ab5915` |

两次 images 成功命中是根据同线程后续确实发出 POST /servers 推断的；images 返回列表本身没有记录。两次 POST 请求体中的 image_id 都是 1accc765-3d04-4eca-9f98-24558716395d，完整 JSON 在对应调用详情中逐字段列出。请求包含 power_on=1、cores=4、sockets=2、memory_mb=8192、derive_disk 大小 102400 MB；网络 IP 分别是 192.168.140.145 和 192.168.140.168。

### 3.2 第一次创建返回 UUID 后的控制台失败

第一次创建在 2026-10-30 14:44:41.783 返回虚拟机 UUID 96933f7a-dd0f-43a9-8f1c-5d09f98890ec。随后 14:46:47.297—15:47:35.864 共 9 次向同一厂商地址请求该 UUID 的 remote-consoles，每次均为 HTTP 404，返回业务 JSON code=-1，消息为“找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！”。这些 UUID 与创建响应完全一致，控制台查询不是误用镜像 UUID。

| 调用详情 | 请求时间 | 虚拟机 UUID | HTTP 状态 | 业务响应原文行 |
| --- | --- | --- | --- | --- |
| [A-81467](#call-a-81467) | 2026-10-30 14:46:47.297 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L81469 |
| [A-81831](#call-a-81831) | 2026-10-30 14:46:58.604 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L81833 |
| [A-82187](#call-a-82187) | 2026-10-30 14:47:07.998 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L82190 |
| [A-85837](#call-a-85837) | 2026-10-30 14:58:48.884 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L85848 |
| [A-86175](#call-a-86175) | 2026-10-30 14:58:56.072 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L86177 |
| [A-86566](#call-a-86566) | 2026-10-30 14:59:04.588 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L86569 |
| [A-86889](#call-a-86889) | 2026-10-30 14:59:09.337 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L86891 |
| [A-88136](#call-a-88136) | 2026-10-30 15:02:02.359 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L88138 |
| [A-100604](#call-a-100604) | 2026-10-30 15:47:35.864 | `96933f7a-dd0f-43a9-8f1c-5d09f98890ec` | 404 Not found | A:L100606 |

同日 14:43 另有 4 次对旧 UUID 6dd49b5e-8d35-4593-ab49-c5d284b26c58 的控制台请求也返回 404，发生在本次创建之前，已在全量清单独立保留。第二次创建返回 f72a1059-2fbd-4b25-b52c-f5f6a4ab5915 后，日志 A 没有对这个 UUID 的厂商二控制台请求、厂商二状态查询或对应 task_id 的任务查询，不能给它套用第一次的控制台失败结果。

### 3.3 实现中需要与厂商核对的行为

当前 createVm 直接取创建响应 data.uuids[0] 作为资源 ID，没有等待或查询 data.task_id，也没有确认虚拟机进入运行状态。厂商二 ipAutoGen() 返回 false，此分支也不会调用 getVmIpAndMac() 查询 /servers。故 MP 日志中的“创建虚拟机 结束”仅说明已返回 UUID，不足以证明开机完成。

两次创建复用了源码模板中的 device_id=c7fbd868-e1f6-480c-851d-7b387cee13ea 和 port_id=b2bf40b4-2637-4849-b2f2-e3b37236d16b；代码还把镜像返回磁盘的 size_mb 乘以 10。它们属于需对照厂商 API 契约和异步任务错误核验的具体参数，现有日志不能证明它们就是本次失败根因。

### 3.4 清理请求的独立异常

日志 A 还包含 11 次空 UUID 关机请求 /servers//stop，均为 HTTP 401，业务消息为“EC2 授权无效。错误码：0x020170010002”；随后 11 次空 UUID 删除请求 /servers/ 均为 HTTP 404，业务消息为“The resource could not be found.”。空 UUID、签名认证失败、镜像查找失败和异步创建后控制台不可见是不同阶段，不能用关机的 401 直接解释 images 未命中或 POST /servers 的 code=0。对有值旧 UUID 的两次清理也逐条列在下文。

## 4. 完整请求清单

编号是“日志标记-请求原始行号”。请求体空表示日志记录为空请求体。未记录 HTTP 状态的请求不补写状态码。

| 调用详情 | 时间 | 方法 / 路径 | 已记录 HTTP 状态 | 已记录业务结果 |
| --- | --- | --- | --- | --- |
| [A-77843](#call-a-77843) | 2026-10-30 14:42:32.967 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-77844](#call-a-77844) | 2026-10-30 14:42:32.967 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-78030](#call-a-78030) | 2026-10-30 14:42:43.429 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-78031](#call-a-78031) | 2026-10-30 14:42:43.429 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-78452](#call-a-78452) | 2026-10-30 14:43:17.104 | POST `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles` | 404 | code=-1；找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！ |
| [A-78773](#call-a-78773) | 2026-10-30 14:43:21.305 | POST `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles` | 404 | code=-1；找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！ |
| [A-79090](#call-a-79090) | 2026-10-30 14:43:23.976 | POST `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles` | 404 | code=-1；找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！ |
| [A-79413](#call-a-79413) | 2026-10-30 14:43:30.103 | POST `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles` | 404 | code=-1；找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！ |
| [A-79960](#call-a-79960) | 2026-10-30 14:44:03.057 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-79961](#call-a-79961) | 2026-10-30 14:44:03.057 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-80128](#call-a-80128) | 2026-10-30 14:44:30.451 | POST `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/stop` | 404 | code=-1；云主机不存在，请确认云主机是否已被删除或联系技术支持！ |
| [A-80274](#call-a-80274) | 2026-10-30 14:44:40.757 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-80280](#call-a-80280) | 2026-10-30 14:44:40.864 | POST `/janus/20180725/servers` | 未记录 | code=0； |
| [A-81137](#call-a-81137) | 2026-10-30 14:46:00.606 | DELETE `/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58` | 未记录 | code=0； |
| [A-81467](#call-a-81467) | 2026-10-30 14:46:47.297 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-81831](#call-a-81831) | 2026-10-30 14:46:58.604 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-82187](#call-a-82187) | 2026-10-30 14:47:07.998 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-85837](#call-a-85837) | 2026-10-30 14:58:48.884 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-86175](#call-a-86175) | 2026-10-30 14:58:56.072 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-86566](#call-a-86566) | 2026-10-30 14:59:04.588 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-86889](#call-a-86889) | 2026-10-30 14:59:09.337 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-88136](#call-a-88136) | 2026-10-30 15:02:02.359 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-100604](#call-a-100604) | 2026-10-30 15:47:35.864 | POST `/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles` | 404 | code=-1；找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！ |
| [A-101675](#call-a-101675) | 2026-10-30 15:50:00.785 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-101676](#call-a-101676) | 2026-10-30 15:50:00.785 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-101677](#call-a-101677) | 2026-10-30 15:50:00.785 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-101894](#call-a-101894) | 2026-10-30 15:50:12.338 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-101895](#call-a-101895) | 2026-10-30 15:50:12.338 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-101896](#call-a-101896) | 2026-10-30 15:50:12.338 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-102234](#call-a-102234) | 2026-10-30 15:51:15.399 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-102263](#call-a-102263) | 2026-10-30 15:51:30.832 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-102264](#call-a-102264) | 2026-10-30 15:51:30.843 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-102265](#call-a-102265) | 2026-10-30 15:51:30.860 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-105352](#call-a-105352) | 2026-10-30 16:07:43.369 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-105367](#call-a-105367) | 2026-10-30 16:07:53.818 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-105530](#call-a-105530) | 2026-10-30 16:08:05.633 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-120092](#call-a-120092) | 2026-10-30 17:34:13.072 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-120093](#call-a-120093) | 2026-10-30 17:34:13.072 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-120151](#call-a-120151) | 2026-10-30 17:34:17.107 | POST `/janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692/stop` | 404 | code=-1；云主机不存在，请确认云主机是否已被删除或联系技术支持！ |
| [A-120261](#call-a-120261) | 2026-10-30 17:34:29.398 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-120612](#call-a-120612) | 2026-10-30 17:35:43.147 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-120613](#call-a-120613) | 2026-10-30 17:35:43.147 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-120665](#call-a-120665) | 2026-10-30 17:35:48.295 | DELETE `/janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692` | 未记录 | code=0； |
| [A-120731](#call-a-120731) | 2026-10-30 17:35:59.421 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-135286](#call-a-135286) | 2026-10-30 19:41:51.826 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-135296](#call-a-135296) | 2026-10-30 19:41:52.879 | POST `/janus/20180725/servers` | 未记录 | code=0； |
| [A-136895](#call-a-136895) | 2026-10-30 19:47:07.027 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-136896](#call-a-136896) | 2026-10-30 19:47:07.027 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-136897](#call-a-136897) | 2026-10-30 19:47:07.027 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-144201](#call-a-144201) | 2026-10-30 20:52:16.799 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-144202](#call-a-144202) | 2026-10-30 20:52:16.799 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-144203](#call-a-144203) | 2026-10-30 20:52:16.799 | POST `/janus/20180725/servers//stop` | 401 | code=401；EC2 授权无效。错误码：0x020170010002 |
| [A-144335](#call-a-144335) | 2026-10-30 20:52:28.065 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-144336](#call-a-144336) | 2026-10-30 20:52:28.066 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-144337](#call-a-144337) | 2026-10-30 20:52:28.066 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [A-144670](#call-a-144670) | 2026-10-30 20:53:46.925 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-144671](#call-a-144671) | 2026-10-30 20:53:46.940 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [A-144672](#call-a-144672) | 2026-10-30 20:53:46.953 | DELETE `/janus/20180725/servers/` | 404 | code=-1；The resource could not be found. |
| [B-84933](#call-b-84933) | 2026-11-04 15:10:19.576 | GET `/janus/20180725/images` | 未记录 | 未记录响应正文 |
| [B-92572](#call-b-92572) | 2026-11-04 16:19:29.610 | GET `/janus/20180725/hosts` | 未记录 | 未记录响应正文 |
| [B-92573](#call-b-92573) | 2026-11-04 16:19:29.610 | GET `/janus/20180725/hosts` | 未记录 | 未记录响应正文 |
| [B-92578](#call-b-92578) | 2026-11-04 16:19:30.433 | GET `/janus/20180725/hosts` | 未记录 | 未记录响应正文 |
| [B-92587](#call-b-92587) | 2026-11-04 16:19:30.917 | GET `/janus/20180725/servers` | 未记录 | 未记录响应正文 |

## 5A. 日志 A 的逐次请求、请求头、请求体与响应

<a id="call-a-77843"></a>

### A-77843 · 2026-10-30 14:42:32.967 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 77843 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4`。

- 追踪号：`17933425668373016834`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425668373016834
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 77893 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L77893: 2026-10-30 14:42:33.056 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933425668373016834 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L77892: 2026-10-30 14:42:33.056 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933425668373016834 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933425668373016834, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-77844"></a>

### A-77844 · 2026-10-30 14:42:32.967 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 77844 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6`。

- 追踪号：`17933425668373016834`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425668373016834
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 77894 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L77894: 2026-10-30 14:42:33.056 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933425668373016834 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L77891: 2026-10-30 14:42:33.056 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933425668373016834 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933425668373016834, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-78030"></a>

### A-78030 · 2026-10-30 14:42:43.429 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 78030 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4`。

- 追踪号：`17933425684528727865`。

- 场景节点：Centos7 redis；element ID=`ccaefffdb5bc464ca91d0c510810003a`。

- 数据库快照：scene=`1df5403077a34bc6925844227f4cacc6`；插件行 ID=`7cb3f2a84bea4551be97babcc6bcea02`；image_uuid=`a8b3785f-08b4-4da5-bc5e-b7ded3c72ede`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425684528727865
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L78027: 2026-10-30 14:42:43.423 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933425684528727865 | 网元 Centos7 redis  创建虚拟机 开始 process[,ccaefffdb5bc464ca91d0c510810003a,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L78041: 2026-10-30 14:42:44.347 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933425684528727865 | failed to run element progress
L78042:
L78043: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L78044: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L78045: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L78046: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L78047: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L78048: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L78049: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L78050: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L78051: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L78052: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L78053: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L78054: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L78055: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L78056: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L78057: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L78059: 2026-10-30 14:42:44.347 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933425684528727865 | element ccaefffdb5bc464ca91d0c510810003a real error info 未找到指定镜像
```

```text
L78060: 2026-10-30 14:42:44.347 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933425684528727865 | 网元 Centos7 redis  执行 创建虚拟机 出错: 未找到指定镜像 process[,ccaefffdb5bc464ca91d0c510810003a,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-78031"></a>

### A-78031 · 2026-10-30 14:42:43.429 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 78031 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2`。

- 追踪号：`17933425684528727865`。

- 场景节点：Centos7 redis；element ID=`d6635e9843304df3a0e91d3689403f58`。

- 数据库快照：scene=`1df5403077a34bc6925844227f4cacc6`；插件行 ID=`c6b2e718611d4091b3bb4a25744f305c`；image_uuid=`a8b3785f-08b4-4da5-bc5e-b7ded3c72ede`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425684528727865
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L78026: 2026-10-30 14:42:43.423 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933425684528727865 | 网元 Centos7 redis  创建虚拟机 开始 process[,d6635e9843304df3a0e91d3689403f58,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L78079: 2026-10-30 14:42:44.357 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933425684528727865 | failed to run element progress
L78080:
L78081: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L78082: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L78083: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L78084: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L78085: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L78086: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L78087: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L78088: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L78089: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L78090: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L78091: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L78092: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L78093: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L78094: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L78095: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L78097: 2026-10-30 14:42:44.357 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933425684528727865 | element d6635e9843304df3a0e91d3689403f58 real error info 未找到指定镜像
```

```text
L78098: 2026-10-30 14:42:44.357 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933425684528727865 | 网元 Centos7 redis  执行 创建虚拟机 出错: 未找到指定镜像 process[,d6635e9843304df3a0e91d3689403f58,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-78452"></a>

### A-78452 · 2026-10-30 14:43:17.104 · POST /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles

- 请求原文位置：日志 A 第 78452 行。

- 线程：`14-http-nio-8080-exec-36`。

- 追踪号：`17933426088594415477`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426088594415477
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 78454 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L78454: 2026-10-30 14:43:17.461 [14-http-nio-8080-exec-36] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933426088594415477 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L78453: 2026-10-30 14:43:17.459 [14-http-nio-8080-exec-36] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933426088594415477 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933426088594415477, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-78773"></a>

### A-78773 · 2026-10-30 14:43:21.305 · POST /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles

- 请求原文位置：日志 A 第 78773 行。

- 线程：`14-http-nio-8080-exec-61`。

- 追踪号：`17933426130633321410`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426130633321410
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 78775 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L78775: 2026-10-30 14:43:21.444 [14-http-nio-8080-exec-61] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933426130633321410 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L78774: 2026-10-30 14:43:21.443 [14-http-nio-8080-exec-61] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933426130633321410 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933426130633321410, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-79090"></a>

### A-79090 · 2026-10-30 14:43:23.976 · POST /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles

- 请求原文位置：日志 A 第 79090 行。

- 线程：`14-http-nio-8080-exec-62`。

- 追踪号：`17933426157177994817`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426157177994817
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 79094 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L79094: 2026-10-30 14:43:24.105 [14-http-nio-8080-exec-62] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933426157177994817 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L79093: 2026-10-30 14:43:24.104 [14-http-nio-8080-exec-62] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933426157177994817 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933426157177994817, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-79413"></a>

### A-79413 · 2026-10-30 14:43:30.103 · POST /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles

- 请求原文位置：日志 A 第 79413 行。

- 线程：`14-http-nio-8080-exec-55`。

- 追踪号：`17933426218631597796`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426218631597796
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 79415 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L79415: 2026-10-30 14:43:30.229 [14-http-nio-8080-exec-55] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933426218631597796 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（6dd49b5e-8d35-4593-ab49-c5d284b26c58）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L79414: 2026-10-30 14:43:30.228 [14-http-nio-8080-exec-55] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933426218631597796 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_6683bdf7>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933426218631597796, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-79960"></a>

### A-79960 · 2026-10-30 14:44:03.057 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 79960 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4`。

- 追踪号：`17933425668373016834`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425668373016834
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 79965 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L79965: 2026-10-30 14:44:03.133 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933425668373016834 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L79964: 2026-10-30 14:44:03.133 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933425668373016834 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933425668373016834, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-79961"></a>

### A-79961 · 2026-10-30 14:44:03.057 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 79961 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6`。

- 追踪号：`17933425668373016834`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933425668373016834
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 79963 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L79963: 2026-10-30 14:44:03.132 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933425668373016834 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L79962: 2026-10-30 14:44:03.132 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933425668373016834 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933425668373016834, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-80128"></a>

### A-80128 · 2026-10-30 14:44:30.451 · POST /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/stop

- 请求原文位置：日志 A 第 80128 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-7`。

- 追踪号：`17933426821177087766`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_0dc13652>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426821177087766
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 80135 行记录的 JSON，以下完整列出：

```json
{
  "message": "云主机不存在，请确认云主机是否已被删除或联系技术支持！",
  "code": -1,
  "data": null
}
```

对应业务日志原文：

```text
L80135: 2026-10-30 14:44:30.606 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-7]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933426821177087766 | close vm result = {"message":"\u4e91\u4e3b\u673a\u4e0d\u5b58\u5728\uff0c\u8bf7\u786e\u8ba4\u4e91\u4e3b\u673a\u662f\u5426\u5df2\u88ab\u5220\u9664\u6216\u8054\u7cfb\u6280\u672f\u652f\u6301\uff01","code":-1,"data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L80134: 2026-10-30 14:44:30.605 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-7] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933426821177087766 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58/stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_0dc13652>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933426821177087766, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not found
```

<a id="call-a-80274"></a>

### A-80274 · 2026-10-30 14:44:40.757 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 80274 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9`。

- 追踪号：`17933426841599171087`。

- 场景节点：windows2003；element ID=`82031b99a00c4149890ad4fcd7688ed4`。

- 数据库快照：scene=`2ab75483218746d8af574f205e195746`；插件行 ID=`4cbef2287f1b42d0b5e42fe56e43e215`；image_uuid=`1accc765-3d04-4eca-9f98-24558716395d`；resource_id=`96933f7a-dd0f-43a9-8f1c-5d09f98890ec`；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426841599171087
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L80268: 2026-10-30 14:44:40.714 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933426841599171087 | 网元 windows2003  创建虚拟机 开始 process[,82031b99a00c4149890ad4fcd7688ed4,scene.event.create,2,create_virtual_machine,2]
```

<a id="call-a-80280"></a>

### A-80280 · 2026-10-30 14:44:40.864 · POST /janus/20180725/servers

- 请求原文位置：日志 A 第 80280 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9`。

- 追踪号：`17933426841599171087`。

- 场景节点：windows2003；element ID=`82031b99a00c4149890ad4fcd7688ed4`。

- 数据库快照：scene=`2ab75483218746d8af574f205e195746`；插件行 ID=`4cbef2287f1b42d0b5e42fe56e43e215`；image_uuid=`1accc765-3d04-4eca-9f98-24558716395d`；resource_id=`96933f7a-dd0f-43a9-8f1c-5d09f98890ec`；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_65be420b>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426841599171087
Accept-Language: zh
```

**请求体**

```json
{
  "cores": 4,
  "disks": [
    {
      "size_mb": 102400,
      "id": "ide0",
      "preallocate": 0,
      "type": "derive_disk"
    }
  ],
  "power_on": 1,
  "storage_id": "62c115ff_vs_vol_rep3",
  "name": "MP_970b67a1038546de8748408ca04a7386",
  "memory_mb": 8192,
  "sockets": 2,
  "image_id": "1accc765-3d04-4eca-9f98-24558716395d",
  "networks": [
    {
      "host_tso": 0,
      "vif_id": "net0",
      "device_id": "c7fbd868-e1f6-480c-851d-7b387cee13ea",
      "port_id": "b2bf40b4-2637-4849-b2f2-e3b37236d16b",
      "model": "virtio",
      "connect": 1,
      "ip_info": {
        "netmask": "255.255.255.0",
        "ip_address": "192.168.140.145",
        "gateway": "192.168.140.254"
      }
    }
  ],
  "az_id": "a5fc5061-2ce2-4fb5-8981-b27c4c3a1e4a"
}
```

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 80285 行记录的 JSON，以下完整列出：

```json
{
  "code": 0,
  "data": {
    "task_id": "ba746e08-d42e-11f1-a270-fefcfe5dad37",
    "uuids": [
      "96933f7a-dd0f-43a9-8f1c-5d09f98890ec"
    ]
  },
  "message": ""
}
```

对应业务日志原文：

```text
L80285: 2026-10-30 14:44:41.783 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.VendorSecondCloudServiceImpl     185 - 17933426841599171087 | 厂商二：创建虚拟机结果: {"code":0,"data":{"task_id":"ba746e08-d42e-11f1-a270-fefcfe5dad37","uuids":["96933f7a-dd0f-43a9-8f1c-5d09f98890ec"]},"message":""}
```

同线程、同追踪号的后续处理与异常原文：

```text
L80286: 2026-10-30 14:44:41.784 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933426841599171087 | 网元 windows2003  创建虚拟机 结束 process[,82031b99a00c4149890ad4fcd7688ed4,scene.event.create,2,create_virtual_machine,4]
```

<a id="call-a-81137"></a>

### A-81137 · 2026-10-30 14:46:00.606 · DELETE /janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58

- 请求原文位置：日志 A 第 81137 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-7`。

- 追踪号：`17933426821177087766`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/6dd49b5e-8d35-4593-ab49-c5d284b26c58
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_c82579cf>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933426821177087766
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 81162 行记录的 JSON，以下完整列出：

```json
{
  "message": "",
  "code": 0,
  "data": {
    "task_id": "e9f1e3b8-d42e-11f1-a270-fefcfe5dad37"
  }
}
```

对应业务日志原文：

```text
L81162: 2026-10-30 14:46:00.799 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-7]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933426821177087766 | delete vm result = {"message":"","code":0,"data":{"task_id":"e9f1e3b8-d42e-11f1-a270-fefcfe5dad37"}}
```

<a id="call-a-81467"></a>

### A-81467 · 2026-10-30 14:46:47.297 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 81467 行。

- 线程：`14-http-nio-8080-exec-68`。

- 追踪号：`17933428190449018955`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933428190449018955
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 81469 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L81469: 2026-10-30 14:46:47.422 [14-http-nio-8080-exec-68] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933428190449018955 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L81468: 2026-10-30 14:46:47.421 [14-http-nio-8080-exec-68] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933428190449018955 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933428190449018955, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-81831"></a>

### A-81831 · 2026-10-30 14:46:58.604 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 81831 行。

- 线程：`14-http-nio-8080-exec-36`。

- 追踪号：`17933428303193959097`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933428303193959097
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 81833 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L81833: 2026-10-30 14:46:58.737 [14-http-nio-8080-exec-36] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933428303193959097 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L81832: 2026-10-30 14:46:58.736 [14-http-nio-8080-exec-36] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933428303193959097 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933428303193959097, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-82187"></a>

### A-82187 · 2026-10-30 14:47:07.998 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 82187 行。

- 线程：`14-http-nio-8080-exec-61`。

- 追踪号：`17933428397522981775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933428397522981775
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 82190 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L82190: 2026-10-30 14:47:08.117 [14-http-nio-8080-exec-61] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933428397522981775 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L82189: 2026-10-30 14:47:08.116 [14-http-nio-8080-exec-61] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933428397522981775 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933428397522981775, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-85837"></a>

### A-85837 · 2026-10-30 14:58:48.884 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 85837 行。

- 线程：`14-http-nio-8080-exec-62`。

- 追踪号：`17933435406232515691`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933435406232515691
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 85848 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L85848: 2026-10-30 14:58:49.223 [14-http-nio-8080-exec-62] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933435406232515691 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L85847: 2026-10-30 14:58:49.222 [14-http-nio-8080-exec-62] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933435406232515691 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933435406232515691, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-86175"></a>

### A-86175 · 2026-10-30 14:58:56.072 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 86175 行。

- 线程：`14-http-nio-8080-exec-61`。

- 追踪号：`17933435478177149771`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933435478177149771
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 86177 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L86177: 2026-10-30 14:58:56.195 [14-http-nio-8080-exec-61] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933435478177149771 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L86176: 2026-10-30 14:58:56.194 [14-http-nio-8080-exec-61] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933435478177149771 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933435478177149771, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-86566"></a>

### A-86566 · 2026-10-30 14:59:04.588 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 86566 行。

- 线程：`14-http-nio-8080-exec-67`。

- 追踪号：`17933435563353459579`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933435563353459579
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 86569 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L86569: 2026-10-30 14:59:04.710 [14-http-nio-8080-exec-67] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933435563353459579 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L86568: 2026-10-30 14:59:04.709 [14-http-nio-8080-exec-67] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933435563353459579 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933435563353459579, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-86889"></a>

### A-86889 · 2026-10-30 14:59:09.337 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 86889 行。

- 线程：`14-http-nio-8080-exec-51`。

- 追踪号：`1793343561085867029`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 1793343561085867029
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 86891 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L86891: 2026-10-30 14:59:09.456 [14-http-nio-8080-exec-51] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 1793343561085867029 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L86890: 2026-10-30 14:59:09.456 [14-http-nio-8080-exec-51] ERROR c.c.mp.base.util.http.HttpClient         172 - 1793343561085867029 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 1793343561085867029, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-88136"></a>

### A-88136 · 2026-10-30 15:02:02.359 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 88136 行。

- 线程：`14-http-nio-8080-exec-36`。

- 追踪号：`17933437341057614378`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933437341057614378
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 88138 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L88138: 2026-10-30 15:02:02.557 [14-http-nio-8080-exec-36] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933437341057614378 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L88137: 2026-10-30 15:02:02.556 [14-http-nio-8080-exec-36] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933437341057614378 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933437341057614378, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-100604"></a>

### A-100604 · 2026-10-30 15:47:35.864 · POST /janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles

- 请求原文位置：日志 A 第 100604 行。

- 线程：`14-http-nio-8080-exec-36`。

- 追踪号：`17933464675624380157`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933464675624380157
Accept-Language: zh
```

**请求体**

```json
{
  "remote_console": {
    "protocol": "vnc",
    "type": "novnc"
  }
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 100606 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"
}
```

对应业务日志原文：

```text
L100606: 2026-10-30 15:47:36.359 [14-http-nio-8080-exec-36] ERROR c.c.m.c.service.CsceneInstanceService    1255 - 17933464675624380157 | xd two get remote connection error. jsonObject:{"code":-1,"message":"找不到（96933f7a-dd0f-43a9-8f1c-5d09f98890ec）的云主机，请确认后重试！"}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L100605: 2026-10-30 15:47:36.357 [14-http-nio-8080-exec-36] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933464675624380157 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/96933f7a-dd0f-43a9-8f1c-5d09f98890ec/remote-consoles, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_2a47a754>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933464675624380157, Accept-Language: zh], body: {"remote_console":{"protocol":"vnc","type":"novnc"}}, http code: 404, error: Not found
```

<a id="call-a-101675"></a>

### A-101675 · 2026-10-30 15:50:00.785 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 101675 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 101683 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L101683: 2026-10-30 15:50:00.840 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933466123652320016 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L101682: 2026-10-30 15:50:00.840 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-101676"></a>

### A-101676 · 2026-10-30 15:50:00.785 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 101676 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 101681 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L101681: 2026-10-30 15:50:00.831 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933466123652320016 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L101680: 2026-10-30 15:50:00.831 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-101677"></a>

### A-101677 · 2026-10-30 15:50:00.785 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 101677 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 101686 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L101686: 2026-10-30 15:50:00.859 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933466123652320016 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L101685: 2026-10-30 15:50:00.859 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-101894"></a>

### A-101894 · 2026-10-30 15:50:12.338 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 101894 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-7`。

- 追踪号：`17933466155174260506`。

- 场景节点：360沙箱_win10；element ID=`4888c550894e4fbeb2cc575b9446d197`。

- 数据库快照：scene=`7cbeb1d7d9344d0bbee65b4da7d34365`；插件行 ID=`db43a8f2a28247d39a87a43e2ecaa93f`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466155174260506
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L101892: 2026-10-30 15:50:12.336 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-7]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  创建虚拟机 开始 process[,4888c550894e4fbeb2cc575b9446d197,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L101936: 2026-10-30 15:50:12.477 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-7] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933466155174260506 | failed to run element progress
L101937:
L101938: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L101939: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L101940: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L101941: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L101942: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L101943: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L101944: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L101945: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L101946: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L101947: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L101948: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L101949: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L101950: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L101951: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L101952: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L101954: 2026-10-30 15:50:12.477 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-7] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933466155174260506 | element 4888c550894e4fbeb2cc575b9446d197 real error info 未找到指定镜像
```

```text
L101955: 2026-10-30 15:50:12.477 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-7]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,4888c550894e4fbeb2cc575b9446d197,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-101895"></a>

### A-101895 · 2026-10-30 15:50:12.338 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 101895 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-10`。

- 追踪号：`17933466155174260506`。

- 场景节点：360沙箱_win10；element ID=`65154af705fc44c183d4d369358903f1`。

- 数据库快照：scene=`7cbeb1d7d9344d0bbee65b4da7d34365`；插件行 ID=`ceb79c901e814554a0a13382aa82c740`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466155174260506
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L101893: 2026-10-30 15:50:12.336 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-10]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  创建虚拟机 开始 process[,65154af705fc44c183d4d369358903f1,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L101898: 2026-10-30 15:50:12.465 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-10] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933466155174260506 | failed to run element progress
L101899:
L101900: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L101901: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L101902: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L101903: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L101904: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L101905: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L101906: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L101907: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L101908: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L101909: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L101910: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L101911: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L101912: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L101913: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L101914: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L101916: 2026-10-30 15:50:12.465 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-10] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933466155174260506 | element 65154af705fc44c183d4d369358903f1 real error info 未找到指定镜像
```

```text
L101917: 2026-10-30 15:50:12.466 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-10]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,65154af705fc44c183d4d369358903f1,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-101896"></a>

### A-101896 · 2026-10-30 15:50:12.338 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 101896 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-3`。

- 追踪号：`17933466155174260506`。

- 场景节点：360沙箱_win10；element ID=`6b12835b1b854699aea56a8beb789bed`。

- 数据库快照：scene=`7cbeb1d7d9344d0bbee65b4da7d34365`；插件行 ID=`b0518c79611c4432bf45f656e228f612`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466155174260506
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L101891: 2026-10-30 15:50:12.336 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-3]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  创建虚拟机 开始 process[,6b12835b1b854699aea56a8beb789bed,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L101974: 2026-10-30 15:50:12.556 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-3] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933466155174260506 | failed to run element progress
L101975:
L101976: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L101977: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L101978: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L101979: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L101980: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L101981: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L101982: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L101983: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L101984: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L101985: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L101986: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L101987: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L101988: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L101989: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L101990: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L101992: 2026-10-30 15:50:12.557 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-3] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933466155174260506 | element 6b12835b1b854699aea56a8beb789bed real error info 未找到指定镜像
```

```text
L101993: 2026-10-30 15:50:12.557 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-3]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933466155174260506 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,6b12835b1b854699aea56a8beb789bed,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-102234"></a>

### A-102234 · 2026-10-30 15:51:15.399 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 102234 行。

- 线程：`14-http-nio-8080-exec-73`。

- 追踪号：`17933466871381400140`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466871381400140
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-a-102263"></a>

### A-102263 · 2026-10-30 15:51:30.832 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 102263 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 102267 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L102267: 2026-10-30 15:51:30.898 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933466123652320016 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L102266: 2026-10-30 15:51:30.898 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-4] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-102264"></a>

### A-102264 · 2026-10-30 15:51:30.843 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 102264 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 102270 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L102270: 2026-10-30 15:51:30.904 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933466123652320016 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L102269: 2026-10-30 15:51:30.904 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-102265"></a>

### A-102265 · 2026-10-30 15:51:30.860 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 102265 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10`。

- 追踪号：`17933466123652320016`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933466123652320016
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 102273 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L102273: 2026-10-30 15:51:30.940 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933466123652320016 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L102272: 2026-10-30 15:51:30.940 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933466123652320016 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933466123652320016, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-105352"></a>

### A-105352 · 2026-10-30 16:07:43.369 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 105352 行。

- 线程：`14-http-nio-8080-exec-74`。

- 追踪号：`17933476750913375796`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933476750913375796
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-a-105367"></a>

### A-105367 · 2026-10-30 16:07:53.818 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 105367 行。

- 线程：`14-http-nio-8080-exec-74`。

- 追踪号：`17933476855412943047`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933476855412943047
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-a-105530"></a>

### A-105530 · 2026-10-30 16:08:05.633 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 105530 行。

- 线程：`14-http-nio-8080-exec-80`。

- 追踪号：`1793347697355588896`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 1793347697355588896
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-a-120092"></a>

### A-120092 · 2026-10-30 17:34:13.072 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 120092 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3`。

- 追踪号：`17933528668508758758`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528668508758758
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120102 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L120102: 2026-10-30 17:34:13.146 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933528668508758758 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120099: 2026-10-30 17:34:13.145 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528668508758758 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528668508758758, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-120093"></a>

### A-120093 · 2026-10-30 17:34:13.072 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 120093 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6`。

- 追踪号：`17933528668508758758`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528668508758758
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120101 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L120101: 2026-10-30 17:34:13.146 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933528668508758758 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120100: 2026-10-30 17:34:13.146 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528668508758758 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528668508758758, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-120151"></a>

### A-120151 · 2026-10-30 17:34:17.107 · POST /janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692/stop

- 请求原文位置：日志 A 第 120151 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9`。

- 追踪号：`17933528708697210565`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692/stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_b5f297a2>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528708697210565
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120207 行记录的 JSON，以下完整列出：

```json
{
  "message": "云主机不存在，请确认云主机是否已被删除或联系技术支持！",
  "code": -1,
  "data": null
}
```

对应业务日志原文：

```text
L120207: 2026-10-30 17:34:18.294 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933528708697210565 | close vm result = {"message":"\u4e91\u4e3b\u673a\u4e0d\u5b58\u5728\uff0c\u8bf7\u786e\u8ba4\u4e91\u4e3b\u673a\u662f\u5426\u5df2\u88ab\u5220\u9664\u6216\u8054\u7cfb\u6280\u672f\u652f\u6301\uff01","code":-1,"data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120206: 2026-10-30 17:34:18.294 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528708697210565 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692/stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_b5f297a2>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528708697210565, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not found
```

<a id="call-a-120261"></a>

### A-120261 · 2026-10-30 17:34:29.398 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 120261 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2`。

- 追踪号：`17933528831657798539`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528831657798539
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120265 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L120265: 2026-10-30 17:34:29.420 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933528831657798539 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120264: 2026-10-30 17:34:29.420 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528831657798539 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528831657798539, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-120612"></a>

### A-120612 · 2026-10-30 17:35:43.147 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 120612 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3`。

- 追踪号：`17933528668508758758`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528668508758758
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120615 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L120615: 2026-10-30 17:35:43.258 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933528668508758758 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120614: 2026-10-30 17:35:43.257 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-3] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528668508758758 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528668508758758, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-120613"></a>

### A-120613 · 2026-10-30 17:35:43.147 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 120613 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6`。

- 追踪号：`17933528668508758758`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528668508758758
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120617 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L120617: 2026-10-30 17:35:43.259 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933528668508758758 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120616: 2026-10-30 17:35:43.258 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-6] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528668508758758 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528668508758758, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-120665"></a>

### A-120665 · 2026-10-30 17:35:48.295 · DELETE /janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692

- 请求原文位置：日志 A 第 120665 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9`。

- 追踪号：`17933528708697210565`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/df5f8ad2-9d6d-4359-94f5-ab6e45cc4692
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_fde82bd7>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528708697210565
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120668 行记录的 JSON，以下完整列出：

```json
{
  "message": "",
  "code": 0,
  "data": {
    "task_id": "a26b1177-d446-11f1-a270-fefcfe5dad37"
  }
}
```

对应业务日志原文：

```text
L120668: 2026-10-30 17:35:48.542 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-9]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933528708697210565 | delete vm result = {"message":"","code":0,"data":{"task_id":"a26b1177-d446-11f1-a270-fefcfe5dad37"}}
```

<a id="call-a-120731"></a>

### A-120731 · 2026-10-30 17:35:59.421 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 120731 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2`。

- 追踪号：`17933528831657798539`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933528831657798539
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 120733 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L120733: 2026-10-30 17:35:59.466 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933528831657798539 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L120732: 2026-10-30 17:35:59.466 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-2] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933528831657798539 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933528831657798539, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-135286"></a>

### A-135286 · 2026-10-30 19:41:51.826 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 135286 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2`。

- 追踪号：`17933605159786482326`。

- 场景节点：windows2003；element ID=`1e2749fd54d94e40b1b25ceb8b1e5539`。

- 数据库快照：scene=`010cb2a3d9284832b8ac4e48ba3072ea`；插件行 ID=`8c4549c675ab4840a9af0752d2bc76d2`；image_uuid=`1accc765-3d04-4eca-9f98-24558716395d`；resource_id=`f72a1059-2fbd-4b25-b52c-f5f6a4ab5915`；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933605159786482326
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L135282: 2026-10-30 19:41:51.816 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933605159786482326 | 网元 windows2003  创建虚拟机 开始 process[,1e2749fd54d94e40b1b25ceb8b1e5539,scene.event.create,2,create_virtual_machine,2]
```

<a id="call-a-135296"></a>

### A-135296 · 2026-10-30 19:41:52.879 · POST /janus/20180725/servers

- 请求原文位置：日志 A 第 135296 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2`。

- 追踪号：`17933605159786482326`。

- 场景节点：windows2003；element ID=`1e2749fd54d94e40b1b25ceb8b1e5539`。

- 数据库快照：scene=`010cb2a3d9284832b8ac4e48ba3072ea`；插件行 ID=`8c4549c675ab4840a9af0752d2bc76d2`；image_uuid=`1accc765-3d04-4eca-9f98-24558716395d`；resource_id=`f72a1059-2fbd-4b25-b52c-f5f6a4ab5915`；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_37b18e32>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933605159786482326
Accept-Language: zh
```

**请求体**

```json
{
  "cores": 4,
  "disks": [
    {
      "size_mb": 102400,
      "id": "ide0",
      "preallocate": 0,
      "type": "derive_disk"
    }
  ],
  "power_on": 1,
  "storage_id": "62c115ff_vs_vol_rep3",
  "name": "MP_bbfe9c5991a14ea88d551bbccefe2fae",
  "memory_mb": 8192,
  "sockets": 2,
  "image_id": "1accc765-3d04-4eca-9f98-24558716395d",
  "networks": [
    {
      "host_tso": 0,
      "vif_id": "net0",
      "device_id": "c7fbd868-e1f6-480c-851d-7b387cee13ea",
      "port_id": "b2bf40b4-2637-4849-b2f2-e3b37236d16b",
      "model": "virtio",
      "connect": 1,
      "ip_info": {
        "netmask": "255.255.255.0",
        "ip_address": "192.168.140.168",
        "gateway": "192.168.140.254"
      }
    }
  ],
  "az_id": "a5fc5061-2ce2-4fb5-8981-b27c4c3a1e4a"
}
```

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 135300 行记录的 JSON，以下完整列出：

```json
{
  "code": 0,
  "data": {
    "task_id": "3f665891-d458-11f1-a270-fefcfe5dad37",
    "uuids": [
      "f72a1059-2fbd-4b25-b52c-f5f6a4ab5915"
    ]
  },
  "message": ""
}
```

对应业务日志原文：

```text
L135300: 2026-10-30 19:41:54.608 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.VendorSecondCloudServiceImpl     185 - 17933605159786482326 | 厂商二：创建虚拟机结果: {"code":0,"data":{"task_id":"3f665891-d458-11f1-a270-fefcfe5dad37","uuids":["f72a1059-2fbd-4b25-b52c-f5f6a4ab5915"]},"message":""}
```

同线程、同追踪号的后续处理与异常原文：

```text
L135301: 2026-10-30 19:41:54.609 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933605159786482326 | 网元 windows2003  创建虚拟机 结束 process[,1e2749fd54d94e40b1b25ceb8b1e5539,scene.event.create,2,create_virtual_machine,4]
```

<a id="call-a-136895"></a>

### A-136895 · 2026-10-30 19:47:07.027 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 136895 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4`。

- 追踪号：`17933608319932091799`。

- 场景节点：360沙箱_win10；element ID=`400c730f614e4729b2f03e44276b6090`。

- 数据库快照：scene=`db8a90271fb64bc4a155c81ea58a5a4a`；插件行 ID=`63b8a81229a74ad3b87a436eb4dd9f6d`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933608319932091799
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L136894: 2026-10-30 19:47:07.025 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  创建虚拟机 开始 process[,400c730f614e4729b2f03e44276b6090,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L136899: 2026-10-30 19:47:07.237 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933608319932091799 | failed to run element progress
L136900:
L136901: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L136902: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L136903: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L136904: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L136905: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L136906: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L136907: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L136908: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L136909: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L136910: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L136911: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L136912: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L136913: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L136914: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L136915: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L136917: 2026-10-30 19:47:07.237 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933608319932091799 | element 400c730f614e4729b2f03e44276b6090 real error info 未找到指定镜像
```

```text
L136918: 2026-10-30 19:47:07.237 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,400c730f614e4729b2f03e44276b6090,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-136896"></a>

### A-136896 · 2026-10-30 19:47:07.027 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 136896 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2`。

- 追踪号：`17933608319932091799`。

- 场景节点：360沙箱_win10；element ID=`5b653708399b4610a509acdfb64d3a57`。

- 数据库快照：scene=`db8a90271fb64bc4a155c81ea58a5a4a`；插件行 ID=`a708c311683b48268fb24fdae9841af7`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933608319932091799
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L136892: 2026-10-30 19:47:07.025 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  创建虚拟机 开始 process[,5b653708399b4610a509acdfb64d3a57,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L136975: 2026-10-30 19:47:07.323 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933608319932091799 | failed to run element progress
L136976:
L136977: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L136978: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L136979: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L136980: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L136981: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L136982: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L136983: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L136984: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L136985: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L136986: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L136987: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L136988: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L136989: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L136990: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L136991: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L136993: 2026-10-30 19:47:07.323 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933608319932091799 | element 5b653708399b4610a509acdfb64d3a57 real error info 未找到指定镜像
```

```text
L136994: 2026-10-30 19:47:07.323 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-2]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,5b653708399b4610a509acdfb64d3a57,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-136897"></a>

### A-136897 · 2026-10-30 19:47:07.027 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 136897 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9`。

- 追踪号：`17933608319932091799`。

- 场景节点：360沙箱_win10；element ID=`2565da066aed44dfacfbacb4c0a98aa7`。

- 数据库快照：scene=`db8a90271fb64bc4a155c81ea58a5a4a`；插件行 ID=`475b2697b5d44ffeb646aece22e5d105`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933608319932091799
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L136891: 2026-10-30 19:47:07.025 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  创建虚拟机 开始 process[,2565da066aed44dfacfbacb4c0a98aa7,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L136937: 2026-10-30 19:47:07.241 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933608319932091799 | failed to run element progress
L136938:
L136939: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L136940: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L136941: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L136942: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L136943: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L136944: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L136945: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L136946: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L136947: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L136948: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L136949: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L136950: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L136951: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L136952: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L136953: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L136955: 2026-10-30 19:47:07.241 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933608319932091799 | element 2565da066aed44dfacfbacb4c0a98aa7 real error info 未找到指定镜像
```

```text
L136956: 2026-10-30 19:47:07.241 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933608319932091799 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,2565da066aed44dfacfbacb4c0a98aa7,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-144201"></a>

### A-144201 · 2026-10-30 20:52:16.799 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 144201 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144208 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L144208: 2026-10-30 20:52:16.933 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933647504861171775 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144207: 2026-10-30 20:52:16.932 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-144202"></a>

### A-144202 · 2026-10-30 20:52:16.799 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 144202 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144210 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L144210: 2026-10-30 20:52:16.952 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933647504861171775 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144209: 2026-10-30 20:52:16.952 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-144203"></a>

### A-144203 · 2026-10-30 20:52:16.799 · POST /janus/20180725/servers//stop

- 请求原文位置：日志 A 第 144203 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
POST https://192.168.140.15/janus/20180725/servers//stop
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：401 Unauthorized。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144206 行记录的 JSON，以下完整列出：

```json
{
  "code": 401,
  "message": "EC2 授权无效。错误码：0x020170010002",
  "data": null
}
```

对应业务日志原文：

```text
L144206: 2026-10-30 20:52:16.923 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1]  INFO c.c.m.s.VendorSecondCloudServiceImpl     268 - 17933647504861171775 | close vm result = {"code":401,"message":"EC2 授权无效。错误码：0x020170010002","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144205: 2026-10-30 20:52:16.923 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http POST status error - url: https://192.168.140.15/janus/20180725/servers//stop, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_ad242bd9>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 401, error: Unauthorized
```

<a id="call-a-144335"></a>

### A-144335 · 2026-10-30 20:52:28.065 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 144335 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-8`。

- 追踪号：`17933647529539207515`。

- 场景节点：360沙箱_win10；element ID=`3d92bc8b00c640caac51e5dee27bb410`。

- 数据库快照：scene=`c0bbaab2f9104fc0945773791a6f5a0a`；插件行 ID=`c562de0a2f384f7a90ad413e7bdf5fe1`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647529539207515
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L144331: 2026-10-30 20:52:28.065 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-8]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  创建虚拟机 开始 process[,3d92bc8b00c640caac51e5dee27bb410,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L144384: 2026-10-30 20:52:28.991 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-8] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933647529539207515 | failed to run element progress
L144385:
L144386: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L144387: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L144388: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L144389: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L144390: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L144391: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L144392: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L144393: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L144394: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L144395: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L144396: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L144397: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L144398: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L144399: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L144400: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L144402: 2026-10-30 20:52:28.992 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-8] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933647529539207515 | element 3d92bc8b00c640caac51e5dee27bb410 real error info 未找到指定镜像
```

```text
L144403: 2026-10-30 20:52:28.992 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-8]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,3d92bc8b00c640caac51e5dee27bb410,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-144336"></a>

### A-144336 · 2026-10-30 20:52:28.066 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 144336 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9`。

- 追踪号：`17933647529539207515`。

- 场景节点：360沙箱_win10；element ID=`dc9d9433fc8e4a24a3a6fdcf5b4c3fe5`。

- 数据库快照：scene=`c0bbaab2f9104fc0945773791a6f5a0a`；插件行 ID=`e055dc9ed9b943c09287b58994127ffc`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647529539207515
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L144333: 2026-10-30 20:52:28.065 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  创建虚拟机 开始 process[,dc9d9433fc8e4a24a3a6fdcf5b4c3fe5,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L144346: 2026-10-30 20:52:28.981 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933647529539207515 | failed to run element progress
L144347:
L144348: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L144349: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L144350: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L144351: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L144352: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L144353: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L144354: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L144355: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L144356: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L144357: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L144358: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L144359: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L144360: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L144361: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L144362: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L144364: 2026-10-30 20:52:28.982 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933647529539207515 | element dc9d9433fc8e4a24a3a6fdcf5b4c3fe5 real error info 未找到指定镜像
```

```text
L144365: 2026-10-30 20:52:28.982 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-9]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,dc9d9433fc8e4a24a3a6fdcf5b4c3fe5,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-144337"></a>

### A-144337 · 2026-10-30 20:52:28.066 · GET /janus/20180725/images

- 请求原文位置：日志 A 第 144337 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4`。

- 追踪号：`17933647529539207515`。

- 场景节点：360沙箱_win10；element ID=`cb83fa18d58c4f9887f095b8f5d1c33d`。

- 数据库快照：scene=`c0bbaab2f9104fc0945773791a6f5a0a`；插件行 ID=`3efeac08d3ea40e180e89ad3c0410ef5`；image_uuid=`014c7b7d-a10e-461a-b6c6-a586299a5417`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647529539207515
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L144332: 2026-10-30 20:52:28.065 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  创建虚拟机 开始 process[,cb83fa18d58c4f9887f095b8f5d1c33d,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L144422: 2026-10-30 20:52:29.068 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17933647529539207515 | failed to run element progress
L144423:
L144424: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L144425: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L144426: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L144427: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L144428: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L144429: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L144430: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L144431: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L144432: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L144433: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L144434: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L144435: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L144436: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L144437: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L144438: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L144440: 2026-10-30 20:52:29.068 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17933647529539207515 | element cb83fa18d58c4f9887f095b8f5d1c33d real error info 未找到指定镜像
```

```text
L144441: 2026-10-30 20:52:29.068 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-4]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17933647529539207515 | 网元 360沙箱_win10  执行 创建虚拟机 出错: 未找到指定镜像 process[,cb83fa18d58c4f9887f095b8f5d1c33d,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-a-144670"></a>

### A-144670 · 2026-10-30 20:53:46.925 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 144670 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144676 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L144676: 2026-10-30 20:53:47.019 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933647504861171775 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144674: 2026-10-30 20:53:47.019 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-1] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-144671"></a>

### A-144671 · 2026-10-30 20:53:46.940 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 144671 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144675 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L144675: 2026-10-30 20:53:47.019 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933647504861171775 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144673: 2026-10-30 20:53:47.018 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-10] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

<a id="call-a-144672"></a>

### A-144672 · 2026-10-30 20:53:46.953 · DELETE /janus/20180725/servers/

- 请求原文位置：日志 A 第 144672 行。

- 线程：`14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5`。

- 追踪号：`17933647504861171775`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
DELETE https://192.168.140.15/janus/20180725/servers/
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17933647504861171775
Accept-Language: zh
```

**请求体**

```json
{
  "force": 1
}
```

**厂商二响应**

- HTTP 状态：404 Not Found。

- HTTP 响应头：未记录。

- 响应正文：业务层日志第 144680 行记录的 JSON，以下完整列出：

```json
{
  "code": -1,
  "message": "The resource could not be found.",
  "data": null
}
```

对应业务日志原文：

```text
L144680: 2026-10-30 20:53:47.118 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5]  INFO c.c.m.s.VendorSecondCloudServiceImpl     256 - 17933647504861171775 | delete vm result = {"code":-1,"message":"The resource could not be found.","data":null}
```

HTTP 错误原文（其中 header/body 仍是请求信息，不是响应头/响应体）：

```text
L144679: 2026-10-30 20:53:47.117 [14-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.deletetaskpool-5] ERROR c.c.mp.base.util.http.HttpClient         172 - 17933647504861171775 | Http DELETE status error - url: https://192.168.140.15/janus/20180725/servers/, header: [Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_1114c744>, Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>, X-Request-Id: 17933647504861171775, Accept-Language: zh], body: {"force":1}, http code: 404, error: Not Found
```

## 5B. 日志 B 的逐次请求、请求头、请求体与响应

<a id="call-b-84933"></a>

### B-84933 · 2026-11-04 15:10:19.576 · GET /janus/20180725/images

- 请求原文位置：日志 B 第 84933 行。

- 线程：`13-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-6`。

- 追踪号：`17937762237629175628`。

- 场景节点：蜜罐诱捕ubuntu；element ID=`2417aabe46bf4d4bb1b0d655b34156f9`。

- 数据库快照：scene=`cf515b81dc584e7ba95ae180e7960d44`；插件行 ID=`cf9bd1ac6a31491091fc91a5e10966a5`；image_uuid=`60e41054-14a7-42ed-ab5b-4d0cff2ff7a4`；resource_id=``；导出 SQL 第 18412 行。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/images
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_618a100e>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17937762237629175628
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

调用起点原文：

```text
L84932: 2026-11-04 15:10:19.566 [13-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-6]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17937762237629175628 | 网元 蜜罐诱捕ubuntu  创建虚拟机 开始 process[,2417aabe46bf4d4bb1b0d655b34156f9,scene.event.create,2,create_virtual_machine,2]
```

同线程、同追踪号的后续处理与异常原文：

```text
L84935: 2026-11-04 15:10:20.116 [13-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-6] ERROR .m.s.c.p.c.PluginSceneEventHandlerCommon 86 - 17937762237629175628 | failed to run element progress
L84936:
L84937: cn.cyberpeace.mp.base.exception.MpRuntimeException: 未找到指定镜像
L84938: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.getTargetImage(VendorSecondCloudServiceImpl.java:207)
L84939: 	at cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl.createVm(VendorSecondCloudServiceImpl.java:141)
L84940: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createVirtualMachine(CreateHandler.java:1040)
L84941: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$createElementProcess$51(CreateHandler.java:1568)
L84942: 	at cn.cyberpeace.mp.scene.core.plugin.common.PluginSceneEventHandlerCommon.runElementProgress(PluginSceneEventHandlerCommon.java:84)
L84943: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.createElementProcess(CreateHandler.java:1567)
L84944: 	at cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.CreateHandler.lambda$create$41(CreateHandler.java:1449)
L84945: 	at cn.cyberpeace.mp.base.util.thread.taskpool.PoolTask.invoke(PoolTask.java:15)
L84946: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.invokeTask(TaskPool.java:56)
L84947: 	at cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool.lambda$invokePoolTasks$0(TaskPool.java:84)
L84948: 	at cn.cyberpeace.mp.base.util.thread.ThreadUtils.lambda$format$0(ThreadUtils.java:17)
L84949: 	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
L84950: 	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
L84951: 	at java.base/java.lang.Thread.run(Thread.java:829)
```

```text
L84953: 2026-11-04 15:10:20.116 [13-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-6] ERROR c.c.m.s.p.l.HandlerCommon                155 - 17937762237629175628 | element 2417aabe46bf4d4bb1b0d655b34156f9 real error info 未找到指定镜像
```

```text
L84954: 2026-11-04 15:10:20.117 [13-cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.createtaskpool-6]  INFO c.c.m.s.c.event.base.BaseEventHandler    212 - 17937762237629175628 | 网元 蜜罐诱捕ubuntu  执行 创建虚拟机 出错: 未找到指定镜像 process[,2417aabe46bf4d4bb1b0d655b34156f9,scene.event.create,5,create_virtual_machine,5]
```

<a id="call-b-92572"></a>

### B-92572 · 2026-11-04 16:19:29.610 · GET /janus/20180725/hosts

- 请求原文位置：日志 B 第 92572 行。

- 线程：`13-http-nio-8080-exec-10`。

- 追踪号：`17937803835894486445`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/hosts
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_60b2c05b>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17937803835894486445
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-b-92573"></a>

### B-92573 · 2026-11-04 16:19:29.610 · GET /janus/20180725/hosts

- 请求原文位置：日志 B 第 92573 行。

- 线程：`13-http-nio-8080-exec-1`。

- 追踪号：`17937803835894619331`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/hosts
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_60b2c05b>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17937803835894619331
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-b-92578"></a>

### B-92578 · 2026-11-04 16:19:30.433 · GET /janus/20180725/hosts

- 请求原文位置：日志 B 第 92578 行。

- 线程：`13-http-nio-8080-exec-26`。

- 追踪号：`1793780384460272964`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/hosts
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_60b2c05b>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 1793780384460272964
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

<a id="call-b-92587"></a>

### B-92587 · 2026-11-04 16:19:30.917 · GET /janus/20180725/servers

- 请求原文位置：日志 B 第 92587 行。

- 线程：`13-http-nio-8080-exec-12`。

- 追踪号：`17937803835899933217`。

**请求与请求头（日志记录字段全量，认证值脱敏）**

```http
GET https://192.168.140.15/janus/20180725/servers
Authorization: AWS4-HMAC-SHA256 Credential=<ACCESS_KEY_f2fe77d9>//cn-south-1/DSP-Client/dmp_request, SignedHeaders=path;, Signature=<SIGNATURE_a50de0a6>
Cookie: aCMPAuthToken=<COOKIE_TOKEN_5068bfe1>
X-Request-Id: 17937803835899933217
Accept-Language: zh
```

**请求体**

无；原始请求日志 body 字段为空。

**厂商二响应**

- HTTP 状态：未记录，不能补推具体状态码。

- HTTP 响应头：未记录。

- 响应正文：未记录，现有文件无法恢复。

## 6. 对应源码位置

- [场景创建入口与 imageUuid 传递](../mp-scene-plugin-cloud/src/main/java/cn/cyberpeace/mp/sceneplugincloud/plugin/lifecyclehandler/CreateHandler.java#L1034)。

- [厂商二创建请求组装与创建响应处理](../mp-base/src/main/java/cn/cyberpeace/mp/service/VendorSecondCloudServiceImpl.java#L136)。

- [厂商二镜像 ID 匹配与本地异常](../mp-base/src/main/java/cn/cyberpeace/mp/service/VendorSecondCloudServiceImpl.java#L192)。

- [厂商二删除、关机和 ipAutoGen](../mp-base/src/main/java/cn/cyberpeace/mp/service/VendorSecondCloudServiceImpl.java#L241)。

- [厂商二控制台请求与业务响应打印](../mp-cscene/src/main/java/cn/cyberpeace/mp/cscene/service/CsceneInstanceService.java#L1240)。

- [厂商二签名与 Header 生成](../mp-base/src/main/java/cn/cyberpeace/mp/base/util/http/XdTwoHttpClient.java#L51)。

- [当前通用 HTTP 请求/响应日志实现](../mp-base/src/main/java/cn/cyberpeace/mp/base/util/http/HttpClient.java#L179)。

响应日志补充提交为 8756992033885f23b03f1c6e56842910b9fdb3bd（2026-11-04）。历史两份文件没有响应记录，不能用当前源码的 setLogResponse(true) 推断这些旧文件已经打印过完整响应。

## 7. 仍缺少的厂商侧原始证据

要完整还原两类故障的厂商内部结果，还需要历史 images 响应的 data.data、分页/总数和业务码；以及创建任务 ba746e08-d42e-11f1-a270-fefcfe5dad37、3f665891-d458-11f1-a270-fefcfe5dad37 的最终状态和错误信息。现有日志没有这些内容，本报告没有重新调用厂商 API，也没有替历史调用编造响应。
