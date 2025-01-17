 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePoolTask.java.
 // File size 534.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;

@Data
public abstract class BasePoolTask<C, T, R> {
    private C context;
    private T task;
    private Integer status = TaskStatus.WAITING;
    private Exception exception;
    private R result;

    public boolean isError() {
        return TaskStatus.ERROR.equals(status);
    }

    public boolean isInterrupted() {
        return TaskStatus.INTERRUPTED.equals(status);
    }

    public abstract void invoke() throws Exception;
}
