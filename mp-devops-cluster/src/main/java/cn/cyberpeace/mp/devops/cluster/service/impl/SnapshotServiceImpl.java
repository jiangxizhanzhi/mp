package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
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
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/snapshot?device_uuid=" + deviceUuid, SnapshotCloudPlatformModel.class);
    }

    @Override
    public Snapshot create(SnapshotCreate snapshotCreate) {
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    @Override
    public Snapshot merge(String deviceUuid) {
        var jsonObject = new JSONObject();
        jsonObject.put("device_uuid", deviceUuid);
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/snapshot", JSONObject.toJSONString(jsonObject), Snapshot.class);
    }

    @Override
    public Boolean delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("snapshot_uuids", uuids);
        BaseCloudPlatformModel model = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/snapshot",JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
        return model.isSuccess();
    }
}
