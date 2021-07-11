package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.scene.core.scene.SceneHandler;


public interface InterruptedStrategy {
    void action(String sceneId, SceneHandler sceneHandler);
}
