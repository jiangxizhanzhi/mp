package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.VmActionCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class VmService extends BaseCpService {

    private static final String INSTANCES = "/instances";

    public VmService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<InstanceModel> page(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + INSTANCES + getParams, InstanceModel.class);
    }

    
    public BaseCloudPlatformResult action(VmActionCloudPlatformModel model, String uuid){
        BaseCloudPlatformResult result = super.sendPost(BaseCloudPlatformConstant.URL_VERSION + INSTANCES + "/" + uuid  + "/action", JsonUtils.toUnderlineJSONString(model), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public CloudPlatformPageResult<InstanceOsServiceModel> osServicesStateFromInstances(){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + INSTANCES + "/os-services", InstanceOsServiceModel.class);
    }
}
