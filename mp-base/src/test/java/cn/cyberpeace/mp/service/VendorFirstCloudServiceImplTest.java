package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static cn.cyberpeace.mp.service.VendorSecondAddressPoolTest.response;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VendorFirstCloudServiceImplTest {
    private static final String VM = "fixture-id";
    private static final String IMAGE = "fixture-image";
    private static final String INSTANCES = "/api/cloud/instances/";
    private HttpClient client;
    private VendorFirstCloudServiceImpl service;
    private VendorFirstVmLedger ledger;
    private Object previousToken;

    @BeforeEach void setup() throws Exception {
        previousToken = ReflectionTestUtils.getField(VendorFirstCloudServiceImpl.class, "TOKEN");
        ReflectionTestUtils.setField(VendorFirstCloudServiceImpl.class, "TOKEN", "fixture-token");
        MultiCloudClient clients = mock(MultiCloudClient.class);
        client = mock(HttpClient.class);
        when(clients.getClient(1)).thenReturn(client);
        VendorFirstVmLedgerTest db = new VendorFirstVmLedgerTest();
        db.setup();
        ledger = db.ledger;
        service = new VendorFirstCloudServiceImpl(clients, ledger) {
            @Override protected long deleteConfirmTimeoutMillis() { return 0; }
        };
        when(client.get(startsWith("/api/cloud/images/"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"results\":[{\"uuid\":\"fixture-image\",\"status\":\"active\",\"image_type\":\"image\"}]}"));
    }

    @AfterEach void restoreToken() {
        ReflectionTestUtils.setField(VendorFirstCloudServiceImpl.class, "TOKEN", previousToken);
    }

    @Test void refreshesExplicit401WithoutChangingCreateRequest() {
        when(client.post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(401, "{}"), response(200, "{\"instances\":[\"fixture-id\"]}"));
        when(client.post(eq("/api/uc/auth/token"), any(), isNull(), isNull(), eq(30000)))
                .thenReturn(response(200, "{\"token\":\"new-token\"}"));
        assertEquals(VM, service.createVm(IMAGE));
        ArgumentCaptor<HttpData> requests = ArgumentCaptor.forClass(HttpData.class);
        verify(client, times(2)).post(startsWith(INSTANCES + "?"), requests.capture(), isNull(), anyMap(), eq(30000));
        assertEquals(requests.getAllValues().get(0).toContent(), requests.getAllValues().get(1).toContent());
        verify(client).post(startsWith(INSTANCES + "?"), any(), isNull(),
                argThat(h -> "Bearer new-token".equals(h.get("Authorization"))), eq(30000));
    }

    @Test void ambiguousCreateResponseDoesNotResubmit() {
        when(client.post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "not-json"));
        assertThrows(MpRuntimeException.class, () -> service.createVm(IMAGE));
        verify(client, times(1)).post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000));
    }

    @Test void emptyCreateIdentityIsRejected() {
        when(client.post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"instances\":[\"\"]}"));
        assertThrows(MpRuntimeException.class, () -> service.createVm(IMAGE));
    }

    @Test void missingImageDoesNotSendCreateRequest() {
        assertThrows(MpRuntimeException.class, () -> service.createVm("missing-image"));
        verify(client, never()).post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000));
    }

    @Test void acceptedAddressRequiresRunningAndValidIpMac() {
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, instance("BUILD", "198.51.100.2", "02:00:00:00:00:01")),
                        response(200, instance("ACTIVE", "invalid", "02:00:00:00:00:01")),
                        response(200, instance("ACTIVE", "198.51.100.2", "02:00:00:00:00:01")));
        assertNull(service.getVmIpAndMac(VM));
        assertNull(service.getVmIpAndMac(VM));
        assertArrayEquals(new String[]{"198.51.100.2", "02:00:00:00:00:01"}, service.getVmIpAndMac(VM));
    }

    @Test void readsLaterPageForExactInstance() {
        when(client.get(startsWith(INSTANCES + "?page=1&"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":2,\"results\":[{\"uuid\":\"other\",\"status\":\"ACTIVE\"}]}"));
        when(client.get(startsWith(INSTANCES + "?page=2&"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, instance("SHUTOFF", "198.51.100.2", "02:00:00:00:00:01")));
        assertEquals(VmPowerState.STOPPED, service.getVmPowerState(VM));
    }

    @Test void structuredInstanceNotFoundIsIdempotent() {
        when(client.delete(startsWith(INSTANCES + VM + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(404, "{\"code\":404,\"error\":\"NotFound\",\"message\":\"实例 fixture-id 没有找到\"}"));
        assertDoesNotThrow(() -> service.deleteVm(VM));
    }

    @Test void proxyNotFoundAndServerErrorDoNotBecomeSuccess() {
        when(client.delete(startsWith(INSTANCES + VM + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(404, "not found"), response(503, "{}"));
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(VM));
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(VM));
    }

    @Test void deleteAcceptanceRequiresResourceAbsence() {
        when(client.delete(startsWith(INSTANCES + VM + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"success\":true}"));
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, instance("SHUTOFF", "198.51.100.2", "02:00:00:00:00:01")),
                        response(200, "{\"count\":0,\"results\":[]}"));
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(VM));
        assertDoesNotThrow(() -> service.deleteVm(VM));
    }

    @Test void incompleteInventoryIsNotProofOfDeletion() {
        when(client.delete(startsWith(INSTANCES + VM + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"success\":true}"));
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":2,\"results\":[]}"));
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(VM));
    }

    @Test void blankIdNeverCallsProvider() {
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(""));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }

    @Test void legacyBlankSceneNodeCompletesWithoutSendingEmptyDelete() {
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":0,\"results\":[]}"));
        assertDoesNotThrow(() -> service.deleteManagedVm("", "old-scene", "old-element"));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }

    @Test void lostSceneCreateResponseUsesPersistedNameAndDoesNotCloneAgain() {
        when(client.post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "not-json"));
        assertThrows(MpRuntimeException.class, () -> service.createSceneVm(IMAGE, "scene", "element"));
        VendorFirstVmLedger.Lease row = ledger.byOwner("scene", "element");
        assertEquals("UNCERTAIN", row.getStatus());
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":0,\"results\":[]}"));
        assertThrows(MpRuntimeException.class, () -> service.createSceneVm(IMAGE, "scene", "element"));
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm("", "scene", "element"));
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":1,\"results\":[{\"uuid\":\"fixture-id\",\"name\":\"" + row.getName() + "\"}]}"));
        assertEquals(VM, service.createSceneVm(IMAGE, "scene", "element"));
        verify(client, times(1)).post(startsWith(INSTANCES + "?"), any(), isNull(), anyMap(), eq(30000));
        assertEquals(VM, ledger.byOwner("scene", "element").getUuid());
        when(client.delete(startsWith(INSTANCES + VM + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"success\":true}"));
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":0,\"results\":[]}"));
        service.deleteManagedVm("", "scene", "element");
        assertNull(ledger.byOwner("scene", "element"));
    }

    @Test void sceneDeleteCannotUseDifferentRecordedUuid() {
        VendorFirstVmLedger.Lease lease = ledger.reserve("scene", "element", IMAGE, "MP_fixture");
        ledger.created(lease, VM);
        assertThrows(MpRuntimeException.class, () -> service.deleteManagedVm("someone-else", "scene", "element"));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }

    @Test void fallbackClosesCreationLedgerWhenProviderVmIsAlreadyGone() {
        VendorFirstVmLedger.Lease lease = ledger.reserve("scene", "element", IMAGE, "MP_fixture");
        ledger.created(lease, VM);
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":0,\"results\":[]}"));
        assertTrue(service.reconcileAbsentVm(VM, "scene", "element"));
        assertNull(ledger.byOwner("scene", "element"));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
    }

    @Test void reclaimVerifiesCurrentIdentityBeforeDeleting() {
        String id = "11111111-2222-3333-4444-555555555555";
        String name = "MP_0123456789abcdef0123456789abcdef";
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":1,\"results\":[{\"uuid\":\"" + id + "\",\"name\":\"renamed\"}]}"));
        assertThrows(MpRuntimeException.class, () -> service.reclaimClosedVm(id, name, "scene", "node"));
        verify(client, never()).delete(anyString(), any(), any(), anyMap(), anyInt());
        when(client.get(startsWith(INSTANCES + "?"), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"count\":1,\"results\":[{\"uuid\":\"" + id + "\",\"name\":\"" + name + "\"}]}"),
                        response(200, "{\"count\":0,\"results\":[]}"));
        when(client.delete(startsWith(INSTANCES + id + "/?"), isNull(), isNull(), anyMap(), eq(30000)))
                .thenReturn(response(200, "{\"success\":true}"));
        assertDoesNotThrow(() -> service.reclaimClosedVm(id, name, "scene", "node"));
        verify(client).delete(startsWith(INSTANCES + id + "/?"), isNull(), isNull(), anyMap(), eq(30000));
    }

    private String instance(String status, String ip, String mac) {
        return "{\"results\":[{\"uuid\":\"fixture-id\",\"status\":\"" + status
                + "\",\"address\":{\"network1\":{\"fixed\":[{\"addr\":\"" + ip
                + "\",\"OS-EXT-IPS-MAC:mac_addr\":\"" + mac + "\"}]}}}]}";
    }
}
