package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 三级分类
     */
    private Integer thirdCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否导入
     */
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    private Boolean isInternal;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    /**
     * 交换机
     */
    private List<ExtEntityAccess> extEntityAccess;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * Docker容器信息
     */
    private ExtDocker extDocker;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 授权人数
     */
    private Integer authTotal;

    /**
     * 在位状态
     */
    private StatusEnum inPosition;

    /**
     * 使用状态
     */
    private StatusEnum useStatus;

    /**
     * 引用状态
     */
    private Integer referenceStatus;

}
