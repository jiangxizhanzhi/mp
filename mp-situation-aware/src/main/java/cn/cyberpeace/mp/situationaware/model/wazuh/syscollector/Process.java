package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@ApiModel("进程信息")
public class Process {

    private Scan scan;

    @ApiModelProperty(value = "真实用户")
    private String ruser;

    @ApiModelProperty(value = "进程驻留大小")
    private Integer resident;

    @ApiModelProperty(value = "开始时间")
    @JSONField(name = "start_time")
    private String startTime;

    @ApiModelProperty(value = "共享内存")
    private Integer share;

    @ApiModelProperty(value = "真实组")
    private String rgroup;

    @ApiModelProperty(value = "文件系统组名")
    private String fgroup;

    @ApiModelProperty(value = "执行的命令")
    private String cmd;

    @ApiModelProperty(value = "保存集用户")
    private String suser;

    @ApiModelProperty(value = "系统代码执行时长")
    private Long stime;

    @ApiModelProperty(value = "优先级")
    private String priority;

    @ApiModelProperty(value = "进程参数")
    private String argvs;

    @ApiModelProperty(value = "保存集组")
    private String sgroup;

    @ApiModelProperty(value = "父进程ID")
    private Integer ppid;

    @ApiModelProperty(value = "进程TTY数")
    private Long tty;

    @ApiModelProperty(value = "处理器编号")
    private Integer processor;

    @ApiModelProperty(value = "用户代码执行时长")
    private Long utime;

    @ApiModelProperty(value = "有效用户")
    private String euser;

    @ApiModelProperty(value = "虚拟内存总大小(KB)")
    @JSONField(name = "vm_size")
    private Long vmSize;

    @ApiModelProperty(value = "线程组ID")
    private Integer tgid;

    @ApiModelProperty(value = "进程组")
    private Integer pgrp;

    @ApiModelProperty(value = "进程大小(KB)")
    private Long size;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "父进程")
    private Integer pid;

    @ApiModelProperty(value = "会话")
    private Integer session;

    
    private Integer nice;

    @ApiModelProperty(value = "轻量级进程数")
    private Integer nlwp;

    @ApiModelProperty(value = "线程组 ID")
    private String egroup;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "代理ID")
    @JSONField(name = "agent_id")
    private String agentId;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "级别")
    private int level;

}
