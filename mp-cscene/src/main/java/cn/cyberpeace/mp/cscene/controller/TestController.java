package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/test")
public class TestController {

    @Resource
    private Environment environment;

    @GetMapping("/get")
    public String getProperty(@Param("key") String key) {
        return environment.getProperty(key);
    }
}
