 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotConverter.java.
 // File size 13348.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.DatePattern;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.CloudClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ContainerSnapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.DeviceSnapshotMergeCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.ScenariosSnapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ScenariosService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SnapshotService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.DeviceAndSnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.SnapshotConvertModel;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.*;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName SnapshotConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SnapshotConverter {

    private static final String CONTAINER = "container";


    private final SnapshotService snapshotService;

    private final ScenariosService scenariosService;

    private final DeviceTargetMapper deviceTargetMapper;

    private final DeviceService deviceService;

    private final CsceneSpecialService csceneSpecialService;

    private final CloudClient cloudClient;

    private final SceneElementSnapshotMapper snapshotMapper;


    /**
     * 根据设备UUID查询快照列表
     * @param deviceUuid
     * @return
     */
    public BasePageBO<SnapshotConvertModel> list(String deviceUuid) {
        CloudPlatformPageResult<cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot> model = snapshotService.list(deviceUuid);
        List<SnapshotConvertModel> list = new ArrayList<>();
        for (Snapshot item : model.getList()) {
            SnapshotConvertModel snapshotConvertModel = getSnapshotConvertModel(item);
            list.add(snapshotConvertModel);
        }
        BasePageBO<SnapshotConvertModel> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setSuccess(model.isSuccess());
        return convertModel;
    }


    /**
     * listSnapshot
     *
     * @param deviceUuid
     * @return
     */
    public List<SnapshotConvertModel> listSnapshot(String deviceUuid) {
        Device device = deviceService.findByUuid(deviceUuid);
        SnapshotPageRequest snapshotPageRequest = new SnapshotPageRequest();
        if ("vm".equals(device.getImageType())) {
            snapshotPageRequest.setInstanceId(device.getExtVm().getInstanceUuid());
            List<Snapshot> snapshots = cloudClient.listVmSnapshot(snapshotPageRequest).getList();
            if (CollectionUtils.isEmpty(snapshots)) {
                return Collections.emptyList();
            } else {
                return snapshots.stream().map(snapshot -> {
                    SnapshotConvertModel snapshotConvertModel = new SnapshotConvertModel();
                    snapshotConvertModel.setUuid(snapshot.getId());
                    snapshotConvertModel.setDescription(snapshot.getDescription());
                    snapshotConvertModel.setName(snapshot.getName());
                    snapshotConvertModel.setCreatedAt(LocalDateTime.parse(snapshot.getCreatedAt(), DatePattern.NORM_WITH_MISE_DATETIME_FORMAT));
                    return snapshotConvertModel;
                }).collect(Collectors.toList());
            }
        } else if ("container".equals(device.getImageType())) {
            snapshotPageRequest.setInstanceId(device.getExtDocker().getInstanceUuid());
            List<ContainerSnapshot> containerSnapshots = cloudClient.listContainerSnapshot(snapshotPageRequest).getList();
            if (CollectionUtils.isEmpty(containerSnapshots)) {
                return Collections.emptyList();
            } else {
                return containerSnapshots.stream().map(containerSnapshot -> {
                    SnapshotConvertModel snapshotConvertModel = new SnapshotConvertModel();
                    snapshotConvertModel.setUuid(containerSnapshot.getImageId());
                    snapshotConvertModel.setDescription(containerSnapshot.getDescription());
                    snapshotConvertModel.setName(containerSnapshot.getName());
                    snapshotConvertModel.setCreatedAt(LocalDateTime.parse(containerSnapshot.getCreateAt(), DatePattern.NORM_WITH_MISE_DATETIME_FORMAT));
                    return snapshotConvertModel;
                }).collect(Collectors.toList());
            }
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * getSnapshotConvertModel
     *
     * @param item
     * @return
     */
    private static SnapshotConvertModel getSnapshotConvertModel(Snapshot item) {
        SnapshotConvertModel result = new SnapshotConvertModel(item.getUuid(), item.getName(), item.getUserSnapshotName(), item.getDeviceUuid(),
                item.getDescription(), TimeUtils.utcStringToLocalDateTime(item.getCreatedAt()));
        result.setImageName(item.getImageName());
        if (item.getExtInfo() != null) {
            result.setSceneId(item.getExtInfo().getSceneId());
        }
        return result;
    }

    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    public DeviceAndSnapshotConvertModel deviceAndSnapshotByName(String name) {
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.list(new HumanCondition().select(DeviceTargetPO::getName, DeviceTargetPO::getUuid)
                .filterEqual(DeviceTargetPO::getName, name));
        if (CollectionUtils.isEmpty(deviceTargetPOS)) {
            return new DeviceAndSnapshotConvertModel();
        } else {
            DeviceTargetPO deviceTargetPO = deviceTargetPOS.get(0);
            DeviceAndSnapshotConvertModel deviceAndSnapshotConvertModel = new DeviceAndSnapshotConvertModel();
            deviceAndSnapshotConvertModel.setDeviceName(deviceTargetPO.getName());
            deviceAndSnapshotConvertModel.setDeviceUuid(deviceTargetPO.getUuid());

            SnapshotConvertModel snapshotConvertModel = new SnapshotConvertModel();
            snapshotConvertModel.setDeviceUuid(deviceTargetPO.getUuid());
            snapshotConvertModel.setUuid(deviceTargetPO.getUuid());
            snapshotConvertModel.setName(deviceTargetPO.getName());
            snapshotConvertModel.setUserSnapshotName(deviceTargetPO.getName());
            deviceAndSnapshotConvertModel.setItems(Collections.singletonList(snapshotConvertModel));
            return deviceAndSnapshotConvertModel;
        }
    }

    /**
     * 创建快照
     * @param request
     * @return
     */
    public void create(SnapshotRequest request) {
        ScenariosSnapshot snapshotCreate = new ScenariosSnapshot();
        snapshotCreate.setInstanceUuid(request.getInstanceUuid());
        snapshotCreate.setImageType(request.getImageType());
        snapshotCreate.setDescription(request.getDescription());
        scenariosService.create(snapshotCreate);
    }


    /**
     * 快照操作
     (path)
     虚拟机快照操作( preview | commit | undo | rollback )
     * @param request
     */
    public void action(SnapshotActionRequest request) {
        if (CONTAINER.equals(request.getImageType())) {
            cloudClient.containerSnapshotAction(request.getInstanceId(), request.getSnapshotId(), request.getAction());
        } else {
            cloudClient.vmSnapshotAction(request.getInstanceId(), request.getSnapshotId(), request.getAction());
        }
    }

    /**
     * 合并快照
     * @param request
     * @return
     */
    @Deprecated
    public void merge(SnapshotMergeRequest request) {
        validSnapshot(Collections.singletonList(request.getUuid()));
        DeviceSnapshotMergeCloudPlatform mergeCloudPlatform = new DeviceSnapshotMergeCloudPlatform();
        mergeCloudPlatform.setSnapshotName(request.getSnapshotName());
        mergeCloudPlatform.setNewName(request.getNewName());
        mergeCloudPlatform.setDeviceUuid(request.getUuid());
        mergeCloudPlatform.setImageType(request.getImageType());
        scenariosService.mergeSnapshot(mergeCloudPlatform);
    }

    /**
     * 删除快照
     * @param request
     * @return
     */
    public void delete(SnapshotDeleteRequest request) {
        Device device = deviceService.findByUuid(request.getDeviceUuid());
        if (CONTAINER.equals(device.getImageType())) {
            cloudClient.deleteContainerSnapshot(device.getExtDocker().getInstanceUuid(), request.getSnapshotUuid());
        } else {
            cloudClient.deleteVmSnapshot(device.getExtVm().getInstanceUuid(), request.getSnapshotUuid());
        }
        snapshotMapper.delete(request.getSnapshotId());
    }

    /**
     * 验证快照是否被引用或使用
     * @param uuids
     */
    public void validSnapshot(List<String> uuids) {

        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenesByPrefix(uuids);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneStat>> entry : referStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneStat deviceReferCsceneStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneStat.getName())) {
                        String name = deviceReferCsceneStat.getName();
                        names.add(name);
                    }
                }
                sceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME.format(sceneName));
        }
        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstancesByPrefix(uuids);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneInstanceStat>> entry : useStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneInstanceStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneInstanceStat deviceReferCsceneInstanceStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName())) {
                        String name = deviceReferCsceneInstanceStat.getName();
                        names.add(name);
                    }
                }
                sceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_USED_SCENE.format(sceneName));
        }
    }
}
