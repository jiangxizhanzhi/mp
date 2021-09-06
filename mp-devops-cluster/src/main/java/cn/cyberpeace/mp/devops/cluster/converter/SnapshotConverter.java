package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;

import java.util.List;

public interface SnapshotConverter {

    /**
     * 查询列表
     * @param deviceUuid
     */
    SnapshotListConvertModel list(String deviceUuid);

    /**
     * 创建
     */
    Boolean create(SnapshotRequest request);

    /**
     * 合并快照
     * @param deviceUuid
     * @return
     */
    Snapshot merge(String deviceUuid);

    /**
     * 删除
     */
    Boolean delete(List<String> uuids);
}
