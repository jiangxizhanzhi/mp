 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowSingleton.java.
 // File size 464.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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
