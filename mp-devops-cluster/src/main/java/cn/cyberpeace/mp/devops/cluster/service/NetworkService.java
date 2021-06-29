package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
public interface NetworkService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @param request
     * @return
     */
    NetworkCloudPlatformModel page(NetworkPageRequest request, String... ignoreFields);
}
