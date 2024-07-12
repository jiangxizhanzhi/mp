 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TestFlowApplication.java.
 // File size 533.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
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
