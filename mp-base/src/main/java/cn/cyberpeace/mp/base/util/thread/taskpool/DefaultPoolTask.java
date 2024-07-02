package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DefaultPoolTask
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see PoolTask
 */
@Data
@NoArgsConstructor
public class DefaultPoolTask extends PoolTask<Object> {
    /**
     * DefaultPoolTask
     *
     * @param task
     */
    public DefaultPoolTask(Runnable task) {
        super(task);
    }
}
