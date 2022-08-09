package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSnapshotService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAndSnapshotCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.request.SnapshotDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 快照管理
 * @author wanggang
 */
@Service
@Slf4j
public class SnapshotService extends BaseCpService {

    private static final String SNAPSHOT = "/snapshot";

    private static final String DEVICE_SNAPSHOT = "/scenarios/device_snapshot";

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Resource
    private CsceneSnapshotService csceneSnapshotService;

    public SnapshotService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询列表
     * @param deviceUuid
     */
    public CloudPlatformPageResult<Snapshot> list(String deviceUuid){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT + "?device_uuid=" + deviceUuid, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Snapshot> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Snapshot.class);
        CloudPlatformPageResult<Snapshot> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }


    /**
     * 根据名称查询设备名称和快照列表
     * @param name
     * @return
     */
    public DeviceAndSnapshotCloudPlatformResult deviceAndSnapshotByName(String name){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT + "/device_snapshots?name=" + name, DeviceAndSnapshotCloudPlatformResult.class);
    }

    /**
     * 创建
     */
    public Snapshot create(SnapshotCreate snapshotCreate){
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT, JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    /**
     * 删除
     */
    public Boolean delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("snapshot_uuids", uuids);
        BaseCloudPlatformResult model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT,JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        return model.isSuccess();
    }

    /**
     * 删除快照
     * @param request
     * @return
     */
    public BaseCloudPlatformResult deleteSnapshot(SnapshotDeleteRequest request) {
        String imageName = request.getDeviceUuid().concat(request.getSnapshotName());
        this.validSnapshot(imageName);
        var jsonObject = new JSONObject();
        jsonObject.put("device_uuid", request.getDeviceUuid());
        jsonObject.put("snapshot_uuid", request.getSnapshotUuid());
        jsonObject.put("snapshot_name", request.getSnapshotName());
        BaseCloudPlatformResult model = super.sendDelete(URL_VERSION + DEVICE_SNAPSHOT, JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        if (StringUtils.isNotEmpty(model.getMessage()) && model.getMessage().equals("snapshot not exists")) {
            csceneSnapshotService.deleteSnapshotRelativeInfoByResource(request.getSnapshotUuid());
        } else {
            super.checkResult(model);
        }
        return model;
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
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>>  useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{imageName});
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap) && CollectionUtils.isNotEmpty(useStateMap.get(imageName))){
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_SNAPSHOT_REFERENCED_SCENE_WITH_NAME.format(useStateMap.get(imageName).get(0).getName()));
        }
    }

}
