package cn.cyberpeace.mp.base.util.thread.taskpool;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
