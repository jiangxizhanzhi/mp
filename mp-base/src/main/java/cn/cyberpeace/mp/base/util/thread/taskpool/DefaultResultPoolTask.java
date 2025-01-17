 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultResultPoolTask.java.
 // File size 341.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

@Data
@NoArgsConstructor
public class DefaultResultPoolTask<R> extends ResultPoolTask<Object, R> {
    public DefaultResultPoolTask(Callable<R> task) {
        super(task);
    }
}
