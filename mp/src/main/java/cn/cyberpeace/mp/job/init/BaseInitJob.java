package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.base.localcall.LocalCallServerListener;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.base.websocket.BaseGroupServer;
import cn.cyberpeace.mp.base.websocket.BaseServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName BaseInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Component
public class BaseInitJob {

    @Autowired
    private BaseAppConfig baseAppConfig;

    @MpInitiate(id = "base_init", description = "初始化文件资源")
    public void run() {
        String homeDir = baseAppConfig.getHomeDir();
        String resourcesDir = baseAppConfig.getResourcesDir();
        String fileServerDir = baseAppConfig.getFileServerDir();
        FileUtil.mkdir(homeDir);
        FileUtil.mkdir(resourcesDir);
        FileUtil.mkdir(fileServerDir);
        log.info("Home dir: {}", homeDir);
        log.info("Resource dir: {}", resourcesDir);
        log.info("Local file server dir: {}", fileServerDir);

        if (Boolean.TRUE.equals(baseAppConfig.getEnableLocalCall())) {
            TaskCommon.executeTask(() -> LocalCallServerListener.initStart(baseAppConfig, BeanCommon.getContext()));
        }

        BaseServer.initServerSessionMap();
        BaseGroupServer.initServerGroupSessionsListMap();
    }

}
