package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InjectFileCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;

import java.util.List;

/**
 * 文件注入管理
 */
public interface InjectFileService extends BaseCloudPlatformService{

    String INJECT_FILE = "/inject_file";

    /**
     * 查询列表
     * @param deviceUuid
     */
    InjectFileCloudPlatformModel list(String deviceUuid);

    /**
     * 创建
     */
    Snapshot create(SnapshotCreate snapshotCreate);

    /**
     * 删除
     */
    BaseCloudPlatformModel delete(List<String> uuids);
}
