 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResultPoolTask.java.
 // File size 616.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

/**
 * @ClassName ResultPoolTask
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePoolTask
 */
@Data
@NoArgsConstructor
public class ResultPoolTask<C, R> extends BasePoolTask<C, Callable<R>, R> {
    public ResultPoolTask(Callable<R> task) {
        setTask(task);
    }

    /**
     * invoke
     */
    @Override
    public void invoke() throws Exception {
        setResult(getTask().call());
    }
}
