package cn.cyberpeace.mp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableSwagger2
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

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> log.info("Application loaded");
    }
}
