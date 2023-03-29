package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
import cn.cyberpeace.mp.auth.handler.AbstractRoleHandler;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Aspect
public class OperateAspect {

    @Resource
    private UserService userService ;

    @Pointcut(value = "@annotation(cn.cyberpeace.mp.auth.config.OperateAuth)")
    public void pointCut() {

    }

    @Around(value = "pointCut() && @annotation(opearte))")
    public Object before(ProceedingJoinPoint joinPoint, OperateAuth opearte) {
        if (this.isTrailAccount()) {
            try {
                IOperateResource operateResource = (IOperateResource) BeanCommon.getBean(opearte.resourceType());
                operateResource.resourceType(joinPoint.getArgs(), opearte);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTrailAccount() {
        var user = userService.getCurrentUserDto();
        if (user != null && RoleAuthControlConstant.TRAIL_TYPE.TRUE.getType().equals(user.getIsTrialAccount())) {
            return true;
        }
        return false;
    }

}