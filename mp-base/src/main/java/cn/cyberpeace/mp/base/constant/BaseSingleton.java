package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;

/**
 * 模块下单例对象的快捷引用
 */
public class BaseSingleton {

    /** 模块配置bean的引用 TODO: 适配配置热更新场景 */
    public static final BaseAppConfig baseAppConfig = BeanUtils.getBean(BaseAppConfig.class);

    private BaseSingleton() {}

}
