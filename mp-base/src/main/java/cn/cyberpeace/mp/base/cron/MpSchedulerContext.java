package cn.cyberpeace.mp.base.cron;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpSchedulerContext implements BeanPostProcessor, ApplicationRunner {

    private List<TaskInfo> taskInfos = new ArrayList<>();

    @Resource
    private DynamicScheduler dynamicScheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (TaskInfo taskInfo : taskInfos) {
            dynamicScheduler.startJob(taskInfo);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), (method) -> {
            MpScheduled mpScheduled = method.getAnnotation(MpScheduled.class);
            if (mpScheduled != null) {
                String taskId = mpScheduled.id();
                String desc = mpScheduled.description();
                String cron = mpScheduled.cron();
                TaskInfo.MethodTask methodTask = new TaskInfo.MethodTask(method, bean);
                taskInfos.add(new TaskInfo(taskId, desc, cron, methodTask));
            }
        });
        return bean;
    }
}
