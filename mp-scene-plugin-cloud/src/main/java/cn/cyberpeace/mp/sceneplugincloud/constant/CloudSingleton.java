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
