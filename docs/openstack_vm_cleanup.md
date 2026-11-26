# MP OpenStack 虚拟机审计及回收

适用：controller01，具备 Nova、Neutron、MP、cpcloud 数据库只读权限，以及实际需要回收时的 OpenStack 管理员权限。要求 Python 3 和 mysql/MariaDB 客户端；回收模式还要求 OpenStack CLI。不要在新建、重建、回收拓扑的并发操作期间进行人工资源回收。

## 1. 审计虚拟机

```bash
bash audit_openstack_scene_vms.sh -- -u root -p > vm_audit.tsv 2> vm_summary.txt
bash audit_openstack_scene_vms.sh --candidates-only -- -u root -p > vm_candidates.tsv 2> vm_summary.txt
```

`cleanup_status` 的含义：

| 状态 | 说明 |
|---|---|
| ELIGIBLE | Nova 虚拟机仍存在，MP 场景和节点均已确认回收完成，无在用业务引用，无冲突资源归属。仍需管理员指定 UUID 并确认。 |
| REVIEW | 可能残留，如异常回收、MP 命名资源缺失记录、镜像调试标记不存在。需独立确认该实例未被任何业务、镜像调试或其他系统使用。 |
| KEEP | 仍由业务管理、仅停止、正在操作、受保护、外部接入或归属冲突等情形。脚本禁止回收。 |
| UNMANAGED | 无法确认归属。非 MP 不等于可删除，脚本不允许删除。 |
| HYPERVISOR_ONLY | Nova 已无可回收的正常资源记录，需要现场管理员单独确认宿主机状态；脚本不会强制干预虚拟机进程。 |

可选择在 compute01 通过既有只读 libvirt 管理接口获取活动 UUID 列表，设置 `LIBVIRT_ACTIVE_UUIDS` 指向该列表。它只用于审计辅助，并不证明删除后的物理资源已经释放。

离线分析历史 SQL（不能进行回收）：

```bash
bash audit_openstack_scene_vms.sh --dump all_databases.sql.gz --candidates-only
```

## 2. 按指定 UUID 回收

在 controller01 加载本站已有 OpenStack 管理认证环境。先检查 `vm_candidates.tsv`，选择单个或多个完整 Nova UUID。请把命令中的 `<UUID>` 替换为本次现场核对选出的真实 UUID；不要从旧快照复制。

```bash
bash audit_openstack_scene_vms.sh --delete <UUID> --dry-run -- -u root -p
bash audit_openstack_scene_vms.sh --delete <UUID> -- -u root -p
```

可以在 `--delete` 后用空格分隔多个 UUID。实际回收前脚本展示选中对象，要求输入 `DELETE <数量>`，例如 `DELETE 1`。如需处理 `REVIEW`，必须先由管理员核实归属和资源使用状态，再额外加 `--allow-reviewed`。脚本禁止删除 `KEEP`、`UNMANAGED`、`HYPERVISOR_ONLY`，无全量删除选项。

删除前重新读取一致性数据库快照，并校验 OpenStack 返回的 UUID、名称、项目、计算节点和任务状态与审计记录匹配；使用普通 OpenStack 服务端删除接口等待结果，再核查 Nova 记录。不直接修改数据库、不强制删除、不自动重复提交不确定的删除请求。任何异常停止剩余批次，需重新审计。若 `-p` 提示重复输入密码，可使用现场已配置的受保护 MySQL 连接配置文件传给 mysql 客户端；不要在命令行中写明文密码。

脚本不能保证控制面确认后宿主机和所有存储资源立即完成清理，需要用现场现有运维工具进行后续确认。普通删除接口参考 [OpenStack Client server commands](https://docs.openstack.org/python-openstackclient/rocky/cli/command-objects/server.html)。
