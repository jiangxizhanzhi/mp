package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ContainerOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.ContainerPageRequest;

/**
 * 运维管控-系统运维-计算实例管理-容器
 * @author wanggang
 */
public interface ContainerService extends BaseOpenStackService {

    ContainerOpenStackModel page(ContainerPageRequest request);
}
