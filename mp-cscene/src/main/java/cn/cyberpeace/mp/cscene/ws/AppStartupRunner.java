package cn.cyberpeace.mp.cscene.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements CommandLineRunner {

    @Autowired
    private WebSocketClientService webSocketClientService; // 注入客户端服务

    @Override
    public void run(String... args) throws Exception {
        webSocketClientService.connectToServer(); // 启动时连接到服务器
    }
}