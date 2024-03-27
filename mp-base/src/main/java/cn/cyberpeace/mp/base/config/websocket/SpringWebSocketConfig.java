package cn.cyberpeace.mp.base.config.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class SpringWebSocketConfig {

    @Bean
    WebSocketConfigurer createWebSocketConfigurer(
            @Autowired SceneTestInstanceProcessHandler sceneTestInstanceProcessHandler, @Autowired SceneWebSocketInterceptor sceneWebSocketInterceptor)
    {
        return new WebSocketConfigurer() {
            public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
                // 把URL与指定的WebSocketHandler关联，可关联多个:
                registry.addHandler(sceneTestInstanceProcessHandler, "/cscene/test_instance/process/{csceneInstanceId}")
                        .addInterceptors(sceneWebSocketInterceptor);
            }
        };
    }
}
