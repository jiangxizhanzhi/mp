package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.MultiCloudClient;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VendorThirdCloudServiceImplTest {

    private static final String NETWORK = "36eca7b1-5fe7-48a5-be8e-19f1a01017d2";
    private static final String NODE = "test-node";
    private final List<String> events = new ArrayList<>();
    private final Deque<String> cloneStates = new ArrayDeque<>();
    private final Deque<String> startStates = new ArrayDeque<>();
    private final Map<String, String> bodies = new HashMap<>();
    private final Map<String, Integer> codes = new HashMap<>();
    private final ListAppender<ILoggingEvent> logs = new ListAppender<>();
    private MultiCloudClient multiCloudClient;
    private VendorThirdCloudServiceImpl service;
    private AtomicInteger previousId;
    private long waitMillis = 10000;
    private int vmId;
    private int configReads;
    private boolean started;
    private JSONObject cloneRequest;
    private JSONObject networkRequest;
    private String cloneState = "stopped";
    private String startState = "running";
    private String readback = "virtio=02:00:00:00:00:02,ip=198.51.100.2,network=" + NETWORK;

    @BeforeEach
    void setUp() {
        previousId = (AtomicInteger) ReflectionTestUtils.getField(VendorThirdCloudServiceImpl.class, "maxId");
        ReflectionTestUtils.setField(VendorThirdCloudServiceImpl.class, "maxId", new AtomicInteger(9000));
        logs.start();
        ((Logger) LoggerFactory.getLogger(VendorThirdCloudServiceImpl.class)).addAppender(logs);
        multiCloudClient = mock(MultiCloudClient.class);
        HttpClient client = mock(HttpClient.class);
        when(multiCloudClient.getClient(3)).thenReturn(client);
        when(client.get(anyString(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("GET", inv.getArgument(0), null));
        when(client.post(anyString(), any(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("POST", inv.getArgument(0), inv.getArgument(1)));
        when(client.put(anyString(), any(), isNull(), anyMap(), eq(30000)))
                .thenAnswer(inv -> respond("PUT", inv.getArgument(0), inv.getArgument(1)));
        service = new VendorThirdCloudServiceImpl(multiCloudClient) {
            @Override
            public Map<String, String> getHeaders() {
                return Collections.emptyMap();
            }

            @Override
            void waitForVmState(int id, String node, String status, long timeoutMillis) {
                assertEquals(600000, timeoutMillis);
                super.waitForVmState(id, node, status, waitMillis);
            }
        };
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtils.setField(VendorThirdCloudServiceImpl.class, "maxId", previousId);
        ((Logger) LoggerFactory.getLogger(VendorThirdCloudServiceImpl.class)).detachAppender(logs);
        logs.stop();
    }

    @Test
    void configuresAndReadsBackNetworkBeforeStarting() {
        assertEquals("9001", service.createVm("115"));
        assertEquals(Arrays.asList("templates", "templates", "clone", "poll-clone", "read-config",
                "write-config", "readback", "start", "poll-start"), events);
        assertEquals(9001, cloneRequest.getIntValue("newid"));
        assertEquals(NODE, cloneRequest.getString("target"));
        assertTrue(cloneRequest.getString("name").startsWith("MP_CREATE_"));
        assertEquals("initial-digest", networkRequest.getString("digest"));
        assertEquals("virtio,network=" + NETWORK, networkRequest.getString("vnet0"));
        assertTrue(warnings().isEmpty());
    }

    @Test
    void pollsPastMissingVmAndCloneLockUntilReady() {
        cloneStates.addAll(Arrays.asList("missing", "stopped,clone", "stopped"));
        startStates.addAll(Arrays.asList("stopped", "running"));
        assertEquals("9001", service.createVm("115"));
        assertEquals(3, Collections.frequency(events, "poll-clone"));
        assertEquals(2, Collections.frequency(events, "poll-start"));
        assertTrue(events.indexOf("write-config") > events.lastIndexOf("poll-clone"));
        assertTrue(events.indexOf("start") > events.indexOf("readback"));
        assertTrue(warnings().isEmpty());
    }

    @Test
    void cloneTimeoutStillAttemptsNetworkAndStart() {
        waitMillis = 0;
        cloneState = "unknown,clone";
        assertEquals("9001", service.createVm("115"));
        assertTrue(events.contains("write-config"));
        assertTrue(events.contains("start"));
        assertEquals(1, warnings().size());
        assertTrue(warnings().get(0).contains("等待克隆关机就绪超时"));
        assertTrue(warnings().get(0).contains("status=unknown, lock=clone"));
    }

    @Test
    void startupTimeoutStillReturnsVmId() {
        waitMillis = 0;
        startState = "stopped";
        assertEquals("9001", service.createVm("115"));
        assertEquals(1, warnings().size());
        assertTrue(warnings().get(0).contains("等待启动运行超时"));
        assertTrue(warnings().get(0).contains("目标状态尚未确认"));
    }

    @Test
    void bothTimeoutsOnlyWarnAndContinue() {
        waitMillis = 0;
        cloneState = "unknown,clone";
        startState = "stopped";
        assertEquals("9001", service.createVm("115"));
        assertEquals(2, warnings().size());
        assertTrue(warnings().stream().allMatch(s -> s.contains("node=" + NODE)
                && s.contains("elapsedMs=") && s.contains("继续后续流程")));
    }

    @Test
    void missingVmTimeoutUsesRequestedCloneNodeForNextSteps() {
        waitMillis = 0;
        cloneState = "missing";
        startState = "missing";
        bodies.put("poll-clone", "{\"success\":1,\"data\":[{\"vmid\":42,\"status\":\"stopped\"}]}");
        assertEquals("9001", service.createVm("115"));
        assertEquals(2, warnings().size());
        assertTrue(warnings().stream().allMatch(s -> s.contains("found=false")));
        assertTrue(events.contains("write-config"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"clone", "poll-clone", "read-config", "write-config", "readback", "start", "poll-start"})
    void businessFailureStopsFollowingStepsEvenWithHttp200(String stage) {
        bodies.put(stage, "{\"success\":0,\"status\":500,\"message\":\"fixture rejection\"}");
        MpRuntimeException error = assertThrows(MpRuntimeException.class, () -> service.createVm("115"));
        assertTrue(error.getMessage().contains("业务失败"));
        assertTrue(error.getMessage().contains("fixture rejection"));
        assertEquals(stage, events.get(events.size() - 1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"clone", "write-config", "start"})
    void httpFailureStopsFollowingSteps(String stage) {
        codes.put(stage, 500);
        MpRuntimeException error = assertThrows(MpRuntimeException.class, () -> service.createVm("115"));
        assertTrue(error.getMessage().contains("HTTP=500"));
        assertEquals(stage, events.get(events.size() - 1));
    }

    @Test
    void missingDigestDoesNotSkipConfigurationAndStart() {
        bodies.put("read-config", "{\"success\":1,\"data\":{}}");
        assertTrue(assertThrows(MpRuntimeException.class, () -> service.createVm("115"))
                .getMessage().contains("digest"));
        assertFalse(events.contains("write-config"));
        assertFalse(events.contains("start"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"virtio=02:00:00:00:00:02,network=36eca7b1-5fe7-48a5-be8e-19f1a01017d2-other",
            "e1000=02:00:00:00:00:02,network=36eca7b1-5fe7-48a5-be8e-19f1a01017d2", ""})
    void readbackMismatchStopsBeforeStart(String value) {
        readback = value;
        assertTrue(assertThrows(MpRuntimeException.class, () -> service.createVm("115"))
                .getMessage().contains("回读与目标配置不一致"));
        assertTrue(events.contains("write-config"));
        assertFalse(events.contains("start"));
    }

    @Test
    void elapsedDeadlineEndsPollingWithoutThrowing() {
        vmId = 9001;
        cloneState = "unknown,clone";
        waitMillis = 20;
        long started = System.nanoTime();
        assertDoesNotThrow(() -> service.waitForVmState(vmId, NODE, "stopped", 600000));
        assertTrue(System.nanoTime() - started >= 20000000);
        assertTrue(events.contains("poll-clone"));
        assertEquals(1, warnings().size());
    }

    @Test
    void interruptedPollingRestoresInterruptAndStops() {
        vmId = 9001;
        cloneState = "unknown,clone";
        Thread.currentThread().interrupt();
        try {
            assertTrue(assertThrows(MpRuntimeException.class,
                    () -> service.waitForVmState(vmId, NODE, "stopped", 600000))
                    .getMessage().contains("被中断"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void realHttpRoundTripHonorsOrderingAndBusinessFailure(boolean rejectNetwork) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String content = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            HttpResult response = respond(exchange.getRequestMethod(), exchange.getRequestURI().toString(),
                    content.isEmpty() ? null : new StringHttpData(content));
            byte[] bytes = response.getResult().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(response.getResponse().getStatusLine().getStatusCode(), bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        try {
            HttpClient client = HttpClient.builder().configServer("http://127.0.0.1:" + server.getAddress().getPort()).build();
            when(multiCloudClient.getClient(3)).thenReturn(client);
            if (rejectNetwork) {
                bodies.put("write-config", "{\"success\":0,\"status\":500,\"message\":\"hotplug rejected\"}");
                assertTrue(assertThrows(MpRuntimeException.class, () -> service.createVm("115"))
                        .getMessage().contains("hotplug rejected"));
                assertFalse(events.contains("start"));
            } else {
                assertEquals("9001", service.createVm("115"));
                assertTrue(events.indexOf("write-config") < events.indexOf("start"));
                assertTrue(events.indexOf("readback") < events.indexOf("start"));
                assertEquals("initial-digest", networkRequest.getString("digest"));
            }
        } finally {
            server.stop(0);
        }
    }

    private List<String> warnings() {
        return logs.list.stream().filter(e -> e.getLevel() == Level.WARN)
                .map(ILoggingEvent::getFormattedMessage).collect(Collectors.toList());
    }

    private HttpResult respond(String method, String path, HttpData body) {
        String stage;
        String response;
        if (path.equals("/api2/json/cluster/resources/")) {
            stage = "templates";
            response = "{\"success\":1,\"data\":[{\"template\":1,\"vmid\":115,\"node\":\"" + NODE
                    + "\",\"name\":\"fixture-template\"}]}";
        } else if (path.equals("/api2/json/cluster/resources/?type=vm")) {
            stage = started ? "poll-start" : "poll-clone";
            Deque<String> states = started ? startStates : cloneStates;
            String state = states.isEmpty() ? (started ? startState : cloneState) : states.removeFirst();
            String data = "[]";
            if (!state.equals("missing")) {
                String[] parts = state.split(",", 2);
                data = "[{\"vmid\":" + vmId + ",\"node\":\"" + NODE + "\",\"status\":\"" + parts[0] + "\""
                        + (parts.length == 2 ? ",\"lock\":\"" + parts[1] + "\"" : "") + "}]";
            }
            response = "{\"success\":1,\"data\":" + data + "}";
        } else {
            assertTrue(path.startsWith("/api2/json/nodes/" + NODE + "/qemu/"));
            if (path.endsWith("/115/clone") && method.equals("POST")) {
                stage = "clone";
                cloneRequest = JSONObject.parseObject(body.toContent());
                vmId = cloneRequest.getIntValue("newid");
                response = "{\"success\":1,\"data\":\"fixture-clone-UPID\"}";
            } else if (path.endsWith("/config") && method.equals("GET")) {
                stage = ++configReads == 1 ? "read-config" : "readback";
                JSONObject config = new JSONObject();
                config.put("digest", "initial-digest");
                config.put("vnet0", configReads == 1 ? "virtio=02:00:00:00:00:01,ip=198.51.100.1,network=" + NETWORK : readback);
                JSONObject result = new JSONObject();
                result.put("success", 1);
                result.put("data", config);
                response = result.toJSONString();
            } else if (path.endsWith("/config") && method.equals("PUT")) {
                stage = "write-config";
                networkRequest = JSONObject.parseObject(body.toContent());
                response = "{\"success\":1,\"data\":null}";
            } else if (path.endsWith("/status/start") && method.equals("POST")) {
                stage = "start";
                assertNull(body);
                started = true;
                response = "{\"success\":1,\"data\":\"fixture-start-UPID\"}";
            } else {
                throw new AssertionError("Unexpected request: " + method + " " + path);
            }
        }
        events.add(stage);
        HttpResult result = new HttpResult();
        result.setResponse(new BasicHttpResponse(HttpVersion.HTTP_1_1, codes.getOrDefault(stage, 200), "fixture"));
        result.setResult(bodies.getOrDefault(stage, response));
        return result;
    }
}
