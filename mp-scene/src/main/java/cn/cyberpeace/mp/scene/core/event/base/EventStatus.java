package cn.cyberpeace.mp.scene.core.event.base;

/**
 * @ClassName EventStatus
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class EventStatus {
    public static final int INITIAL = 0;
    public static final int START = 1;
    public static final int IN_PROGRESS = 2;
    public static final int PAUSE = 3;
    public static final int OVER = 4;
    public static final int ABNORMAL = 5;

    private EventStatus() {}
}
