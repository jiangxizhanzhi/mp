 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultPoolTask.java.
 // File size 273.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DefaultPoolTask extends PoolTask<Object> {
    public DefaultPoolTask(Runnable task) {
        super(task);
    }
}
