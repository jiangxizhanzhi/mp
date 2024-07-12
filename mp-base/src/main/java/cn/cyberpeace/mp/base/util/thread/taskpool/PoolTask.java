 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PoolTask.java.
 // File size 587.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
