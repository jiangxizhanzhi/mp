 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PoolTask.java.
 // File size 359.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PoolTask<C> extends BasePoolTask<C, Runnable, Object> {
    public PoolTask(Runnable task) {
        setTask(task);
    }

    @Override
    public void invoke() {
        getTask().run();
    }
}
