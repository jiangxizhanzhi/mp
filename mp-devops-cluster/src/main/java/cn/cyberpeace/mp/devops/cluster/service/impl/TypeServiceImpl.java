package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdStringCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdIntegerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.TypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl extends BaseOpenStackServiceImpl implements TypeService {

    public TypeServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public TypeCloudPlatformModel findL1AndL2AndL3Type() {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=l3", TypeCloudPlatformModel.class);
    }

    @Override
    public TypeCloudPlatformModel findL1AndL2Type() {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=l2", TypeCloudPlatformModel.class);
    }

    @Override
    public TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=" + type, TypeIdIntegerCloudPlatformModel.class);
    }

    @Override
    public TypeIdStringCloudPlatformModel findIdStringByType(String type) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=" + type, TypeIdStringCloudPlatformModel.class);
    }
}
