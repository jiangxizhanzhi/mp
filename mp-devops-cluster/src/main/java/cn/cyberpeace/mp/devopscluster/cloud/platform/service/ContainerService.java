package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.ContainerActionCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.ContainerModel;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
@Service
@Slf4j
public class ContainerService extends BaseCpService {

    /**
     * 基础容器url
     */
    private static final String CONTAINERS = "/containers";

    public ContainerService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询容器信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<ContainerModel> page(String getParams) {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + CONTAINERS + getParams,ContainerModel.class);
    }

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public void action(ContainerActionCloudPlatformModel model, String uuid) {
        BaseCloudPlatformResult result = super.sendPost(BaseCloudPlatformConstant.URL_VERSION + CONTAINERS + "/" + uuid +"/action", JsonUtils.toUnderlineJSONString(model), BaseCloudPlatformResult.class);
        super.checkResult(result);
        return;
    }
}
