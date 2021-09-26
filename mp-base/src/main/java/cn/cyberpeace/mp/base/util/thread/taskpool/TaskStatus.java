package cn.cyberpeace.mp.base.util.thread.taskpool;

public class TaskStatus {
    public static final Integer WAITING = 0;
    public static final Integer RUNNING = 1;
    public static final Integer OVER = 2;
    public static final Integer ERROR = 3;
    public static final Integer INTERRUPTED = 4;

    private TaskStatus() {}
}
