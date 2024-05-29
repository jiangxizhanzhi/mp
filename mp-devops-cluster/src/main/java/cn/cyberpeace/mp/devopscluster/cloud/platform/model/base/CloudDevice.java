package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 靶标信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CloudDevice extends BaseCloudPlatformResult {

    /**
     * ID
     */
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * uuid
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private String updatedAt;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createdAt;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    private String deletedAt;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 镜像名称（靶标的英文名称）
     */
    @ApiModelProperty(value = "镜像名称（靶标的英文名称）")
    private String image;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图标地址
     */
    @ApiModelProperty(value = "图标地址")
    private String logo;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    private Integer role;

    /**
     * 云服务类型
     */
    @ApiModelProperty(value = "云服务类型")
    private String cloudServiceType;

    /**
     * 二级类型
     */
    @ApiModelProperty(value = "二级类型")
    private String serviceType;

    /**
     * 三级类型
     */
    @ApiModelProperty(value = "三级类型")
    private String subType;

    /**
     * 是否导入
     */
    @ApiModelProperty(value = "是否导入")
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    @ApiModelProperty(value = "是否默认靶标")
    private Boolean isInternal;

    /**
     * 是否初始靶标
     */
    @ApiModelProperty(value = "是否初始靶标")
    private Boolean isInit;

    /**
     * 是否内置镜像
     */
    private Boolean isBuildIn;

    /**
     * 是否隐藏
     */
    private Boolean isHide;

    /**
     * 默认可运行的镜像名称
     */
    private String defaultLaunchImageName;

    /**
     * 是否支持导入
     */
    private Boolean supportExport;

    /**
     * 容器-基础设施-容器注册表
     */
    private DockerInfrastructureContainerRegistries dockerInfrastructureContainerRegistries;

    /**
     * 计算-laas-虚拟机信息
     */
    private CalculateLaasVM calculateLaasVM;
}
