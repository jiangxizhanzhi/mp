package cn.cyberpeace.mp.devopscluster.cloud.platform.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_devops_device")
public class DeviceModel extends BaseModel {

    /**
     * uuid
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @DefaultStr(value = "2022-12-23 16:46:00")
    private String updatedAt;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    @DefaultBool
    private Boolean deleted;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @DefaultStr(value = "2022-12-23 16:46:00")
    private String createdAt;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @DefaultStr(value = "2022-12-23 16:46:00")
    private String deletedAt;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @DefaultStr
    private String name;

    /**
     * 镜像名称（靶标的英文名称）
     */
    @ApiModelProperty(value = "镜像名称（靶标的英文名称）")
    @DefaultStr
    private String image;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String description;

    /**
     * 图标地址
     */
    @ApiModelProperty(value = "图标地址")
    @DefaultStr
    private String logo;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    @DefaultStr
    private String imageType;

    /**
     *
     */
    @ApiModelProperty(value = "靶标分类(TRM:1,TGM:2,CGM:3,其他:0)")
    @DefaultInt
    private Integer mainType;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色")
    @DefaultInt
    private Integer role;

    /**
     * 一级分类
     */
    @ApiModelProperty(value = "一级分类")
    @DefaultInt
    private Integer category;

    /**
     * 二级分类
     */
    @ApiModelProperty(value = "二级分类")
    @DefaultInt
    private Integer secondCategory;

    /**
     * 三级分类
     */
    @ApiModelProperty(value = "三级分类")
    @DefaultInt
    private Integer l3Category;

    /**
     * 是否导入
     */
    @ApiModelProperty(value = "是否导入")
    @DefaultBool
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    @ApiModelProperty(value = "是否默认靶标")
    @DefaultBool
    private Boolean isInternal;

    /**
     * 是否初始靶标
     */
    @ApiModelProperty(value = "是否初始靶标")
    @DefaultBool
    private Boolean isInit;

    /**
     * 实体信息
     */
    @DefaultStr
    private String extEntity;

    /**
     * 交换机
     */
    @DefaultStr
    private String extEntityAccess;

    /**
     * 系统信息
     */
    @DefaultStr
    private String extSystemInfo;

    /**
     * 网关信息
     */
    @DefaultStr
    private String extGateway;

    /**
     * 虚拟机信息
     */
    @DefaultStr
    private String extVm;

    /**
     * 镜像信息
     */
    @DefaultStr
    private String extImageInfo;

    @DefaultStr
    private String extDocker;

    /**
     * 是否内置镜像
     */
    @DefaultBool
    private Boolean isBuildIn;

    /**
     * 是否隐藏
     */
    @DefaultBool
    private Boolean isHide = Boolean.FALSE;

    /**
     * 默认可运行的镜像名称
     */
    @DefaultStr
    private String defaultLaunchImageName;
}
