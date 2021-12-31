package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Installer extends BaseCloudPlatformModel {

    /**
     * ID
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标
     */
    private String logo;

    /**
     * 分类
     */
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    private Integer deployType;

    /**
     * 对应的docker服务的扩展信息
     */
    private ExtDocker extDocker;

    /**
     * 对应的windows服务扩展信息
     */
    private ExtNormal extNormal;

    /**
     * windows服务存放资源
     */
    private List<Resource> resourceList;

    /**
     * 关联镜像数量
     */
    private Integer deviceCount;
}
