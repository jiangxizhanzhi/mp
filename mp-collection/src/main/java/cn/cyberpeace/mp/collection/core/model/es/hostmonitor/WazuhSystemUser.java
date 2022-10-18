package cn.cyberpeace.mp.collection.core.model.es.hostmonitor;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WazuhSystemUser {

    @JSONField(name = "comment")
    private String comment;

    @JSONField(name = "shell")
    private String shell;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "group_id")
    private String groupId;

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "dir")
    private String dir;

    private LocalDateTime time;

    private String agentIp;

    private String agentName;
}
