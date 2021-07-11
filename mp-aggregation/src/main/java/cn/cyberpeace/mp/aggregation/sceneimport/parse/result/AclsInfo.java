package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AclsInfo {
    private String protocol;
    private String action;
    private String direction;
    private String sourceIP;
    private String sourcePort;
    private String targetIP;
    private String targetPort;
}
