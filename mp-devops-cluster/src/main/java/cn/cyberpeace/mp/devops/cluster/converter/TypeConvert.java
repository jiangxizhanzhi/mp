package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.service.BaseCloudPlatformService;

public interface TypeConvert extends BaseCloudPlatformService {

    /**
     * 查询所有类型数据
     * @return
     */
    TypeAllConvertModel findL1AndL2AndL3Type();

    /**
     * 查询类型数据，仅返回L1和L2级类型数据
     * @return
     */
    TypeAllConvertModel findL1AndL2Type();

    /**
     * 查询所有role
     * @return
     */
    TypeAllConvertModel findAllRoleType();

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    TypeAllConvertModel findByType(String type);
}
