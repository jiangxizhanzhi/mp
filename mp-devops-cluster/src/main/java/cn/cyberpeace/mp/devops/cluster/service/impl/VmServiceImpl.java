package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.InstancePageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.VmCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devops.cluster.service.VmService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class VmServiceImpl extends BaseOpenStackServiceImpl implements VmService {

    public VmServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public InstanceCloudPlatformModel page(String getParams) {
        InstancePageResult pageResult = super.sendGet(URL_VERSION + "/instances" + getParams, InstancePageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<InstanceModel> data = pageResult.getData();
        List<InstanceModel> instanceModels = data.getList();
        InstanceCloudPlatformModel model = new InstanceCloudPlatformModel();
        model.setInstances(CollectionUtils.isEmpty(instanceModels) ? Lists.newArrayList() : instanceModels);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    @Override
    public void create(VmCreateCloudPlatformModel vmCreateCloudPlatformModel) {
        super.sendPost(URL_VERSION + "/scenarios/vm", JsonUtils.toUnderlineJSONString(vmCreateCloudPlatformModel));
    }


}
