package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;

public interface ScenariosSnapshotService extends BaseCloudPlatformService{

    /**
     * 创建
     */
    Snapshot create(ScenariosSnapshot snapshotCreate);


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
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    BaseCloudPlatformModel delete(ImagesDeleteInstanceCloudPlatform cloudPlatform);
}
