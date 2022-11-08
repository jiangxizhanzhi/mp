package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/30
 */
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
