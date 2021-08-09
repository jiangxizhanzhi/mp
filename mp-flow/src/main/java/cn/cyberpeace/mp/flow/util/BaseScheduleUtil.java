package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description: 定时任务基本工具类
 * @Autho qiaokang
 * @Date：2021/6/16 11:35
 */
@Slf4j
@Service
@Data
public class BaseScheduleUtil<B extends FlowBaseModel, M extends ScheduleMethod> {

    private B flowBaseModel;

    private M method;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;

    /**
     * 当前执行点
     */
    private AtomicInteger currentAtomicInteger = new AtomicInteger(1);

    /**
     * 初始化任务
     */
    public void schedule() {
        FlowBaseModel flowBaseModel = this.getFlowBaseModel();
        long delay = flowBaseModel.getDelay();
        long interval = flowBaseModel.getInterval() <= 0 ? 1L : flowBaseModel.getInterval();
        int count = flowBaseModel.getCount();
        log.info("start task==> delay：{}，interval：{}，count：{}", delay, interval, count);

        if (count > 0) {
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("scheduled-thread-pool-%d").build();
            this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2, namedThreadFactory);
            this.scheduledThreadPoolExecutor.scheduleAtFixedRate (
                    () -> executor(), delay, interval, TimeUnit.SECONDS
            );
        } else {
            log.warn("no need to run task");
        }
    }

    /**
     * 执行任务
     */
    private void executor() {
        FlowBaseModel flowBaseModel = this.getFlowBaseModel();
        long interval = flowBaseModel.getInterval() <= 0 ? 1L : flowBaseModel.getInterval();
        int count = flowBaseModel.getCount();

        if (currentAtomicInteger.get() < (count + 1)) {
            //需要处理的逻辑
            log.info("current count：{}，delay：{} second", currentAtomicInteger.get(), interval);
            // 调用流量发生接口
            getMethod().execute(getFlowBaseModel());
            //当满足条件时，停止任务
            if (currentAtomicInteger.get() == count) {
                // 停止任务
                this.scheduledThreadPoolExecutor.shutdownNow();
                log.info("task stop");
            } else {
                // 执行次数加1
                currentAtomicInteger.getAndIncrement();
                log.info("execute next task");
            }
        } else {
            this.scheduledThreadPoolExecutor.shutdownNow();
            this.scheduledThreadPoolExecutor = null;
        }
    }


}
