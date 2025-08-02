package cn.cyberpeace.mp.cscene.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class WebSocketClientConfig {

    @Bean
    public WebSocketClient webSocketClient() {
        System.setProperty("org.apache.tomcat.websocket.textBufferSize", "65536");  // 64KB 文本缓冲区
        System.setProperty("org.apache.tomcat.websocket.binaryBufferSize", "65536"); // 64KB 二进制缓冲区
        return new StandardWebSocketClient(); // 使用标准WebSocket客户端
    }
}