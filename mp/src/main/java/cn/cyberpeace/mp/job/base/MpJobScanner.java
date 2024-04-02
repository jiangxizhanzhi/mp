package cn.cyberpeace.mp.job.base;

import cn.cyberpeace.mp.job.base.init.MpInitJobInfo;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.job.base.schedule.MpScheduleJobInfo;
import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MpJobScanner implements BeanPostProcessor {

    private List<MpScheduleJobInfo> SCHEDULE_JOB_INFOS = new ArrayList<>();

    private List<MpInitJobInfo> INIT_JOB_INFOS = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithMethods(bean.getClass(), (method) -> {
            this.parseMpScheduled(bean, method);
            this.parseMpInitiate(bean, method);
        });
        return bean;
    }

    public List<MpScheduleJobInfo> getScheduleJobInfos() {
        return SCHEDULE_JOB_INFOS;
    }

    public List<MpInitJobInfo> getInitJobInfos() {
        return INIT_JOB_INFOS;
    }

    private void parseMpScheduled(Object bean, Method method) {
        MpScheduled mpScheduled = method.getAnnotation(MpScheduled.class);
        if (mpScheduled != null) {
            String taskId = mpScheduled.id();
            String desc = mpScheduled.description();
            String cron = mpScheduled.cron();
            int initialDelaySeconds = mpScheduled.initialDelaySeconds();
            MethodJobWrapper methodJobWrapper = new MethodJobWrapper(method, bean);
            MpScheduleJobInfo jobInfo = new MpScheduleJobInfo(taskId, desc, cron, initialDelaySeconds, methodJobWrapper);
            SCHEDULE_JOB_INFOS.add(jobInfo);
        }
    }

    private void parseMpInitiate(Object bean, Method method) {
        MpInitiate mpInitiate = method.getAnnotation(MpInitiate.class);
        if (mpInitiate != null) {
            String id = mpInitiate.id();
            String desc = mpInitiate.description();
            int order = mpInitiate.order();
            boolean stopApplicationIfError = mpInitiate.stopApplicationIfError();
            MethodJobWrapper methodJobWrapper = new MethodJobWrapper(method, bean);
            MpInitJobInfo jobInfo = new MpInitJobInfo(id, desc, order, stopApplicationIfError, methodJobWrapper);
            INIT_JOB_INFOS.add(jobInfo);
        }
    }
}
