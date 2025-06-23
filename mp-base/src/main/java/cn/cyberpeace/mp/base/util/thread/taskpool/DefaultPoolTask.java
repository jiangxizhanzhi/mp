 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultPoolTask.java.
 // File size 478.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
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
