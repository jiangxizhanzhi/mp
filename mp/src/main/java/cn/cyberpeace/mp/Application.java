package cn.cyberpeace.mp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableAsync
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(Application.class, args).getEnvironment();
        String port = env.getProperty("server.port", "8080");
        try {
            log.info("Access URLs:\n----------------------------------------------------------\n\t"
                            + "Local: \t\thttp://127.0.0.1:{}\n\t"
                            + "External: \thttp://{}:{}\n"
                            + "----------------------------------------------------------",
                    port,
                    InetAddress.getLocalHost().getHostAddress(),
                    port
            );
        } catch (UnknownHostException e) {
            log.error("getLocalHost error", e);
        }
    }
}
