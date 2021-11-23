package cn.cyberpeace.mp.base.init;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class BaseInit implements ApplicationRunner {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        FileUtil.mkdir(baseAppConfig.getHomeDir());
        FileUtil.mkdir(baseAppConfig.getResourcesDir());
    }

}
