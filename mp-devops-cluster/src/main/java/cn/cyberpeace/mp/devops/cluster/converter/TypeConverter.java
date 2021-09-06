package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.devops.cluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devops.cluster.service.BaseCloudPlatformService;

public interface TypeConverter extends BaseCloudPlatformService {

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
    TypeAllConvertModel findIdIntegerByType(String type);

    /**
     * 根据类型查询
     * @param type
     * @return
     */
    TypeAllConvertModel findIdStringByType(String type);

    /**
     * 查询角色
     * @return
     */
    TypeAllConvertModel  findRoles();

    /**
     * 查询部署类型
     * @return
     */
    TypeAllConvertModel findDeployType();

    /**
     * 查询镜像类型
     * @return
     */
    TypeAllConvertModel findImageType();

    /**
     * 查询语言
     * @return
     */
    TypeAllConvertModel findLanguage();

    /**
     * 查询系统架构
     * @return
     */
    TypeAllConvertModel findSystemArchitecure();

    /**
     * 查询磁盘格式
     * @return
     */
    TypeAllConvertModel findDiskFormat();

    /**
     * 查询访问类型
     * @return
     */
    TypeAllConvertModel findAccessType();

    /**
     * 查询服务分类
     * @return
     */
    TypeAllConvertModel findServiceCategories();

    /**
     * 查询平台
     * @return
     */
    TypeAllConvertModel findInstallerPlatforms();

    /**
     * 查询服务管理-docker服务：启动参数类型
     * @return
     */
    TypeAllConvertModel findServiceStartType();
}
