package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.RouterModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
@Slf4j
public class RouterService extends BaseOpenStackService {

    public RouterService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询路由信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<RouterModel> page(String getParams) {
        return super.page(URL_VERSION + "/routers" + getParams, RouterModel.class);
    }
}
