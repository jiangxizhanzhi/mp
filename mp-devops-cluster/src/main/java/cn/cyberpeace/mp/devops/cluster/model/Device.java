package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.devops.cluster.model.common.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 设备信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseCloudPlatformModel{

    /**
     * ID
     */
    private Integer id;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 更新时间
     */
    private String updatedAt;

    /**
     * 删除标记
     */
    private Boolean deleted;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 删除时间
     */
    private String deletedAt;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 图标地址
     */
    private String logo;

    /**
     * 类型
     */
    private String imageType;

    /**
     *
     */
    private Integer maintype;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 一级分类
     */
    private Integer category;

    /**
     * 二级分类
     */
    private Integer secondCategory;

    /**
     * 三级分类
     */
    private Integer l3Category;

    /**
     * 是否导入
     */
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    private Boolean isInternal;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    /**
     * 交换机
     */
    private List<ExtEntityAccess> extEntityAccess;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    private ExtDocker extDocker;

}
