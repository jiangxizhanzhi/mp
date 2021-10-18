package cn.cyberpeace.mp.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Description: 测试启动类
 * @Author qiaokang
 * @Date：2021/8/18 15:54
 */
@SpringBootApplication
@ComponentScan({"cn.cyberpeace"})
public class TestFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestFlowApplication.class, args);
    }

}
