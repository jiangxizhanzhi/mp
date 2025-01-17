 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaskStatus.java.
 // File size 345.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

public class TaskStatus {
    public static final Integer WAITING = 0;
    public static final Integer RUNNING = 1;
    public static final Integer OVER = 2;
    public static final Integer ERROR = 3;
    public static final Integer INTERRUPTED = 4;

    private TaskStatus() {}
}
