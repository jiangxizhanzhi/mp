package cn.cyberpeace.mp.log.init;

import cn.cyberpeace.mp.log.service.ILogSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DynamicLogLevelLoader implements ApplicationRunner {

    @Autowired
    private ILogSettingService logSettingService;

    
    @Value("${mp.log.dynamic-level-keeping:false}")
    private boolean dynamicLevelKeeping;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        if(dynamicLevelKeeping){
            log.info("begin to load dynamic log levels from db");
            logSettingService.loadDynamicLogLevel();
            log.info("finish set dynamic log levels from db");
        }
    }
}
