package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorVmReclaimServiceTest {
    private static final String NAME = "MP_0123456789abcdef0123456789abcdef";
    private static final Timestamp OLD = new Timestamp(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(3));
    private JdbcTemplate jdbc;
    private DataSourceTransactionManager tx;
    private XdAppConfig config;
    private List<MultiCloudService> providers;
    private VendorVmReclaimService service;

    @BeforeEach void setup() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        tx = new DataSourceTransactionManager(source);
        for (String migration : List.of("V202611260900__vendor_second_address_pool.sql",
                "V202611261000__vendor_third_vm_ledger.sql", "V202611261100__vendor_vm_reclaim.sql")) {
            String ddl = Files.readString(Paths.get("../mp/src/main/resources/db/migration", migration));
            for (String statement : ddl.split(";")) if (!statement.trim().isEmpty()) jdbc.execute(statement);
        }
        jdbc.execute("CREATE TABLE mp_scene_scene(id VARCHAR(64) PRIMARY KEY,deleted INT,event INT,event_status INT,update_time TIMESTAMP)");
        jdbc.execute("CREATE TABLE mp_scene_scene_element(id VARCHAR(64) PRIMARY KEY,scene VARCHAR(64),node_id VARCHAR(64),vendor INT,event INT,event_status INT,update_time TIMESTAMP)");
        jdbc.execute("CREATE TABLE mp_sceneplugincloud_scene_element(scene VARCHAR(64),scene_element VARCHAR(64),resource_id VARCHAR(64),virtual_type INT,external INT)");
        jdbc.execute("CREATE TABLE mp_cscene_cscene_instance(scene VARCHAR(64),deleted INT)");
        config = new XdAppConfig();
        providers = new ArrayList<>();
        for (int vendor = 1; vendor <= 3; vendor++) {
            MultiCloudService p = mock(MultiCloudService.class);
            when(p.getVendor()).thenReturn(vendor);
            providers.add(p);
        }
        service = new VendorVmReclaimService(jdbc, tx, config, providers);
    }

    private MultiCloudService provider(int vendor) { return providers.get(vendor - 1); }

    private void seed(int vendor, String scene, String vm, boolean closed) {
        jdbc.update("INSERT INTO mp_scene_scene VALUES (?,?,?,?,?)", scene, closed ? 1 : 0, closed ? 9 : 2, 4, OLD);
        jdbc.update("INSERT INTO mp_scene_scene_element VALUES (?,?,?,?,?,?,?)", "element-" + scene, scene,
                "node-" + scene, vendor, closed ? 9 : 2, 4, OLD);
        jdbc.update("INSERT INTO mp_sceneplugincloud_scene_element VALUES (?,?,?,4,0)", scene, "node-" + scene, vm);
        jdbc.update("INSERT INTO mp_cscene_cscene_instance VALUES (?,?)", scene, closed ? 1 : 0);
    }

    private void visible(int vendor, String scene, String id, String name) {
        when(provider(vendor).readReclaimInventory()).thenReturn(Collections.singletonMap(id, name));
        when(provider(vendor).matchesReclaimIdentity(id, name, scene, "element-" + scene)).thenReturn(true);
    }

    private String state(int vendor, String id) {
        return jdbc.queryForObject("SELECT status FROM mp_vendor_vm_reclaim WHERE vendor=? AND vm_id=?", String.class, vendor, id);
    }

    @Test void allThreeProvidersReclaimOnlyConfirmedClosedRuntimeReferences() {
        for (int vendor = 1; vendor <= 3; vendor++) {
            String scene = "scene-" + vendor, id = "id-" + vendor;
            seed(vendor, scene, id, true);
            visible(vendor, scene, id, NAME);
            service.reclaim(vendor);
            verify(provider(vendor)).reclaimClosedVm(id, NAME, scene, "element-" + scene);
            assertEquals("CONFIRMED", state(vendor, id));
        }
    }

    @Test void activeSceneWithSameUuidBlocksHistoricalRecord() {
        seed(1, "closed", "same", true);
        seed(1, "active", "same", false);
        visible(1, "closed", "same", NAME);
        service.reclaim(1);
        verify(provider(1), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void unfinishedClosedTopologyAndRecentlyClosedNodesAreSkipped() {
        seed(2, "active-topology", "a", true);
        seed(2, "failed-node", "b", true);
        seed(2, "fresh", "c", true);
        seed(2, "external", "d", true);
        jdbc.update("UPDATE mp_cscene_cscene_instance SET deleted=0 WHERE scene='active-topology'");
        jdbc.update("UPDATE mp_scene_scene_element SET event_status=5 WHERE scene='failed-node'");
        jdbc.update("UPDATE mp_scene_scene SET update_time=CURRENT_TIMESTAMP WHERE id='fresh'");
        jdbc.update("UPDATE mp_sceneplugincloud_scene_element SET external=1 WHERE scene='external'");
        when(provider(2).readReclaimInventory()).thenReturn(Map.of("a", NAME, "b", NAME, "c", NAME, "d", NAME));
        service.reclaim(2);
        verify(provider(2), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void unknownProviderVmAndAmbiguousHistoricalReferencesAreNeverDeleted() {
        seed(3, "first", "9001", true);
        seed(3, "second", "9001", true);
        when(provider(3).readReclaimInventory()).thenReturn(Map.of("9001", NAME, "9002", "manual"));
        service.reclaim(3);
        verify(provider(3), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void duplicateRemoteNamesAndIdentityFailureAreSkipped() {
        seed(1, "closed", "a", true);
        when(provider(1).readReclaimInventory()).thenReturn(Map.of("a", NAME, "other", NAME));
        service.reclaim(1);
        when(provider(1).readReclaimInventory()).thenReturn(Map.of("a", NAME));
        when(provider(1).matchesReclaimIdentity("a", NAME, "closed", "element-closed")).thenReturn(false);
        service.reclaim(1);
        verify(provider(1), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void providerInventoryFailureCannotCauseDeletion() {
        seed(1, "closed", "a", true);
        when(provider(1).readReclaimInventory()).thenThrow(new MpRuntimeException("unavailable"));
        assertThrows(MpRuntimeException.class, () -> service.reclaim(1));
        verify(provider(1), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void rechecksSceneAfterClaimAndDoesNotDeleteReopenedScene() {
        seed(1, "closed", "a", true);
        visible(1, "closed", "a", NAME);
        when(provider(1).matchesReclaimIdentity("a", NAME, "closed", "element-closed")).thenAnswer(inv -> {
            jdbc.update("UPDATE mp_scene_scene SET deleted=0 WHERE id='closed'");
            return true;
        });
        service.reclaim(1);
        assertEquals("SKIPPED", state(1, "a"));
        verify(provider(1), never()).reclaimClosedVm(anyString(), anyString(), anyString(), anyString());
    }

    @Test void persistentClaimPreventsDuplicateConcurrentReclamation() throws Exception {
        seed(1, "closed", "a", true);
        visible(1, "closed", "a", NAME);
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        doAnswer(inv -> {
            entered.countDown();
            assertTrue(release.await(5, TimeUnit.SECONDS));
            return null;
        }).when(provider(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
        ExecutorService runner = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = runner.submit(() -> service.reclaim(1));
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            VendorVmReclaimService other = new VendorVmReclaimService(jdbc, tx, config, providers);
            runner.submit(() -> other.reclaim(1)).get(5, TimeUnit.SECONDS);
            release.countDown();
            first.get(5, TimeUnit.SECONDS);
            verify(provider(1), times(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
            assertEquals("CONFIRMED", state(1, "a"));
        } finally { release.countDown(); runner.shutdownNow(); }
    }

    @Test void failureRetainsAssociationAndLaterSuccessfulReclaimIsConfirmed() {
        seed(1, "closed", "a", true);
        visible(1, "closed", "a", NAME);
        doThrow(new MpRuntimeException("unknown result")).doNothing()
                .when(provider(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
        service.reclaim(1);
        assertEquals("VERIFY_PENDING", state(1, "a"));
        service.reclaim(1);
        verify(provider(1), times(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
        jdbc.update("UPDATE mp_vendor_vm_reclaim SET updated_at=?", OLD);
        service.reclaim(1);
        assertEquals("CONFIRMED", state(1, "a"));
        assertEquals(2, jdbc.queryForObject("SELECT attempts FROM mp_vendor_vm_reclaim", Integer.class));
    }

    @Test void lateAbsenceConfirmsPendingWithoutResubmittingDelete() {
        seed(1, "closed", "a", true);
        visible(1, "closed", "a", NAME);
        doThrow(new MpRuntimeException("unknown result"))
                .when(provider(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
        service.reclaim(1);
        assertEquals("VERIFY_PENDING", state(1, "a"));
        jdbc.update("UPDATE mp_vendor_vm_reclaim SET updated_at=?", OLD);
        when(provider(1).readReclaimInventory()).thenReturn(Collections.emptyMap());
        service.reclaim(1);
        assertEquals("CONFIRMED", state(1, "a"));
        verify(provider(1), times(1)).reclaimClosedVm("a", NAME, "closed", "element-closed");
    }

    @Test void persistedVendorIdentityCanRecoverBlankRuntimeId() {
        seed(3, "closed", "", true);
        jdbc.update("INSERT INTO mp_vendor_third_vm_ledger(vmid,scene_id,element_id,template_id,request_name,node_name,status,stage,observed,updated_at) "
                + "VALUES (9001,'closed','element-closed','115',?,'node','DELETED','DELETE_CONFIRMED',1,?)", NAME, OLD);
        visible(3, "closed", "9001", NAME);
        service.reclaim(3);
        verify(provider(3)).reclaimClosedVm("9001", NAME, "closed", "element-closed");
        assertEquals("CONFIRMED", state(3, "9001"));
    }

    @Test void disabledSettingMakesNoProviderCall() {
        config.setXdVendorReclaimEnabled(false);
        seed(1, "closed", "a", true);
        service.reclaim(1);
        verify(provider(1), never()).readReclaimInventory();
    }
}
