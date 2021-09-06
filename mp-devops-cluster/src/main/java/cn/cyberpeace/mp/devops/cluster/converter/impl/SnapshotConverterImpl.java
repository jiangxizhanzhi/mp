package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;
import cn.cyberpeace.mp.devops.cluster.service.SnapshotService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SnapshotConverterImpl implements SnapshotConverter {

    private SnapshotService snapshotService;

    @Autowired
    public SnapshotConverterImpl(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @Override
    public SnapshotListConvertModel list(String deviceUuid) {
        SnapshotCloudPlatformModel model = snapshotService.list(deviceUuid);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }
        List<SnapshotConvertModel> list = model.getItems().stream().map(item ->
                new SnapshotConvertModel(item.getSnapshotUuid(), item.getName(), item.getDescription(), item.getCreatedAt()))
                .collect(Collectors.toList());
        SnapshotListConvertModel convertModel = new SnapshotListConvertModel();
        convertModel.setModelList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    @Override
    public Boolean create(SnapshotRequest request) {
        SnapshotCreate snapshotCreate = new SnapshotCreate();
        snapshotCreate.setDeviceUuid(request.getUuid());
        snapshotCreate.setName(request.getName());
        snapshotCreate.setDescription(request.getDescription());
        Snapshot snapshot = snapshotService.create(snapshotCreate);
        return snapshot.isSuccess();
    }

    @Override
    public Snapshot merge(String deviceUuid) {
        return snapshotService.merge(deviceUuid);
    }

    @Override
    public Boolean delete(List<String> uuids) {
        return null;
    }
}
