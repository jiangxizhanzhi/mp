package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.base.util.thread.ThreadPoolUtils;
import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import cn.cyberpeace.mp.flow.service.ScheduleMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@Data
public class BaseScheduleUtil<B extends FlowBaseModel, M extends ScheduleMethod> {

    private B flowBaseModel;

    private M method;

    private ScheduledFuture<?> future;

    
    private AtomicInteger currentAtomicInteger = new AtomicInteger(1);

    
    public void schedule() {
        FlowBaseModel flowBaseModel = this.getFlowBaseModel();
        int delay = flowBaseModel.getDelay();
        int interval = flowBaseModel.getInterval() <= 0 ? 1 : flowBaseModel.getInterval();
        int count = flowBaseModel.getCount();
        log.info("start task==> delay：{}，interval：{}，count：{}", delay, interval, count);

        if (count > 0) {
            future = ThreadPoolUtils.getScheduledExecutorService().scheduleAtFixedRate (
                    this::executor, delay, interval, TimeUnit.SECONDS
            );
        } else {
            log.warn("no need to run task");
        }
    }

    
    private void executor() {
        FlowBaseModel flowBaseModel = this.getFlowBaseModel();
        int interval = flowBaseModel.getInterval() <= 0 ? 1 : flowBaseModel.getInterval();
        int count = flowBaseModel.getCount();

        if (currentAtomicInteger.get() < (count + 1)) {
            
            log.info("current count：{}，delay：{} second", currentAtomicInteger.get(), interval);
            
            getMethod().execute(getFlowBaseModel());
            
            if (currentAtomicInteger.get() == count) {
                
                future.cancel(false);
                log.info("task stop");
            } else {
                
                currentAtomicInteger.getAndIncrement();
                log.info("execute next task");
            }
        } else {
            future.cancel(false);
            log.info("task finished");
        }
    }


}
