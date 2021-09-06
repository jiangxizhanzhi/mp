package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.TypeCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdStringCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TypeIdIntegerCloudPlatformModel;

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

    /**
     * 查询所有角色
     * @return
     */
    TypeIdIntegerCloudPlatformModel findIdIntegerByType(String type);

    /**
     * 查询所有
     * @return
     */
    TypeIdStringCloudPlatformModel findIdStringByType(String type);
}
