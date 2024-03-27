package cn.cyberpeace.mp.base.config.websocket;


import cn.cyberpeace.mp.base.common.JsonCommon;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SceneTestInstanceProcessHandler extends TextWebSocketHandler {


    // 保存所有Client的WebSocket会话实例:
    private final Map<String, Map<String, WebSocketSession>> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String csceneInstanceId = (String) session.getAttributes().get("csceneInstanceId");
        if (StringUtils.isNotEmpty(csceneInstanceId)) {
            if (Objects.isNull(clients.get(csceneInstanceId))) {
                clients.put(csceneInstanceId, new ConcurrentHashMap<>());
            }
            clients.get(csceneInstanceId).put(session.getId(), session);
            log.info("成功建立消息{}", csceneInstanceId);
        } else {
            log.error("保存websocket失败，csceneInstanceId为空");
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String csceneInstanceId = (String) session.getAttributes().get("csceneInstanceId");
        if (StringUtils.isNotEmpty(csceneInstanceId)) {
            if (Objects.nonNull(clients.get(csceneInstanceId))) {
                clients.get(csceneInstanceId).remove(session.getId());
            }
        }

    }

    public void pushCsceneInstanceSockets(String csceneInstanceId, Object object) {
        if (Objects.nonNull(clients.get(csceneInstanceId))) {
            clients.get(csceneInstanceId).values().forEach(webSocketSession -> {
                try {
                    webSocketSession.sendMessage(new TextMessage(JsonCommon.snakeStringify(object)));
                } catch (IOException e) {
                    log.error(String.format("实例%s推送消息失败，发生异常%s", csceneInstanceId, e.getMessage()), e);
                }
            });
        }
    }



}
