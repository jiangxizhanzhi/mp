 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionSingleton.java.
 // File size 669.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.constant;

import cn.cyberpeace.mp.sceneplugincollection.config.CollectionAppConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName CollectionSingleton
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class CollectionSingleton {

    public static CollectionAppConfig collectionAppConfig;

    /**
     * CollectionSingleton
     *
     * @param collectionAppConfig
     */
    private CollectionSingleton(CollectionAppConfig collectionAppConfig) {
        CollectionSingleton.collectionAppConfig = collectionAppConfig;
    }

}
