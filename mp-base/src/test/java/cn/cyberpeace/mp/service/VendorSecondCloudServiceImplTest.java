package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.cyberpeace.mp.service.VendorSecondAddressPoolTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VendorSecondCloudServiceImplTest {
    private static final String A = "198.51.100.10", B = "198.51.100.11";
    private static final String SERVERS = "/janus/20180725/servers";
    private HttpClient client;
    private VendorSecondAddressPool pool;
    private VendorSecondCloudServiceImpl service;
    private List<JSONObject> requests;
    private AtomicInteger reservations;
    private long confirmMillis;

    @BeforeEach void setup() {
        XdAppConfig config = new XdAppConfig();
        config.setXdTwoIpPoolStart(A);
        config.setXdTwoIpPoolEnd(B);
        config.setXdTwoIpGateway("198.51.100.1");
        config.setXdTwoDeviceId("fixture-network");
        config.setXdTwoPortId("fixture-port");
        MultiCloudClient multi = mock(MultiCloudClient.class);
        client = mock(HttpClient.class);
        pool = mock(VendorSecondAddressPool.class);
        when(multi.getClient(2)).thenReturn(client);
        service = new VendorSecondCloudServiceImpl(multi, config, pool) {
            @Override public Map<String, String> getHeaders() { return Collections.emptyMap(); }
            @Override protected long createConfirmTimeoutMillis() { return confirmMillis; }
            @Override protected long createConfirmPollMillis() { return 1; }
            @Override protected long powerTimeoutMillis() { return confirmMillis; }
            @Override protected long powerPollIntervalMillis() { return 1; }
            @Override protected long allocationRecoveryTimeoutMillis() { return confirmMillis; }
            @Override protected long recycleStopTimeoutMillis() { return confirmMillis; }
            @Override protected long recycleDeleteTimeoutMillis() { return confirmMillis; }
            @Override protected long recoveryPollMillis() { return 1; }
        };
        confirmMillis = 0;
        requests = new ArrayList<>();
        reservations = new AtomicInteger();
        when(pool.reserve(any(), any(), any(), any(), any(), anyString())).thenAnswer(inv -> {
            String address = reservations.getAndIncrement() == 0 ? A : B;
            return new VendorSecondAddressPool.Lease(address, "RESERVED", inv.getArgument(4), inv.getArgument(0), inv.getArgument(1),
                    inv.getArgument(2), inv.getArgument(5), null, null, "CREATE_IN_FLIGHT", false, Timestamp.from(Instant.now()));
        });
        when(client.get(eq("/janus/20180725/images"), isNull(), anyMap(), eq(30000))).thenReturn(response(200,
                "{\"code\":0,\"data\":{\"data\":[{\"id\":\"image\",\"azs\":[{\"id\":\"fixture\"}],\"disks\":[{\"id\":\"disk\",\"storage_id\":\"fixture\",\"preallocate\":\"off\",\"size_mb\":1}]}]}}"));
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            if (requests.isEmpty()) return listResponse(0);
            JSONObject last = requests.get(requests.size()-1);
            return listResponse(1, runningVm("created", last.getString("name"), payloadIp(last)));
        });
    }

    private JSONObject runningVm(String id, String name, String ip) {
        JSONObject vm = vm(id, name, ip);
        vm.put("status", "running");
        vm.put("power_state", "on");
        vm.put("is_stopped", 0);
        return vm;
    }

    private String payloadIp(JSONObject request) {
        return request.getJSONArray("networks").getJSONObject(0).getJSONObject("ip_info").getString("ip_address");
    }

    private void capture(HttpData body) {
        requests.add(JSONObject.parseObject(body.toContent()));
    }

    private HttpResult accepted() {
        return response(200, "{\"code\":0,\"data\":{\"uuids\":[\"created\"],\"task_id\":\"task\"}}");
    }

    @Test void imageLookupChecksAllVisibleImagesBeforeAllocating() {
        when(client.get(eq("/janus/20180725/images"), isNull(), anyMap(), eq(30000))).thenReturn(response(200,
                "{\"code\":0,\"data\":{\"data\":[{\"id\":\"other\"},{\"id\":\"image\",\"azs\":[{\"id\":\"fixture\"}],\"disks\":[{\"id\":\"disk\",\"storage_id\":\"fixture\",\"preallocate\":\"off\",\"size_mb\":1}]}]}}"));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A, "02:00:00:00:00:01"});
        assertEquals("created", service.createManagedVm("image", "scene", "element", "port", A).getResourceId());
        assertEquals("image", requests.get(0).getString("image_id"));
    }

    @Test void usesReservedAddressNotTemplateOrRandomIp() {
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A,"02:00:00:00:00:01"});
        ManagedVmCreationResult result = service.createManagedVm("image", "scene", "element", "port", "198.51.100.2");
        assertEquals("created", result.getResourceId());
        assertTrue(result.isReady());
        assertEquals("task", result.getTaskId());
        assertEquals(A, result.getActualIp());
        assertEquals(1, requests.size());
        assertEquals(A, payloadIp(requests.get(0)));
        JSONObject ipInfo = requests.get(0).getJSONArray("networks").getJSONObject(0).getJSONObject("ip_info");
        assertEquals("255.255.255.0", ipInfo.getString("netmask"));
        assertEquals("198.51.100.1", ipInfo.getString("gateway"));
        verify(pool).recordCreated(any(), eq("created"));
    }

    @Test void explicitConflictCanRetryButWithDifferentReservation() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0), listResponse(0),
                listResponse(1, runningVm("created", "fixture", B)));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return requests.size() == 1 ? response(409,"{\"code\":-1,\"message\":\"IP地址已占用\"}") : accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{B, "02:00:00:00:00:01"});
        assertEquals(B, service.createManagedVm("image", "scene", "element", "port", A).getActualIp());
        assertEquals(2, requests.size());
        assertEquals(A, payloadIp(requests.get(0)));
        assertEquals(B, payloadIp(requests.get(1)));
        assertNotEquals(requests.get(0).getString("name"), requests.get(1).getString("name"));
        verify(pool).rejectBeforeCreation(any(), eq(true));
    }

    @Test void ambiguousTimeoutDoesNotRetry() {
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return new HttpResult(null, null, null, new java.net.SocketTimeoutException("fixture"));
        });
        assertThrows(MpRuntimeException.class, () -> service.createManagedVm("image", "scene", "element", "port", A));
        assertEquals(1, requests.size());
        verify(pool).uncertainCreation(any());
        verify(pool, never()).rejectBeforeCreation(any(), anyBoolean());
    }

    @Test void confirmedBadRequestReturnsAddressButDoesNotRetry() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return response(400, "{\"code\":-1,\"message\":\"invalid fixture request\"}");
        });
        assertThrows(MpRuntimeException.class, () -> service.createManagedVm("image", "scene", "element", "port", A));
        assertEquals(1, requests.size());
        verify(pool).rejectBeforeCreation(any(), eq(false));
    }

    @Test void acceptedResourceIdSurvivesUnverifiedAddress() {
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(null);
        ManagedVmCreationResult created = service.createManagedVm("image", "scene", "element", "port", A);
        assertEquals("created", created.getResourceId());
        assertNull(created.getActualIp());
        assertFalse(created.isReady());
        verify(pool).recordCreated(any(), eq("created"));
    }

    @Test void waitsForPowerAndAddressInTheSameObservation() {
        confirmMillis = 2000;
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(
                listResponse(0), listResponse(0), listResponse(1, vm("created", "fixture", A)),
                listResponse(1, runningVm("created", "fixture", A)), listResponse(1, runningVm("created", "fixture", A)));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenReturn(accepted());
        when(pool.confirm(any(), eq("created"), any())).thenReturn(null, new String[]{A, "02:00:00:00:00:01"},
                null, new String[]{A, "02:00:00:00:00:01"});
        ManagedVmCreationResult result = service.createManagedVm("image", "scene", "element", "port", A);
        assertTrue(result.isReady());
        assertEquals(A, result.getActualIp());
        verify(client, times(5)).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        verify(client, times(1)).post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000));
    }

    @Test void addressReadbackWithoutRunningDoesNotConfirmCreation() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0),
                listResponse(1, vm("created", "fixture", A)));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenReturn(accepted());
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A, "02:00:00:00:00:01"});
        ManagedVmCreationResult result = service.createManagedVm("image", "scene", "element", "port", A);
        assertEquals(A, result.getActualIp());
        assertFalse(result.isRunningConfirmed());
        assertFalse(result.isReady());
        assertEquals("created", result.getResourceId());
    }

    @Test void partialListCannotConfirmCreation() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0),
                listResponse(2, runningVm("created", "fixture", A)));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenReturn(accepted());
        when(pool.confirm(any(), eq("created"), any())).thenReturn(null);
        ManagedVmCreationResult result = service.createManagedVm("image", "scene", "element", "port", A);
        assertTrue(result.isRunningConfirmed());
        assertFalse(result.isReady());
    }

    @Test void followsNextPageForCreationConfirmation() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0),
                VendorSecondInventoryTest.page(0, 2, "1", runningVm("other", "fixture", B)));
        when(client.get(eq(SERVERS), notNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            cn.cyberpeace.mp.base.util.http.HttpParam param = inv.getArgument(1);
            assertEquals("page_num=1", param.toPathString());
            return VendorSecondInventoryTest.page(1, 2, "", runningVm("created", "fixture", A));
        });
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenReturn(accepted());
        when(pool.confirm(any(), eq("created"), any())).thenAnswer(inv -> {
            VendorSecondInventory inventory = inv.getArgument(2);
            assertTrue(inventory.complete);
            assertEquals(A, inventory.primaryAddress("created")[0]);
            return inventory.primaryAddress("created");
        });
        assertTrue(service.createManagedVm("image", "scene", "element", "port", A).isReady());
        verify(client).get(eq(SERVERS), notNull(), anyMap(), eq(30000));
    }

    @Test void exhaustedPoolReconcilesBeforeSubmittingCreate() {
        AtomicInteger attempts = new AtomicInteger();
        when(pool.reserve(any(), any(), any(), any(), any(), anyString())).thenAnswer(inv -> {
            if (attempts.incrementAndGet() == 1) throw new VendorSecondAddressPool.Exhausted("fixture shortage");
            return new VendorSecondAddressPool.Lease(A, "RESERVED", inv.getArgument(4), inv.getArgument(0),
                    inv.getArgument(1), inv.getArgument(2), inv.getArgument(5), null, null,
                    "CREATE_IN_FLIGHT", false, Timestamp.from(Instant.now()));
        });
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A, "02:00:00:00:00:01"});
        assertTrue(service.createManagedVm("image", "scene", "node", "port", A).isReady());
        verify(pool).recoverForAllocation(anyList(), any());
        assertEquals(2, attempts.get());
        assertEquals(1, requests.size());
    }

    @Test void exhaustionWithNoRecoverableLeaseDoesNotSubmitCreate() {
        when(pool.reserve(any(), any(), any(), any(), any(), anyString()))
                .thenThrow(new VendorSecondAddressPool.Exhausted("fixture shortage"));
        assertThrows(VendorSecondAddressPool.Exhausted.class,
                () -> service.createManagedVm("image", "scene", "node", "port", A));
        verify(pool).recoverForAllocation(anyList(), any());
        verify(client, never()).post(eq(SERVERS), any(), any(), anyMap(), anyInt());
    }

    @Test void waitsForAnotherRecoveryRoundWhenAnAddressIsStillBeingReleased() {
        confirmMillis = 2000;
        AtomicInteger attempts = new AtomicInteger();
        when(pool.hasReleasingAddressInPool()).thenReturn(true);
        when(pool.reserve(any(), any(), any(), any(), any(), anyString())).thenAnswer(inv -> {
            if (attempts.incrementAndGet() <= 2) {
                throw new VendorSecondAddressPool.Exhausted("fixture release pending");
            }
            return new VendorSecondAddressPool.Lease(A, "RESERVED", inv.getArgument(4), inv.getArgument(0),
                    inv.getArgument(1), inv.getArgument(2), inv.getArgument(5), null, null,
                    "CREATE_IN_FLIGHT", false, Timestamp.from(Instant.now()));
        });
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A, "02:00:00:00:00:01"});
        assertTrue(service.createManagedVm("image", "scene", "node", "port", A).isReady());
        verify(pool, times(2)).recoverForAllocation(anyList(), any());
        assertEquals(3, attempts.get());
        assertEquals(1, requests.size());
    }

    @Test void shortageCanUseEligibleProvisionalCandidateAfterNormalRecovery() {
        when(pool.reserve(any(), any(), any(), any(), any(), anyString()))
                .thenThrow(new VendorSecondAddressPool.Exhausted("fixture shortage"));
        when(pool.reserveOnDemand(any(), any(), any(), any(), any(), anyString(), any(), anySet())).thenAnswer(inv ->
                new VendorSecondAddressPool.Lease(A, "RESERVED", inv.getArgument(4), inv.getArgument(0),
                        inv.getArgument(1), inv.getArgument(2), inv.getArgument(5), null, null,
                        "CREATE_IN_FLIGHT_ON_DEMAND", false, Timestamp.from(Instant.now())));
        when(client.post(eq(SERVERS), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
            capture(inv.getArgument(1));
            return accepted();
        });
        when(pool.confirm(any(), eq("created"), any())).thenReturn(new String[]{A, "02:00:00:00:00:01"});
        assertTrue(service.createManagedVm("image", "scene", "node", "port", A).isReady());
        verify(pool).recoverForAllocation(anyList(), any());
        assertEquals(A, payloadIp(requests.get(0)));
    }

    @Test void duplicateOwnerIsNotRetriedAsAddressShortage() {
        when(pool.reserve(any(), any(), any(), any(), any(), anyString()))
                .thenThrow(new MpRuntimeException("fixture duplicate owner"));
        assertThrows(MpRuntimeException.class, () -> service.createManagedVm("image", "scene", "node", "port", A));
        verify(pool, never()).recoverForAllocation(anyList(), any());
        verify(client, never()).post(eq(SERVERS), any(), any(), anyMap(), anyInt());
    }

    @Test void coldRestartWaitsForShutdownAndDoesNotReleaseLease() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(
                listResponse(1, runningVm("existing", "fixture", A)),
                listResponse(1, vm("existing", "fixture", A)),
                listResponse(1, vm("existing", "fixture", A)),
                listResponse(1, runningVm("existing", "fixture", A)));
        when(client.post(eq(SERVERS + "/existing/stop"), isNull(), isNull(), anyMap(), eq(30000))).thenReturn(response(201, "{\"code\":0}"));
        when(client.post(eq(SERVERS + "/existing/start"), isNull(), isNull(), anyMap(), eq(30000))).thenReturn(response(201, "{\"code\":0}"));
        service.restartVm("existing");
        InOrder order = inOrder(client);
        order.verify(client).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        order.verify(client).post(eq(SERVERS + "/existing/stop"), isNull(), isNull(), anyMap(), eq(30000));
        order.verify(client, times(2)).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        order.verify(client).post(eq(SERVERS + "/existing/start"), isNull(), isNull(), anyMap(), eq(30000));
        order.verify(client).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        verifyNoInteractions(pool);
    }

    @Test void unconfirmedShutdownPreventsRestartAtTimeout() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(1, runningVm("existing", "fixture", A)));
        when(client.post(eq(SERVERS + "/existing/stop"), isNull(), isNull(), anyMap(), eq(30000))).thenReturn(response(201, "{\"code\":0}"));
        assertThrows(MpRuntimeException.class, () -> service.restartVm("existing"));
        verify(client, never()).post(eq(SERVERS + "/existing/start"), any(), any(), anyMap(), anyInt());
        verifyNoInteractions(pool);
    }

    @Test void providerBusinessFailurePreventsRestart() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(1, runningVm("existing", "fixture", A)));
        when(client.post(eq(SERVERS + "/existing/stop"), isNull(), isNull(), anyMap(), eq(30000))).thenReturn(response(200, "{\"code\":-1}"));
        assertThrows(MpRuntimeException.class, () -> service.restartVm("existing"));
        verify(client, never()).post(eq(SERVERS + "/existing/start"), any(), any(), anyMap(), anyInt());
    }

    @Test void deleteChecksPresenceAndDoesNotTreatMissingUuidAsSuccess() {
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(""));
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(1, vm("existing", "fixture", A)), listResponse(0));
        when(client.delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000))).thenReturn(response(200,"{\"code\":0}"));
        when(pool.reconcileDeleted(eq("existing"), any(), anyList())).thenAnswer(inv -> {
            VendorSecondInventory snapshot = inv.getArgument(1);
            return snapshot.complete && !snapshot.vms.containsKey("existing");
        });
        assertDoesNotThrow(() -> service.deleteVm("existing"));
        verify(pool).beginDelete(eq("existing"), any());
        verify(pool).deleteSubmitted("existing", true);
        verify(pool).reconcileDeleted(eq("existing"), any(), anyList());
        verify(client, never()).post(eq(SERVERS + "/existing/stop"), any(), any(), anyMap(), anyInt());
    }

    @Test void recycleWaitsForStopAndDeletionWithoutFixedDelay() {
        confirmMillis = 2000;
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(
                listResponse(1, runningVm("existing", "fixture", A)),
                listResponse(1, runningVm("existing", "fixture", A)),
                listResponse(1, vm("existing", "fixture", A)),
                listResponse(1, vm("existing", "fixture", A)),
                listResponse(0));
        when(client.post(eq(SERVERS + "/existing/stop"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(201, "{\"code\":0}"));
        when(client.delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"code\":0}"));
        when(pool.reconcileDeleted(eq("existing"), any(), anyList())).thenAnswer(inv -> {
            VendorSecondInventory inventory = inv.getArgument(1);
            return inventory.complete && !inventory.vms.containsKey("existing");
        });
        service.deleteVm("existing");
        InOrder order = inOrder(client);
        order.verify(client).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        order.verify(client).post(eq(SERVERS + "/existing/stop"), any(), isNull(), anyMap(), eq(30000));
        order.verify(client, times(2)).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        order.verify(client).delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000));
        order.verify(client, times(2)).get(eq(SERVERS), isNull(), anyMap(), eq(30000));
        verify(pool).deleteSubmitted("existing", true);
    }

    @Test void rejectedStopDoesNotContinueToDeleteRunningVm() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000)))
                .thenReturn(listResponse(1, runningVm("existing", "fixture", A)));
        when(client.post(eq(SERVERS + "/existing/stop"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(401, "{\"code\":401}"));
        MpRuntimeException error = assertThrows(MpRuntimeException.class, () -> service.deleteVm("existing"));
        assertTrue(error.getMessage().contains("HTTP=401"));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
        verify(pool).deleteSubmitted("existing", false);
    }

    @Test void absentVmIsNotSentEmptyOrRedundantStopOrDelete() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0));
        when(pool.reconcileDeleted(eq("existing"), any(), anyList())).thenReturn(true);
        service.deleteVm("existing");
        verify(client, never()).post(anyString(), any(), any(), anyMap(), anyInt());
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }

    @Test void deletion404OnlySucceedsIfAbsenceConfirmed() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(
                listResponse(1, vm("existing", "fixture", A)), listResponse(0));
        when(client.delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(404, "{\"code\":-1}"));
        when(pool.reconcileDeleted(eq("existing"), any(), anyList())).thenAnswer(inv -> {
            VendorSecondInventory inventory = inv.getArgument(1);
            return inventory.complete && !inventory.vms.containsKey("existing");
        });
        service.deleteVm("existing");
        verify(pool).deleteSubmitted("existing", false);
    }

    @Test void deletion402CannotBeReportedAsSuccessWhileResourceExists() {
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(1, vm("existing", "fixture", A)));
        when(client.delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(402, "{\"code\":-1}"));
        assertTrue(assertThrows(MpRuntimeException.class, () -> service.deleteVm("existing"))
                .getMessage().contains("HTTP=402"));
        verify(pool).deleteSubmitted("existing", false);
    }

    @Test void managedDeletionResolvesOnlyRecordedResourceInsteadOfSubmittingBlankUuid() {
        when(pool.resolveManagedVmForDelete(eq("scene"), eq("node"), any())).thenReturn("existing");
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(0));
        when(pool.reconcileDeleted(eq("existing"), any(), anyList())).thenReturn(true);
        service.deleteManagedVm("", "scene", "node");
        verify(pool).resolveManagedVmForDelete(eq("scene"), eq("node"), any());
        verify(pool).beginDelete(eq("existing"), any());
        verify(client, never()).post(anyString(), any(), any(), anyMap(), anyInt());
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }
}
