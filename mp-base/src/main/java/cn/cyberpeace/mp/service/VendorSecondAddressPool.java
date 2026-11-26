package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/** Persistent, exclusive address ownership. Only confirmed releases become FREE. */
@Service
@Slf4j
public class VendorSecondAddressPool {
    static final String FREE = "FREE";
    static final String RESERVED = "RESERVED";
    static final String USED = "USED";
    static final String RELEASING = "RELEASING";
    static final String PENDING = "VERIFY_PENDING";
    private static final String TABLE = "mp_vendor_second_address_pool";
    private static final String INITIAL = "INITIAL_UNREVIEWED";
    private static final long STALE_MS = 10 * 60 * 1000L;
    private final JdbcTemplate jdbc;
    private final XdAppConfig config;
    private final TransactionTemplate storeTransaction;

    public VendorSecondAddressPool(JdbcTemplate jdbc, XdAppConfig config, PlatformTransactionManager transactions) {
        this.jdbc = jdbc;
        this.config = config;
        this.storeTransaction = new TransactionTemplate(transactions);
        this.storeTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    // The ledger must commit before invoking a remote resource creation/deletion API.
    // It must survive even if an outer scene transaction is rolled back.
    private int write(String sql, Object... args) {
        return Objects.requireNonNull(storeTransaction.execute(tx -> jdbc.update(sql, args)));
    }


    @Getter
    @AllArgsConstructor
    public static final class Lease {
        private final String ip;
        private final String status;
        private final String owner;
        private final String sceneId;
        private final String elementId;
        private final String portId;
        private final String requestName;
        private final String vmUuid;
        private final String macAddress;
        private final String reason;
        private final boolean seenInInventory;
        private final Timestamp updatedAt;
    }

    enum ProbeResult { REACHABLE, NO_REPLY, UNAVAILABLE }

    /** Distinguish exhaustion from duplicate owner, database or configuration errors. */
    static final class Exhausted extends MpRuntimeException {
        Exhausted(String message) { super(message); }
    }

    public List<Lease> list() {
        return Objects.requireNonNull(storeTransaction.execute(tx -> jdbc.query("SELECT ip,status,owner_key,scene_id,element_id,port_id,request_name,vm_uuid,mac_addr,reason,seen_in_inventory,updated_at FROM " + TABLE,
                (rs, n) -> new Lease(rs.getString("ip"), rs.getString("status"), rs.getString("owner_key"),
                        rs.getString("scene_id"), rs.getString("element_id"), rs.getString("port_id"), rs.getString("request_name"),
                        rs.getString("vm_uuid"), rs.getString("mac_addr"), rs.getString("reason"), rs.getInt("seen_in_inventory") == 1, rs.getTimestamp("updated_at")))));
    }

    private Lease read(String ip) {
        return list().stream().filter(r -> ip.equals(r.ip)).findFirst().orElseThrow(() -> new MpRuntimeException("厂商二地址记录不存在"));
    }

    /** Seed only missing rows, and protect existing observations BEFORE opening reviewed addresses. */
    void prepare(VendorSecondInventory inventory) {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        Set<String> reviewed = range.reviewedFree(config.getXdTwoIpReviewedFree());
        Set<String> existing = new HashSet<>();
        list().forEach(row -> existing.add(row.ip));
        for (String ip : range.addresses()) {
            if (existing.contains(ip)) continue;
            try {
                write("INSERT INTO " + TABLE + " (ip,status,reason,updated_at) VALUES (?,?,?,CURRENT_TIMESTAMP)", ip, PENDING, INITIAL);
            } catch (DuplicateKeyException ignored) {
                // A concurrent instance initialized this address first.
            }
        }
        importLegacyRecords(range, inventory);
        protectObserved(range, inventory);
        for (Lease row : list()) {
            if (!reviewed.contains(row.ip) || !INITIAL.equals(row.reason) || !PENDING.equals(row.status)) continue;
            write("UPDATE " + TABLE + " SET status=?,reason=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND status=? AND reason=? AND owner_key IS NULL AND vm_uuid IS NULL",
                    FREE, "INITIAL_REVIEWED", row.ip, PENDING, INITIAL);
        }
    }

    private void importLegacyRecords(VendorSecondIpRange range, VendorSecondInventory inventory) {
        // Only initial rows can be affected by legacy/template records; runtime records alone are not proof of the actual IP.
        if (list().stream().noneMatch(row -> INITIAL.equals(row.reason))) return;
        List<String[]> legacy = jdbc.query("SELECT e.scene AS scene_id,e.id AS element_id,p.id AS port_id,p.ip AS port_ip,c.resource_id AS vm_uuid "
                        + "FROM mp_scene_scene_element e JOIN mp_scene_scene s ON s.id=e.scene "
                        + "JOIN mp_sceneplugincloud_scene_element c ON c.scene=e.scene AND c.scene_element=e.node_id "
                        + "JOIN mp_scene_scene_element_port p ON p.scene=e.scene AND p.scene_element=e.id "
                        + "WHERE e.vendor=2 AND c.resource_id IS NOT NULL AND c.resource_id<>'' AND (s.deleted=0 OR (s.event=9 AND s.event_status<>4))",
                (rs, n) -> new String[]{rs.getString("scene_id"), rs.getString("element_id"), rs.getString("port_id"), rs.getString("port_ip"), rs.getString("vm_uuid")});
        Map<String, Set<String>> owners = inventory.ownersByAddress();
        for (String[] row : legacy) {
            String uuid = row[4];
            String[] observed = inventory.primaryAddress(uuid);
            String ip = observed != null && range.contains(observed[0]) ? observed[0] : row[3];
            if (!range.contains(ip)) continue;
            boolean verified = inventory.complete && observed != null && ip.equals(observed[0])
                    && owners.getOrDefault(ip, Collections.emptySet()).size() == 1;
            write("UPDATE " + TABLE + " SET status=?,reason=?,scene_id=?,element_id=?,port_id=?,vm_uuid=?,mac_addr=?,seen_in_inventory=?,updated_at=CURRENT_TIMESTAMP "
                            + "WHERE ip=? AND status=? AND reason=? AND owner_key IS NULL AND vm_uuid IS NULL",
                    verified ? USED : PENDING, verified ? "LEGACY_VERIFIED" : "LEGACY_RECORD", row[0], row[1], row[2], uuid,
                    verified ? observed[1] : null, inventory.vms.containsKey(uuid) ? 1 : 0, ip, PENDING, INITIAL);
        }
    }

    private void protectObserved(VendorSecondIpRange range, VendorSecondInventory inventory) {
        Map<String, Set<String>> observations = inventory.ownersByAddress();
        if (observations.isEmpty()) return;
        Map<String, Lease> existing = new HashMap<>();
        list().forEach(row -> existing.put(row.ip, row));
        observations.forEach((ip, owners) -> {
            Lease row = existing.get(ip);
            if (!range.contains(ip) || row == null || row.owner != null || row.vmUuid != null
                    || !(FREE.equals(row.status) || (PENDING.equals(row.status)
                    && Arrays.asList(INITIAL, "PROBE_OCCUPIED", "PROVIDER_IP_CONFLICT").contains(row.reason)))) return;
            String uuid = owners.size() == 1 ? owners.iterator().next() : null;
            write("UPDATE " + TABLE + " SET status=?,reason=?,vm_uuid=?,seen_in_inventory=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND owner_key IS NULL AND vm_uuid IS NULL "
                            + "AND (status=? OR (status=? AND (reason=? OR reason IN ('PROBE_OCCUPIED','PROVIDER_IP_CONFLICT'))))",
                    PENDING, owners.size() == 1 ? "OBSERVED_RESOURCE" : "OBSERVED_CONFLICT", uuid, uuid == null ? 0 : 1, ip, FREE, PENDING, INITIAL);
        });
    }

    /** Only the selected member of the configured address pool is checked. */
    ProbeResult probe(String ip) {
        if (!new VendorSecondIpRange(config).contains(ip) || !config.isXdTwoIpProbeEnabled()) return ProbeResult.UNAVAILABLE;
        int timeout = config.getXdTwoIpProbeTimeoutMs();
        if (timeout <= 0 || timeout > 10000) {
            log.warn("厂商二地址可达性检查超时配置无效");
            return ProbeResult.UNAVAILABLE;
        }
        try {
            long n = VendorSecondIpRange.number(ip);
            byte[] bytes = {(byte) (n >>> 24), (byte) (n >>> 16), (byte) (n >>> 8), (byte) n};
            return InetAddress.getByAddress(bytes).isReachable(timeout) ? ProbeResult.REACHABLE : ProbeResult.NO_REPLY;
        } catch (IOException | SecurityException e) {
            log.warn("厂商二地址辅助可达性检查异常，ip={}", ip, e);
            return ProbeResult.UNAVAILABLE;
        }
    }

    Lease reserve(String sceneId, String elementId, String portId, String preferred, String owner, String requestName) {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        List<String> candidates = range.addresses();
        if (range.contains(preferred)) {
            candidates.remove(preferred);
            candidates.add(0, preferred);
        }
        List<Lease> snapshot = list();
        if (snapshot.stream().anyMatch(row -> owner.equals(row.owner) || (elementId != null && elementId.equals(row.elementId) && row.vmUuid != null))) {
            throw new MpRuntimeException("厂商二节点已有地址分配或待核查资源，请先确认原资源状态");
        }
        Set<String> free = new HashSet<>();
        snapshot.stream().filter(row -> FREE.equals(row.status) && row.owner == null && row.vmUuid == null).forEach(row -> free.add(row.ip));
        for (String ip : candidates) {
            if (!free.contains(ip)) continue;
            int changed;
            try {
                changed = write("UPDATE " + TABLE + " SET status=?,owner_key=?,scene_id=?,element_id=?,port_id=?,request_name=?,reason=?,updated_at=CURRENT_TIMESTAMP "
                                + "WHERE ip=? AND status=? AND owner_key IS NULL AND vm_uuid IS NULL",
                        RESERVED, owner, sceneId, elementId, portId, requestName, "CREATE_IN_FLIGHT", ip, FREE);
            } catch (DuplicateKeyException e) {
                throw new MpRuntimeException("厂商二节点存在并发分配请求");
            }
            if (changed == 0) continue;
            Lease lease = read(ip);
            ProbeResult probe = probe(ip);
            if (probe == ProbeResult.REACHABLE) {
                clearOwnership(lease, PENDING, "PROBE_OCCUPIED", RESERVED);
                log.warn("厂商二候选地址存在占用响应，跳过 ip={}", ip);
                continue;
            }
            if (probe == ProbeResult.UNAVAILABLE) log.warn("厂商二候选地址辅助检测不可用，依据已核对的台账 ip={}", ip);
            log.info("厂商二预占 ip={}, scene={}, element={}", ip, sceneId, elementId);
            return lease;
        }
        throw new Exhausted("厂商二无已确认可用的IP地址，请核对地址池占用及初始已核对空闲地址配置");
    }

    void rejectBeforeCreation(Lease lease, boolean occupied) {
        boolean provisional = "CREATE_IN_FLIGHT_ON_DEMAND".equals(lease.reason);
        clearOwnership(lease, occupied || provisional ? PENDING : FREE,
                occupied ? "PROVIDER_IP_CONFLICT" : provisional ? INITIAL : "CREATE_REJECTED", RESERVED);
    }

    private void clearOwnership(Lease lease, String status, String reason, String expected) {
        write("UPDATE " + TABLE + " SET status=?,reason=?,owner_key=NULL,scene_id=NULL,element_id=NULL,port_id=NULL,request_name=NULL,vm_uuid=NULL,mac_addr=NULL,seen_in_inventory=0,updated_at=CURRENT_TIMESTAMP "
                        + "WHERE ip=? AND status=? AND owner_key=? AND request_name=?",
                status, reason, lease.ip, expected, lease.owner, lease.requestName);
    }

    void uncertainCreation(Lease lease) {
        write("UPDATE " + TABLE + " SET status=?,reason=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND status=? AND owner_key=? AND request_name=?",
                PENDING, "CREATE_UNCERTAIN", lease.ip, RESERVED, lease.owner, lease.requestName);
    }

    void recordCreated(Lease lease, String uuid) {
        int changed = write("UPDATE " + TABLE + " SET status=?,vm_uuid=?,reason=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND owner_key=? AND request_name=? "
                        + "AND (status=? OR (status=? AND reason=?)) AND vm_uuid IS NULL",
                PENDING, uuid, "CREATED_UNVERIFIED", lease.ip, lease.owner, lease.requestName, RESERVED, PENDING, "CREATE_UNCERTAIN");
        if (changed != 1) {
            Lease current = read(lease.ip);
            if (!Objects.equals(current.owner, lease.owner) || !Objects.equals(current.vmUuid, uuid)) {
                throw new MpRuntimeException("厂商二已返回虚拟机UUID，但地址关联状态待核查");
            }
        }
    }

    String[] confirm(Lease original, String uuid, VendorSecondInventory inventory) {
        if (inventory.vms.containsKey(uuid)) {
            write("UPDATE " + TABLE + " SET seen_in_inventory=1 WHERE ip=? AND vm_uuid=? AND status<>?", original.ip, uuid, FREE);
        }
        String[] actual = inventory.primaryAddress(uuid);
        if (actual == null || actual[0] == null || actual[0].isEmpty()) return null;
        Set<String> owners = inventory.ownersByAddress().getOrDefault(original.ip, Collections.emptySet());
        boolean matches = original.ip.equals(actual[0]) && owners.size() == 1 && owners.contains(uuid);
        // A partial list may prove a mismatch or occupation, but not global uniqueness.
        if (matches && !inventory.complete) return null;
        int changed = write("UPDATE " + TABLE + " SET status=?,reason=?,mac_addr=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND vm_uuid=? AND "
                        + "(status=? OR (status=? AND reason IN ('CREATED_UNVERIFIED','ADDRESS_MISMATCH','LEGACY_RECORD','OBSERVED_RESOURCE')))",
                matches ? USED : PENDING, matches ? "VERIFIED" : "ADDRESS_MISMATCH", matches ? actual[1] : null,
                original.ip, uuid, USED, PENDING);
        if (!matches) {
            protectObserved(new VendorSecondIpRange(config), inventory);
            log.warn("厂商二虚拟机地址与台账不一致，uuid={}, 分配地址={}, 地址暂不可复用", uuid, original.ip);
            return null;
        }
        if (changed == 1) return actual;
        Lease current = read(original.ip);
        return USED.equals(current.status) && uuid.equals(current.vmUuid) ? actual : null;
    }

    void beginDelete(String uuid, VendorSecondInventory inventory) {
        if (inventory.vms.containsKey(uuid)) {
            write("UPDATE " + TABLE + " SET seen_in_inventory=1 WHERE vm_uuid=?", uuid);
        }
        write("UPDATE " + TABLE + " SET status=?,reason=?,updated_at=CURRENT_TIMESTAMP WHERE vm_uuid=? AND status<>? AND reason<>?",
                RELEASING, "DELETE_IN_FLIGHT", uuid, FREE, "POST_DELETE_OCCUPIED");
    }

    void deleteSubmitted(String uuid, boolean accepted) {
        write("UPDATE " + TABLE + " SET reason=?,updated_at=CURRENT_TIMESTAMP WHERE vm_uuid=? AND status=? AND reason=?",
                accepted ? "DELETE_REQUESTED" : "DELETE_UNCERTAIN", uuid, RELEASING, "DELETE_IN_FLIGHT");
    }

    List<Lease> releasingLeases() {
        List<Lease> leases = new ArrayList<>();
        for (Lease row : list()) {
            if (RELEASING.equals(row.status) && row.vmUuid != null) leases.add(row);
        }
        return leases;
    }

    /** Refuse a stale scene reference when the persistent lease identifies a different owner. */
    void validateManagedOwner(String uuid, String sceneId, String elementId) {
        if (sceneId == null || elementId == null) return;
        for (Lease row : list()) {
            if (row.sceneId == null || row.elementId == null || FREE.equals(row.status) || row.vmUuid == null) continue;
            boolean sameOwner = sceneId.equals(row.sceneId) && elementId.equals(row.elementId);
            boolean sameVm = uuid.equals(row.vmUuid);
            if (sameOwner != sameVm) {
                throw new MpRuntimeException("厂商二场景节点与地址台账虚拟机关联不一致，禁止操作");
            }
        }
    }

    String resolveManagedVmForDelete(String sceneId, String elementId, VendorSecondInventory inventory) {
        if (sceneId == null || sceneId.isEmpty() || elementId == null || elementId.isEmpty()) {
            throw new MpRuntimeException("厂商二场景或节点标识缺失，无法核对原虚拟机");
        }
        List<Lease> matches = new ArrayList<>();
        for (Lease row : list()) {
            if (sceneId.equals(row.sceneId) && elementId.equals(row.elementId) && !FREE.equals(row.status)) {
                matches.add(row);
            }
        }
        if (matches.size() != 1) {
            throw new MpRuntimeException("厂商二虚拟机UUID为空且无法唯一关联地址台账，未提交回收请求");
        }
        Lease lease = matches.get(0);
        if (lease.vmUuid != null && !lease.vmUuid.isEmpty()) return lease.vmUuid;
        // Only a previously recorded uncertain creation may be resolved by its
        // unique request name. Never infer a resource identity from its IP alone.
        if (inventory.complete && PENDING.equals(lease.status) && "CREATE_UNCERTAIN".equals(lease.reason)) {
            String found = inventory.byName(lease.requestName);
            if (found != null) {
                recordCreated(lease, found);
                return found;
            }
        }
        throw new MpRuntimeException("厂商二创建结果尚未核实，无法确认待回收的虚拟机UUID");
    }

    boolean hasReleasingAddressInPool() {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        return releasingLeases().stream().anyMatch(row -> range.contains(row.ip));
    }

    boolean hasOnDemandCandidate(Set<String> tried) {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        Set<String> approved = range.onDemandScope(config.getXdTwoIpOnDemandProbeRange());
        return config.isXdTwoIpProbeEnabled() && list().stream().anyMatch(row ->
                approved.contains(row.ip) && !tried.contains(row.ip) && PENDING.equals(row.status)
                        && INITIAL.equals(row.reason) && row.owner == null && row.vmUuid == null);
    }

    /**
     * A separately approved exclusive assignment is necessary for addresses
     * not individually reviewed. Never use this path for conflict or unknown-owner
     * records. Claim only the selected address, not the entire unreviewed pool.
     */
    Lease reserveOnDemand(String sceneId, String elementId, String portId, String preferred,
                          String owner, String requestName, VendorSecondInventory inventory, Set<String> tried) {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        Set<String> approved = range.onDemandScope(config.getXdTwoIpOnDemandProbeRange());
        if (approved.isEmpty() || !config.isXdTwoIpProbeEnabled() || !inventory.complete) return null;
        Set<String> occupied = inventory.ownersByAddress().keySet();
        Set<String> runtimeClaims = new HashSet<>(jdbc.query(
                "SELECT p.ip,p.scene,p.scene_element FROM mp_scene_scene_element_port p "
                        + "JOIN mp_scene_scene s ON s.id=p.scene WHERE s.deleted=0 AND p.ip<>''",
                (rs, n) -> Objects.equals(sceneId, rs.getString("scene"))
                        && Objects.equals(elementId, rs.getString("scene_element")) ? "" : rs.getString("ip")));
        List<Lease> snapshot = list();
        if (snapshot.stream().anyMatch(row -> owner.equals(row.owner)
                || (elementId != null && elementId.equals(row.elementId) && row.vmUuid != null))) {
            throw new MpRuntimeException("厂商二节点已有地址分配或待核查资源，请先确认原资源状态");
        }
        snapshot.sort(Comparator.comparingLong(row -> VendorSecondIpRange.number(row.ip)));
        if (range.contains(preferred)) {
            snapshot.sort(Comparator.comparing(row -> !preferred.equals(row.ip)));
        }
        int checked = 0;
        for (Lease row : snapshot) {
            if (!approved.contains(row.ip) || !PENDING.equals(row.status) || !INITIAL.equals(row.reason)
                    || row.owner != null || row.vmUuid != null || !tried.add(row.ip)) continue;
            if (occupied.contains(row.ip) || runtimeClaims.contains(row.ip)) continue;
            try {
                int changed = write("UPDATE " + TABLE + " SET status=?,owner_key=?,scene_id=?,element_id=?,port_id=?,request_name=?,reason=?,updated_at=CURRENT_TIMESTAMP "
                                + "WHERE ip=? AND status=? AND reason=? AND owner_key IS NULL AND vm_uuid IS NULL",
                        RESERVED, owner, sceneId, elementId, portId, requestName, "CREATE_IN_FLIGHT_ON_DEMAND", row.ip, PENDING, INITIAL);
                if (changed == 0) continue;
            } catch (DuplicateKeyException e) {
                throw new MpRuntimeException("厂商二节点存在并发分配请求");
            }
            Lease lease = read(row.ip);
            ProbeResult result = probe(row.ip);
            if (result == ProbeResult.NO_REPLY) {
                log.info("厂商二紧缺时核对并预占已划归专属范围的候选地址，ip={}, scene={}, element={}",
                        row.ip, sceneId, elementId);
                return lease;
            }
            clearOwnership(lease, PENDING, result == ProbeResult.REACHABLE ? "PROBE_OCCUPIED" : INITIAL, RESERVED);
            if (++checked >= 12) break;
        }
        return null;
    }

    /** Reuse only addresses with positive release evidence, not merely a silent probe. */
    void recoverForAllocation(List<Lease> beforeRead, VendorSecondInventory inventory) {
        prepare(inventory);
        if (!inventory.complete) return;
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        Set<String> checked = new HashSet<>();
        for (Lease row : beforeRead) {
            // Recover a bounded batch so concurrent creators can reuse one inventory
            // read; do not perform an unrestricted sweep of the management network.
            if (range.contains(row.ip) && row.seenInInventory && !inventory.vms.containsKey(row.vmUuid)
                    && (!"DELETE_IN_FLIGHT".equals(row.reason) || stale(row)) && checked.add(row.vmUuid)) {
                reconcileDeleted(row.vmUuid, inventory, beforeRead);
                if (checked.size() >= 12) return;
            }
        }
    }

    /** Absence is only actionable after a delete attempt and a complete VM-list response. */
    boolean reconcileDeleted(String uuid, VendorSecondInventory inventory) {
        return reconcileDeleted(uuid, inventory, releasingLeases());
    }

    boolean reconcileDeleted(String uuid, VendorSecondInventory inventory, List<Lease> beforeRead) {
        if (inventory.vms.containsKey(uuid)) {
            write("UPDATE " + TABLE + " SET seen_in_inventory=1 WHERE vm_uuid=? AND status=?", uuid, RELEASING);
            return false;
        }
        if (!inventory.complete) return false;
        Map<String, Set<String>> occupied = inventory.ownersByAddress();
        for (Lease row : beforeRead) {
            if (!uuid.equals(row.vmUuid) || !RELEASING.equals(row.status)) continue;
            // An accepted create can be missing from a list while the provider is still creating it.
            // Do not infer that its address is released if that resource was never observed.
            if (!row.seenInInventory || ("DELETE_IN_FLIGHT".equals(row.reason) && !stale(row))) return false;
            // Do not release a lease changed by a concurrent deletion/recovery since
            // the inventory request began. The CAS also protects against concurrent probes.
            Lease current = read(row.ip);
            if (!uuid.equals(current.vmUuid) || !RELEASING.equals(current.status)) continue;
            if (!Objects.equals(row.reason, current.reason) || !Objects.equals(row.updatedAt, current.updatedAt)) return false;
            if (occupied.containsKey(row.ip) || probe(row.ip) == ProbeResult.REACHABLE) {
                write("UPDATE " + TABLE + " SET status=?,reason=?,updated_at=CURRENT_TIMESTAMP WHERE ip=? AND vm_uuid=? AND status=? AND reason=? AND updated_at=?",
                        PENDING, "POST_DELETE_OCCUPIED", row.ip, uuid, RELEASING, row.reason, row.updatedAt);
            } else {
                int released = write("UPDATE " + TABLE + " SET status=?,reason=?,owner_key=NULL,scene_id=NULL,element_id=NULL,port_id=NULL,request_name=NULL,vm_uuid=NULL,mac_addr=NULL,seen_in_inventory=0,updated_at=CURRENT_TIMESTAMP "
                                + "WHERE ip=? AND vm_uuid=? AND status=? AND reason=? AND updated_at=? AND seen_in_inventory=1",
                        FREE, "DELETE_CONFIRMED", row.ip, uuid, RELEASING, row.reason, row.updatedAt);
                if (released == 1) log.info("厂商二已确认资源不存在，释放 ip={}, uuid={}", row.ip, uuid);
            }
        }
        return releasingLeases().stream().noneMatch(row -> uuid.equals(row.vmUuid));
    }

    private boolean stale(Lease lease) {
        return lease.updatedAt != null && lease.updatedAt.toInstant().isBefore(Instant.now().minusMillis(STALE_MS));
    }

    void reconcile(VendorSecondInventory inventory) {
        reconcile(inventory, releasingLeases());
    }

    void reconcile(VendorSecondInventory inventory, List<Lease> beforeRead) {
        prepare(inventory);
        Set<String> deleting = new HashSet<>();
        for (Lease row : list()) {
            if (RELEASING.equals(row.status) && row.vmUuid != null && deleting.add(row.vmUuid)) {
                reconcileDeleted(row.vmUuid, inventory, beforeRead);
                continue;
            }
            if (RESERVED.equals(row.status) && stale(row)) uncertainCreation(row);
            boolean pendingCreate = (PENDING.equals(row.status) && "CREATE_UNCERTAIN".equals(row.reason))
                    || (RESERVED.equals(row.status) && stale(row));
            String uuid = row.vmUuid;
            // Association by request name needs global uniqueness, which a
            // partially returned inventory cannot establish.
            if (pendingCreate && uuid == null && inventory.complete) {
                uuid = inventory.byName(row.requestName);
                if (uuid != null) recordCreated(row, uuid);
            }
            if (uuid == null) continue;
            if (USED.equals(row.status) || pendingCreate || (PENDING.equals(row.status)
                    && Arrays.asList("CREATED_UNVERIFIED", "ADDRESS_MISMATCH", "LEGACY_RECORD", "OBSERVED_RESOURCE").contains(row.reason))) {
                String[] actual = confirm(row, uuid, inventory);
                if (actual != null) syncRuntimePort(row, uuid, actual);
            }
        }
        log.info("厂商二地址池核对汇总，inventoryComplete={}, status/reason={}", inventory.complete, statusSummary());
    }

    Map<String, Long> statusSummary() {
        Map<String, Long> summary = new TreeMap<>();
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        for (Lease row : list()) {
            if (range.contains(row.ip)) summary.merge(row.status + "/" + row.reason, 1L, Long::sum);
        }
        return summary;
    }

    private void syncRuntimePort(Lease lease, String uuid, String[] actual) {
        if (lease.portId == null || lease.elementId == null || lease.sceneId == null) return;
        // Only the original, still-current runtime port may be changed; never a template or recreated node.
        write("UPDATE mp_scene_scene_element_port SET ip=?,mac_addr=COALESCE(?,mac_addr),netmask=?,gateway=?,update_time=CURRENT_TIMESTAMP "
                        + "WHERE id=? AND scene=? AND scene_element=? AND EXISTS (SELECT 1 FROM mp_scene_scene_element e "
                        + "JOIN mp_scene_scene s ON s.id=e.scene JOIN mp_sceneplugincloud_scene_element c ON c.scene=e.scene AND c.scene_element=e.node_id "
                        + "WHERE e.id=? AND e.scene=? AND e.vendor=2 AND s.deleted=0 AND e.event<>9 AND e.event_status=4 AND c.resource_id=?) "
                        + "AND EXISTS (SELECT 1 FROM " + TABLE + " a WHERE a.ip=? AND a.vm_uuid=? AND a.status=?)",
                actual[0], actual[1], config.getXdTwoIpNetmask(), config.getXdTwoIpGateway(),
                lease.portId, lease.sceneId, lease.elementId, lease.elementId, lease.sceneId, uuid, lease.ip, uuid, USED);
    }
}
