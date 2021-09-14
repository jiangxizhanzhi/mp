package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.FirewallCloudPlatformModel;

/**
 * 运维管控-系统运维-防火墙
 * @author wanggang
 */
public interface FirewallService extends BaseCloudPlatformService {

    /**
     * 分页查询防火墙
     * @param getParams
     * @return
     */
    FirewallCloudPlatformModel page(String getParams);
}
