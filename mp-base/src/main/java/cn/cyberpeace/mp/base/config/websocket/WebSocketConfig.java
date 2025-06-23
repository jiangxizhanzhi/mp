 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WebSocketConfig.java.
 // File size 1449.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Map;

/**
 * @ClassName WebSocketConfig
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Configuration
public class WebSocketConfig {

    /**
     * serverEndpointExporter
     *
     * @return fromorg.springframework.web.socket.server.standard.ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    public static class Configurator extends ServerEndpointConfig.Configurator {

        public static final String USER_HEADERS_KEY = "headers";

        /**
         * modifyHandshake
         *
         * @param sec
         * @param request
         * @param response
         */
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            super.modifyHandshake(sec, request, response);

            Map<String, Object> userProperties = sec.getUserProperties();
            userProperties.put(USER_HEADERS_KEY, request.getHeaders());
        }

    }

}
