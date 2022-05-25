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
