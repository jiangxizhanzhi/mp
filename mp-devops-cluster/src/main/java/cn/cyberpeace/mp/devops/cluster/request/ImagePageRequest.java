package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.QuickCreateSupportEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import io.swagger.annotations.ApiModel;
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
    private RoleEnum role;

    /**
     * 系统类型
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 镜像类型
     */
    @ApiModelProperty(value = "镜像类型")
    private ImageTypeEnum imageType;

    /**
     * 镜像名称
     */
    private String name;

    /**
     * 快速创建
     */
    private QuickCreateSupportEnum quickCreateSupport;

    /**
     * 系统类型
     */
    private Integer systemType;

    /**
     * 系统架构
     */
    private String systemArchitecure;

    /**
     * 语言
     */
    private String language;

    /**
     * 镜像状态
     */
    private Integer imageStatus;

}
