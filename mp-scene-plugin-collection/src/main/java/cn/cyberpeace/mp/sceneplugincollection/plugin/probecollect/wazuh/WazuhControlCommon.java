 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhControlCommon.java.
 // File size 1673.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName WazuhControlCommon
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class WazuhControlCommon {

    public static WazuhControl wazuhControl;

    /**
     * WazuhControlCommon
     *
     * @param collectionSingleton
     */
    private WazuhControlCommon(CollectionSingleton collectionSingleton) {
        WazuhControl.WazuhControlBuilder builder =  WazuhControl.builder()
                .configWazuh(
                        CollectionSingleton.collectionAppConfig.getWazuhApiServer(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiUser(),
                        CollectionSingleton.collectionAppConfig.getWazuhApiPass())
                .configMongo(CollectionSingleton.collectionAppConfig.getWazuhMongoServer())
                .configEs(CollectionSingleton.collectionAppConfig.getWazuhEsServer())
                .configGodfs(CollectionSingleton.collectionAppConfig.getWazuhGodfsServer());

        String httpCallbackUrl = CollectionSingleton.collectionAppConfig.getWazuhRunCommandHttpCallbackUrl();
        if (!StringUtils.isEmpty(httpCallbackUrl)) {
            builder.configCallbackSelf(true)
                    .configHttpCallbacks(new WazuhLauncher.RunCommandHttpCallback(httpCallbackUrl, null));
        }

        WazuhControlCommon.wazuhControl = builder.build();
    }

}
