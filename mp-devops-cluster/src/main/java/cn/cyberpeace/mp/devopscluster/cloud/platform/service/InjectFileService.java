package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devopscluster.model.common.InjectFile;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class InjectFileService extends BaseCpService {

    private static final String INJECT_FILE = "/inject_file";

    public InjectFileService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<InjectFile> list(String deviceUuid){
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INJECT_FILE + "?device_uuid=" + deviceUuid, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<InjectFile> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),InjectFile.class);
        CloudPlatformPageResult<InjectFile> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    
    public Snapshot create(SnapshotCreate snapshotCreate){
        return super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INJECT_FILE, JsonUtils.toUnderlineJSONString(snapshotCreate), Snapshot.class);
    }

    
    public BaseCloudPlatformResult delete(List<String> uuids) {
        var jsonObject = new JSONObject();
        jsonObject.put("inject_uuids", uuids);
        return super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INJECT_FILE,JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
    }
}
