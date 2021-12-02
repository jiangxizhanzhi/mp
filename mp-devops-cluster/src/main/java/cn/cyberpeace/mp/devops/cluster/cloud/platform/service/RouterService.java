package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
public interface RouterService extends BaseCloudPlatformService {

    /**
     * 分页查询路由信息
     * @param getParams
     * @return
     */
    RouterCloudPlatformModel page(String getParams);
}
