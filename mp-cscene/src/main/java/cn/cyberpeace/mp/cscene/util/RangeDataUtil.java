package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.RangeTaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import cn.cyberpeace.mp.scene.model.RangeModel;
import cn.cyberpeace.mp.scene.repository.RangeMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    @Resource
    private RangeMapper rangeMapper;

    private volatile long startTime = 0L;

    private Set<String> removedIds = new HashSet<>();

    ExecutorService executor = new ThreadPoolExecutor(2, 10, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    //    @Scheduled(cron = "0 0/1 * * * ?")
    public void test() {
        // 周期按照分钟计算 startTime 表示从第一次启动到现在经过了几分钟
        startTime++;
        LocalDateTime currentTime = LocalDateTime.now();
        log.info("current time : {}", TimeUtils.localDateTimeToString(currentTime));
        if (lock.tryLock()) {
            try {
                doAction(currentTime);
            } finally {
                lock.unlock();
            }
        }
    }

    private void doAction(LocalDateTime currentTime) {
        long timeA = System.currentTimeMillis();
        // 查询所有的分靶场 并给每个靶场设置默认的周期配置
        List<RangeModel> rangeModelList = rangeMapper.list(new HumanCondition());
        Map<String, RangeModel> id2RangeModel = new HashMap<>();
        Map<String, CollectAgentConfig> configs = new HashMap<>();
        for (RangeModel r : rangeModelList) {
            id2RangeModel.put(r.getRangeName(), r);
            configs.put(r.getRangeName(), CollectAgentConfig.defaultValue());
        }
        log.info("查询所有分靶场耗时 {}", System.currentTimeMillis() - timeA);

        timeA = System.currentTimeMillis();
        // 如果数据库中有数据 ， 则使用数据库中的数据覆盖默认配置
        SysConfigMapper sysConfigMapper = BeanCommon.getBean(SysConfigMapper.class);
        var models = sysConfigMapper.list(new HumanCondition().filterStartsWith(SysConfigModel::getKeyName, "collect_agent_info"));
        for (var model : models) {
            var result = JsonCommon.parse(model.getValue(), CollectAgentConfig.class);
            String rangeId = StringUtils.replace(model.getKeyName(), "collect_agent_info_", "");
            if (id2RangeModel.containsKey(rangeId)) {
                var rangeName = id2RangeModel.get(rangeId).getRangeName();
                configs.put(rangeName, result);
            }
        }
        log.info("查询所有分靶场周期配置耗时 {}", System.currentTimeMillis() - timeA);

        timeA = System.currentTimeMillis();
        // 查询所有运行着的场景
        var instances = csceneService.getRunningCscenes();
        log.info("查询所有运行着的场景耗时 {}", System.currentTimeMillis() - timeA);

        // 顺序执行所有场景
        for (var instance : instances) {
//            if (!instance.getId().equals("ffff18d5d9d34ac2996862c1097cd56a")) {
//                continue;
//            }
            handleOneInstance(instance, configs, currentTime);
        }

//         并行执行所有场景
//        List<Runnable> tasks = new ArrayList<>();
//        for (var instance : instances) {
////            if (!instance.getId().equals("ffff18d5d9d34ac2996862c1097cd56a")) {
////                continue;
////            }
//            tasks.add(() -> handleOneInstance(instance, configs, currentTime));
//        }
//        RangeTaskCommon.runTasks(tasks);
    }

    private void handleOneInstance(CsceneInstanceModel instance, Map<String, CollectAgentConfig> configs, LocalDateTime currentTime) {
        long timeA = System.currentTimeMillis();
        try {
            rangeDataLocalService.test(instance.getId(), true, true, true, currentTime);
        } catch (Exception e) {
            log.error("failed to get range data local , cscene instance id {}", instance.getId(), e);
        }
        log.info("查询场景 {} 主靶场数据耗时 {}", instance.getId(), System.currentTimeMillis() - timeA);

        timeA = System.currentTimeMillis();
        try {
            if (!removedIds.contains(instance.getId())) {
                rangeDataRemoteService.test(instance.getId(), startTime, configs, currentTime);
            }
        } catch (MpRuntimeException e) {
            log.error("failed to get range data remote , cscene instance id {} , exception : {}", instance.getId(), e.getMessage());
            removedIds.add(instance.getId());
        } catch (Exception e) {
            log.error("failed to get range data remote , cscene instance id {}", instance.getId(), e);
        }
        log.info("查询场景 {} 分靶场数据耗时 {}", instance.getId(), System.currentTimeMillis() - timeA);
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

        public static CollectAgentConfig defaultValue() {
            CollectAgentConfig agentConfig = new CollectAgentConfig();
            agentConfig.setEnable(true);
            agentConfig.setPeriod(1);
            agentConfig.setInfo(true);
            agentConfig.setFlow(true);
            agentConfig.setEvent(true);
            agentConfig.setLog(true);
            return agentConfig;
        }
    }
}
