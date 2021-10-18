package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;

public interface SnapshotConverter {

    /**
     * 查询列表
     * @param deviceUuid
     */
    SnapshotListConvertModel list(String deviceUuid);

    /**
     * 创建
     */
    BaseCloudPlatformModel create(SnapshotRequest request);

    /**
     * 合并快照
     * @param request
     * @return
     */
    BaseCloudPlatformModel merge(SnapshotMergeRequest request);
}
