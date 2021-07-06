package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.NetworkPageRequest;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
public interface NetworkService extends BaseCloudPlatformService {

    /**
     * 分页查询网络信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    NetworkCloudPlatformModel page(NetworkPageRequest request, String... ignoreFields);
}
