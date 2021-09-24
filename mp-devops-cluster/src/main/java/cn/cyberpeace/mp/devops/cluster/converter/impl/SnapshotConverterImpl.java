package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.devops.cluster.converter.SnapshotConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.model.ScenariosSnapshot;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;
import cn.cyberpeace.mp.devops.cluster.service.ScenariosSnapshotService;
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

    private ScenariosSnapshotService scenariosSnapshotService;

    @Autowired
    public SnapshotConverterImpl(SnapshotService snapshotService,ScenariosSnapshotService scenariosSnapshotService) {
        this.snapshotService = snapshotService;
        this.scenariosSnapshotService = scenariosSnapshotService;
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
    public BaseCloudPlatformModel create(SnapshotRequest request) {
        ScenariosSnapshot snapshotCreate = new ScenariosSnapshot();
        snapshotCreate.setDeviceUuid(request.getUuid());
        snapshotCreate.setImageType(request.getImageType());
        snapshotCreate.setName(request.getName());
        snapshotCreate.setDescription(request.getDescription());
        return scenariosSnapshotService.create(snapshotCreate);
    }

    @Override
    public BaseCloudPlatformModel merge(SnapshotMergeRequest request) {
        DeviceSnapshotMergeCloudPlatform mergeCloudPlatform = new DeviceSnapshotMergeCloudPlatform();
        mergeCloudPlatform.setSnapshotName(request.getSnapshotName());
        mergeCloudPlatform.setNewName(request.getNewName());
        mergeCloudPlatform.setDeviceUuid(request.getUuid());
        mergeCloudPlatform.setImageType(request.getImageType());
        return scenariosSnapshotService.mergeSnapshot(mergeCloudPlatform);
    }

    @Override
    public Boolean delete(List<String> uuids) {
        return null;
    }
}
