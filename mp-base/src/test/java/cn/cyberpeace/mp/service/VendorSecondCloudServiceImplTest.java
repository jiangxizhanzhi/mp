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
            @Override void waitForStop() { }
        };
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
            return listResponse(1, vm("created", last.getString("name"), payloadIp(last)));
        });
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
                listResponse(1, vm("created", "fixture", B)));
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
        verify(pool).recordCreated(any(), eq("created"));
    }

    @Test void deleteChecksPresenceAndDoesNotTreatMissingUuidAsSuccess() {
        assertThrows(MpRuntimeException.class, () -> service.deleteVm(""));
        when(client.get(eq(SERVERS), isNull(), anyMap(), eq(30000))).thenReturn(listResponse(1, vm("existing", "fixture", A)), listResponse(0));
        when(client.post(eq(SERVERS + "/existing/stop"), any(), isNull(), anyMap(), eq(30000))).thenReturn(response(200,"{\"code\":0}"));
        when(client.delete(eq(SERVERS + "/existing"), any(), isNull(), anyMap(), eq(30000))).thenReturn(response(200,"{\"code\":0}"));
        when(pool.reconcileDeleted(eq("existing"), any())).thenAnswer(inv -> {
            VendorSecondInventory snapshot = inv.getArgument(1);
            return snapshot.complete && !snapshot.vms.containsKey("existing");
        });
        assertDoesNotThrow(() -> service.deleteVm("existing"));
        verify(pool).beginDelete(eq("existing"), any());
        verify(pool).deleteSubmitted("existing", true);
        verify(pool).reconcileDeleted(eq("existing"), any());
    }
}
