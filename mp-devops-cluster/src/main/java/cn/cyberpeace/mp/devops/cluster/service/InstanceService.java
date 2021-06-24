package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.InstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
public interface InstanceService extends BaseOpenStackService {

    /**
     * 分页查询
     * @return
     */
    InstanceOpenStackModel page(InstancePageRequest request);
}
