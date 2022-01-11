package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.ContainerPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ContainerActionCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
@Service
@Slf4j
public class ContainerService extends BaseOpenStackService {

    /**
     * 基础容器url
     */
    private static final String CONTAINERS = "/containers";

    public ContainerService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询容器信息
     * @param getParams
     * @return
     */
    public ContainerCloudPlatformModel page(String getParams) {
        ContainerPageResult pageResult =  super.sendGet(URL_VERSION + CONTAINERS + getParams, ContainerPageResult.class);
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

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public BaseCloudPlatformModel action(ContainerActionCloudPlatformModel model,String uuid) {
        return super.sendPost(URL_VERSION + CONTAINERS + "/" + uuid +"/action", JsonUtils.toUnderlineJSONString(model), BaseCloudPlatformModel.class);
    }
}
