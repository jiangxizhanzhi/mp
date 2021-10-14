package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.InstanceOsServiceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.VmActionCloudPlatformModel;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
public interface VmService extends BaseCloudPlatformService {

    String INSTANCES = "/instances";

    /**
     * 分页查询虚拟机信息
     * @param getParams
     * @return
     */
    InstanceCloudPlatformModel page(String getParams);

    /**
     * 操作
     * @param model
     * @param uuid
     * @return
     */
    BaseCloudPlatformModel action(VmActionCloudPlatformModel model, String uuid);

    /**
     *
     * @return
     */
    InstanceOsServiceCloudPlatformModel osServicesStateFromInstances();
}
