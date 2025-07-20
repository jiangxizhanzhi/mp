package cn.cyberpeace.mp.cscene.ws;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
public class WebSocketClientService {

    @Resource
    private CsceneAppConfig csceneAppConfig;

    @Autowired
    private WebSocketClient webSocketClient; // 注入WebSocket客户端

    @Autowired
    private MyWebSocketClientHandler handler;

    private static WebSocketSession SESSION;

    private static final String WS_URL = "ws://%s:%s";

    public static WebSocketSession getSession() {
        return SESSION;
    }

    public static void sendMessage(String message) {
        if (SESSION != null && SESSION.isOpen()) {
            try {
                SESSION.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void connectToServer() {
        // 指定服务器URL，例如 ws://localhost:8080/my-websocket
        String url = String.format(WS_URL, csceneAppConfig.getWsIp(), csceneAppConfig.getWsPort());
        try {
            // 连接到服务器并获取会话对象
            SESSION = webSocketClient.doHandshake(handler, url).get();
            log.info("320 WebSocket连接成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定期检查连接状态并尝试重连
    @Scheduled(fixedRate = 5000) // 每5秒检查一次连接状态。可以根据实际需求调整时间间隔。
    public void checkAndReconnect() {
        if (SESSION == null || !SESSION.isOpen()) { // 如果session为null或已关闭，则尝试重新连接。
            log.info("320 WebSocket连接已断开，尝试重新连接...");
            connectToServer(); // 调用connectToServer方法重新连接。
        }
    }
}