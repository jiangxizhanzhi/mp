package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SnapshotService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.converter.model.DeviceAndSnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAndSnapshotCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnapshotConverter {

    private final SnapshotService snapshotService;

    private final ScenariosService scenariosService;

    /**
     * 根据设备UUID查询快照列表
     * @param deviceUuid
     * @return
     */
    public BasePageBO<SnapshotConvertModel> list(String deviceUuid) {
        CloudPlatformPageResult<Snapshot> model = snapshotService.list(deviceUuid);
        List<SnapshotConvertModel> list = model.getList().stream().map(item ->
                new SnapshotConvertModel(item.getUuid(), item.getName(), item.getUserSnapshotName(), item.getDeviceUuid(),
                        item.getDescription(), TimeUtils.utcStringToLocalDateTime(item.getCreatedAt())))
                .collect(Collectors.toList());
        BasePageBO<SnapshotConvertModel> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    public DeviceAndSnapshotConvertModel deviceAndSnapshotByName(String name) {
        DeviceAndSnapshotCloudPlatformResult model = snapshotService.deviceAndSnapshotByName(name);
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
    public BaseCloudPlatformResult create(SnapshotRequest request) {
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
    public BaseCloudPlatformResult merge(SnapshotMergeRequest request) {
        DeviceSnapshotMergeCloudPlatform mergeCloudPlatform = new DeviceSnapshotMergeCloudPlatform();
        mergeCloudPlatform.setSnapshotName(request.getSnapshotName());
        mergeCloudPlatform.setNewName(request.getNewName());
        mergeCloudPlatform.setDeviceUuid(request.getUuid());
        mergeCloudPlatform.setImageType(request.getImageType());
        return scenariosService.mergeSnapshot(mergeCloudPlatform);
    }
}
