package cn.cyberpeace.mp.collection.core.model.es.hostmonitor;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WazuhProcess {

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "parent_name")
    private String parentName;

    @JSONField(name = "create_time")
    private String createTime;

    @JSONField(name = "parent_id")
    private String parentId;

    @JSONField(name = "vms")
    private String vms;

    @JSONField(name = "cwd")
    private String cwd;

    @JSONField(name = "rss")
    private String rss;

    @JSONField(name = "priority")
    private String priority;

    @JSONField(name = "num_threads")
    private String numThreads;

    @JSONField(name = "command")
    private String command;

    @JSONField(name = "username")
    private String username;

    @JSONField(name = "pid")
    private String pid;

    @JSONField(name = "exe")
    private String exe;

    @JSONField(name = "name")
    private String name;

    private LocalDateTime time;

    private String agentIp;

    private String agentName;
}
