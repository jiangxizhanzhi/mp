package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.config.XdAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static cn.cyberpeace.mp.service.VendorSecondAddressPoolTest.response;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VendorPowerLifecycleTest {
    private static final String ONE_INSTANCES = "/api/cloud/instances/";
    private static final String THREE_RESOURCES = "/api2/json/cluster/resources/?type=vm";

    private class Fixture implements AutoCloseable {
        final HttpClient client = mock(HttpClient.class);
        final MultiCloudService service;
        final String id;
        final int vendor;
        final AtomicReference<VmPowerState> state = new AtomicReference<>(VmPowerState.RUNNING);
        final List<String> events = new ArrayList<>();
        final Object oldToken;
        boolean reject;
        boolean keepState;

        Fixture(int vendor) {
            this.vendor = vendor;
            MultiCloudClient multi = mock(MultiCloudClient.class);
            when(multi.getClient(vendor)).thenReturn(client);
            if (vendor == 1) {
                id = "fixture-id";
                oldToken = ReflectionTestUtils.getField(VendorFirstCloudServiceImpl.class, "TOKEN");
                ReflectionTestUtils.setField(VendorFirstCloudServiceImpl.class, "TOKEN", "fixture");
                service = new VendorFirstCloudServiceImpl(multi, mock(VendorFirstVmLedger.class)) {
                    @Override protected long powerTimeoutMillis() { return 0; }
                    @Override public Map<String, String> getHeaders() { return new HashMap<>(); }
                };
            } else {
                id = "9001";
                oldToken = null;
                service = new VendorThirdCloudServiceImpl(multi, new XdAppConfig(), mock(VendorThirdVmLedger.class)) {
                    @Override protected long powerTimeoutMillis() { return 0; }
                    @Override public Map<String, String> getHeaders() { return new HashMap<>(); }
                };
            }
            when(client.get(anyString(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
                String url = inv.getArgument(0);
                events.add("read:" + state.get());
                if (vendor == 1) {
                    assertTrue(url.startsWith(ONE_INSTANCES + "?"));
                    String status = state.get() == VmPowerState.RUNNING ? "ACTIVE"
                            : state.get() == VmPowerState.STOPPED ? "SHUTOFF" : "BUILD";
                    return response(200, "{\"results\":[{\"uuid\":\"fixture-id\",\"status\":\"" + status + "\"}]}");
                }
                assertEquals(THREE_RESOURCES, url);
                String status = state.get() == VmPowerState.RUNNING ? "running"
                        : state.get() == VmPowerState.STOPPED ? "stopped" : "unknown";
                return response(200, "{\"success\":1,\"data\":[{\"node\":\"fixture-node\",\"vmid\":9001,\"status\":\"" + status + "\"}]}");
            });
            when(client.post(anyString(), any(), isNull(), anyMap(), eq(30000))).thenAnswer(inv -> {
                String url = inv.getArgument(0);
                HttpData body = inv.getArgument(1);
                boolean start;
                if (vendor == 1) {
                    assertTrue(url.startsWith(ONE_INSTANCES + id + "/action/?cluster="));
                    String action = JSONObject.parseObject(body.toContent()).getString("action");
                    assertTrue(Arrays.asList("power_on", "power_off").contains(action));
                    start = "power_on".equals(action);
                } else {
                    assertTrue(url.startsWith("/api2/json/nodes/fixture-node/qemu/9001/status/"));
                    assertNull(body);
                    start = url.endsWith("/start");
                }
                events.add(start ? "start" : "stop");
                if (!reject && !keepState) state.set(start ? VmPowerState.RUNNING : VmPowerState.STOPPED);
                return response(200, vendor == 1 ? "{\"success\":" + !reject + "}"
                        : "{\"success\":" + (reject ? 0 : 1) + "}");
            });
        }

        @Override public void close() {
            if (vendor == 1) ReflectionTestUtils.setField(VendorFirstCloudServiceImpl.class, "TOKEN", oldToken);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void restartConfirmsStoppedBeforeStartAndRunningAfter(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            f.service.restartVm(f.id);
            assertEquals(VmPowerState.RUNNING, f.state.get());
            assertTrue(f.events.indexOf("stop") < f.events.indexOf("read:STOPPED"));
            assertTrue(f.events.indexOf("read:STOPPED") < f.events.indexOf("start"));
            assertEquals("read:RUNNING", f.events.get(f.events.size() - 1));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void failedOrUnconfirmedStopNeverStarts(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            f.reject = true;
            assertThrows(MpRuntimeException.class, () -> f.service.restartVm(f.id));
            assertFalse(f.events.contains("start"));
            f.reject = false;
            f.keepState = true;
            assertThrows(MpRuntimeException.class, () -> f.service.restartVm(f.id));
            assertFalse(f.events.contains("start"));
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void idempotentAndUnknownPowerRequests(int vendor) {
        try (Fixture f = new Fixture(vendor)) {
            f.service.startVm(f.id);
            assertFalse(f.events.contains("start"));
            f.state.set(VmPowerState.UNKNOWN);
            assertThrows(MpRuntimeException.class, () -> f.service.stopVm(f.id));
            assertFalse(f.events.contains("stop"));
        }
    }
}
