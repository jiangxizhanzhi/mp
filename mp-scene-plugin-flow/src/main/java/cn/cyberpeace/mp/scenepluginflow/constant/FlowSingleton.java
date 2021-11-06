package cn.cyberpeace.mp.scenepluginflow.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.scenepluginflow.config.FlowAppConfig;

public class FlowSingleton {

    public static final FlowAppConfig flowAppConfig = BeanCommon.getBean(FlowAppConfig.class);

    private FlowSingleton() {}

}
