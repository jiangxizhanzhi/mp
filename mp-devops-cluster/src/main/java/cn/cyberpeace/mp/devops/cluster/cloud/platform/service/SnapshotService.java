package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAndSnapshotCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 快照管理
 * @author wanggang
 */
@Service
@Slf4j
public class SnapshotService extends BaseOpenStackService {

    private static final String SNAPSHOT = "/snapshot";

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
}
