package cn.cyberpeace.mp.sceneplugincloud.constant;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;

public class CloudSingleton {

    public static final CloudAppConfig cloudAppConfig = BeanUtils.getBean(CloudAppConfig.class);

    private CloudSingleton() {}

}
