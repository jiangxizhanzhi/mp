 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonSingleton.java.
 // File size 440.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.common.config.CommonAppConfig;
import org.springframework.stereotype.Component;

/**
 * 模块下单例对象的快捷引用
 */
@Component
public class CommonSingleton {

    public static CommonAppConfig commonAppConfig;

    private CommonSingleton(CommonAppConfig commonAppConfig) {
        CommonSingleton.commonAppConfig = commonAppConfig;
    }

}
