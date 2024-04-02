package cn.cyberpeace.mp.scene.core.event.dispatch;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scene.constant.SceneErrorCode;
import cn.cyberpeace.mp.scene.core.event.SceneEventHandler;
import cn.cyberpeace.mp.scene.core.event.interrupt.MpSceneOperateInterrupted;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.SceneEventUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/12
 */
@Slf4j
public class SceneHandlerProxy implements MethodInterceptor {
    private SceneHandler sceneHandler;

    public SceneHandlerProxy(SceneHandler sceneHandler) {
        this.sceneHandler = sceneHandler;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, org.springframework.cglib.proxy.MethodProxy methodProxy) throws Throwable {
        try {
            return method.invoke(sceneHandler, objects);
        } catch (Exception e) {
            Throwable realException = e;
            if (e instanceof InvocationTargetException) {
                realException = ((InvocationTargetException) e).getTargetException();
            }
            if (realException instanceof MpSceneOperateInterrupted) {
                MpSceneOperateInterrupted interrupted = (MpSceneOperateInterrupted) realException;
                interrupted.getStrategy().action(sceneHandler.getSceneId(), sceneHandler);
                return false;
            } else {
                log.error("invoke error , {}", e);
                throw e;
            }
        } finally {
            if (sceneHandler.getSceneId() != null) {
                SceneEventControllerUtils.releaseEventLock(sceneHandler.getSceneId());
            }
        }
    }

}
