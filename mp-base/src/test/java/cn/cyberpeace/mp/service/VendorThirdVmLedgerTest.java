package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

import static cn.cyberpeace.mp.service.VendorThirdVmLedger.*;
import static org.junit.jupiter.api.Assertions.*;

class VendorThirdVmLedgerTest {
    JdbcTemplate jdbc;
    DataSourceTransactionManager tx;
    VendorThirdVmLedger ledger;

    @BeforeEach void setup() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        tx = new DataSourceTransactionManager(source);
        String ddl = Files.readString(Paths.get("../mp/src/main/resources/db/migration/V202611261000__vendor_third_vm_ledger.sql"));
        for (String stmt : ddl.split(";")) if (!stmt.trim().isEmpty()) jdbc.execute(stmt);
        jdbc.execute("CREATE TABLE mp_scene_scene (id VARCHAR(64) PRIMARY KEY, deleted INT)");
        jdbc.execute("CREATE TABLE mp_scene_scene_element (id VARCHAR(64) PRIMARY KEY,scene VARCHAR(64),node_id VARCHAR(64),vendor INT,event INT)");
        jdbc.execute("CREATE TABLE mp_sceneplugincloud_scene_element (scene VARCHAR(64),scene_element VARCHAR(64),resource_id VARCHAR(128),update_time TIMESTAMP)");
        jdbc.execute("CREATE TABLE mp_scene_scene_element_port (id VARCHAR(64) PRIMARY KEY,scene VARCHAR(64),scene_element VARCHAR(64),ip VARCHAR(15),mac_addr VARCHAR(64),netmask VARCHAR(15),gateway VARCHAR(15),update_time TIMESTAMP)");
        ledger = new VendorThirdVmLedger(jdbc, tx);
    }

    Lease reserve(VendorThirdVmLedger use, String element, Set<Integer> occupied) {
        return use.reserve(ownerKey("scene", element), "scene", element, "port-" + element,
                "115", "MP_CREATE_" + UUID.randomUUID(), "node", occupied);
    }

    void scene(String element, String resourceId) {
        jdbc.update("INSERT INTO mp_scene_scene VALUES ('scene',0)");
        jdbc.update("INSERT INTO mp_scene_scene_element VALUES (?,'scene','node-id',3,1)", element);
        jdbc.update("INSERT INTO mp_sceneplugincloud_scene_element(scene,scene_element,resource_id) VALUES ('scene','node-id',?)", resourceId);
        jdbc.update("INSERT INTO mp_scene_scene_element_port(id,scene,scene_element,ip,mac_addr,netmask,gateway) VALUES (?,'scene',?,'','','','')", "port-" + element, element);
    }

    @Test void allocatesAboveProviderAndHistoricalIdsEvenAfterRestart() {
        scene("old", "9500");
        Lease first = reserve(ledger, "first", Set.of(115, 9000));
        assertEquals(9501, first.getVmid());
        ledger.observed(first, "migrated");
        ledger.beginDelete(first);
        assertTrue(ledger.deleted(first));
        VendorThirdVmLedger restarted = new VendorThirdVmLedger(jdbc, tx);
        assertNull(restarted.byOwner(first.getOwner()));
        assertEquals(DELETED, restarted.byId(first.getVmid()).getStatus());
        assertEquals(9502, reserve(restarted, "first", Set.of(115, 9000)).getVmid());
    }

    @Test void concurrentReservationsAcrossInstancesAreUnique() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < 36; i++) {
                String owner = "element-" + i;
                futures.add(executor.submit(() -> reserve(new VendorThirdVmLedger(jdbc, tx), owner, Set.of(9000)).getVmid()));
            }
            Set<Integer> ids = new HashSet<>();
            for (Future<Integer> future : futures) assertTrue(ids.add(future.get(20, TimeUnit.SECONDS)));
            assertEquals(36, ids.size());
            assertEquals(36, ledger.list().size());
        } finally { executor.shutdownNow(); }
    }

    @Test void duplicateOwnerCannotReserveAnotherId() {
        Lease row = reserve(ledger, "element", Set.of(115));
        assertThrows(MpRuntimeException.class, () -> reserve(ledger, "element", Set.of(115)));
        assertEquals(row.getVmid(), ledger.byOwner(row.getOwner()).getVmid());
    }

    @Test void ambiguousCloneCannotBeRetiredJustBecauseItIsNotYetVisible() {
        Lease row = reserve(ledger, "element", Set.of(115));
        ledger.advance(row, "CLONE_IN_FLIGHT", null);
        ledger.beginDelete(row);
        assertFalse(ledger.deleted(row));
        assertEquals(DELETING, ledger.byId(row.getVmid()).getStatus());
    }

    @Test void noSubmissionRetirementCannotRaceWithClone() {
        Lease row = reserve(ledger, "element", Set.of(115));
        long cutoff = System.currentTimeMillis() + 10000;
        ledger.advance(row, "CLONE_IN_FLIGHT", null);
        assertFalse(ledger.retireUnsubmitted(row, cutoff));
        Lease unsubmitted = reserve(ledger, "second", Set.of(115));
        assertTrue(ledger.retireUnsubmitted(unsubmitted, cutoff));
        assertNull(ledger.byOwner(unsubmitted.getOwner()));
    }

    @Test void concurrentDeleteClaimsDoNotRepeatMutationsOrOverwriteLaterStages() {
        Lease row = reserve(ledger, "element", Set.of(115));
        ledger.observed(row, "node");
        ledger.beginDelete(row);
        Set<String> stopBlocked = Set.of("STOP_IN_FLIGHT", "DELETE_IN_FLIGHT");
        assertTrue(ledger.claimDeleteAction(row, "STOP_IN_FLIGHT", stopBlocked));
        assertFalse(ledger.claimDeleteAction(row, "STOP_IN_FLIGHT", stopBlocked));
        assertTrue(ledger.claimDeleteAction(row, "DELETE_IN_FLIGHT", Set.of("DELETE_IN_FLIGHT")));
        ledger.deleteStage(row, "STOP_IN_FLIGHT", "STOP_REQUESTED", "late-stop-task");
        assertEquals("DELETE_IN_FLIGHT", ledger.byId(row.getVmid()).getStage());
        assertFalse(ledger.claimDeleteAction(row, "DELETE_IN_FLIGHT", Set.of("DELETE_IN_FLIGHT")));
    }

    @Test void lateAddressOnlyUpdatesExactMatchingActiveSceneElement() {
        scene("element", "");
        Lease row = reserve(ledger, "element", Set.of(115));
        ledger.pending(row, "START_REQUESTED");
        assertTrue(ledger.lateReady(row, "198.51.100.10", "02:00:00:00:00:01", "", ""));
        ledger.syncRuntimePort(row, "198.51.100.10", "02:00:00:00:00:01", "", "");
        assertEquals(String.valueOf(row.getVmid()), jdbc.queryForObject("SELECT resource_id FROM mp_sceneplugincloud_scene_element", String.class));
        assertEquals("198.51.100.10", jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port", String.class));
        jdbc.update("UPDATE mp_sceneplugincloud_scene_element SET resource_id='other'");
        ledger.syncRuntimePort(row, "198.51.100.11", "02:00:00:00:00:02", "", "");
        assertEquals("198.51.100.10", jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port", String.class));
        jdbc.update("UPDATE mp_sceneplugincloud_scene_element SET resource_id=?", String.valueOf(row.getVmid()));
        jdbc.update("UPDATE mp_scene_scene SET deleted=1");
        ledger.syncRuntimePort(row, "198.51.100.12", "02:00:00:00:00:03", "", "");
        assertEquals("198.51.100.10", jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port", String.class));
    }
}
