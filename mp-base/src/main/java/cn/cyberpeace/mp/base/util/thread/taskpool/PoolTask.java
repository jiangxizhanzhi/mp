package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PoolTask
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePoolTask
 */
@Data
@NoArgsConstructor
public class PoolTask<C> extends BasePoolTask<C, Runnable, Object> {
    /**
     * PoolTask
     *
     * @param task
     */
    public PoolTask(Runnable task) {
        setTask(task);
    }

    /**
     * invoke
     */
    @Override
    public void invoke() {
        getTask().run();
    }
}
