package cn.cyberpeace.mp.scenepluginconfig.constant;

import cn.cyberpeace.mp.scenepluginconfig.config.ConfigAppConfig;
import org.springframework.stereotype.Component;

@Component
public class ConfigSingleton {

    public static ConfigAppConfig configAppConfig;

    private ConfigSingleton(ConfigAppConfig configAppConfig) {
        ConfigSingleton.configAppConfig = configAppConfig;
    }

}
