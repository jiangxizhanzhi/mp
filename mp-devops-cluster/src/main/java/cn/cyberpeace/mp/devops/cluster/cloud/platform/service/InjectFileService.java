package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InjectFileCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.model.common.Snapshot;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 文件注入管理
 * @author wanggang
 */
@Service
@Slf4j
public class InjectFileService extends BaseOpenStackServiceImpl {

    private static final String INJECT_FILE = "/inject_file";

    public InjectFileService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询列表
     * @param deviceUuid
     */
    public InjectFileCloudPlatformModel list(String deviceUuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE + "?device_uuid=" + deviceUuid, InjectFileCloudPlatformModel.class);
    }

    /**
     * 创建
     */
    public Snapshot create(SnapshotCreate snapshotCreate){
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE, JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    /**
     * 删除
     */
    public BaseCloudPlatformModel delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("inject_uuids", uuids);
        return super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + INJECT_FILE,JSONObject.toJSONString(jsonObject), BaseCloudPlatformModel.class);
    }
}
