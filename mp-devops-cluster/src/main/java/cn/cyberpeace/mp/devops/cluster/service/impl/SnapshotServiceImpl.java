package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAndSnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.service.SnapshotService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnapshotServiceImpl extends BaseOpenStackServiceImpl implements SnapshotService {

    public SnapshotServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public SnapshotCloudPlatformModel list(String deviceUuid) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT + "?device_uuid=" + deviceUuid, SnapshotCloudPlatformModel.class);
    }

    @Override
    public DeviceAndSnapshotCloudPlatformModel deviceAndSnapshotByName(String name){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT + "/device_snapshots?name=" + name,DeviceAndSnapshotCloudPlatformModel.class);
    }

    @Override
    public Snapshot create(SnapshotCreate snapshotCreate) {
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT, JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    @Override
    public Boolean delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("snapshot_uuids", uuids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + SNAPSHOT,JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
        return model.isSuccess();
    }
}
