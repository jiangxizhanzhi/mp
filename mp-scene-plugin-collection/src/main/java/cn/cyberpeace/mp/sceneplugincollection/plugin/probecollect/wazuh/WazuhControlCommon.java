package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import org.apache.commons.lang3.StringUtils;

public class WazuhControlCommon {

    public static WazuhControl buildWazuhControl() {
        var builder =  WazuhControl.builder()
                .configWazuh(
                        CollectionSingleton.collectionAppConfig.getWazuhApiServer(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiUser(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiPass())
                .configEs(CollectionSingleton.collectionAppConfig.getWazuhEsServer())
                .configGodfs(CollectionSingleton.collectionAppConfig.getWazuhGodfsServer());

        var httpCallbackUrl = CollectionSingleton.collectionAppConfig.getWazuhRunCommandHttpCallbackUrl();
        if (!StringUtils.isEmpty(httpCallbackUrl)) {
            builder.configCallbackSelf(true)
                   .configHttpCallbacks(new WazuhLauncher.RunCommandHttpCallback(httpCallbackUrl, null));
        }

        return builder.build();
    }

}
