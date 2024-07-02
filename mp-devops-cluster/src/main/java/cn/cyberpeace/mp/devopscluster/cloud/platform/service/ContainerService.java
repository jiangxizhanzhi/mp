package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.ContainerActionCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.ContainerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
@Service
@Slf4j
public class ContainerService extends BaseCpService {

    /**
     * 获取容器列表
     */
    private static final String CONTAINER_LIST = "/cloud/v1/container";

    /**
     * container_id = 容器实例id
     * action = 容器操作( start | stop | suspend | resume | reboot | console )
     */
    private static final String CONTAINER_ACTION = "/cloud/v1/container/{container_id}/{action}";

    /**
     * ContainerService
     *
     * @param devopsClusterConfig
     */
    public ContainerService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询容器信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<ContainerModel> page(String getParams) {
        return super.page(CONTAINER_LIST + getParams,ContainerModel.class);
    }

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    public void action(ContainerActionCloudPlatformModel model, String uuid) {
        String url = UriComponentsBuilder.fromPath(CONTAINER_ACTION).buildAndExpand(uuid, model.getAction()).toUriString();
        BaseCloudPlatformResult result = super.sendPost(url, "", BaseCloudPlatformResult.class);
        super.checkResult(result);
    }


}
