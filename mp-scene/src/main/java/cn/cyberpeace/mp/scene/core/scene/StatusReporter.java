package cn.cyberpeace.mp.scene.core.scene;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/21
 */
public interface StatusReporter<T> {
    public void reportStatus(T data);
}
