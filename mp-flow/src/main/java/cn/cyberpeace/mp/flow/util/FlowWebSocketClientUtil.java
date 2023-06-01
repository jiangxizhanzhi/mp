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

/**
 * @Description: 流量任务ws客户端
 * @Author qiaokang
 * @Date：2023/5/23 17:18
 */
@Slf4j
@ClientEndpoint
public class FlowWebSocketClientUtil {

    private Session session;

    public static Map<String, FlowWebSocketClientUtil> webSocketClientMap = new HashMap<>();

    private static FlowTaskRecordService flowTaskRecordService = BeanCommon.getBean(FlowTaskRecordService.class);

//    public static void main(String[] args) throws URISyntaxException, IOException, DeploymentException {
//        FlowWebSocketClientUtil client = FlowWebSocketClientUtil.getWebSocketClient("10.100.7.129");
//        client.sendMessage("taskid111,taskid222");
//
//        client.closeConnection("10.100.7.129");
//    }

    /**
     * 获取客户端
     * @param ip 流量执行器ip
     * @return
     */
    public static FlowWebSocketClientUtil getWebSocketClient(String ip) {
        FlowWebSocketClientUtil client = new FlowWebSocketClientUtil();
        try {
            if (webSocketClientMap.containsKey(ip)) {
                client = webSocketClientMap.get(ip);
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

    /**
     * 开启链接
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        log.info("Connected to WebSocket server");
    }

    /**
     * 接收消息
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("Received message: " + message);
        JSONObject jsonMessage = JSONObject.parseObject(message);
        flowTaskRecordService.syncRecord(jsonMessage);
    }

    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    /**
     * 终止链接
     * @throws IOException
     */
    public void closeConnection(String ip) throws IOException {
        webSocketClientMap.remove(ip);
        session.close();
        log.info("Disconnected from WebSocket server");
    }
}
