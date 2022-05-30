package cn.cyberpeace.mp.devops.cluster.request;


import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetSceneRequest extends BasePageRequest implements Serializable {

    /**
     * 系统主类型
     */
    @ApiModelProperty(value = "系统一级类型")
    private Integer category;

    /**
     * 系统子类型
     */
    @ApiModelProperty(value = "系统二级类型")
    private Integer secondCategory;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    private Integer role;

    /**
     * 三级分类
     */
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    /**
     * 镜像类型
     */
    @ApiModelProperty("镜像类型")
    private ImageTypeEnum imageType;

    @ApiModelProperty("忽略用户Token")
    private Boolean ignoreUser;

    @ApiModelProperty("靶标分类，1：内置靶标，2：实体靶标，3：自定义靶标")
    private Integer targetCategory;

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

    @ApiModelProperty("是否模糊查询")
    private Boolean likeName = Boolean.TRUE;

    @ApiModelProperty("是否查询隐藏数据，false:排除隐藏数据")
    private Boolean isHide = Boolean.FALSE;
}
