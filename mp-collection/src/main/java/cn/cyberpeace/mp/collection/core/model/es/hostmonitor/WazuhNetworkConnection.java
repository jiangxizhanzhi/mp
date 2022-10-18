package cn.cyberpeace.mp.collection.core.model.es.hostmonitor;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WazuhNetworkConnection {

    @JSONField(name = "pid")
    private String pid;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "proto")
    private String proto;

    @JSONField(name = "laddr_ip")
    private String laddrIp;

    @JSONField(name = "laddr_port")
    private String laddrPort;

    @JSONField(name = "raddr_ip")
    private String raddrIp;

    @JSONField(name = "raddr_port")
    private String raddrPort;

    private LocalDateTime time;

    private String agentIp;

    private String agentName;
}
