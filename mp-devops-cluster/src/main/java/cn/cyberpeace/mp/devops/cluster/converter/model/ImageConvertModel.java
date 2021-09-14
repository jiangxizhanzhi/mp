package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtImageInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageConvertModel extends BasePageConvertModel{

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 描述
     */
    private String description;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 授权人数
     */
    private Integer authTotal;

}
