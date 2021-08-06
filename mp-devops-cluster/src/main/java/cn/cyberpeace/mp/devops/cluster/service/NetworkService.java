package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
public interface NetworkService extends BaseCloudPlatformService {

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    NetworkCloudPlatformModel page(String getParams);
}
