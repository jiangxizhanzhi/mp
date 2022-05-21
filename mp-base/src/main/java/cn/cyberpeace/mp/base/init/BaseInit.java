package cn.cyberpeace.mp.base.init;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.localcall.LocalCallServerListener;
import cn.cyberpeace.mp.base.websocket.BaseGroupServer;
import cn.cyberpeace.mp.base.websocket.BaseServer;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseInit implements ApplicationRunner {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @Override
    public void run(ApplicationArguments args) {
        var homeDir = baseAppConfig.getHomeDir();
        var resourcesDir = baseAppConfig.getResourcesDir();
        var fileServerDir = baseAppConfig.getFileServerDir();
        FileUtil.mkdir(homeDir);
        FileUtil.mkdir(resourcesDir);
        FileUtil.mkdir(fileServerDir);
        log.info("Home dir: {}", homeDir);
        log.info("Resource dir: {}", resourcesDir);
        log.info("Local file server dir: {}", fileServerDir);

        BaseServer.initServerSessionMap();
        BaseGroupServer.initServerGroupSessionsListMap();
        LocalCallServerListener.initStart(baseAppConfig.getProjectName(), BeanCommon.getContext());
    }

}
