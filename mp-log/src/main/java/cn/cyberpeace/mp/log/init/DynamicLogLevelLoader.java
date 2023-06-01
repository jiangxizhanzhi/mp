package cn.cyberpeace.mp.log.init;

import cn.cyberpeace.mp.log.service.ILogSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName DynamicLogLevelKeeper
 * @description 动态日志级别初始化加载设置
 * @author wangssh
 * @Date 2023/6/1
 * @version 1.0
 **/
@Slf4j
@Component
public class DynamicLogLevelLoader implements ApplicationRunner {

    @Autowired
    private ILogSettingService logSettingService;

    /**
     * 是否记录动态日志级别设置
     */
    @Value("${mp.log.dynamic-level-keeping:false}")
    private boolean dynamicLevelKeeping;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //系统启动后设置最近一次动态调整日志级别，不保证一定设置成功
        if(dynamicLevelKeeping){
            log.info("begin to load dynamic log levels from db");
            logSettingService.loadDynamicLogLevel();
            log.info("finish set dynamic log levels from db");
        }
    }
}
