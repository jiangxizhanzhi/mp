package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.cscene.SceneCorrectService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author hewei
 * @Date：2022/3/9 16:37
 */
@Component
@Slf4j
public class ScenePluginCloudSceneCronService {
    @Resource
    private CsceneService csceneService;

    @Resource
    private SceneCorrectService sceneCorrectService;

    @Resource
    private ClearService clearService;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;

    public void action() {
        log.info("start cloud plugin scene correct job");

        // 内存和数据库中节点数量保持同步
        try {
            int count = csceneService.getActiveVirtualDeviceCount();
            NodeLimitUtils.refreshCount(count);
        } catch (Exception e) {
            log.error("failed to refresh node limit count", e);
        }

        // 启动耗时很长的场景,状态置为异常
        try {
            sceneCorrectService.correctErrorStatusScene();
        } catch (Exception e) {
            log.error("failed to correct error status scene", e);
        }

        // 纠正cscene和scene快照表的数据
        try {
            csceneSnapshotService.correctSnapshotRelativeInfo();
        } catch (Exception e) {
            log.error("failed to corrcet snapshot relative info", e);
        }

        // 清理未正常释放的资源
        clearService.clearUselessResource();
        log.info("end cloud plugin scene correct job");
    }
}
