package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.*;

/** Durable identity reservation. Retired VMIDs remain as tombstones to avoid stale scene references. */
@Service
public class VendorThirdVmLedger {
    static final String CREATING = "CREATING", PENDING = "VERIFY_PENDING", READY = "READY",
            DELETING = "DELETING", DELETED = "DELETED", REJECTED = "REJECTED";
    private static final String TABLE = "mp_vendor_third_vm_ledger";
    private final JdbcTemplate jdbc;
    private final TransactionTemplate store;

    public VendorThirdVmLedger(JdbcTemplate jdbc, PlatformTransactionManager tx) {
        this.jdbc = jdbc;
        store = new TransactionTemplate(tx);
        store.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Getter
    @AllArgsConstructor
    static final class Lease {
        private final int vmid;
        private final String owner, sceneId, elementId, portId, templateId, requestName, node,
                status, stage, taskId;
        private final boolean observed;
        private final String ip, mac, netmask, gateway;
        private final Timestamp updatedAt;
    }

    static String ownerKey(String sceneId, String elementId) {
        return sceneId == null || elementId == null ? null : sceneId + ":" + elementId;
    }

    private int write(String sql, Object... args) {
        return Objects.requireNonNull(store.execute(tx -> jdbc.update(sql, args)));
    }

    private List<Lease> query(String condition, Object... args) {
        return Objects.requireNonNull(store.execute(tx -> jdbc.query("SELECT * FROM " + TABLE + condition,
                (rs, n) -> new Lease(rs.getInt("vmid"), rs.getString("owner_key"), rs.getString("scene_id"),
                        rs.getString("element_id"), rs.getString("port_id"), rs.getString("template_id"),
                        rs.getString("request_name"), rs.getString("node_name"), rs.getString("status"),
                        rs.getString("stage"), rs.getString("task_id"), rs.getInt("observed") == 1,
                        rs.getString("ip"), rs.getString("mac_addr"), rs.getString("netmask"),
                        rs.getString("gateway"), rs.getTimestamp("updated_at")), args)));
    }

    List<Lease> list() { return query(""); }

    Lease byId(int vmid) { return query(" WHERE vmid=?", vmid).stream().findFirst().orElse(null); }

    Lease byOwner(String owner) {
        return owner == null ? null : query(" WHERE owner_key=?", owner).stream().findFirst().orElse(null);
    }

    List<Lease> forSceneElement(String sceneId, String elementId) {
        return query(" WHERE scene_id=? AND element_id=? ORDER BY vmid DESC", sceneId, elementId);
    }

    Lease reserve(String owner, String sceneId, String elementId, String portId, String templateId,
                  String name, String node, Set<Integer> occupied) {
        int max = Math.max(1000, occupied.stream().mapToInt(Integer::intValue).max().orElse(0));
        // Avoid reusing historical MP references, including those predating this migration.
        List<String> historical = Objects.requireNonNull(store.execute(tx -> jdbc.queryForList(
                "SELECT c.resource_id FROM mp_sceneplugincloud_scene_element c "
                        + "JOIN mp_scene_scene_element e ON c.scene=e.scene AND c.scene_element=e.node_id WHERE e.vendor=3",
                String.class)));
        for (String id : historical) {
            try { max = Math.max(max, Integer.parseInt(id)); } catch (NumberFormatException ignored) { }
        }
        for (int retry = 0; retry < 100; retry++) {
            if (byOwner(owner) != null) throw new MpRuntimeException("厂商三节点已有VMID预占或资源关联");
            Integer storedMax = store.execute(tx -> jdbc.queryForObject("SELECT MAX(vmid) FROM " + TABLE, Integer.class));
            int previous = storedMax == null ? max : Math.max(max, storedMax);
            if (previous >= Integer.MAX_VALUE - 1) throw new MpRuntimeException("厂商三VMID编号范围已耗尽");
            int vmid = previous + 1;
            try {
                write("INSERT INTO " + TABLE + " (vmid,owner_key,scene_id,element_id,port_id,template_id,request_name,node_name,status,stage,observed,updated_at)"
                                + " VALUES (?,?,?,?,?,?,?,?,?,?,0,CURRENT_TIMESTAMP)",
                        vmid, owner, sceneId, elementId, portId, templateId, name, node, CREATING, "ALLOCATED");
                return byId(vmid);
            } catch (DuplicateKeyException e) {
                // A competing MP instance may have reserved the same VMID.
                if (byOwner(owner) != null) throw new MpRuntimeException("厂商三节点已有并发创建请求");
                max = vmid;
            }
        }
        throw new MpRuntimeException("厂商三VMID并发预占未成功");
    }

    boolean claimResume(Lease lease) {
        return write("UPDATE " + TABLE + " SET status=?,updated_at=CURRENT_TIMESTAMP WHERE vmid=? AND request_name=? AND status=?",
                CREATING, lease.vmid, lease.requestName, PENDING) == 1;
    }

    void advance(Lease lease, String stage, String taskId) {
        if (write("UPDATE " + TABLE + " SET stage=?,task_id=COALESCE(?,task_id),updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=?",
                stage, taskId, lease.vmid, lease.requestName, CREATING) != 1) {
            throw new MpRuntimeException("厂商三资源状态已变化，停止后续创建操作");
        }
    }

    void observed(Lease lease, String node) {
        write("UPDATE " + TABLE + " SET observed=1,node_name=?,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status IN (?,?,?,?)",
                node, lease.vmid, lease.requestName, CREATING, PENDING, READY, DELETING);
    }

    void pending(Lease lease, String stage) {
        write("UPDATE " + TABLE + " SET status=?,stage=?,updated_at=CURRENT_TIMESTAMP WHERE vmid=? AND request_name=? AND status=?",
                PENDING, stage, lease.vmid, lease.requestName, CREATING);
    }

    boolean rejected(Lease lease, String stage) {
        return write("UPDATE " + TABLE + " SET status=?,stage=?,owner_key=NULL,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND observed=0",
                REJECTED, stage, lease.vmid, lease.requestName, CREATING) == 1;
    }

    boolean staleCreating(Lease lease, long cutoffMillis) {
        if (!CREATING.equals(lease.status) || lease.updatedAt == null || lease.updatedAt.getTime() >= cutoffMillis) return false;
        return write("UPDATE " + TABLE + " SET status=?,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND updated_at=?",
                PENDING, lease.vmid, lease.requestName, CREATING, lease.updatedAt) == 1;
    }

    boolean retireUnsubmitted(Lease lease, long cutoffMillis) {
        if (!CREATING.equals(lease.status) || !"ALLOCATED".equals(lease.stage)
                || lease.updatedAt == null || lease.updatedAt.getTime() >= cutoffMillis) return false;
        return write("UPDATE " + TABLE + " SET status=?,stage=?,owner_key=NULL,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND stage=? AND observed=0 AND updated_at=?",
                REJECTED, "NO_CLONE_SUBMITTED", lease.vmid, lease.requestName, CREATING, "ALLOCATED", lease.updatedAt) == 1;
    }

    boolean ready(Lease lease, String ip, String mac, String netmask, String gateway) {
        return markReady(lease, ip, mac, netmask, gateway, CREATING);
    }

    boolean lateReady(Lease lease, String ip, String mac, String netmask, String gateway) {
        return markReady(lease, ip, mac, netmask, gateway, PENDING);
    }

    private boolean markReady(Lease lease, String ip, String mac, String netmask, String gateway, String expectedStatus) {
        return write("UPDATE " + TABLE + " SET status=?,stage=?,ip=?,mac_addr=?,netmask=?,gateway=?,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=?",
                READY, "READY", ip, mac, netmask, gateway, lease.vmid, lease.requestName, expectedStatus) == 1;
    }

    Lease beginDelete(Lease lease) {
        write("UPDATE " + TABLE + " SET status=?,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status IN (?,?,?)",
                DELETING, lease.vmid, lease.requestName, CREATING, PENDING, READY);
        Lease current = byId(lease.vmid);
        if (current == null || !lease.requestName.equals(current.requestName) || !DELETING.equals(current.status)) {
            throw new MpRuntimeException("厂商三资源回收关联状态已变化");
        }
        return current;
    }

    /** Re-open only the original, positively observed identity during closed-scene reclamation. */
    Lease prepareOrphanReclaim(Lease lease, long cutoffMillis) {
        if (CREATING.equals(lease.status)) {
            throw new MpRuntimeException("厂商三仍有创建任务关联，暂不执行兜底回收");
        }
        if (DELETED.equals(lease.status) || REJECTED.equals(lease.status) || DELETING.equals(lease.status)) {
            if (DELETING.equals(lease.status) && (lease.updatedAt == null || lease.updatedAt.getTime() >= cutoffMillis)) {
                throw new MpRuntimeException("厂商三回收仍在确认窗口内");
            }
            if (write("UPDATE " + TABLE + " SET status=?,stage=?,observed=1,updated_at=CURRENT_TIMESTAMP "
                            + "WHERE vmid=? AND request_name=? AND status=? AND updated_at=?",
                    DELETING, "ORPHAN_RECONFIRMED", lease.vmid, lease.requestName, lease.status, lease.updatedAt) != 1) {
                throw new MpRuntimeException("厂商三资源回收状态已变化");
            }
            return byId(lease.vmid);
        }
        return lease;
    }

    boolean claimDeleteAction(Lease lease, String action, Set<String> blockedStages) {
        Lease current = byId(lease.vmid);
        if (current == null || !lease.requestName.equals(current.requestName)
                || !DELETING.equals(current.status) || blockedStages.contains(current.stage)) return false;
        return write("UPDATE " + TABLE + " SET stage=?,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND stage=?",
                action, lease.vmid, lease.requestName, DELETING, current.stage) == 1;
    }

    void deleteStage(Lease lease, String priorStage, String stage, String taskId) {
        write("UPDATE " + TABLE + " SET stage=?,task_id=COALESCE(?,task_id),updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND stage=?",
                stage, taskId, lease.vmid, lease.requestName, DELETING, priorStage);
    }

    boolean deleted(Lease lease) {
        return write("UPDATE " + TABLE + " SET status=?,stage=?,owner_key=NULL,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE vmid=? AND request_name=? AND status=? AND (observed=1 OR stage=?)",
                DELETED, "DELETE_CONFIRMED", lease.vmid, lease.requestName, DELETING, "ALLOCATED") == 1;
    }

    /** Persist late, positively verified provider data only into the original active scene/node. */
    void syncRuntimePort(Lease lease, String ip, String mac, String netmask, String gateway) {
        syncRuntimePort(lease, ip, mac, netmask, gateway, false);
    }

    void syncRuntimePort(Lease lease, String ip, String mac, String netmask, String gateway, boolean onlyMissing) {
        if (lease.sceneId == null || lease.elementId == null || lease.portId == null) return;
        String id = String.valueOf(lease.vmid);
        write("UPDATE mp_sceneplugincloud_scene_element SET resource_id=?,update_time=CURRENT_TIMESTAMP "
                        + "WHERE scene=? AND (resource_id='' OR resource_id=?) AND resource_id<>? AND scene_element IN "
                        + "(SELECT e.node_id FROM mp_scene_scene_element e JOIN mp_scene_scene s ON s.id=e.scene "
                        + "WHERE e.id=? AND e.scene=? AND e.vendor=3 AND e.event<>9 AND s.deleted=0) "
                        + "AND EXISTS (SELECT 1 FROM " + TABLE + " a WHERE a.vmid=? AND a.request_name=? AND a.status=?)",
                id, lease.sceneId, id, id, lease.elementId, lease.sceneId, lease.vmid, lease.requestName, READY);
        write("UPDATE mp_scene_scene_element_port SET ip=?,mac_addr=?,netmask=?,gateway=?,update_time=CURRENT_TIMESTAMP "
                        + "WHERE id=? AND scene=? AND scene_element=? AND EXISTS "
                        + "(SELECT 1 FROM mp_scene_scene_element e JOIN mp_scene_scene s ON s.id=e.scene "
                        + "JOIN mp_sceneplugincloud_scene_element c ON c.scene=e.scene AND c.scene_element=e.node_id "
                        + "WHERE e.id=? AND e.scene=? AND e.vendor=3 AND e.event<>9 AND s.deleted=0 AND c.resource_id=?) "
                        + "AND EXISTS (SELECT 1 FROM " + TABLE + " a WHERE a.vmid=? AND a.request_name=? AND a.status=?)"
                        + (onlyMissing ? " AND (ip='' OR mac_addr='')" : ""),
                ip, mac, netmask, gateway, lease.portId, lease.sceneId, lease.elementId,
                lease.elementId, lease.sceneId, id, lease.vmid, lease.requestName, READY);
    }
}
