package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import lombok.Data;

import java.util.List;

@Data
public class SnapshotCloudPlatformModel extends BaseCloudPlatformModel{

    /**
     * 快照列表
     */
    private List<Snapshot> items;

}
