package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class HandlerCommon {

    private final List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();

    private final CloudScenePluginHandler pluginHandler;

    public List<String> randomCidrs(int count, List<String> existCidrs) {
        return IpUtils.randomCidrs(cidrPool, count, existCidrs);
    }

    public List<String> statExistCidrs() {
        List<String> existCidrs = new ArrayList<>();
        pluginHandler.getElementMap().forEach((nodeId, element) -> {
            var cidr = element.getCidr();
            if (!StringUtils.isEmpty(cidr)) {
                existCidrs.add(cidr);
            }

            var auxiliaryNetworkCidr = element.getAuxiliaryNetworkCidr();
            if (!StringUtils.isEmpty(auxiliaryNetworkCidr)) {
                existCidrs.add(auxiliaryNetworkCidr);
            }
        });
        return existCidrs;
    }

    public HandlerCommon(CloudScenePluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
    }

}
