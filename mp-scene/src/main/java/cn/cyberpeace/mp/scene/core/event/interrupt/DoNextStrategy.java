package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.scene.core.event.dispatch.SceneEventControllerUtils;
import cn.cyberpeace.mp.scene.core.event.dispatch.SceneHandlerProxyBuilder;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.model.SceneModel;


public class DoNextStrategy implements InterruptedStrategy {
    @Override
    public void action(String sceneId, SceneHandler sceneHandler) {
        Integer event = SceneEventControllerUtils.getLatestEvent(sceneId);
        if (event != null) {
            SceneHandler proxy = SceneHandlerProxyBuilder.build(sceneHandler);
            SceneEventControllerUtils.releaseEventLock(sceneHandler.getSceneId());
            if (event == SceneModel.Event.DELETE) {
                proxy.logicDelete();
            } else if (event == SceneModel.Event.PAUSE) {
                proxy.pause();
            } else if (event == SceneModel.Event.UNPAUSE) {
                proxy.unpause();
            } else if (event == SceneModel.Event.INITIATE) {
                proxy.initiate();
            } else if (event == SceneModel.Event.CREATE) {
                proxy.create();
            } else if (event == SceneModel.Event.START) {
                proxy.start();
            } else if (event == SceneModel.Event.STOP) {
                proxy.stop();
            } else if (event == SceneModel.Event.RESTART) {
                proxy.restart();
            } else if (event == SceneModel.Event.SUSPEND) {
                proxy.suspend();
            } else if (event == SceneModel.Event.RESUME) {
                proxy.resume();
            }
        }
    }
}
