package cn.cyberpeace.mp.devops.cluster.vo;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 靶标信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty("ID")
    private Integer id;

    /**
     * uuid
     */
    @ApiModelProperty("uuid")
    private String uuid;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private String updatedAt;

    /**
     * 删除标记
     */
    @ApiModelProperty("删除标记")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createdAt;

    /**
     * 删除时间
     */
    @ApiModelProperty("删除时间")
    private String deletedAt;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 图标地址
     */
    @ApiModelProperty("图标地址")
    private String logo;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String imageType;

    /**
     *
     */
    @ApiModelProperty("自定义类型")
    private Integer maintype;

    /**
     * 角色
     */
    @ApiModelProperty("角色")
    private Integer role;

    /**
     * 一级分类
     */
    @ApiModelProperty("一级分类")
    private Integer category;

    /**
     * 二级分类
     */
    @ApiModelProperty("二级分类")
    private Integer secondCategory;

    /**
     * 三级分类
     */
    @ApiModelProperty("三级分类")
    private Integer thirdCategory;

    /**
     * 是否导入
     */
    @ApiModelProperty("是否导入")
    private Boolean isImport;

    /**
     * 是否默认靶标
     */
    @ApiModelProperty("是否默认靶标")
    private Boolean isInternal;

    /**
     * 实体信息
     */
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    /**
     * 交换机
     */
    @ApiModelProperty("交换机信息")
    private List<ExtEntityAccess> extEntityAccess;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 网关信息
     */
    @ApiModelProperty("网关信息")
    private ExtGateway extGateway;

    /**
     * 虚拟机信息
     */
    @ApiModelProperty("虚拟机信息")
    private ExtVm extVm;

    /**
     * 镜像信息
     */
    @ApiModelProperty("镜像信息")
    private ExtImageInfo extImageInfo;

    @ApiModelProperty("容器信息")
    private ExtDocker extDocker;

}
