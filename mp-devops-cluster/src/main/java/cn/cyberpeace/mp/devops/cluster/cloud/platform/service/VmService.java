package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageData;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.VmActionCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 虚拟机
 * @author wanggang
 */
@Service
@Slf4j
public class VmService extends BaseOpenStackService {

    private static final String INSTANCES = "/instances";

    public VmService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询虚拟机信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<InstanceModel> page(String getParams){
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + INSTANCES + getParams, CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<InstanceModel> instanceModels = JSONObject.parseArray(JSONObject.toJSONString(data.getList()), InstanceModel.class);
        CloudPlatformPageModel<InstanceModel> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(instanceModels) ? Lists.newArrayList() : instanceModels);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public BaseCloudPlatformModel action(VmActionCloudPlatformModel model, String uuid){
        return super.sendPost(URL_VERSION + INSTANCES + "/" + uuid  + "/action", JsonUtils.toUnderlineJSONString(model),BaseCloudPlatformModel.class);
    }

    /**
     *
     * @return
     */
    public CloudPlatformPageModel<InstanceOsServiceModel> osServicesStateFromInstances(){
        CloudPlatformPageResult pageResult = super.sendGet(URL_VERSION + INSTANCES + "/os-services", CloudPlatformPageResult.class);
        super.checkResult(pageResult);
        CloudPlatformPageData data = pageResult.getData();
        List<InstanceOsServiceModel> list =  JSONObject.parseArray(JSONObject.toJSONString(data.getList()), InstanceOsServiceModel.class);
        CloudPlatformPageModel<InstanceOsServiceModel> model = new CloudPlatformPageModel();
        model.setList(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }
}
