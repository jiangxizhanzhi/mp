package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

/**
 * @ClassName DefaultResultPoolTask
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ResultPoolTask
 */
@Data
@NoArgsConstructor
public class DefaultResultPoolTask<R> extends ResultPoolTask<Object, R> {
    /**
     * DefaultResultPoolTask
     *
     * @param task
     */
    public DefaultResultPoolTask(Callable<R> task) {
        super(task);
    }
}
