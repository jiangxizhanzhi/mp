package cn.cyberpeace.mp.cscene.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//该路径免认证    /ws/devops/cluster/*
@ServerEndpoint(value = "/ws/devops/cluster/loadResultWebSocket/{scene}")
@Component
public class LoadResultWebSocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    public static ConcurrentHashMap<Session, LoadResultWebSocketServer> sessionPools = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Session> sceneIdAndSession = new ConcurrentHashMap<>();

    //发送消息
    public synchronized void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
//                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "scene") String scene){
        try {
            sessionPools.put(session, this);
            if(sceneIdAndSession.get(scene)==null){
                sceneIdAndSession.put(scene, session);
            }
            addOnlineCount();
            System.out.println(scene + "加入webSocket! 当前会话数量为" + onlineNum + "个");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭连接时调用
    @OnClose
    public void onClose(Session session){
        //判断是否有连接
        if (sessionPools.containsKey(session)){
            sessionPools.remove(session);
            subOnlineCount();
            System.out.println("断开webSocket连接！当前人数为" + onlineNum);
        }else{
            System.out.println("该session:"+session.getId()+"不存在连接，无需断开连接");
        }
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message){
        try {
            //接收参数
            System.out.println("收到客户端信息:" + message);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable){
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    //增加在线人数
    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }

    //减少在线人数
    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }
}
