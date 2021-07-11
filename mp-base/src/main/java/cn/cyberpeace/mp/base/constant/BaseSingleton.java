package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import org.springframework.stereotype.Component;


@Component
public class BaseSingleton {

    
    public static BaseAppConfig baseAppConfig;

    private BaseSingleton(BaseAppConfig baseAppConfig) {
        BaseSingleton.baseAppConfig = baseAppConfig;
    }

}
