package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

public interface TypeService extends BaseCloudPlatformService{

    String TYPE = "/type";

    /**
     * 查询所有类型数据
     * @return
     */
    TypeCloudPlatformModel findL1AndL2AndL3Type();

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    TypeL2CloudPlatformModel findL1AndL2Type();

    /**
     * 查询接入协议端口
     * @return
     */
    AccessProtocolsPortCloudPlatform findAccessProtocolsPort();

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
