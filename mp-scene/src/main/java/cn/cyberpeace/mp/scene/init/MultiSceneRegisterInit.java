package cn.cyberpeace.mp.scene.init;

import cn.cyberpeace.mp.scene.config.MultiSceneConfig;
import cn.cyberpeace.mp.scene.service.CyberRangeHeartbeatService;
import cn.cyberpeace.mp.scene.service.ManageCenterService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多靶场，分靶场启动时，会首先向联管中心注册。
 * 然后每隔一定频率向联管中心发送心跳。（默认一分钟）
 * 联管中心每隔三分钟检查心跳，如果三分钟内，都没有心跳更新，则说明该节点下线了
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MultiSceneRegisterInit implements ApplicationRunner {

    private final MultiSceneConfig multiSceneConfig;
    private final CyberRangeHeartbeatService cyberRangeHeartbeatService;
    private final ManageCenterService manageCenterService;

    private static final ScheduledThreadPoolExecutor REGISTER_EXECUTOR = new ScheduledThreadPoolExecutor(2, new ThreadFactoryBuilder().setNameFormat("scheduled-register-pool-%d").build());
    private static final ScheduledThreadPoolExecutor SCAN_EXECUTOR = new ScheduledThreadPoolExecutor(2,
            new ThreadFactoryBuilder().setNameFormat("scheduled-scan-pool-%d").build());

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("执行多靶场心跳初始化");
        if (multiSceneConfig.isManagerCenterFlag()) {
            if (multiSceneConfig.getScanInterval() > multiSceneConfig.getHeartbeatInterval()) {
                SCAN_EXECUTOR.scheduleWithFixedDelay(() -> manageCenterService.scanNotHeartBeatCyberRange(multiSceneConfig.getScanInterval()),
                        multiSceneConfig.getHeartbeatInterval(), multiSceneConfig.getScanInterval(), TimeUnit.SECONDS);
            }
        }

        if (multiSceneConfig.isSubCyberRangeFlag()) {
            if (multiSceneConfig.getHeartbeatInterval() > 0) {
                log.info("提交心跳任务");
                REGISTER_EXECUTOR.scheduleWithFixedDelay(cyberRangeHeartbeatService::registerCurrentRange,
                        1L, multiSceneConfig.getHeartbeatInterval(), TimeUnit.SECONDS);

            }
        }

    }
}
