package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ContainerCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
public interface ContainerService extends BaseCloudPlatformService {

    /**
     * 分页查询容器信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    ContainerCloudPlatformModel page(ContainerPageRequest request, String... ignoreFields);

    /**
     * 获取容器总数
     * @return
     */
    Integer getTotalContainers(ContainerPageRequest request);
}
