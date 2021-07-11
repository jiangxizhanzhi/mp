package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@ClientEndpoint
public class FlowWebSocketClientUtil {

    private Session session;

    public static Map<String, FlowWebSocketClientUtil> webSocketClientMap = new HashMap<>();

    private static FlowTaskRecordService flowTaskRecordService = BeanCommon.getBean(FlowTaskRecordService.class);

    
    public static FlowWebSocketClientUtil getWebSocketClient(String ip) {
        FlowWebSocketClientUtil client = new FlowWebSocketClientUtil();
        try {
            if (webSocketClientMap.containsKey(ip)) {
                client = webSocketClientMap.get(ip);
                if (!client.session.isOpen()) {
                    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                    container.connectToServer(client, new URI("ws://"  + ip + ":8888/task_running"));
                }
            } else {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(client, new URI("ws://"  + ip + ":8888/task_running"));
                webSocketClientMap.put(ip, client);
            }
        } catch (Exception e) {
            log.error("getWebSocketClient error", e);
            throw new MpRuntimeException("get websocket client error");
        }

        return client;
    }

    
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("Connected to WebSocket server");
    }

    
    @OnMessage
    public void onMessage(String message) {
        log.info("Received message: " + message);
        JSONObject jsonMessage = JSONObject.parseObject(message);
        flowTaskRecordService.syncRecord(jsonMessage);
    }

    
    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    
    public void closeConnection(String ip) throws IOException {
        webSocketClientMap.remove(ip);
        session.close();
        log.info("Disconnected from WebSocket server");
    }
}
