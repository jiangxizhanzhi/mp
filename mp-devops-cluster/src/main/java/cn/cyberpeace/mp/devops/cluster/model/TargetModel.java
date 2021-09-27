package cn.cyberpeace.mp.devops.cluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_target")
public class TargetModel extends BaseModel {

    /**
     * 镜像UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

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
     * 子分类
     */
    private Integer thirdCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 是否支持快速创建
     */
    private Boolean quickCreateSupport;

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

    /**
     * 系统类型
     */
    private Integer systemType;

    /**
     * 系统架构
     */
    private String systemArchitecure;

    /**
     * 系统语言
     */
    private String language;

    /**
     * 镜像状态
     */
    private Integer imageStatus;

    /**
     * 是否支持基础镜像
     */
    private Boolean baseImageSupport;
}
