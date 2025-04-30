package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 公有云wazuh控制类
 */

@Component
public class PublicWazuhControlCommon {

    public static WazuhControl wazuhControl;

    public PublicWazuhControlCommon() {
        var builder = WazuhControl.builder()
                .configWazuh(
                        CollectionSingleton.collectionAppConfig.getPublicWazuhApiServer(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiUser(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiPass())
                .configMongo(CollectionSingleton.collectionAppConfig.getWazuhMongoServer())
                .configEs(CollectionSingleton.collectionAppConfig.getWazuhEsServer())
                .configGodfs(CollectionSingleton.collectionAppConfig.getWazuhGodfsServer());

        var httpCallbackUrl = CollectionSingleton.collectionAppConfig.getWazuhRunCommandHttpCallbackUrl();
        if (!StringUtils.isEmpty(httpCallbackUrl)) {
            builder.configCallbackSelf(true)
                    .configHttpCallbacks(new WazuhLauncher.RunCommandHttpCallback(httpCallbackUrl, null));
        }

        PublicWazuhControlCommon.wazuhControl = builder.build();
    }

}
