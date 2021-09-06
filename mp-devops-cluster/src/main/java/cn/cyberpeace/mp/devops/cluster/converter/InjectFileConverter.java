package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;

import java.util.List;

public interface InjectFileConverter {


    /**
     * 查询列表
     * @param deviceUuid
     */
    InjectFileListConvertModel list(String deviceUuid);

    /**
     * 创建
     */
    Boolean create(SnapshotCreate snapshotCreate);

    /**
     * 删除
     */
    Boolean delete(List<String> uuids);
}
