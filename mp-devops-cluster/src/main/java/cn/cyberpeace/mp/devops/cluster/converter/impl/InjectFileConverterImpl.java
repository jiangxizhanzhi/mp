package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.InjectFileConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.InjectFileListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;

import java.util.List;

public class InjectFileConverterImpl implements InjectFileConverter {
    @Override
    public InjectFileListConvertModel list(String deviceUuid) {
        return null;
    }

    @Override
    public Boolean create(SnapshotCreate snapshotCreate) {
        return null;
    }

    @Override
    public Boolean delete(List<String> uuids) {
        return null;
    }
}
