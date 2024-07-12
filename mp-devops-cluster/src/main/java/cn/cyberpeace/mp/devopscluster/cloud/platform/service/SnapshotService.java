 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnapshotService.java.
 // File size 8133.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSnapshotService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.CloudClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ContainerSnapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.Snapshot;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceAndSnapshotCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.request.SnapshotPageRequest;
import cn.cyberpeace.mp.scene.model.SceneElementSnapshotModel;
import cn.cyberpeace.mp.scene.repository.SceneElementSnapshotMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName SnapshotService
 * @Description 快照管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
@Deprecated(since = "30s的云平台获取snapshot是虚机和docker分开的")
public class SnapshotService extends BaseCpService {

    private static final String SNAPSHOT = "/snapshot";

    private static final String DEVICE_SNAPSHOT = "/scenarios/device_snapshot";

    @Autowired
    private CsceneSpecialService csceneSpecialService;
    @Resource
    private SceneElementSnapshotMapper snapshotMapper;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;
    @Resource
    private CloudClient cloudClient;

    /**
     * SnapshotService
     *
     * @param devopsClusterConfig
     */
    public SnapshotService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询列表
     * @param deviceUuid
     */
    @Deprecated
    public CloudPlatformPageResult<Snapshot> list(String deviceUuid){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + SNAPSHOT + "?device_uuid=" + deviceUuid, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Snapshot> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()),Snapshot.class);
        CloudPlatformPageResult<Snapshot> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }


    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    @Deprecated
    public DeviceAndSnapshotCloudPlatformResult deviceAndSnapshotByName(String name) {
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + SNAPSHOT + "/device_snapshots?name=" + UriEncoder.encode(name), DeviceAndSnapshotCloudPlatformResult.class);
    }



    /**
     * 校验快照是否被引用或者使用
     * @param imageName
     */
    public void validSnapshot(String imageName) {
        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(new String[]{imageName});
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(referStateMap) && CollectionUtils.isNotEmpty(referStateMap.get(imageName))) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME.format(referStateMap.get(imageName).get(0).getName()));
        }

        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{imageName});
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap) && CollectionUtils.isNotEmpty(useStateMap.get(imageName))) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME.format(useStateMap.get(imageName).get(0).getName()));
        }
    }


    /**
     * 根据虚拟机id分页查询虚拟机快照列表
     * @param request
     * @return
     */
    public ListObjServiceResult<Snapshot> getVmSnapshotById(SnapshotPageRequest request) {
        var res = cloudClient.listVmSnapshot(request);
        if (Objects.isNull(res) || CollectionUtils.isEmpty(res.getList())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        var result = res.getList();
        Map<String, SceneElementSnapshotModel> snapshotId2ModelMap = snapshotMapper.list(new HumanCondition()
                        .filterIn(SceneElementSnapshotModel::getId, result.stream().map(Snapshot::getId).toArray()))
                .stream()
                .collect(Collectors.toMap(BaseModel::getId, sceneElementSnapshotModel -> sceneElementSnapshotModel));
        result.forEach(snapshot -> {
            var snapshotModel = snapshotId2ModelMap.get(snapshot.getId());
            if (Objects.nonNull(snapshotModel)) {
                snapshot.setName(snapshotModel.getName());
            } else {
                snapshot.setName(StringUtils.EMPTY);
            }
        });
        return new ListObjServiceResult<>(new ListObjResult<>(result, res.getTotal()));
    }

    /**
     * getContainerSnapshotById
     *
     * @param request
     * @return
     */
    public ListObjServiceResult<ContainerSnapshot> getContainerSnapshotById(SnapshotPageRequest request) {
        var res = cloudClient.listContainerSnapshot(request);
        if (Objects.isNull(res) || CollectionUtils.isEmpty(res.getList())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        var result = res.getList();
        Map<String, SceneElementSnapshotModel> snapshotId2ModelMap = snapshotMapper.list(new HumanCondition()
                        .filterIn(SceneElementSnapshotModel::getId, result.stream().map(ContainerSnapshot::getImageId).toArray()))
                .stream()
                .collect(Collectors.toMap(BaseModel::getId, sceneElementSnapshotModel -> sceneElementSnapshotModel));
        result.forEach(snapshot -> {
            var snapshotModel = snapshotId2ModelMap.get(snapshot.getImageId());
            if (Objects.nonNull(snapshotModel)) {
                snapshot.setDescription(snapshotModel.getDescription());
            } else {
                snapshot.setDescription(StringUtils.EMPTY);
            }
        });
        return new ListObjServiceResult<>(new ListObjResult<>(result, res.getTotal()));
    }


}
