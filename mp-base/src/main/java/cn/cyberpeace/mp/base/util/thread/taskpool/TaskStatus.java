 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaskStatus.java.
 // File size 494.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

/**
 * @ClassName TaskStatus
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class TaskStatus {
    public static final Integer WAITING = 0;
    public static final Integer RUNNING = 1;
    public static final Integer OVER = 2;
    public static final Integer ERROR = 3;
    public static final Integer INTERRUPTED = 4;

    /**
     * TaskStatus
     */
    private TaskStatus() {}
}
