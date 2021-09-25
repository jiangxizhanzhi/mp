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
