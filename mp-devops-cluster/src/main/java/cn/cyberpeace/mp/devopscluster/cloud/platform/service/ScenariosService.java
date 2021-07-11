package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.*;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ScenariosService extends BaseCpService {

    private static final String SCENARIOS = "/scenarios";

    public ScenariosService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public BaseCloudPlatformResult create(ScenariosSnapshot snapshotCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public BaseCloudPlatformResult injectFile(DeviceInjectFileCreate injectFileCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(injectFileCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public BaseCloudPlatformResult deleteInjectFile(DeviceInjectFileDelete delete){
        BaseCloudPlatformResult result = super.sendDelete(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(delete), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public BaseCloudPlatformResult create(ImagesCreateInstanceCloudPlatform snapshotCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public BaseCloudPlatformResult mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.toUnderlineJSONString(mergeCloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public BaseCloudPlatformResult delete(ImagesDeleteInstanceCloudPlatform cloudPlatform){
        BaseCloudPlatformResult result = super.sendDelete(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(cloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/service_status", ServiceStatus.class);
    }

}
