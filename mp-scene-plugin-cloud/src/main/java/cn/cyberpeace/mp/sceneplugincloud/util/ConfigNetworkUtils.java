package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.ConfigNetworkModel;
import cn.cyberpeace.mp.sceneplugincloud.repository.ConfigNetworkMapper;

public class ConfigNetworkUtils {
    public static void save(String ip, String sceneEid) {
        ConfigNetworkModel model = new ConfigNetworkModel();
        model.setScene(sceneEid);
        model.setStatus(1);
        model.setNetworkId(CloudSingleton.cloudAppConfig.getConfigNetId());
        model.setIp(ip);
        BeanCommon.getBean(ConfigNetworkMapper.class).save(model);
    }
}
