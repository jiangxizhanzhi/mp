package cn.cyberpeace.mp.scene.core.event.dispatch;

/**
 * @Description 场景事件控制接口
 * @Author maojun
 * @Date 2022/5/12
 */
public interface SceneEventControl {
    /**
     * 获取场景下一个需要执行的目标事件
     * @return
     */
    Integer getLatestEvent();

    /**
     * 更新场景下一个需要执行的目标事件
     * @param event
     * @param eventStatus
     * @return
     */
    boolean tryUpdateLatestEvent(int event, int eventStatus);

    /**
     * 获取事件执行权限
     * @param event
     * @param eventStatus
     * @return
     */
    boolean acquireEventLock(int event, int eventStatus);

    /**
     * 释放事件执行权限的锁
     * @return
     */
    boolean releaseEventLock();
}
