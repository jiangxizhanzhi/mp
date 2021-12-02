package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

public interface ScenariosSnapshotService extends BaseCloudPlatformService {

    String SCENARIOS = "/scenarios";

    /**
     * 创建
     */
    BaseCloudPlatformModel create(ScenariosSnapshot snapshotCreate);

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    BaseCloudPlatformModel injectFile(DeviceInjectFileCreate injectFileCreate);

    /**
     * 删除注入文件
     * @param delete
     * @return
     */
    BaseCloudPlatformModel deleteInjectFile(DeviceInjectFileDelete delete);

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
