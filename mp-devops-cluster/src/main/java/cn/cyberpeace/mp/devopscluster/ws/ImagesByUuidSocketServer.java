 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImagesByUuidSocketServer.java.
 // File size 3667.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName ImagesByUuidSocketServer
 * @Description 镜像分页对应的ws
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
@ServerEndpoint("/ws" + ControllerConstant.BASE_API_PATH + "/images-page")
public class ImagesByUuidSocketServer {

    /**
     * KEY:Session，value:连接实例
     */
    private static final Map<Session, ImagesByUuidSocketServer> websocketMap = new ConcurrentHashMap<>();

    /**
     * 与客户端建立的连接session
     */
    private Session session;

    /**
     * 镜像ID
     */
    private String uuid = "";

    /**
     * 建立连接触发
     * @param session
     * @param uuid
     */
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

    /**
     * 关闭连接触发
     */
    @OnClose
    public void onClose() {
        // 删除
        websocketMap.remove(this.session);
        log.info("close websocket : {}", this.uuid);
    }

    /**
     * 接收到客户端消息触发
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("来自客户端{}的消息:{}", this.uuid, message);
    }

    /**
     * 发送异常时触发
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("websocket 发生错误,移除当前websocket:{},err:{}", this.uuid, error.getMessage());
        websocketMap.remove(this.session);
    }

    /**
     * 发送消息 (异步发送)
     *
     * @param message 消息主题
     */
    private void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 向指定镜像ID发送信息
     *
     * @param uuid 镜像ID
     * @param wsInfo 信息
     */
    public void sendInfo(String uuid, String wsInfo) {
        if (MapUtils.isEmpty(websocketMap)){
            return;
        }
        for (Map.Entry<Session, ImagesByUuidSocketServer> entry : websocketMap.entrySet()) {
            Session k = entry.getKey();
            ImagesByUuidSocketServer v = entry.getValue();
            if (v.uuid.equals(uuid)) {
                v.sendMessage(wsInfo);
            }
        }
    }

    /**
     * 批量发给指定UUID
     */
    public void batchSendInfo(String json, List<String> uuids) {
        for (String s : uuids) {
            sendInfo(s, json);
        }
    }

    /**
     * 群发
     */
    public void batchSendInfo(String json) {
        if (MapUtils.isEmpty(websocketMap)){
            return;
        }
        for (Map.Entry<Session, ImagesByUuidSocketServer> entry : websocketMap.entrySet()) {
            Session k = entry.getKey();
            ImagesByUuidSocketServer v = entry.getValue();
            v.sendMessage(json);
        }
    }
}
