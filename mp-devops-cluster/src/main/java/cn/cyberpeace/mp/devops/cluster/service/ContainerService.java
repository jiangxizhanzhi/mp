package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
public interface ContainerService extends BaseCloudPlatformService {

    /**
     * 分页查询容器信息
     * @param getParams
     * @return
     */
    ContainerCloudPlatformModel page(String getParams);
}
