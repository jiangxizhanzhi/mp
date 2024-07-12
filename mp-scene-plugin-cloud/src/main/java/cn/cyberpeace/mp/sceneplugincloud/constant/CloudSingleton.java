 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudSingleton.java.
 // File size 594.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName CloudSingleton
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class CloudSingleton {

    public static CloudAppConfig cloudAppConfig;

    /**
     * CloudSingleton
     *
     * @param cloudAppConfig
     */
    private CloudSingleton(CloudAppConfig cloudAppConfig) {
        CloudSingleton.cloudAppConfig = cloudAppConfig;
    }

}
