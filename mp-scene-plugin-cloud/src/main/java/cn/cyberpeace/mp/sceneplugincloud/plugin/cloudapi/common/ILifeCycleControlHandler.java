package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/21
 */
public interface ILifeCycleControlHandler {
    boolean lifeControl(String nodeId, String action);
}
