package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.cscene.util.SceneDeleteCallback;
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

/**
 * 靶标-场景接口
 * @author wanggang
 */
@Service
@Slf4j
public class ScenariosService extends BaseCpService {

    private static final String SCENARIOS = "/scenarios";

    public ScenariosService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 创建快照
     */
    public BaseCloudPlatformResult create(ScenariosSnapshot snapshotCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    public BaseCloudPlatformResult injectFile(DeviceInjectFileCreate injectFileCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(injectFileCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 删除注入文件
     * @param delete
     * @return
     */
    public BaseCloudPlatformResult deleteInjectFile(DeviceInjectFileDelete delete){
        BaseCloudPlatformResult result = super.sendDelete(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(delete), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 创建虚拟靶标实例
     * @param snapshotCreate
     * @return
     */
    public BaseCloudPlatformResult create(ImagesCreateInstanceCloudPlatform snapshotCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 合并快照
     * @param mergeCloudPlatform
     * @return
     */
    public BaseCloudPlatformResult mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.toUnderlineJSONString(mergeCloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    public BaseCloudPlatformResult delete(ImagesDeleteInstanceCloudPlatform cloudPlatform){
        BaseCloudPlatformResult result = super.sendDelete(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(cloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 获取物理主机服务状态
     * @return
     */
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/service_status", ServiceStatus.class);
    }

}
