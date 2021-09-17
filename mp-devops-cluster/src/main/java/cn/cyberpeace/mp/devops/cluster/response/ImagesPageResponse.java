package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtImageInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 镜像分页返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "镜像分页返回信息")
public class ImagesPageResponse  extends BaseCloudPlatformResponse  {

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标地址")
    private String logo;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("子分类")
    private Integer subCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("子分类")
    private Integer thirdCategory;

    /**
     * 虚拟类型：vm/container
     */
    @ApiModelProperty("虚拟类型：vm/container")
    private String imageType;

    /**
     * 操作系统类型
     */
    @ApiModelProperty("操作系统类型")
    private Integer systemType;

    /**
     * 大小
     */
    @ApiModelProperty("大小")
    private String flavor;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 是否公开
     */
    @ApiModelProperty("0:私有，1：自定义，2：公开")
    private PublicEnum isPublic;

    /**
     * 授权人数
     */
    @ApiModelProperty("授权人数")
    private Integer authTotal;

    private Integer imageStatus;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 镜像信息
     */
    private ExtImageInfo extImageInfo;

    /**
     * 虚拟机信息
     */
    private ExtVm extVm;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;
}
