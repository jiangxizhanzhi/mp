package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Reclaims only resources positively associated with a completed, deleted MP
 * scene. No inference from IP, provider-name prefix alone, or missing scene rows.
 */
@Service
@Slf4j
public class VendorVmReclaimService {
    private static final String TABLE = "mp_vendor_vm_reclaim";
    private static final String FROM = " FROM mp_scene_scene_element e JOIN mp_scene_scene s ON s.id=e.scene "
            + "JOIN mp_sceneplugincloud_scene_element c ON c.scene=e.scene AND c.scene_element=e.node_id ";
    // Event DELETE=9, event status OVER=4, virtual type VM=4.
    private static final String CLOSED = " e.vendor=? AND c.virtual_type=4 AND c.external=0 "
            + "AND s.deleted=1 AND s.event=9 AND s.event_status=4 AND e.event=9 AND e.event_status=4 "
            + "AND s.update_time<=? AND e.update_time<=? "
            + "AND NOT EXISTS (SELECT 1 FROM mp_cscene_cscene_instance i WHERE i.scene=s.id AND i.deleted=0) ";
    private static final long CLAIM_TIMEOUT = TimeUnit.HOURS.toMillis(2);
    private static final long RETRY_INTERVAL = TimeUnit.MINUTES.toMillis(2);
    private final JdbcTemplate jdbc;
    private final TransactionTemplate store;
    private final XdAppConfig config;
    private final List<MultiCloudService> providers;

    public VendorVmReclaimService(JdbcTemplate jdbc, PlatformTransactionManager tx, XdAppConfig config,
                                  List<MultiCloudService> providers) {
        this.jdbc = jdbc;
        this.config = config;
        this.providers = providers;
        store = new TransactionTemplate(tx);
        store.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @AllArgsConstructor
    private static final class Ref {
        final String vmId, scene, element;
        final boolean fromLedger;
        String ownerKey() { return scene + ":" + element; }
    }

    private int write(String sql, Object... args) {
        return Objects.requireNonNull(store.execute(tx -> jdbc.update(sql, args)));
    }

    private Timestamp cutoff() {
        long minutes = Math.max(1, config.getXdVendorReclaimClosedGraceMinutes());
        return new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(minutes));
    }

    private Map<String, Map<String, Ref>> closedReferences(int vendor) {
        Timestamp cutoff = cutoff();
        List<Ref> refs = new ArrayList<>(jdbc.query("SELECT c.resource_id AS vm_id,s.id AS scene_id,e.id AS element_id"
                        + FROM + "WHERE " + CLOSED + "AND c.resource_id<>''",
                (rs, n) -> new Ref(rs.getString("vm_id"), rs.getString("scene_id"), rs.getString("element_id"), false),
                vendor, cutoff, cutoff));

        // Provider ledgers can preserve an identity even when the failed
        // original lifecycle did not write the UUID into the runtime plugin.
        if (vendor >= 1 && vendor <= 3) {
            String ledger = vendor == 1 ? "mp_vendor_first_vm_ledger"
                    : vendor == 2 ? "mp_vendor_second_address_pool" : "mp_vendor_third_vm_ledger";
            String idColumn = vendor == 3 ? "vmid" : "vm_uuid";
            refs.addAll(jdbc.query("SELECT a." + idColumn + " AS vm_id,c.resource_id AS recorded_id,s.id AS scene_id,e.id AS element_id"
                            + FROM + "JOIN " + ledger + " a ON a.scene_id=s.id AND a.element_id=e.id WHERE " + CLOSED
                            + "AND a." + idColumn + " IS NOT NULL",
                    (rs, n) -> {
                        String vmId = rs.getString("vm_id"), recorded = rs.getString("recorded_id");
                        // Vendor three retains old, exact-name VMIDs even if this
                        // closed node later acquired a different VMID.
                        return StringUtils.isNotBlank(vmId) && (vendor == 3 || StringUtils.isBlank(recorded) || vmId.equals(recorded))
                                ? new Ref(vmId, rs.getString("scene_id"), rs.getString("element_id"), true) : null;
                    }, vendor, cutoff, cutoff));
        }
        Map<String, Map<String, Ref>> grouped = new LinkedHashMap<>();
        for (Ref ref : refs) {
            if (ref == null || StringUtils.isBlank(ref.vmId)) continue;
            grouped.computeIfAbsent(ref.vmId, id -> new LinkedHashMap<>()).put(ref.ownerKey(), ref);
        }
        return grouped;
    }

    private boolean hasUnclosedReference(int vendor, String vmId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM mp_sceneplugincloud_scene_element c "
                        + "LEFT JOIN mp_scene_scene_element e ON c.scene=e.scene AND c.scene_element=e.node_id "
                        + "LEFT JOIN mp_scene_scene s ON s.id=c.scene WHERE c.resource_id=? AND (e.vendor=? OR e.vendor IS NULL) "
                        + "AND (s.id IS NULL OR e.id IS NULL OR COALESCE(s.deleted,0)<>1 OR COALESCE(s.event,0)<>9 "
                        + "OR COALESCE(s.event_status,0)<>4 OR COALESCE(e.event,0)<>9 OR COALESCE(e.event_status,0)<>4 "
                        + "OR EXISTS (SELECT 1 FROM mp_cscene_cscene_instance i WHERE i.scene=c.scene AND i.deleted=0))",
                Integer.class, vmId, vendor);
        return count == null || count > 0;
    }

    private boolean stillClosed(int vendor, Ref ref) {
        Timestamp cutoff = cutoff();
        List<String> current = jdbc.query("SELECT c.resource_id" + FROM + "WHERE " + CLOSED + "AND s.id=? AND e.id=?",
                (rs, n) -> rs.getString(1), vendor, cutoff, cutoff, ref.scene, ref.element);
        if (current.size() != 1) return false;
        if (!ref.vmId.equals(current.get(0))) {
            if (!ref.fromLedger || (vendor != 3 && StringUtils.isNotBlank(current.get(0)))) return false;
            String table = vendor == 1 ? "mp_vendor_first_vm_ledger"
                    : vendor == 2 ? "mp_vendor_second_address_pool" : "mp_vendor_third_vm_ledger";
            String column = vendor == 3 ? "vmid" : "vm_uuid";
            if (vendor < 1 || vendor > 3) return false;
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + table + " WHERE scene_id=? AND element_id=? AND " + column + "=?",
                    Integer.class, ref.scene, ref.element, vendor == 3 ? Integer.valueOf(ref.vmId) : ref.vmId);
            if (count == null || count != 1) return false;
        }
        return !hasUnclosedReference(vendor, ref.vmId);
    }

    private String claim(int vendor, Ref ref, String name) {
        String token = TextUtils.uuid();
        try {
            write("INSERT INTO " + TABLE + " (vendor,vm_id,request_name,scene_id,element_id,status,claim_id,attempts,updated_at) "
                            + "VALUES (?,?,?,?,?,'RECLAIMING',?,1,CURRENT_TIMESTAMP)",
                    vendor, ref.vmId, name, ref.scene, ref.element, token);
            return token;
        } catch (DuplicateKeyException e) {
            long now = System.currentTimeMillis();
            int changed = write("UPDATE " + TABLE + " SET status='RECLAIMING',claim_id=?,attempts=attempts+1,updated_at=CURRENT_TIMESTAMP "
                            + "WHERE vendor=? AND vm_id=? AND request_name=? AND scene_id=? AND element_id=? "
                            + "AND ((status='RECLAIMING' AND updated_at<?) OR (status<>'RECLAIMING' AND updated_at<?))",
                    token, vendor, ref.vmId, name, ref.scene, ref.element,
                    new Timestamp(now - CLAIM_TIMEOUT), new Timestamp(now - RETRY_INTERVAL));
            return changed == 1 ? token : null;
        }
    }

    private void complete(int vendor, Ref ref, String token, String status) {
        write("UPDATE " + TABLE + " SET status=?,updated_at=CURRENT_TIMESTAMP WHERE vendor=? AND vm_id=? AND claim_id=? AND status='RECLAIMING'",
                status, vendor, ref.vmId, token);
    }

    private void confirmLateAbsence(int vendor, Map<String, Map<String, Ref>> candidates, Map<String, String> inventory,
                                    Timestamp inventoryStartedAt) {
        List<String[]> pending = jdbc.query("SELECT vm_id,request_name,scene_id,element_id,claim_id,status FROM " + TABLE
                        + " WHERE vendor=? AND ((status='VERIFY_PENDING' AND updated_at<?) OR (status='RECLAIMING' AND updated_at<?))",
                (rs, n) -> new String[]{rs.getString("vm_id"), rs.getString("request_name"),
                        rs.getString("scene_id"), rs.getString("element_id"), rs.getString("claim_id"), rs.getString("status")},
                vendor, inventoryStartedAt, new Timestamp(inventoryStartedAt.getTime() - CLAIM_TIMEOUT));
        for (String[] row : pending) {
            if (inventory.containsKey(row[0]) || inventory.containsValue(row[1])) continue;
            Map<String, Ref> owners = candidates.get(row[0]);
            Ref ref = owners == null || owners.size() != 1 ? null : owners.get(row[2] + ":" + row[3]);
            if (ref != null && stillClosed(vendor, ref)) {
                int changed = write("UPDATE " + TABLE + " SET status='CONFIRMED',updated_at=CURRENT_TIMESTAMP "
                                + "WHERE vendor=? AND vm_id=? AND status=? AND claim_id=?",
                        vendor, row[0], row[5], row[4]);
                if (changed == 1) log.info("厂商{}兜底回收已确认原资源消失，uuid={}", vendor, row[0]);
            }
        }
    }

    public void reclaim(int vendor) {
        if (!config.isXdVendorReclaimEnabled()) return;
        MultiCloudService provider = providers.stream().filter(p -> p.getVendor() == vendor).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown provider " + vendor));
        Map<String, Map<String, Ref>> candidates = closedReferences(vendor);
        if (candidates.isEmpty()) return;
        Timestamp inventoryStartedAt = new Timestamp(System.currentTimeMillis());
        Map<String, String> inventory = provider.readReclaimInventory();
        if (inventory == null) throw new IllegalStateException("厂商资源清单不可用");
        confirmLateAbsence(vendor, candidates, inventory, inventoryStartedAt);
        Map<String, Long> nameCounts = new HashMap<>();
        inventory.values().forEach(name -> { if (name != null) nameCounts.merge(name, 1L, Long::sum); });

        Map<String, Timestamp> previous = new HashMap<>();
        jdbc.query("SELECT vm_id,updated_at FROM " + TABLE + " WHERE vendor=?", rs -> {
            previous.put(rs.getString("vm_id"), rs.getTimestamp("updated_at"));
        }, vendor);
        List<String> ordered = new ArrayList<>(candidates.keySet());
        ordered.sort(Comparator.comparing(previous::get, Comparator.nullsFirst(Comparator.naturalOrder())));
        int observed = 0, skipped = 0, attempted = 0, confirmed = 0, settled = 0;
        int limit = Math.max(1, Math.min(100, config.getXdVendorReclaimMaxPerRun()));
        long deadline = System.nanoTime() + TimeUnit.MINUTES.toNanos(45);
        for (String id : ordered) {
            if (Thread.currentThread().isInterrupted() || System.nanoTime() >= deadline) break;
            if (!inventory.containsKey(id)) {
                Map<String, Ref> missingRefs = candidates.get(id);
                if (missingRefs.size() != 1) continue;
                Ref missing = missingRefs.values().iterator().next();
                try {
                    if (stillClosed(vendor, missing) && provider.reconcileAbsentVm(id, missing.scene, missing.element)) settled++;
                } catch (RuntimeException e) {
                    log.warn("厂商{}已消失资源的台账核对待重试，uuid={}, scene={}", vendor, id, missing.scene, e);
                }
                continue;
            }
            observed++;
            Map<String, Ref> refs = candidates.get(id);
            String name = inventory.get(id);
            if (refs.size() != 1 || StringUtils.isBlank(name) || nameCounts.getOrDefault(name, 0L) != 1L) {
                skipped++;
                continue;
            }
            Ref ref = refs.values().iterator().next();
            if (attempted >= limit || System.nanoTime() >= deadline) break;
            String token = null;
            try {
                if (!stillClosed(vendor, ref) || !provider.matchesReclaimIdentity(id, name, ref.scene, ref.element)) {
                    skipped++;
                    continue;
                }
                token = claim(vendor, ref, name);
                if (token == null) { skipped++; continue; }
                // Re-check the closure after the durable claim. Provider-specific
                // implementations then re-read the exact provider identity.
                if (!stillClosed(vendor, ref)) {
                    complete(vendor, ref, token, "SKIPPED");
                    skipped++;
                    continue;
                }
                attempted++;
                log.info("厂商{}兜底回收，uuid={}, scene={}, element={}", vendor, id, ref.scene, ref.element);
                provider.reclaimClosedVm(id, name, ref.scene, ref.element);
                complete(vendor, ref, token, "CONFIRMED");
                confirmed++;
            } catch (RuntimeException e) {
                if (token != null) complete(vendor, ref, token, "VERIFY_PENDING");
                log.warn("厂商{}残留虚拟机回收待核查，uuid={}, scene={}", vendor, id, ref.scene, e);
            }
            if (Thread.currentThread().isInterrupted()) break;
        }
        log.info("厂商{}兜底回收汇总，历史候选={}, 仍存在={}, 跳过={}, 尝试={}, 已确认={}, 已消失资源台账结案={}",
                vendor, candidates.size(), observed, skipped, attempted, confirmed, settled);
    }
}
