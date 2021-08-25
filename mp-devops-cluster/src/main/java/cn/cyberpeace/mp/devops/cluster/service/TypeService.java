package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;

public interface TypeService extends BaseCloudPlatformService{

    /**
     * 查询所有类型数据
     * @return
     */
    TypeCloudPlatformModel findL1AndL2AndL3Type();

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    TypeCloudPlatformModel findL1AndL2Type();
}
