package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.ContainerActionCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public CloudPlatformPageResult<ContainerModel> page(String getParams) {
        return super.page(URL_VERSION + CONTAINERS + getParams,ContainerModel.class);
    }

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public BaseCloudPlatformResult action(ContainerActionCloudPlatformModel model, String uuid) {
        return super.sendPost(URL_VERSION + CONTAINERS + "/" + uuid +"/action", JsonUtils.toUnderlineJSONString(model), BaseCloudPlatformResult.class);
    }
}
