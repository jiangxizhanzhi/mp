 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonSingleton.java.
 // File size 624.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.common.config.CommonAppConfig;
import org.springframework.stereotype.Component;

/**
 * @ClassName CommonSingleton
 * @Description 模块下单例对象的快捷引用
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
public class CommonSingleton {

    public static CommonAppConfig commonAppConfig;

    /**
     * CommonSingleton
     *
     * @param commonAppConfig
     */
    private CommonSingleton(CommonAppConfig commonAppConfig) {
        CommonSingleton.commonAppConfig = commonAppConfig;
    }

}
