package cn.cyberpeace.mp.base.constant;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.BeanUtils;

public class BaseSingleton {

    public static final BaseAppConfig baseAppConfig = BeanUtils.getBean(BaseAppConfig.class);

    private BaseSingleton() {}

}
