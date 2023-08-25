package cn.cyberpeace.mp.aggregation.init;

import cn.cyberpeace.mp.aggregation.common.SystemTimeCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SystemTimeInit implements ApplicationRunner {

    @Resource
    private SystemTimeCommon systemTimeCommon;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        systemTimeCommon.setStartTime(LocalDateTime.now());
    }
}