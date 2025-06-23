 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultResultPoolTask.java.
 // File size 564.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
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
