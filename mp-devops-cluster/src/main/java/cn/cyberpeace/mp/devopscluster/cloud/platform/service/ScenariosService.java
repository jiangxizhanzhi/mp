package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.*;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 靶标-场景接口
 * @author wanggang
 */
@Service
@Slf4j
public class ScenariosService extends BaseCpService {

    private static final String DELETE_OR_CREATE_IMAGE_INSTANCE = "/cloud/v1/device/device_instance";

    private static final String SCENARIOS = "/scenarios";
    private static final String SNAPSHOT = "/snapshot";

    public ScenariosService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 创建快照
     */
    @Deprecated
    public BaseCloudPlatformResult  create(ScenariosSnapshot snapshotCreate){

        String url = BaseCloudPlatformConstant.CPCLOUD_URL + BaseCloudPlatformConstant.URL_VERSION;
        if ("vm".equals(snapshotCreate.getImageType())){
            url = url + "/vm/" + snapshotCreate.getInstanceUuid() + SNAPSHOT;
        }else if ("container".equals(snapshotCreate.getImageType())){
            url = url + "/container/" + snapshotCreate.getInstanceUuid() + SNAPSHOT;
        }

        // 新平台只需要快照描述信息
        JSONObject messageJson = new JSONObject();
        messageJson.put("description", snapshotCreate.getDescription());
        BaseCloudPlatformResult result = super.sendPost(url, messageJson.toJSONString(), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    @Deprecated
    public BaseCloudPlatformResult injectFile(DeviceInjectFileCreate injectFileCreate){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.snakeStringify(injectFileCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 删除注入文件
     * @param delete
     * @return
     */
    @Deprecated
    public BaseCloudPlatformResult deleteInjectFile(DeviceInjectFileDelete delete){
        BaseCloudPlatformResult result = super.sendDelete(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.snakeStringify(delete), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 创建虚拟靶标实例
     * @param snapshotCreate
     * @return
     */
    public BaseCloudPlatformResult create(ImagesCreateInstanceCloudPlatform snapshotCreate){
        BaseCloudPlatformResult result = super.sendPost(  DELETE_OR_CREATE_IMAGE_INSTANCE, JsonUtils.snakeStringify(snapshotCreate), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 合并快照
     * @param mergeCloudPlatform
     * @return
     */
    @Deprecated
    public BaseCloudPlatformResult mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform){
        BaseCloudPlatformResult result = super.sendPost(  BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.snakeStringify(mergeCloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    public BaseCloudPlatformResult delete(ImagesDeleteInstanceCloudPlatform cloudPlatform){
        BaseCloudPlatformResult result = super.sendDelete(DELETE_OR_CREATE_IMAGE_INSTANCE, JsonUtils.snakeStringify(cloudPlatform), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 获取物理主机服务状态
     * @return
     */
    @Deprecated
    public CloudPlatformPageResult<ServiceStatus> listServiceStatus() {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + SCENARIOS + "/service_status", ServiceStatus.class);
    }

}
