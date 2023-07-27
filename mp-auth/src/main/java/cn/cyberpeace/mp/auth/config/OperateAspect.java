package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.RoleAuthControlConstant;
import cn.cyberpeace.mp.auth.handler.AbstractRoleHandler;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Aspect
public class OperateAspect {

    @Resource
    private UserService userService;

    @Pointcut(value = "@annotation(cn.cyberpeace.mp.auth.config.OperateAuth)")
    public void pointCut() {

    }

    @Around(value = "pointCut() && @annotation(opearte))")
    public Object before(ProceedingJoinPoint joinPoint, OperateAuth opearte) {
        if (this.isTrailAccount()) {
            IOperateResource operateResource = (IOperateResource) BeanCommon.getBean(opearte.resourceType());
            try {
                operateResource.resourceType(joinPoint.getArgs(), opearte);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
        }
        try {
            return joinPoint.proceed();
        } catch (MpRuntimeException mpRuntimeException) {
            throw mpRuntimeException;
        } catch (Throwable e) {
            log.error("faild to exec proceed", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isTrailAccount() {
        UserBo user = null;
        try {
            user = userService.getCurrentUserDto();
        } catch (MpRuntimeException e) {
            user = null;
        }
        if (user != null && RoleAuthControlConstant.TRAIL_TYPE.TRUE.getType().equals(user.getIsTrialAccount())) {
            return true;
        }
        return false;
    }

}