package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SnapshotService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.DeviceAndSnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceAndSnapshotCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.SnapshotMergeRequest;
import cn.cyberpeace.mp.devopscluster.request.SnapshotRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SnapshotConverter {

    private final SnapshotService snapshotService;

    private final ScenariosService scenariosService;

    private final DeviceTargetMapper deviceTargetMapper;

    private final DeviceService deviceService;

    private final CsceneSpecialService csceneSpecialService;

    
    public BasePageBO<SnapshotConvertModel> list(String deviceUuid) {
        CloudPlatformPageResult<Snapshot> model = snapshotService.list(deviceUuid);
        List<SnapshotConvertModel> list = model.getList().stream().map(item -> {
                    var result = new SnapshotConvertModel(item.getUuid(), item.getName(), item.getUserSnapshotName(), item.getDeviceUuid(),
                            item.getDescription(), TimeUtils.utcStringToLocalDateTime(item.getCreatedAt()));
                    result.setImageName(item.getImageName());
                    if (item.getExtInfo() != null) {
                        result.setSceneId(item.getExtInfo().getSceneId());
                    }
                    return result;
                })
                .collect(Collectors.toList());
        BasePageBO<SnapshotConvertModel> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }

    
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

    
    public void create(SnapshotRequest request) {
        ScenariosSnapshot snapshotCreate = new ScenariosSnapshot();
        snapshotCreate.setDeviceUuid(request.getUuid());
        snapshotCreate.setImageType(request.getImageType());
        if (StringUtils.isNotEmpty(request.getName())) {
            request.setName(request.getName().trim());
        }
        snapshotCreate.setName(request.getName());
        snapshotCreate.setDescription(request.getDescription());
        scenariosService.create(snapshotCreate);
    }

    
    public void merge(SnapshotMergeRequest request) {
        validSnapshot(Collections.singletonList(request.getUuid()));
        DeviceSnapshotMergeCloudPlatform mergeCloudPlatform = new DeviceSnapshotMergeCloudPlatform();
        mergeCloudPlatform.setSnapshotName(request.getSnapshotName());
        mergeCloudPlatform.setNewName(request.getNewName());
        mergeCloudPlatform.setDeviceUuid(request.getUuid());
        mergeCloudPlatform.setImageType(request.getImageType());
        scenariosService.mergeSnapshot(mergeCloudPlatform);
    }

    
    public void validSnapshot(List<String> uuids) {

        
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenesByPrefix(uuids);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            referStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME.format(sceneName));
        }
        
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstancesByPrefix(uuids);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            useStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneInstanceStat -> StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName()))
                        .map(deviceReferCsceneInstanceStat -> deviceReferCsceneInstanceStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_USED_SCENE.format(sceneName));
        }
    }
}
