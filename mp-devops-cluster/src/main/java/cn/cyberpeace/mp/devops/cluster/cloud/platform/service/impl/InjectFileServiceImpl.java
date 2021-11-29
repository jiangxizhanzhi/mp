package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.InjectFileService;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InjectFileCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件注入
 * @author wanggang
 */
@Service
public class InjectFileServiceImpl extends BaseOpenStackServiceImpl implements InjectFileService {

    public InjectFileServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public InjectFileCloudPlatformModel list(String deviceUuid) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE + "?device_uuid=" + deviceUuid, InjectFileCloudPlatformModel.class);
    }

    @Override
    public Snapshot create(SnapshotCreate snapshotCreate) {
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE, JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    @Override
    public BaseCloudPlatformModel delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("inject_uuids", uuids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE,JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
    }
}
