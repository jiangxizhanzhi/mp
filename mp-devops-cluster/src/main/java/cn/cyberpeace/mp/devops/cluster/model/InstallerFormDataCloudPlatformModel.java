package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerFormDataCloudPlatformModel implements Serializable {

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
     * 启动参数
     */
    private String settings;

    /**
     * 资源
     */
    private String resources;
}
