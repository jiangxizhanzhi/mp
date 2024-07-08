package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.flow.service.FlowTaskRecordService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * 镜像的连接信息WS
 */
@Slf4j
@Component
@ServerEndpoint("/ws/flow/task_info")
public class FlowConnectSocketServer {

    @Resource
    private FlowTaskRecordService flowTaskRecordService;

    @OnOpen
    public void onOpen(Session session) {
        log.info("open one websocket");
    }

    @OnClose
    public void onClose() {
        log.info("clone one websocket");
    }

    /**
     * 接收到客户端消息触发
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("Received message v2: " + message);
        JSONObject jsonMessage = JSONObject.parseObject(message);
        try {
            BeanCommon.getBean(FlowTaskRecordService.class).syncRecord(jsonMessage);
        }catch (Exception e){
            log.error("error", e);
            e.printStackTrace();
        }
    }

    /**
     * 发送异常时触发
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.info("error one websocket");
    }
}
