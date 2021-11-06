package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;

public class CloudSingleton {

    public static final CloudAppConfig cloudAppConfig = BeanCommon.getBean(CloudAppConfig.class);

    private CloudSingleton() {}

}
