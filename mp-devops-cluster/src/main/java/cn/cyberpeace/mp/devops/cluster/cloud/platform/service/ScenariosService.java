package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 靶标-场景接口
 * @author wanggang
 */
@Service
@Slf4j
public class ScenariosService extends BaseOpenStackService {

    private static final String SCENARIOS = "/scenarios";

    public ScenariosService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 创建快照
     */
    public BaseCloudPlatformModel create(ScenariosSnapshot snapshotCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformModel.class);
    }

    /**
     * 注入文件
     * @param injectFileCreate
     * @return
     */
    public BaseCloudPlatformModel injectFile(DeviceInjectFileCreate injectFileCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(injectFileCreate), BaseCloudPlatformModel.class);
    }

    /**
     * 删除注入文件
     * @param delete
     * @return
     */
    public BaseCloudPlatformModel deleteInjectFile(DeviceInjectFileDelete delete){
        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_inject_file", JsonUtils.toUnderlineJSONString(delete), BaseCloudPlatformModel.class);
    }

    /**
     * 创建虚拟靶标实例
     * @param snapshotCreate
     * @return
     */
    public BaseCloudPlatformModel create(ImagesCreateInstanceCloudPlatform snapshotCreate){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(snapshotCreate), BaseCloudPlatformModel.class);
    }

    /**
     * 合并快照
     * @param mergeCloudPlatform
     * @return
     */
    public BaseCloudPlatformModel mergeSnapshot(DeviceSnapshotMergeCloudPlatform mergeCloudPlatform){
        return super.sendPost(  URL_VERSION + SCENARIOS + "/device_snapshot_merge", JsonUtils.toUnderlineJSONString(mergeCloudPlatform), BaseCloudPlatformModel.class);
    }

    /**
     * 删除虚拟靶标实例
     * @param cloudPlatform
     * @return
     */
    public BaseCloudPlatformModel delete(ImagesDeleteInstanceCloudPlatform cloudPlatform){
        return super.sendDelete(  URL_VERSION + SCENARIOS + "/device_scenario", JsonUtils.toUnderlineJSONString(cloudPlatform), BaseCloudPlatformModel.class);
    }

    /**
     * 获取物理主机服务状态
     * @return
     */
    public CloudPlatformPageModel<ServiceStatus> listServiceStatus() {
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + SCENARIOS + "/service_status", CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<ServiceStatus> list = JSONObject.parseArray(JSONObject.toJSONString(data.getList()),ServiceStatus.class);
        CloudPlatformPageModel<ServiceStatus> model = new CloudPlatformPageModel<>();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(pageResult.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

}
