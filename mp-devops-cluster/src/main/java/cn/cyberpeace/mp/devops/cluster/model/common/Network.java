package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
