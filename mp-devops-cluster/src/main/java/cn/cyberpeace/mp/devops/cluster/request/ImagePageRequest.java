package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageRequest extends BasePageRequest {

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    private Integer role;

    /**
     * 系统类型
     */
    @ApiModelProperty(value = "一级分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "二级分类")
    private Integer subCategory;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型")
    private String imageType;

    /**
     * 镜像名称
     */
    @ApiModelProperty(value = "镜像名称")
    private String name;

    /**
     * 快速创建
     */
    @ApiModelProperty(value = "快速构建")
    private Boolean quickCreateSupport;

    /**
     * 系统类型
     */
    @ApiModelProperty(value = "系统类型")
    private Integer systemType;

    /**
     * 系统架构
     */
    @ApiModelProperty(value = "系统架构")
    private String systemArchitecure;

    /**
     * 语言
     */
    @ApiModelProperty(value = "语言")
    private String language;

    /**
     * 镜像状态
     */
    @ApiModelProperty(value = "镜像状态")
    private Integer imageStatus;

    /**
     * 是否支持基础镜像
     */
    @ApiModelProperty(value = "是否支持基础镜像")
    private Boolean baseImageSupport;

}
