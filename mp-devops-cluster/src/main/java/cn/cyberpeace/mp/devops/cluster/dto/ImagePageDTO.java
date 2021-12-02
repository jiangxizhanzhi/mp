package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageDTO extends BasePageDTO{

    /**
     * 角色
     */
    private Integer role;

    /**
     * 系统类型
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
     * 搜索类型，0：全部; 1：'vm','container','simple'; 2: entity
     */
    private Integer searchType;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 镜像名称
     */
    private String name;

    /**
     * 用户ID
     */
    private String owner;

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
    private Integer inPosition;

    /**
     * 使用状态
     */
    private Integer useStatus;

    /**
     * 1:查询私有和公开的数据
     */
    private Integer searchPublicEnum;

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

    /**
     * 靶标分类，1：内置靶标，2：实体靶标，3：自定义靶标
     */
    private Integer targetCategory;

    /**
     * 是否模糊匹配镜像名称
     */
    private Boolean likeName = Boolean.TRUE;

    public Boolean getLikeName() {
        return Objects.isNull(likeName) ? Boolean.TRUE : likeName;
    }
}
