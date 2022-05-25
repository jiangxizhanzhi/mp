package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import org.springframework.stereotype.Component;

/**
 * 模块下单例对象的快捷引用
 */
@Component
public class BaseSingleton {

    /** 模块配置bean的引用, 需要在bean初始化完成之后调用 TODO: 适配配置热更新场景 */
    public static BaseAppConfig baseAppConfig;

    private BaseSingleton(BaseAppConfig baseAppConfig) {
        BaseSingleton.baseAppConfig = baseAppConfig;
    }

}
