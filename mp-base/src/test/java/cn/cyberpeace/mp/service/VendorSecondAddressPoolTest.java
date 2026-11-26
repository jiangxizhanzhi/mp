package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static cn.cyberpeace.mp.service.VendorSecondAddressPool.*;
import static org.junit.jupiter.api.Assertions.*;

class VendorSecondAddressPoolTest {
    private static final String A = "198.51.100.10", B = "198.51.100.11", C = "198.51.100.12";
    private JdbcTemplate jdbc;
    private XdAppConfig config;
    private DataSourceTransactionManager tx;
    private VendorSecondAddressPool pool;
    private final Map<String, ProbeResult> probes = new ConcurrentHashMap<>();

    @BeforeEach
    void setup() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        tx = new DataSourceTransactionManager(source);
        String ddl = Files.readString(Paths.get("../mp/src/main/resources/db/migration/V202611260900__vendor_second_address_pool.sql"));
        for (String statement : ddl.split(";")) if (!statement.trim().isEmpty()) jdbc.execute(statement);
        jdbc.execute("CREATE TABLE mp_scene_scene (id VARCHAR(64) PRIMARY KEY,deleted INT,event INT,event_status INT)");
        jdbc.execute("CREATE TABLE mp_scene_scene_element (id VARCHAR(64) PRIMARY KEY,scene VARCHAR(64),node_id VARCHAR(64),vendor INT,event INT,event_status INT)");
        jdbc.execute("CREATE TABLE mp_sceneplugincloud_scene_element (scene VARCHAR(64),scene_element VARCHAR(64),resource_id VARCHAR(128))");
        jdbc.execute("CREATE TABLE mp_scene_scene_element_port (id VARCHAR(64) PRIMARY KEY,scene VARCHAR(64),scene_element VARCHAR(64),ip VARCHAR(15),mac_addr VARCHAR(64),netmask VARCHAR(15),gateway VARCHAR(15),update_time TIMESTAMP)");
        config = new XdAppConfig();
        config.setXdTwoIpPoolStart(A);
        config.setXdTwoIpPoolEnd(C);
        config.setXdTwoIpGateway("198.51.100.1");
        config.setXdTwoIpNetmask("255.255.255.0");
        config.setXdTwoIpReviewedFree(A + "-" + C);
        pool = newPool();
    }

    private VendorSecondAddressPool newPool() {
        return new VendorSecondAddressPool(jdbc, config, tx) {
            @Override ProbeResult probe(String ip) {
                return probes.getOrDefault(ip, ProbeResult.NO_REPLY);
            }
        };
    }

    static JSONObject vm(String id, String name, String ip) {
        JSONObject vm = new JSONObject();
        vm.put("id", id);
        vm.put("name", name);
        vm.put("status", "stopped");
        JSONObject network = new JSONObject();
        network.put("ip_address", ip);
        network.put("mac_address", "02:00:00:00:00:01");
        JSONArray networks = new JSONArray();
        networks.add(network);
        vm.put("networks", networks);
        return vm;
    }

    static HttpResult listResponse(int total, JSONObject... vms) {
        JSONObject result = new JSONObject(), data = new JSONObject();
        result.put("code", 0);
        result.put("data", data);
        data.put("page_num", 0);
        data.put("total_size", total);
        JSONArray rows = new JSONArray();
        rows.addAll(Arrays.asList(vms));
        data.put("data", rows);
        return response(200, result.toJSONString());
    }

    static HttpResult response(int status, String body) {
        return new HttpResult(null, new BasicHttpResponse(HttpVersion.HTTP_1_1, status, "fixture"), body, null);
    }

    private VendorSecondInventory inventory(int total, JSONObject... vms) {
        return VendorSecondInventory.parse(listResponse(total, vms));
    }

    private Lease row(String ip) {
        return pool.list().stream().filter(r -> ip.equals(r.getIp())).findFirst().orElseThrow();
    }

    private Lease reserve(VendorSecondAddressPool use, String owner, String preferred) {
        return use.reserve("scene", owner, "port-" + owner, preferred, owner, "MP_" + owner);
    }

    private Lease createVerified(String owner, String uuid, String preferred) {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, owner, preferred);
        pool.recordCreated(lease, uuid);
        assertNotNull(pool.confirm(lease, uuid, inventory(1, vm(uuid, lease.getRequestName(), lease.getIp()))));
        return lease;
    }

    @Test void initialUnreviewedIsNotFree() {
        config.setXdTwoIpReviewedFree("");
        pool.prepare(VendorSecondInventory.unavailable());
        assertEquals(3, pool.list().size());
        assertThrows(MpRuntimeException.class, () -> reserve(pool, "element", A));
        assertEquals(PENDING, row(A).getStatus());
        config.setXdTwoIpReviewedFree(A);
        pool.prepare(inventory(0));
        assertEquals(A, reserve(pool, "element", A).getIp());
        assertEquals(PENDING, row(B).getStatus());
    }

    @Test void defaultRangeContains150Addresses() {
        XdAppConfig defaults = new XdAppConfig();
        VendorSecondIpRange configured = new VendorSecondIpRange(defaults);
        assertEquals(150, configured.addresses().size());
        assertEquals("192.168.140.76", configured.addresses().get(0));
        assertEquals("192.168.140.225", configured.addresses().get(149));
        Set<String> onDemand = configured.onDemandScope(defaults.getXdTwoIpOnDemandProbeRange());
        assertEquals(150, onDemand.size());
        assertTrue(onDemand.contains("192.168.140.76"));
        assertTrue(onDemand.contains("192.168.140.125"));
        assertTrue(onDemand.contains("192.168.140.126"));
        assertTrue(onDemand.contains("192.168.140.225"));
        assertFalse(onDemand.contains("192.168.140.226"));
        assertTrue(configured.reviewedFree(defaults.getXdTwoIpReviewedFree()).isEmpty());
        assertEquals(300000L, defaults.getXdTwoAllocationRecoveryTimeoutMs());
    }

    @Test void onDemandDefaultCannotExtendAnExplicitlyConfiguredSmallerPool() {
        XdAppConfig legacy = new XdAppConfig();
        legacy.setXdTwoIpPoolStart("192.168.140.126");
        Set<String> legacyScope = new VendorSecondIpRange(legacy).onDemandScope(legacy.getXdTwoIpOnDemandProbeRange());
        assertEquals(100, legacyScope.size());
        assertFalse(legacyScope.contains("192.168.140.125"));
        legacy.setXdTwoIpPoolStart("192.168.140.120");
        assertEquals(106, new VendorSecondIpRange(legacy).onDemandScope(legacy.getXdTwoIpOnDemandProbeRange()).size());
        assertTrue(new VendorSecondIpRange(legacy).onDemandScope("").isEmpty());
    }

    @Test void leftExpansionKeepsExistingLeasesAndSeedsOnlyNewAddresses() {
        createVerified("existing", "active-vm", A);
        config.setXdTwoIpPoolStart("198.51.100.8");
        pool.prepare(inventory(2, vm("active-vm", "fixture", A), vm("legacy", "fixture", "198.51.100.9")));
        assertEquals(5, pool.list().size());
        assertEquals(USED, row(A).getStatus());
        assertEquals("active-vm", row(A).getVmUuid());
        assertEquals("INITIAL_UNREVIEWED", row("198.51.100.8").getReason());
        assertEquals("OBSERVED_RESOURCE", row("198.51.100.9").getReason());
        config.setXdTwoIpReviewedFree("198.51.100.8-" + C);
        pool.prepare(inventory(2, vm("active-vm", "fixture", A), vm("legacy", "fixture", "198.51.100.9")));
        assertEquals(FREE, row("198.51.100.8").getStatus());
        assertEquals(PENDING, row("198.51.100.9").getStatus());
        assertEquals(USED, row(A).getStatus());
    }

    @Test void preferredOccupiedAndProbeOccupiedFallBackDeterministically() {
        pool.prepare(inventory(1, vm("existing", "old", A)));
        probes.put(B, ProbeResult.REACHABLE);
        assertEquals(C, reserve(pool, "element", A).getIp());
        assertEquals("OBSERVED_RESOURCE", row(A).getReason());
        assertEquals("PROBE_OCCUPIED", row(B).getReason());
        assertThrows(MpRuntimeException.class, () -> reserve(pool, "other", "10.0.0.1"));
        // Re-running bootstrap must not reopen an address just because it was approved initially.
        pool.prepare(inventory(0));
        assertEquals(PENDING, row(B).getStatus());
    }

    @Test void silentOrUnreachableClosedVmStaysOccupied() {
        pool.prepare(inventory(1, vm("closed", "legacy", A)));
        assertEquals(B, reserve(pool, "new", A).getIp());
        pool.reconcile(inventory(1, vm("closed", "legacy", A)));
        assertEquals(USED, row(A).getStatus());
    }

    @Test void concurrencyAcrossInstancesDoesNotDuplicateIpOrOwner() throws Exception {
        pool.prepare(inventory(0));
        VendorSecondAddressPool another = newPool();
        ExecutorService threads = Executors.newFixedThreadPool(2);
        try {
            CountDownLatch start = new CountDownLatch(1);
            Future<Lease> one = threads.submit(() -> {start.await(); return reserve(pool, "one", A);});
            Future<Lease> two = threads.submit(() -> {start.await(); return reserve(another, "two", A);});
            start.countDown();
            Lease l1 = one.get(10, TimeUnit.SECONDS), l2 = two.get(10, TimeUnit.SECONDS);
            assertNotEquals(l1.getIp(), l2.getIp());
            assertThrows(MpRuntimeException.class, () -> reserve(another, "one", C));
        } finally {
            threads.shutdownNow();
        }
    }

    @Test void simultaneousRequestsForSameNodeReserveOnlyOneAddress() throws Exception {
        pool.prepare(inventory(0));
        VendorSecondAddressPool another = newPool();
        ExecutorService threads = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Callable<Lease> first = () -> { start.await(); return reserve(pool, "same", A); };
            Callable<Lease> second = () -> { start.await(); return reserve(another, "same", B); };
            Future<Lease> a = threads.submit(first), b = threads.submit(second);
            start.countDown();
            int success = 0;
            for (Future<Lease> result : Arrays.asList(a, b)) {
                try {
                    assertNotNull(result.get(10, TimeUnit.SECONDS));
                    success++;
                } catch (ExecutionException e) {
                    assertTrue(e.getCause() instanceof MpRuntimeException);
                }
            }
            assertEquals(1, success);
            assertEquals(1, pool.list().stream().filter(r -> "same".equals(r.getOwner())).count());
        } finally {
            threads.shutdownNow();
        }
    }

    @Test void reservationSurvivesOuterRollback() {
        pool.prepare(inventory(0));
        new TransactionTemplate(tx).execute(status -> {
            reserve(pool, "owner", A);
            status.setRollbackOnly();
            return null;
        });
        assertEquals(RESERVED, newPool().list().stream().filter(r -> A.equals(r.getIp())).findFirst().orElseThrow().getStatus());
    }

    @Test void creationTimeoutDoesNotReleaseAndMatchingNameRecovers() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.uncertainCreation(lease);
        pool.reconcile(inventory(0));
        assertEquals(PENDING, row(A).getStatus());
        assertEquals("CREATE_UNCERTAIN", row(A).getReason());
        pool.reconcile(inventory(1, vm("late-vm", lease.getRequestName(), A)));
        assertEquals(USED, row(A).getStatus());
        assertEquals("late-vm", row(A).getVmUuid());
    }

    @Test void mismatchBlocksBothRequestedAndObservedAddress() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.recordCreated(lease, "vm");
        assertNull(pool.confirm(lease, "vm", inventory(1, vm("vm", "MP_owner", B))));
        assertEquals("ADDRESS_MISMATCH", row(A).getReason());
        assertEquals(PENDING, row(B).getStatus());
        assertEquals(C, reserve(pool, "new", B).getIp());
    }

    @Test void partialInventoryCannotConfirmExclusiveAddress() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.recordCreated(lease, "vm");
        assertNull(pool.confirm(lease, "vm", inventory(2, vm("vm", "MP_owner", A))));
        assertEquals(PENDING, row(A).getStatus());
        assertNotNull(pool.confirm(lease, "vm", inventory(1, vm("vm", "MP_owner", A))));
        assertEquals(USED, row(A).getStatus());
    }

    @Test void onlyConfirmedDeletionAllowsReuse() {
        Lease lease = createVerified("owner", "vm", A);
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        pool.deleteSubmitted("vm", false);
        assertFalse(pool.reconcileDeleted("vm", inventory(2)));
        assertFalse(pool.reconcileDeleted("vm", inventory(1, vm("vm", lease.getRequestName(), A))));
        assertEquals(RELEASING, row(A).getStatus());
        assertTrue(pool.reconcileDeleted("vm", inventory(0)));
        assertEquals(FREE, row(A).getStatus());
        assertNull(row(A).getOwner());
        assertEquals(A, reserve(pool, "new", A).getIp());
    }

    @Test void onDemandRecoveryMakesDeletedAddressAvailableBeforeScheduledJob() {
        Lease lease = createVerified("owner", "vm", A);
        reserve(pool, "second", B);
        reserve(pool, "third", C);
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        pool.deleteSubmitted("vm", true);
        assertThrows(Exhausted.class, () -> reserve(pool, "new", A));
        List<Lease> beforeRead = pool.releasingLeases();
        pool.recoverForAllocation(beforeRead, inventory(0));
        assertEquals(FREE, row(A).getStatus());
        assertEquals(A, reserve(pool, "new", A).getIp());
    }

    @Test void onDemandRecoveryRetainsConflictsUnreviewedAndIncompleteResources() {
        config.setXdTwoIpReviewedFree(A);
        Lease lease = createVerified("owner", "vm", A);
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        pool.deleteSubmitted("vm", true);
        pool.recoverForAllocation(pool.releasingLeases(), inventory(2));
        assertEquals(RELEASING, row(A).getStatus());
        probes.put(A, ProbeResult.REACHABLE);
        pool.recoverForAllocation(pool.releasingLeases(), inventory(2, vm("conflict1", "fixture", B), vm("conflict2", "fixture", B)));
        assertEquals("POST_DELETE_OCCUPIED", row(A).getReason());
        assertEquals("OBSERVED_CONFLICT", row(B).getReason());
        assertEquals("INITIAL_UNREVIEWED", row(C).getReason());
        assertThrows(Exhausted.class, () -> reserve(pool, "new", A));
    }

    @Test void onDemandCanReserveOnlyAnExplicitExclusiveInitialCandidate() {
        config.setXdTwoIpReviewedFree("");
        config.setXdTwoIpOnDemandProbeRange(A + "-" + B);
        pool.prepare(inventory(0));
        probes.put(A, ProbeResult.REACHABLE);
        Lease provisional = pool.reserveOnDemand("scene", "node", "port", A, "owner", "MP_owner",
                inventory(0), new HashSet<>());
        assertNotNull(provisional);
        assertEquals(B, provisional.getIp());
        assertEquals(RESERVED, row(B).getStatus());
        assertEquals("PROBE_OCCUPIED", row(A).getReason());
        assertEquals("INITIAL_UNREVIEWED", row(C).getReason());
        pool.rejectBeforeCreation(provisional, false);
        assertEquals(PENDING, row(B).getStatus());
        assertEquals("INITIAL_UNREVIEWED", row(B).getReason());
    }

    @Test void onDemandRequiresCompleteInventoryAndActualProbe() {
        config.setXdTwoIpReviewedFree("");
        config.setXdTwoIpOnDemandProbeRange(A);
        pool.prepare(inventory(0));
        assertNull(pool.reserveOnDemand("scene", "node", "port", A, "owner", "MP_owner",
                inventory(2), new HashSet<>()));
        probes.put(A, ProbeResult.UNAVAILABLE);
        assertNull(pool.reserveOnDemand("scene", "node", "port", A, "owner", "MP_owner",
                inventory(0), new HashSet<>()));
        assertEquals(PENDING, row(A).getStatus());
        assertEquals("INITIAL_UNREVIEWED", row(A).getReason());
    }

    @Test void onDemandNeverBorrowsAnAddressClaimedByAnotherActiveScene() {
        config.setXdTwoIpReviewedFree("");
        config.setXdTwoIpOnDemandProbeRange(A + "-" + B);
        pool.prepare(inventory(0));
        jdbc.update("INSERT INTO mp_scene_scene VALUES ('existing-scene',0,2,4)");
        jdbc.update("INSERT INTO mp_scene_scene_element_port (id,scene,scene_element,ip) VALUES ('existing-port','existing-scene','other',?)", A);
        Lease lease = pool.reserveOnDemand("scene", "node", "port", A, "owner", "MP_owner",
                inventory(0), new HashSet<>());
        assertNotNull(lease);
        assertEquals(B, lease.getIp());
        assertEquals(PENDING, row(A).getStatus());
    }

    @Test void onDemandCannotOverrideObservedConflictOrUnapprovedAddress() {
        config.setXdTwoIpReviewedFree("");
        config.setXdTwoIpOnDemandProbeRange(A);
        pool.prepare(inventory(2, vm("first", "fixture", A), vm("second", "fixture", A)));
        assertNull(pool.reserveOnDemand("scene", "node", "port", A, "owner", "MP_owner",
                inventory(0), new HashSet<>()));
        assertEquals("OBSERVED_CONFLICT", row(A).getReason());
        assertEquals("INITIAL_UNREVIEWED", row(B).getReason());
    }

    @Test void snapshotBeforeNewDeletionAttemptDoesNotReleaseLease() {
        Lease lease = createVerified("owner", "vm", A);
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        pool.deleteSubmitted("vm", true);
        List<Lease> staleSnapshot = pool.releasingLeases();
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        assertFalse(pool.reconcileDeleted("vm", inventory(0), staleSnapshot));
        assertEquals(RELEASING, row(A).getStatus());
    }

    @Test void missingUuidResolvedFromUniqueUncertainRequestName() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.uncertainCreation(lease);
        assertThrows(MpRuntimeException.class, () -> pool.resolveManagedVmForDelete("scene", "other", inventory(0)));
        assertThrows(MpRuntimeException.class, () -> pool.resolveManagedVmForDelete("scene", "owner", inventory(2,
                vm("one", lease.getRequestName(), A))));
        assertEquals("late-vm", pool.resolveManagedVmForDelete("scene", "owner", inventory(1,
                vm("late-vm", lease.getRequestName(), A))));
        assertEquals("late-vm", row(A).getVmUuid());
    }

    @Test void partialInventoryCannotBindUncertainCreationByName() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.uncertainCreation(lease);
        pool.reconcile(inventory(2, vm("late-vm", lease.getRequestName(), A)));
        assertNull(row(A).getVmUuid());
        assertEquals("CREATE_UNCERTAIN", row(A).getReason());
        pool.reconcile(inventory(1, vm("late-vm", lease.getRequestName(), A)));
        assertEquals("late-vm", row(A).getVmUuid());
    }

    @Test void mismatchingSceneOrVmIdentityCannotOperateOnManagedLease() {
        createVerified("owner", "vm", A);
        assertDoesNotThrow(() -> pool.validateManagedOwner("vm", "scene", "owner"));
        assertThrows(MpRuntimeException.class, () -> pool.validateManagedOwner("vm", "other-scene", "other"));
        assertThrows(MpRuntimeException.class, () -> pool.validateManagedOwner("different-vm", "scene", "owner"));
    }
    @Test void addressRespondingAfterDeletionRemainsQuarantined() {
        Lease lease = createVerified("owner", "vm", A);
        pool.beginDelete("vm", inventory(1, vm("vm", lease.getRequestName(), A)));
        pool.deleteSubmitted("vm", true);
        probes.put(A, ProbeResult.REACHABLE);
        assertTrue(pool.reconcileDeleted("vm", inventory(0)));
        assertEquals("POST_DELETE_OCCUPIED", row(A).getReason());
        // A later non-response is not evidence that the previous, unidentified occupant released it.
        probes.put(A, ProbeResult.NO_REPLY);
        pool.beginDelete("vm", inventory(0));
        pool.deleteSubmitted("vm", true);
        assertTrue(pool.reconcileDeleted("vm", inventory(0)));
        assertEquals("POST_DELETE_OCCUPIED", row(A).getReason());
        assertEquals(B, reserve(pool, "new", A).getIp());
    }

    @Test void acceptedButNeverObservedVmCannotBeReleasedByEmptyList() {
        pool.prepare(inventory(0));
        Lease lease = reserve(pool, "owner", A);
        pool.recordCreated(lease, "async");
        pool.beginDelete("async", inventory(0));
        pool.deleteSubmitted("async", true);
        assertFalse(pool.reconcileDeleted("async", inventory(0)));
        assertEquals(RELEASING, row(A).getStatus());
        assertFalse(pool.reconcileDeleted("async", inventory(1, vm("async", "MP_owner", A))));
        assertTrue(pool.reconcileDeleted("async", inventory(0)));
        assertEquals(FREE, row(A).getStatus());
    }

    @Test void importsLegacyAndUpdatesOnlyMatchingRuntimePort() {
        jdbc.update("INSERT INTO mp_scene_scene VALUES ('scene',0,2,4)");
        jdbc.update("INSERT INTO mp_scene_scene_element VALUES ('old','scene','node',2,2,4)");
        jdbc.update("INSERT INTO mp_sceneplugincloud_scene_element VALUES ('scene','node','legacy')");
        jdbc.update("INSERT INTO mp_scene_scene_element_port (id,scene,scene_element,ip) VALUES ('old-port','scene','old',?)", A);
        pool.reconcile(inventory(1, vm("legacy", "old-name", B)));
        assertEquals(USED, row(B).getStatus());
        assertEquals("old", row(B).getElementId());
        assertEquals(B, jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port WHERE id='old-port'", String.class));
        jdbc.update("UPDATE mp_sceneplugincloud_scene_element SET resource_id='different' WHERE scene='scene'");
        jdbc.update("UPDATE mp_scene_scene_element_port SET ip=? WHERE id='old-port'", C);
        pool.reconcile(inventory(1, vm("legacy", "old-name", B)));
        assertEquals(C, jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port WHERE id='old-port'", String.class));
    }

    @Test void validatesRangeAndReviewedAddresses() {
        VendorSecondIpRange range = new VendorSecondIpRange(config);
        assertFalse(range.contains("198.51.1000.10"));
        assertFalse(range.contains("198.51.100.1"));
        assertThrows(IllegalArgumentException.class, () -> range.reviewedFree("198.51.100.1"));
        config.setXdTwoIpPoolEnd("198.51.101.12");
        assertThrows(IllegalArgumentException.class, () -> new VendorSecondIpRange(config));
    }
}
