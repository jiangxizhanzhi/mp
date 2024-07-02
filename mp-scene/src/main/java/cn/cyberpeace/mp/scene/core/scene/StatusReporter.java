package cn.cyberpeace.mp.scene.core.scene;

/**
 * @ClassName StatusReporter
 * @Description
 * @Author maojun
 * @Date 2022/3/21
 * @Version 1.0.0
 */
public interface StatusReporter<T> {
    public void reportStatus(T data);
}
