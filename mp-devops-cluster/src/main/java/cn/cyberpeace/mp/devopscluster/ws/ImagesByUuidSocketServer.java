package cn.cyberpeace.mp.devopscluster.ws;

import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint("/ws" + ControllerConstant.BASE_API_PATH + "/images-page")
public class ImagesByUuidSocketServer {

    
    private static final Map<Session, ImagesByUuidSocketServer> websocketMap = new ConcurrentHashMap<>();

    
    private Session session;

    
    private String uuid = "";

    
    @OnOpen
    public void onOpen(Session session, @PathParam("uuid") String uuid) {
        try {
            this.session = session;
            this.uuid = uuid;
            websocketMap.put(session, this);
            log.info("websocket 新连接：{}", uuid);
        } catch (Exception e) {
            log.error("websocket 新建连接 IO异常");
        }
    }

    
    @OnClose
    public void onClose() {
        
        websocketMap.remove(this.session);
        log.info("close websocket : {}", this.uuid);
    }

    
    @OnMessage
    public void onMessage(String message) {
        log.info("来自客户端{}的消息:{}", this.uuid, message);
    }

    
    @OnError
    public void onError(Throwable error) {
        log.info("websocket 发生错误,移除当前websocket:{},err:{}", this.uuid, error.getMessage());
        websocketMap.remove(this.session);
    }

    
    private void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    
    public void sendInfo(String uuid, String wsInfo) {
        if (MapUtils.isEmpty(websocketMap)){
            return;
        }
        websocketMap.forEach((k,v) -> {
            if (v.uuid.equals(uuid)){
                v.sendMessage(wsInfo);
            }
        });
    }

    
    public void batchSendInfo(String json, List<String> uuids) {
        uuids.forEach(uuid -> sendInfo(uuid, json));
    }

    
    public void batchSendInfo(String json) {
        if (MapUtils.isEmpty(websocketMap)){
            return;
        }
        websocketMap.forEach((k, v) -> v.sendMessage(json));
    }
}
