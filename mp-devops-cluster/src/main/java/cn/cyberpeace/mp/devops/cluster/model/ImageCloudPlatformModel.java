package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtImageInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCloudPlatformModel extends BaseCloudPlatformModel {

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
     * 角色
     */
    private Integer role;

    /**
     * 分类
     */
    private Integer category;

    /**
     * 子分类
     */
    private Integer secondCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 扩展虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 扩展镜像信息
     */
    private ExtImageInfo extImageInfo;

    /**
     * 扩展网关信息
     */
    private ExtGateway extGateway;
}
