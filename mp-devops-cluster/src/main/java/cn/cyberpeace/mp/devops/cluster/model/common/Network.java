package cn.cyberpeace.mp.devops.cluster.model.common;

import java.util.List;

public class Network {

    /**
     * 网络ID
     */
    private String networkId;

    /**
     * IP V4地址
     */
    private String fixedIp;

    /**
     * 指定qos规则
     */
    private QosRule qosRule;

    /**
     * 是否分配浮动IP地址
     */
    private Boolean enableExternal;

    private List<AccessMode> accessModes;
}
