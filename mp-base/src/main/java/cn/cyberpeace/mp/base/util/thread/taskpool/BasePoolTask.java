package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;

/**
 * @ClassName BasePoolTask
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public abstract class BasePoolTask<C, T, R> {
    private C context;
    private T task;
    private Integer status = TaskStatus.WAITING;
    private Exception exception;
    private R result;

    /**
     * isError
     *
     * @return boolean
     */
    public boolean isError() {
        return TaskStatus.ERROR.equals(status);
    }

    /**
     * isInterrupted
     *
     * @return boolean
     */
    public boolean isInterrupted() {
        return TaskStatus.INTERRUPTED.equals(status);
    }

    /**
     * invoke
     */
    public abstract void invoke() throws Exception;
}
