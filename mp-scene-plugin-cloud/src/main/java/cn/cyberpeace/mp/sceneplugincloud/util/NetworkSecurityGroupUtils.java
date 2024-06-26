package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import com.azure.resourcemanager.network.models.NetworkSecurityGroup;
import com.azure.resourcemanager.network.models.SecurityRuleProtocol;
import org.apache.commons.lang3.StringUtils;

public class NetworkSecurityGroupUtils {

    public static void addInRule(String protocol, NetworkSecurityGroup.Update nsgUpdate, int i) {
        if (StringUtils.isNotBlank(protocol)) {
            nsgUpdate
                    .defineRule("allow-" + protocol)
                    .allowInbound()
                    .fromAnyAddress()
                    .fromAnyPort()
                    .toAnyAddress()
                    .toPort(AccessMode.Protocol.DEFAULT_PORT_MAP.get(protocol))
                    .withProtocol(SecurityRuleProtocol.TCP)
                    .withPriority(i)
                    .attach();
        }
    }
}
