package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.InstancePageRequest;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
public interface InstanceService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @return
     */
    InstanceCloudPlatformModel page(InstancePageRequest request, String... ignoreFields);

    /**
     * 获取虚拟机总数
     * @return
     */
    Integer getTotalInstances(InstancePageRequest request);
}
