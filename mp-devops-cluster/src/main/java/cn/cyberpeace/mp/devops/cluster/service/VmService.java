package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.VmCreateCloudPlatformModel;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
public interface VmService extends BaseCloudPlatformService {

    /**
     * 分页查询虚拟机信息
     * @param getParams
     * @return
     */
    InstanceCloudPlatformModel page(String getParams);

    /**
     * 创建虚拟机
     */
    void create(VmCreateCloudPlatformModel vmCreateCloudPlatformModel);
}
