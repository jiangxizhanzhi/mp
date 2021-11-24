package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.DeviceAndSnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;

import java.util.List;

/**
 * 快照管理
 */
public interface SnapshotService extends BaseCloudPlatformService{

    String SNAPSHOT = "/snapshot";

    /**
     * 查询列表
     * @param deviceUuid
     */
    SnapshotCloudPlatformModel list(String deviceUuid);


    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    DeviceAndSnapshotCloudPlatformModel deviceAndSnapshotByName(String name);

    /**
     * 创建
     */
    Snapshot create(SnapshotCreate snapshotCreate);

    /**
     * 删除
     */
    Boolean delete(List<String> uuids);
}
