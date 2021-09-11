package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ScenarioDeleteDeviceParams;
import cn.cyberpeace.mp.devops.cluster.model.ScenariosSnapshot;
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
}
