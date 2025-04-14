package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class RangeDataUtil {

    private ReentrantLock lock = new ReentrantLock();

    @Resource
    private RangeDataRemoteService rangeDataRemoteService;

    @Resource
    private RangeDataLocalService rangeDataLocalService;

    @Resource
    private CsceneService csceneService;

    private Long startTime = System.currentTimeMillis();

    @Scheduled(cron = "0/30 * * * * ?")
    public void test() {
        SysConfigMapper sysConfigMapper = BeanCommon.getBean(SysConfigMapper.class);
        var model = sysConfigMapper.first(new HumanCondition().filterEqual(SysConfigModel::getKeyName, "collect_agent_info"));
        var result = JsonCommon.parse(model.getValue(), CollectAgentConfig.class);

        if (result.getEnable()) {
            long runTime = System.currentTimeMillis() / 1000 - startTime / 1000;
            log.info("数据采集定时任务 , runtime {} ", runTime);
            if (runTime % result.getPeriod() == 0) {
                if (lock.tryLock()) {
                    try {
                        log.info("开始采集各个节点的数据");
                        rangeDataRemoteService.period = result.getPeriod();
                        rangeDataRemoteService.period = result.getPeriod();
                        var instances = csceneService.getRunningCscenes();
                        for (var instance : instances) {
                            if (!instance.getId().equals("ffff18d5d9d34ac2996862c1097cd56a")) {
                                continue;
                            }
                            try {
                                rangeDataLocalService.test(instance.getId(), result.info, result.event, result.log);
                            } catch (Exception e) {
                                log.error("failed to get range data remote , cscene instance id {}", instance.getId(), e);
                            }
                            try {
                                rangeDataRemoteService.test(instance.getId(), result.info, result.event, result.log);
                            } catch (Exception e) {
                                log.error("failed to get range data remote , cscene instance id {}", instance.getId(), e);
                            }
                        }
                        log.info("结束采集各个节点的数据");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    log.info("锁获取失败,已有线程正在执行数据采集的任务");
                }
            }
        }
    }

    @Getter
    @Setter
    public static class CollectAgentConfig {
        private Boolean enable;
        private Integer period;
        private boolean info;
        private boolean event;
        private boolean log;
        private boolean flow;
    }
}
