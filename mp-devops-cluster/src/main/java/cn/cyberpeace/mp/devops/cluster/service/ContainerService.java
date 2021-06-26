package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ContainerOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
public interface ContainerService extends BaseOpenStackService {

    /**
     * 分页查询容器信息
     * @param request
     * @return
     */
    ContainerOpenStackModel page(ContainerPageRequest request);

    /**
     * 获取容器总数
     * @return
     */
    Integer getTotalContainers(ContainerPageRequest request);
}
