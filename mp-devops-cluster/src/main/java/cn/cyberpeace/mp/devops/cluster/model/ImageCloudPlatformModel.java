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
    private Integer second_category;

    /**
     * 镜像类型
     */
    private String image_type;

    /**
     * 扩展虚拟机信息
     */
    private ExtVm ext_vm;

    /**
     * 系统信息
     */
    private ExtSystemInfo ext_system_info;

    /**
     * 扩展镜像信息
     */
    private ExtImageInfo ext_image_info;

    /**
     * 扩展网关信息
     */
    private ExtGateway ext_gateway;
}
