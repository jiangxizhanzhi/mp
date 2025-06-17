package cn.cyberpeace.mp.sceneplugincollection.service.collect.port;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WazuhPortModel {
    private String agentId;
    private String agentName;
    private String status;
    private String proto;
    private Integer raddrPort;
    private String laddrIp;
    private Integer pid;
    private Integer laddrPort;
    private String raddrIp;
    private String process;
}
