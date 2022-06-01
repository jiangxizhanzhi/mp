package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.VmActionCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 虚拟机
 * @author wanggang
 */
@Service
@Slf4j
public class VmService extends BaseOpenStackService {

    private static final String INSTANCES = "/instances";

    public VmService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询虚拟机信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<InstanceModel> page(String getParams){
        return super.page(URL_VERSION + INSTANCES + getParams, InstanceModel.class);
    }

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public BaseCloudPlatformResult action(VmActionCloudPlatformModel model, String uuid){
        return super.sendPost(URL_VERSION + INSTANCES + "/" + uuid  + "/action", JsonUtils.toUnderlineJSONString(model), BaseCloudPlatformResult.class);
    }

    /**
     *
     * @return
     */
    public CloudPlatformPageResult<InstanceOsServiceModel> osServicesStateFromInstances(){
        return super.page(URL_VERSION + INSTANCES + "/os-services", InstanceOsServiceModel.class);
    }
}
