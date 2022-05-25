package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import org.springframework.stereotype.Component;

@Component
public class CloudSingleton {

    public static CloudAppConfig cloudAppConfig;

    private CloudSingleton(CloudAppConfig cloudAppConfig) {
        CloudSingleton.cloudAppConfig = cloudAppConfig;
    }

}
