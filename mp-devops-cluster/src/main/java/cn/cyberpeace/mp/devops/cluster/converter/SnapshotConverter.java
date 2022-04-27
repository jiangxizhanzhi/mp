package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SnapshotService;
import cn.cyberpeace.mp.devops.cluster.converter.model.DeviceAndSnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotListConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SnapshotConverter {

    private SnapshotService snapshotService;

    private ScenariosService scenariosService;

    @Autowired
    public SnapshotConverter(SnapshotService snapshotService, ScenariosService scenariosService) {
        this.snapshotService = snapshotService;
        this.scenariosService = scenariosService;
    }

    /**
     * 根据设备UUID查询快照列表
     * @param deviceUuid
     * @return
     */
    public SnapshotListConvertModel list(String deviceUuid) {
        SnapshotCloudPlatformModel model = snapshotService.list(deviceUuid);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }
        List<SnapshotConvertModel> list = model.getItems().stream().map(item ->
                new SnapshotConvertModel(item.getUuid(), item.getName(), item.getUserSnapshotName(), item.getDeviceUuid(),
                        item.getDescription(), TimeUtils.utcStringToLocalDateTime(item.getCreatedAt())))
                .collect(Collectors.toList());
        SnapshotListConvertModel convertModel = new SnapshotListConvertModel();
        convertModel.setModelList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    public DeviceAndSnapshotConvertModel deviceAndSnapshotByName(String name) {
        DeviceAndSnapshotCloudPlatformModel model = snapshotService.deviceAndSnapshotByName(name);
        if (Objects.isNull(model)){
            return null;
        }
        List<SnapshotConvertModel> resultList = new ArrayList<>();
        SnapshotConvertModel snapshotConvertModel = new SnapshotConvertModel(model.getDeviceUuid(), model.getImageName(), model.getDeviceName(), model.getDeviceUuid(),
                "", null);
        snapshotConvertModel.setQuickCreateParams(model.getQuickCreateParams());
        DeviceAndSnapshotConvertModel convertModel = new DeviceAndSnapshotConvertModel();
        convertModel.setDeviceName(model.getDeviceName());
        convertModel.setDeviceUuid(model.getDeviceUuid());
        if (CollectionUtils.isEmpty(model.getSnapshots())){
            resultList.add(snapshotConvertModel);
            convertModel.setItems(resultList);
            return convertModel;
        }
        List<SnapshotConvertModel> list = model.getSnapshots().stream().map(item ->
                new SnapshotConvertModel(item.getUuid(), item.getName(), item.getUserSnapshotName(), item.getDeviceUuid(),
                        item.getDescription(), Objects.isNull(item.getCreatedAt()) ? null :
                        TimeUtils.utcStringToLocalDateTime(item.getCreatedAt())))
                .collect(Collectors.toList());
        resultList.addAll(list);
        resultList.add(0,snapshotConvertModel);
        convertModel.setItems(resultList);
        return convertModel;
    }

    /**
     * 创建快照
     * @param request
     * @return
     */
    public BaseCloudPlatformModel create(SnapshotRequest request) {
        ScenariosSnapshot snapshotCreate = new ScenariosSnapshot();
        snapshotCreate.setDeviceUuid(request.getUuid());
        snapshotCreate.setImageType(request.getImageType());
        snapshotCreate.setName(request.getName());
        snapshotCreate.setDescription(request.getDescription());
        return scenariosService.create(snapshotCreate);
    }

    /**
     * 合并快照
     * @param request
     * @return
     */
    public BaseCloudPlatformModel merge(SnapshotMergeRequest request) {
        DeviceSnapshotMergeCloudPlatform mergeCloudPlatform = new DeviceSnapshotMergeCloudPlatform();
        mergeCloudPlatform.setSnapshotName(request.getSnapshotName());
        mergeCloudPlatform.setNewName(request.getNewName());
        mergeCloudPlatform.setDeviceUuid(request.getUuid());
        mergeCloudPlatform.setImageType(request.getImageType());
        return scenariosService.mergeSnapshot(mergeCloudPlatform);
    }
}
