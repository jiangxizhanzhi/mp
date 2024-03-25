package cn.cyberpeace.mp.base.cron;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpSchedulerScanner implements BeanPostProcessor, ApplicationRunner {

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
            MpScheduler mpScheduler = method.getAnnotation(MpScheduler.class);
            if (mpScheduler != null) {
                String taskId = mpScheduler.id();
                String desc = mpScheduler.description();
                String cron = mpScheduler.cron();
                MethodTask methodTask = new MethodTask(method, bean);
                taskInfos.add(new TaskInfo(taskId, desc, cron, methodTask));
            }
        });
        return bean;
    }

    private static class MethodTask implements Runnable {

        private Method method;

        private Object bean;

        public MethodTask(Method method, Object bean) {
            this.method = method;
            this.bean = bean;
        }

        @Override
        public void run() {
            try {
                method.invoke(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
