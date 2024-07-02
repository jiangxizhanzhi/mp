package cn.cyberpeace.mp.scenepluginflow.constant;

import cn.cyberpeace.mp.scenepluginflow.config.FlowAppConfig;
import org.springframework.stereotype.Component;

@Component
public class FlowSingleton {

    public static FlowAppConfig flowAppConfig;

    /**
     * FlowSingleton
     *
     * @param flowAppConfig
     */
    private FlowSingleton(FlowAppConfig flowAppConfig) {
        FlowSingleton.flowAppConfig = flowAppConfig;
    }

}
