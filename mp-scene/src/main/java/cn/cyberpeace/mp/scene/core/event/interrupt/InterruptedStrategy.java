package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.scene.core.scene.SceneHandler;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/12
 */
public interface InterruptedStrategy {
    void action(String sceneId, SceneHandler sceneHandler);
}
