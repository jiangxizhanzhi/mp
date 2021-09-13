package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.ContainerPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.service.ContainerService;
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
public class ContainerServiceImpl extends BaseOpenStackServiceImpl implements ContainerService {

    public ContainerServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public ContainerCloudPlatformModel page(String getParams) {
        ContainerPageResult pageResult =  super.sendGet(URL_VERSION + "/containers" + getParams, ContainerPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<ContainerModel> data = pageResult.getData();
        List<ContainerModel> containerModels = data.getList();
        ContainerCloudPlatformModel model = new ContainerCloudPlatformModel();
        model.setContainers(CollectionUtils.isEmpty(containerModels) ? Lists.newArrayList() : containerModels);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    @Override
    public void create(ContainerCreateCloudPlatformModel model) {
        super.sendPost(URL_VERSION + "/scenarios/container", JsonUtils.toUnderlineJSONString(model));
    }
}
