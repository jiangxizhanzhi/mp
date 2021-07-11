package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.common.config.CommonAppConfig;
import org.springframework.stereotype.Component;


@Component
public class CommonSingleton {

    public static CommonAppConfig commonAppConfig;

    private CommonSingleton(CommonAppConfig commonAppConfig) {
        CommonSingleton.commonAppConfig = commonAppConfig;
    }

}
