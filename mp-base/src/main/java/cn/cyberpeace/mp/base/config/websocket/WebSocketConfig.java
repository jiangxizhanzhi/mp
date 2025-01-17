 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WebSocketConfig.java.
 // File size 1002.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    public static class Configurator extends ServerEndpointConfig.Configurator {

        public static final String USER_HEADERS_KEY = "headers";

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            super.modifyHandshake(sec, request, response);

            var userProperties = sec.getUserProperties();
            userProperties.put(USER_HEADERS_KEY, request.getHeaders());
        }

    }

}
