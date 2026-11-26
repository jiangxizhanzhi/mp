package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static cn.cyberpeace.mp.service.VendorSecondAddressPoolTest.response;
import static cn.cyberpeace.mp.service.VendorThirdVmLedger.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VendorThirdCloudServiceImplTest {
    private static final String NETWORK = "36eca7b1-5fe7-48a5-be8e-19f1a01017d2";
    private static final String IP = "198.51.100.2", MAC = "02:00:00:00:00:02";
    private static final String NODE = "test-node";
    private final List<String> events = new ArrayList<>();
    private VendorThirdVmLedgerTest db;
    private VendorThirdVmLedger ledger;
    private VendorThirdCloudServiceImpl service;
    private MultiCloudClient clients;
    private XdAppConfig config;
    private int id, lockReads, missingReads, startReads, networkReads, addressReads, collisions;
    private String name, node = NODE, state = "stopped";
    private boolean configured, exists, lostClone, lostStart, lostNetwork, lostDelete, rejectNetwork, rejectClone, rejectStart,
            rejectDelete, noAddress, neverUnlock, noInventory, wrongName, noDigest, holdStart, holdDelete;
    private JSONObject request, networkRequest;
    private long wait = 200;

    @BeforeEach void setup() throws Exception {
        db = new VendorThirdVmLedgerTest();
        db.setup();
        ledger = db.ledger;
        config = new XdAppConfig();
        config.setXdThreeNetworkId(NETWORK);
        clients = mock(MultiCloudClient.class);
        HttpClient client = mock(HttpClient.class);
        when(clients.getClient(3)).thenReturn(client);
        when(client.get(anyString(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("GET", inv.getArgument(0), null));
        when(client.post(anyString(), any(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("POST", inv.getArgument(0), inv.getArgument(1)));
        when(client.put(anyString(), any(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("PUT", inv.getArgument(0), inv.getArgument(1)));
        when(client.delete(anyString(), any(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("DELETE", inv.getArgument(0), inv.getArgument(1)));
        service = newService(ledger);
    }

    private VendorThirdCloudServiceImpl newService(VendorThirdVmLedger use) {
        return new VendorThirdCloudServiceImpl(clients, config, use) {
            @Override public Map<String, String> getHeaders() { return Collections.emptyMap(); }
            @Override protected long cloneTimeoutMillis() { return wait; }
            @Override protected long networkTimeoutMillis() { return wait; }
            @Override protected long readyTimeoutMillis() { return wait; }
            @Override protected long deleteTimeoutMillis() { return wait; }
            @Override protected long pollMillis() { return 2; }
        };
    }

    private ManagedVmCreationResult create() {
        return service.createManagedVm("115", "scene", "element", "port-element", "198.51.100.99");
    }

    private Lease row() { return ledger.byId(id); }
    private long count(String e) { return events.stream().filter(e::equals).count(); }

    @Test void verifiesCloneNetworkRunningAndActualAddressNotTemplate() {
        missingReads = 2;
        lockReads = 2;
        networkReads = 2;
        startReads = 2;
        addressReads = 2;
        wait = 1000;
        ManagedVmCreationResult result = create();
        assertTrue(result.isReady());
        assertEquals("9001", result.getResourceId());
        assertEquals(IP, result.getActualIp());
        assertEquals(MAC, result.getMacAddress());
        assertEquals("255.255.255.0", result.getNetmask());
        assertEquals(READY, row().getStatus());
        assertEquals("start-UPID", row().getTaskId());
        assertEquals("initial-digest", networkRequest.getString("digest"));
        assertEquals("virtio,network=" + NETWORK, networkRequest.getString("vnet0"));
        assertEquals(NODE, request.getString("target"));
        assertTrue(request.getString("name").startsWith("MP_CREATE_"));
        assertTrue(events.indexOf("network") > events.lastIndexOf("locked"));
        assertTrue(events.indexOf("start") > events.indexOf("network"));
        assertEquals(1, count("clone"));
        assertEquals(1, count("start"));
    }

    @Test void lostResponsesContinueOnSameIdentityWhenActualStateConfirms() {
        lostClone = lostNetwork = lostStart = true;
        assertTrue(create().isReady());
        assertEquals(1, count("clone"));
        assertEquals(1, count("network"));
        assertEquals(1, count("start"));
    }

    @Test void lockedCloneDoesNotProceedButRetryResumesSameReservation() {
        neverUnlock = true;
        ManagedVmCreationResult pending = create();
        assertFalse(pending.isReady());
        assertEquals(String.valueOf(id), pending.getResourceId());
        assertEquals(PENDING, row().getStatus());
        assertEquals(0, count("network"));
        neverUnlock = false;
        service = newService(new VendorThirdVmLedger(db.jdbc, db.tx));
        assertTrue(create().isReady());
        assertEquals(1, count("clone"));
    }

    @Test void explicitIdentifierConflictAllocatesNewIdAndDelivers() {
        collisions = 1;
        assertTrue(create().isReady());
        assertEquals(9002, id);
        assertEquals(REJECTED, ledger.byId(9001).getStatus());
        assertEquals(2, count("clone"));
    }

    @Test void businessRejectionWithHttp200DoesNotBecomeFalseSuccess() {
        rejectClone = true;
        assertThrows(MpRuntimeException.class, this::create);
        assertEquals(REJECTED, row().getStatus());
        assertNull(ledger.byOwner(ownerKey("scene", "element")));
        assertEquals(0, count("start"));
    }

    @Test void unavailableInventoryNeverSubmitsClone() {
        noInventory = true;
        assertThrows(MpRuntimeException.class, this::create);
        assertEquals(0, count("clone"));
        assertTrue(ledger.list().isEmpty());
    }

    @Test void truncatedOrBusinessFailedInventoryIsNotAuthoritative() {
        assertThrows(MpRuntimeException.class, () -> VendorThirdInventory.parse(
                response(200, "{\"success\":1,\"total\":2,\"data\":[]}")));
        assertThrows(MpRuntimeException.class, () -> VendorThirdInventory.parse(
                response(200, "{\"success\":0,\"data\":[]}")));
        assertThrows(MpRuntimeException.class, () -> VendorThirdInventory.parse(
                response(200, "{\"success\":1,\"data\":[{\"type\":\"qemu\"}]}")));
    }

    @Test void networkRejectionCannotLeadToStartingOnWrongNetwork() {
        rejectNetwork = true;
        assertFalse(create().isReady());
        assertEquals(PENDING, row().getStatus());
        assertEquals(3, count("network"));
        assertEquals(0, count("start"));
    }

    @Test void noDigestCannotLeadToNetworkWrite() {
        noDigest = true;
        assertFalse(create().isReady());
        assertEquals(0, count("network"));
        assertEquals(0, count("start"));
    }

    @Test void retryExplicitStartRejectionAndThenDeliver() {
        rejectStart = true;
        assertTrue(create().isReady());
        assertEquals(2, count("start"));
    }

    @Test void acceptedStartNotRepeatedWhenProviderRemainsStopped() {
        holdStart = true;
        assertFalse(create().isReady());
        assertFalse(create().isReady());
        assertEquals(1, count("start"));
        assertEquals(1, count("clone"));
    }

    @Test void runningWithoutAddressRemainsPendingAndReconciliationReadsBackLater() {
        db.scene("element", "");
        noAddress = true;
        ManagedVmCreationResult pending = create();
        assertFalse(pending.isReady());
        assertTrue(pending.isRunningConfirmed());
        assertNull(pending.getActualIp());
        assertEquals(PENDING, row().getStatus());
        noAddress = false;
        service.reconcileVmLedger();
        assertEquals(READY, row().getStatus());
        assertEquals(IP, db.jdbc.queryForObject("SELECT ip FROM mp_scene_scene_element_port", String.class));
        assertEquals(String.valueOf(id), db.jdbc.queryForObject("SELECT resource_id FROM mp_sceneplugincloud_scene_element", String.class));
        assertTrue(create().isReady());
        assertEquals(1, count("clone"));
        assertEquals(1, count("start"));
    }

    @Test void deleteUsesActualNodeAndConfirmsAbsenceEvenWhenResponseLost() {
        assertTrue(create().isReady());
        node = "migrated-node";
        lostDelete = true;
        service.deleteManagedVm("", "scene", "element");
        assertFalse(exists);
        assertEquals(DELETED, row().getStatus());
        assertNull(ledger.byOwner(ownerKey("scene", "element")));
        assertEquals(1, count("stop"));
        assertEquals(1, count("delete"));
        service.deleteManagedVm(String.valueOf(id), "scene", "element");
        assertEquals(1, count("delete"));
    }

    @Test void deleteBusinessRejectionRetainsAssociationUntilConfirmedGone() {
        assertTrue(create().isReady());
        rejectDelete = true;
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm(String.valueOf(id), "scene", "element"));
        assertEquals(DELETING, row().getStatus());
        assertEquals(3, count("delete"));
        assertTrue(exists);
        rejectDelete = false;
        service.deleteManagedVm(String.valueOf(id), "scene", "element");
        assertEquals(DELETED, row().getStatus());
    }

    @Test void acceptedDeleteNotRepeatedAndLateAbsenceIsReconciled() {
        assertTrue(create().isReady());
        holdDelete = true;
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm(String.valueOf(id), "scene", "element"));
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm(String.valueOf(id), "scene", "element"));
        assertEquals(1, count("delete"));
        exists = false;
        service.reconcileVmLedger();
        assertEquals(DELETED, row().getStatus());
    }

    @Test void mismatchingIdentityIsNeverStoppedOrDeleted() {
        assertTrue(create().isReady());
        wrongName = true;
        assertThrows(MpRuntimeException.class, () -> service.validateManagedVm(String.valueOf(id), "scene", "element"));
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm(String.valueOf(id), "scene", "element"));
        assertEquals(0, count("stop"));
        assertEquals(0, count("delete"));
    }

    @Test void historicalMissingVmIsIdempotentButExistingUnownedVmIsNotDeleted() {
        service.deleteManagedVm("8888", "old-scene", "old-element");
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm("9000", "old-scene", "old-element"));
        assertEquals(0, count("delete"));
    }

    @Test void incompleteInventoryCannotConfirmAbsence() {
        assertTrue(create().isReady());
        noInventory = true;
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm(String.valueOf(id), "scene", "element"));
        assertEquals(DELETING, row().getStatus());
        assertEquals(0, count("delete"));
    }

    private JSONObject vm(int vmid, String vmName, int template, String vmNode, String status) {
        JSONObject vm = new JSONObject();
        vm.put("vmid", vmid);
        vm.put("type", "qemu");
        vm.put("name", vmName);
        vm.put("template", template);
        vm.put("node", vmNode);
        vm.put("status", status);
        return vm;
    }

    private HttpResult ok(Object data) {
        JSONObject json = new JSONObject();
        json.put("success", 1);
        json.put("data", data);
        return response(200, json.toJSONString());
    }

    private HttpResult failed(String message) {
        JSONObject json = new JSONObject();
        json.put("success", 0);
        json.put("message", message);
        return response(200, json.toJSONString());
    }

    private HttpResult uncertain() { return response(503, "{\"message\":\"fixture lost response\"}"); }

    private HttpResult respond(String method, String path, HttpData data) {
        if (path.startsWith("/api2/json/cluster/resources/")) {
            if (noInventory) return uncertain();
            JSONArray inventory = new JSONArray();
            inventory.add(vm(115, "template", 1, NODE, "stopped"));
            inventory.add(vm(9000, "unrelated", 0, NODE, "running"));
            if (exists && missingReads-- <= 0) {
                if (startReads > 0 && count("start") > 0 && !holdStart && --startReads == 0) state = "running";
                JSONObject vm = vm(id, wrongName ? "foreign" : name, 0, node, state);
                if (neverUnlock || lockReads-- > 0) {
                    vm.put("lock", "clone");
                    events.add("locked");
                }
                inventory.add(vm);
            }
            return ok(inventory);
        }
        if (path.equals("/api2/json/nodes/" + NODE + "/qemu/115/clone")) {
            events.add("clone");
            request = JSONObject.parseObject(data.toContent());
            id = request.getIntValue("newid");
            name = request.getString("name");
            if (collisions-- > 0) return failed("VMID already exists");
            if (rejectClone) return failed("fixture rejected");
            exists = true;
            return lostClone ? uncertain() : ok("clone-UPID");
        }
        String prefix = "/api2/json/nodes/" + node + "/qemu/" + id;
        if (path.equals(prefix + "/config") && method.equals("GET")) {
            JSONObject info = new JSONObject();
            info.put("name", name);
            if (!noDigest) info.put("digest", "initial-digest");
            boolean verifiedNetwork = configured && networkReads-- <= 0;
            boolean address = state.equals("running") && !noAddress && addressReads-- <= 0;
            info.put("vnet0", "virtio=" + MAC + (address ? ",ip=" + IP + "/24,gw=198.51.100.1" : "")
                    + ",network=" + (verifiedNetwork ? NETWORK : "wrong-network"));
            return ok(info);
        }
        if (path.equals(prefix + "/config") && method.equals("PUT")) {
            events.add("network");
            networkRequest = JSONObject.parseObject(data.toContent());
            if (rejectNetwork) return failed("fixture config rejected");
            configured = true;
            return lostNetwork ? uncertain() : ok(null);
        }
        if (path.equals(prefix + "/status/start")) {
            events.add("start");
            if (rejectStart) { rejectStart = false; return failed("fixture start rejected"); }
            if (startReads == 0 && !holdStart) state = "running";
            return lostStart ? uncertain() : ok("start-UPID");
        }
        if (path.equals(prefix + "/status/stop")) {
            events.add("stop");
            state = "stopped";
            return ok("stop-UPID");
        }
        if (path.equals("/api2/extjs/nodes/" + node + "/qemu/" + id + "/") && method.equals("DELETE")) {
            events.add("delete");
            if (rejectDelete) return failed("fixture delete rejected");
            if (!holdDelete) exists = false;
            return lostDelete ? uncertain() : ok("delete-UPID");
        }
        throw new AssertionError("Unexpected " + method + " " + path);
    }
}
