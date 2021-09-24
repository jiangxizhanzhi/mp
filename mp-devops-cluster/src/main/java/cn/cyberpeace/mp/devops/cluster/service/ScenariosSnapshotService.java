package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

public interface ScenariosSnapshotService extends BaseCloudPlatformService{

    /**
     * 创建
     */
    BaseCloudPlatformModel create(ScenariosSnapshot snapshotCreate);


    /**
     * 删除
     */
    Boolean deleteDeviceScenario(ScenarioDeleteDeviceParams params);

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    BaseCloudPlatformModel injectFile(DeviceInjectFileCreate injectFileCreate);

    /**
     * 创建虚拟靶标实例
     * @param snapshotCreate
     * @return
     */
    BaseCloudPlatformModel create(ImagesCreateInstanceCloudPlatform snapshotCreate);

    /**
     * 合并快照
     * @param mergeCloudPlatform
     * @return
     */
    BaseCloudPlatformModel mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform);

    /**
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    BaseCloudPlatformModel delete(ImagesDeleteInstanceCloudPlatform cloudPlatform);
}
