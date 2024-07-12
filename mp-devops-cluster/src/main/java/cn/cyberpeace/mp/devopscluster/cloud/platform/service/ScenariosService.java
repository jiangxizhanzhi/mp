 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ScenariosService.java.
 // File size 5302.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.CloudClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.*;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName ScenariosService
 * @Description 靶标-场景接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class ScenariosService extends BaseCpService {

    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private SceneElementSnapshotMapper snapshotMapper;


    private static final String CONTAINER = "container";


    private static final String DELETE_OR_CREATE_IMAGE_INSTANCE = "/cloud/v1/device/device_instance";

    private static final String SCENARIOS = "/scenarios";
    private static final String

            SNAPSHOT = "/snapshot";

    /**
     * ScenariosService
     *
     * @param devopsClusterConfig
     */
    public ScenariosService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * create
     *
     * @param snapshotCreate
     */
    public void create(ScenariosSnapshot snapshotCreate){

        String snapshotId;
        if (CONTAINER.equals(snapshotCreate.getImageType())) {
            snapshotId = cloudClient.containerSnapshot(snapshotCreate.getInstanceUuid(), snapshotCreate.getDescription());
        } else {
            var result = cloudClient.vmSnapshot(snapshotCreate.getInstanceUuid(), snapshotCreate.getDescription());
            snapshotId = result.getId();
        }


        SceneElementSnapshotModel snapshotModel = new SceneElementSnapshotModel();
        snapshotModel.setId(snapshotId);
        snapshotModel.setName(snapshotCreate.getDescription());
        snapshotModel.setDescription(snapshotCreate.getDescription());
        snapshotMapper.create(snapshotModel);
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
