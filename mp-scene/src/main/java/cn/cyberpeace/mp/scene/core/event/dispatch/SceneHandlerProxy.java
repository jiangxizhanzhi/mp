package cn.cyberpeace.mp.scene.core.event.dispatch;

import cn.cyberpeace.mp.scene.core.event.interrupt.MpSceneOperateInterrupted;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


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
                throw e;
            }
        } finally {
            SceneEventControllerUtils.releaseEventLock(sceneHandler.getSceneId());
        }
    }

}
