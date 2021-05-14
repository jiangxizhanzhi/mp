package cn.cyberpeace.mp.scene.core.event;

public class ProcessStatus {
    public static final Integer INITIAL = 0;
    public static final Integer START = 1;
    public static final Integer IN_PROGRESS = 2;
    public static final Integer PAUSE = 3;
    public static final Integer OVER = 4;
    public static final Integer ABNORMAL = 5;

    private ProcessStatus() {}
}
