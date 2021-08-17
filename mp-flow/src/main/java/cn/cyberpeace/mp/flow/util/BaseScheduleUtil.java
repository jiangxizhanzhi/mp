package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
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
        log.info("开始执行任务==> 延迟时间：{}，执行间隔：{}，执行次数：{}", delay, interval, count);

        if (count > 0) {
            this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
            this.scheduledThreadPoolExecutor.scheduleAtFixedRate(
                    () -> executor(), delay, interval, TimeUnit.SECONDS
            );
        } else {
            log.warn("无需执行任务");
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
            log.info("当前执行次数：{}，间隔时间：{}秒", currentAtomicInteger.get(), interval);
            // 调用流量发生接口
            getMethod().execute(getFlowBaseModel());
            //当满足条件时，停止任务
            if (currentAtomicInteger.get() == count) {
                // 停止任务
                this.scheduledThreadPoolExecutor.shutdownNow();
                log.info("停止任务");
            } else {
                // 执行次数加1
                currentAtomicInteger.getAndIncrement();
                log.info("执行下次任务");
            }
        } else {
            this.scheduledThreadPoolExecutor.shutdownNow();
            this.scheduledThreadPoolExecutor = null;
        }
    }


}
