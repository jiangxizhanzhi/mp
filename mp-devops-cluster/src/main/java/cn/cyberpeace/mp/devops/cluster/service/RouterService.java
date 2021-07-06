package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.RouterPageRequest;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
public interface RouterService extends BaseCloudPlatformService {

    /**
     * 分页查询路由信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    RouterCloudPlatformModel page(RouterPageRequest request, String... ignoreFields);

    /**
     * 获取路由总数
     * @return
     */
    Integer getTotalRouters(RouterPageRequest request);
}
