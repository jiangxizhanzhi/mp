package cn.cyberpeace.mp.common.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.common.config.CommonAppConfig;

/**
 * 模块下单例对象的快捷引用
 */
public class CommonSingleton {

    public static final CommonAppConfig commonAppConfig = BeanCommon.getBean(CommonAppConfig.class);

    private CommonSingleton() {}

}
