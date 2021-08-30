package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeInstallerPlatformAndImageTypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeOfRoleCloudPlatformModel;
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
    public TypeOfRoleCloudPlatformModel findAllRoleType() {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=role", TypeOfRoleCloudPlatformModel.class);
    }

    @Override
    public TypeInstallerPlatformAndImageTypeCloudPlatformModel findAllInstallerPlatformAndImageType(String type) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/type?type=" + type, TypeInstallerPlatformAndImageTypeCloudPlatformModel.class);
    }
}
