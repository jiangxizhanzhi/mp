package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.RangeTaskCommon;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.ConcurrentHashSet;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class RangeDataUtil {

    private ReentrantLock lock = new ReentrantLock();

    private volatile long cycleTimes = 0L;

    private Set<String> ignoreIds = new ConcurrentHashSet<>();

    ScheduledExecutorService scheduleExecutor = Executors.newScheduledThreadPool(1);

    ExecutorService workExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.DiscardOldestPolicy() // 拒绝策略
    );

    private boolean executed = false;

    public static Map<String, CollectAgentConfig> configs = null;

    @Scheduled(cron = "0/5 * * * * ?")
    public void registerTasks() {
        if (!executed) {
            // 在第0秒时注册一下任务，任务每分钟执行一次
            scheduleExecutor.scheduleAtFixedRate(() -> doWork(), 0, 1, TimeUnit.MINUTES);
            executed = true;
        }
    }

    public void doWork() {
        // 周期按照分钟计算 startTime 表示从第一次启动到现在经过了几分钟
        long finalCycleTimes = cycleTimes++;
        LocalDateTime currentTime = LocalDateTime.now();
        String traceId = TextUtils.uuid();
        log.info("尝试执行采集数据查询：current time : {} , trace id is {},cycleTimes is {}", TimeUtils.localDateTimeToString(currentTime), traceId, cycleTimes);
        workExecutor.submit(ThreadUtils.format(() -> {
            SysContextHolder.setCallChainId(traceId);
            new RangeDataQueryTask(finalCycleTimes, currentTime, lock, ignoreIds).run();
        }));
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

    @Getter
    @Setter
    private static class RangeDataQueryTask implements Runnable {
        private long cycleTimes;

        private LocalDateTime currentTime;

        private ReentrantLock lock;

        private RangeDataRemoteService rangeDataRemoteService;

        private RangeDataLocalService rangeDataLocalService;

        private CsceneService csceneService;

        private RangeMapper rangeMapper;

        private Set<String> ignoreIds;

        public RangeDataQueryTask(long cycleTimes, LocalDateTime currentTime, ReentrantLock lock, Set<String> ignoreIds) {
            this.cycleTimes = cycleTimes;
            this.currentTime = currentTime;
            this.lock = lock;
            this.rangeMapper = BeanCommon.getBean(RangeMapper.class);
            this.csceneService = BeanCommon.getBean(CsceneService.class);
            this.rangeDataLocalService = BeanCommon.getBean(RangeDataLocalService.class);
            this.rangeDataRemoteService = BeanCommon.getBean(RangeDataRemoteService.class);
            this.ignoreIds = ignoreIds;
        }

        @Override
        public void run() {
            log.info("正在执行任务:{}", TimeUtils.localDateTimeToString(currentTime));
            this.doAction();
        }

        private void doAction() {
            long timeA = System.currentTimeMillis();
            // 查询所有的分靶场 并给每个靶场设置默认的周期配置
            List<RangeModel> rangeModelList = rangeMapper.list(new HumanCondition());
            Map<String, RangeModel> id2RangeModel = new HashMap<>();
            Map<String, CollectAgentConfig> configs = new HashMap<>();
            for (RangeModel r : rangeModelList) {
                id2RangeModel.put(r.getId(), r);
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
            log.info("周期配置：{}", JsonCommon.stringify(configs));
            log.info("查询所有分靶场周期配置耗时 {}", System.currentTimeMillis() - timeA);
            RangeDataUtil.configs = configs;

            timeA = System.currentTimeMillis();
            // 查询所有运行着的场景
            var instances = csceneService.getRunningCscenes();
            log.info("查询所有运行着的场景耗时 {}", System.currentTimeMillis() - timeA);

//        // 顺序执行所有场景
//        for (var instance : instances) {
////            if (!instance.getId().equals("ffff18d5d9d34ac2996862c1097cd56a")) {
////                continue;
////            }
//            handleOneInstance(instance, configs, currentTime);
//        }

//         并行执行所有场景
            List<Runnable> tasks = new ArrayList<>();
            for (var instance : instances) {
                tasks.add(() -> handleOneInstance(instance, configs, currentTime));
            }
            RangeTaskCommon.runTasks(tasks);
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
                rangeDataRemoteService.test(instance.getId(), cycleTimes, configs, currentTime);
            } catch (MpRuntimeException e) {
                log.error("failed to get range data remote , cscene instance id {} , exception : {}", instance.getId(), e.getMessage());
            } catch (Exception e) {
                log.error("failed to get range data remote , cscene instance id {}", instance.getId(), e);
            }
            log.info("查询场景 {} 分靶场数据耗时 {}", instance.getId(), System.currentTimeMillis() - timeA);
        }
    }
}
