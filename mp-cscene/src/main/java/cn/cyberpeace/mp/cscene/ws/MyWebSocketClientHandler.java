package cn.cyberpeace.mp.cscene.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class MyWebSocketClientHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket连接已建立");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            log.info("收到服务器消息: " + message.getPayload());
            // 将消息转换为JSON对象
            String payload = (String) message.getPayload();
//        WebSocketMessagePayload payloadObj = objectMapper.readValue(payload, WebSocketMessagePayload.class);
            JSONObject jsonObject = JSON.parseObject(payload);
            // 根据type字段调用不同的方法
//        int type = payloadObj.getType();
            String function = jsonObject.getString("Function");
            String data = jsonObject.getString("Data");
            switch (function) {
                default:
                    log.warn("未知的320消息类型: " + function);
                    break;
            }
        } catch (Exception e) {
            log.error("320消息解析出错,message:{}", message.getPayload());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("传输错误: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接已关闭，状态码: " + closeStatus.getCode() + ", 原因: " + closeStatus.getReason());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false; // 不支持分片消息
    }
}