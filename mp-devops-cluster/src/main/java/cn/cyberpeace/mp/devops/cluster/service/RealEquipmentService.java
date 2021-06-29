package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.RealEquipmentOpenStackModel;

/**
 * 实装管理接口
 */
public interface RealEquipmentService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @return
     */
    RealEquipmentOpenStackModel page();

    /**
     * 获取实装设备总数
     * @return
     */
    Integer getTotalRealEquipment();
}
