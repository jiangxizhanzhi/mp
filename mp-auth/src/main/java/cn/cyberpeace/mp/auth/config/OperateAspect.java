package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.base.common.BeanCommon;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class OperateAspect {


    @Pointcut(value = "@annotation(cn.cyberpeace.mp.auth.config.OperateAuth)")
    public void pointCut() {

    }

    @Around(value = "pointCut() && @annotation(opearte))")
    public Object before(ProceedingJoinPoint joinPoint, OperateAuth opearte) {
        try {
            IOperateResource operateResource = (IOperateResource) BeanCommon.getBean(opearte.resourceType());
            operateResource.resourceType(joinPoint.getArgs(), opearte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}