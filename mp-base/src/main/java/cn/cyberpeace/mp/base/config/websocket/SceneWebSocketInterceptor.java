package cn.cyberpeace.mp.base.config.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class SceneWebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            // 从HTTP请求中获取路径参数

            String csceneInstanceId = extractIdFromUriPath(servletRequest.getURI().getPath());
            // 将路径参数存储在WebSocketSession中
            attributes.put("csceneInstanceId", csceneInstanceId);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Do nothing
    }

    private String extractIdFromUriPath(String path) {
        String[] pathSegments = path.split("/");
        return pathSegments[pathSegments.length-1];

    }
}
