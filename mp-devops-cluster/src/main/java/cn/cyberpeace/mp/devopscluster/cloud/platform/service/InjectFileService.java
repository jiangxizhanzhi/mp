package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Snapshot;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.SnapshotCreate;
import cn.cyberpeace.mp.devopscluster.model.common.InjectFile;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName InjectFileService
 * @Description 文件注入管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
@Deprecated
public class InjectFileService extends BaseCpService {

    private static final String INJECT_FILE = "/inject_file";

    /**
     * InjectFileService
     *
     * @param devopsClusterConfig
     */
    public InjectFileService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询列表
     * @param deviceUuid
     */
    public CloudPlatformPageResult<InjectFile> list(String deviceUuid){
        CloudPlatformItemsPageResult pageResult = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_URL
                + BaseCloudPlatformConstant.URL_VERSION + BaseCloudPlatformConstant.CPCLOUD_DEVICE
                + INJECT_FILE + "?device_uuid=" + deviceUuid, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<InjectFile> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()),InjectFile.class);
        CloudPlatformPageResult<InjectFile> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    /**
     * create
     *
     * @param snapshotCreate
     * @return
     */
    public Snapshot create(SnapshotCreate snapshotCreate){
        return new Snapshot();
    }

    /**
     * delete
     *
     * @param uuids
     * @return 基础模型，对应云平台接口返回信息
     */
    public BaseCloudPlatformResult delete(List<String> uuids) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("inject_uuids", uuids);
        return super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + INJECT_FILE,JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
    }
}
