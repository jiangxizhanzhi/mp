 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ThreadPoolInfo.java.
 // File size 1104.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.util.thread.taskpool;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wangssh
 * @version 1.0
 * @description 线程池信息
 * @date 2023/6/29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThreadPoolInfo {

    @ApiModelProperty(value = "线程池名前缀")
    public String threadPoolName;

    @ApiModelProperty(value = "核心线程数")
    private int coreThreadSize;

    @ApiModelProperty(value = "最大线程数")
    private int maxThreadSize;

    @ApiModelProperty(value = "当前线程数")
    private int currentThreadSize;

    @ApiModelProperty(value = "活跃线程数")
    private int activeThreadSize;

    @ApiModelProperty(value = "峰值线程数")
    private int largestPoolSize;

    @ApiModelProperty(value = "任务队列大小")
    private int taskQueueSize;

    @ApiModelProperty(value = "总任务数")
    private long taskCount;


}
