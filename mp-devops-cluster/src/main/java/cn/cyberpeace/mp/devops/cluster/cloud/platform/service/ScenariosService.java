package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标-场景接口
 * @author wanggang
 */
@Service
@Slf4j
public class ScenariosService extends BaseOpenStackService {

    private static final String SCENARIOS = "/scenarios";

    public ScenariosService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 创建快照
     */
    public BaseCloudPlatformResult create(ScenariosSnapshot snapshotCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
    }

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    public BaseCloudPlatformResult injectFile(DeviceInjectFileCreate injectFileCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(injectFileCreate), BaseCloudPlatformResult.class);
    }

    /**
     * 删除注入文件
     * @param delete
     * @return
     */
    public BaseCloudPlatformResult deleteInjectFile(DeviceInjectFileDelete delete){
        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(delete), BaseCloudPlatformResult.class);
    }

    /**
     * 创建虚拟靶标实例
     * @param snapshotCreate
     * @return
     */
    public BaseCloudPlatformResult create(ImagesCreateInstanceCloudPlatform snapshotCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
    }

    /**
     * 合并快照
     * @param mergeCloudPlatform
     * @return
     */
    public BaseCloudPlatformResult mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.toUnderlineJSONString(mergeCloudPlatform), BaseCloudPlatformResult.class);
    }

    /**
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    public BaseCloudPlatformResult delete(ImagesDeleteInstanceCloudPlatform cloudPlatform){
        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(cloudPlatform), BaseCloudPlatformResult.class);
    }

    /**
     * 获取物理主机服务状态
     * @return
     */
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        return super.page(URL_VERSION + SCENARIOS + "/service_status", ServiceStatus.class);
    }

}
