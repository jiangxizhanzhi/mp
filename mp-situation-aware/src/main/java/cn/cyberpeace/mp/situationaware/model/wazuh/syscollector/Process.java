package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 14:15
 */
@Data
public class Process {

    private Scan scan;

    private String ruser;

    private Integer resident;

    @JSONField(name = "start_time")
    private Long startTime;

    private Integer share;

    private String rgroup;

    private String fgroup;

    private String cmd;

    private String suser;

    private Long stime;

    private String priority;

    private String argvs;

    private String sgroup;

    private Integer ppid;

    private Long tty;

    private Integer processor;

    private Long utime;

    private String euser;

    @JSONField(name = "vm_size")
    private Long vmSize;

    private Integer tgid;

    private Integer pgrp;

    private Long size;

    private String name;

    private Integer pid;

    private Integer session;

    private Integer nice;

    private Integer nlwp;

    private String egroup;

    private String state;

    @JSONField(name = "agent_id")
    private String agentId;


}
