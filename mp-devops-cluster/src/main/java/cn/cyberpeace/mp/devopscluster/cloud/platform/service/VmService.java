package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceOsServiceModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @ClassName VmService
 * @Description 虚拟机
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class VmService extends BaseCpService {

    private static final String VM_ACTION_URL = "/cloud/v1/vm/{vm_id}/{action}";
    private static final String VM_LIST = "/cloud/v1/vm";


    private static final String INSTANCES = "/instances";

    /**
     * VmService
     *
     * @param devopsClusterConfig
     */
    public VmService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询虚拟机信息
     * @param getParams
     * @return
     */
    @Deprecated
    public CloudPlatformPageResult<InstanceModel> page(String getParams){
        return super.page(VM_LIST + getParams, InstanceModel.class);
    }

    /**
     * 操作
     *
     * @param action
     * @param uuid
     * @return
     */
    public BaseCloudPlatformResult action(String action, String uuid){
        String url = UriComponentsBuilder.fromPath(VM_ACTION_URL).buildAndExpand(uuid, action).toUriString();
        BaseCloudPlatformResult result = super.sendPost(url, null, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * osServicesStateFromInstances
     *
     * @return
     */
    @Deprecated
    public CloudPlatformPageResult<InstanceOsServiceModel> osServicesStateFromInstances(){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + INSTANCES + "/os-services", InstanceOsServiceModel.class);
    }
}
