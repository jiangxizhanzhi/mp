package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;

/**
 * 模块下单例对象的快捷引用
 */
public class BaseSingleton {

    /** 模块配置bean的引用 TODO: 适配配置热更新场景 */
    public static final BaseAppConfig baseAppConfig = BeanCommon.getBean(BaseAppConfig.class);

    private BaseSingleton() {}

}
