 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventStatus.java.
 // File size 477.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
