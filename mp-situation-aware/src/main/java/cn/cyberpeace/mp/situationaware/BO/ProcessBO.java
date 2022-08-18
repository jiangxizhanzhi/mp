package cn.cyberpeace.mp.situationaware.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProcessBO {
    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "父进程名称")
    private String parentName;

    @ApiModelProperty(value = "开始时间")
    private String createTime;

    @ApiModelProperty(name = "父进程id")
    private String parentId;

    @ApiModelProperty(value = "虚拟内存总大小")
    private String vmSize;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "进程数量")
    private String numThreads;

    @ApiModelProperty(value = "执行的命令")
    private String cmd;

    @ApiModelProperty(value = "有效用户")
    private String euser;

    @ApiModelProperty(value = "进程id")
    private String pid;

    @ApiModelProperty(value = "进程名称")
    private String name;

    @ApiModelProperty(value = "上报时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "主机Ip")
    private String agentIp;
}
