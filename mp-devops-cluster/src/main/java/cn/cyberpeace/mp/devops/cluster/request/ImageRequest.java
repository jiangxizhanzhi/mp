package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像新增请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像新增/编辑请求信息")
public class ImageRequest extends BaseRequest {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称",required = true)
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述",required = true)
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标",required = true)
    private String logo;

    /**
     * 角色：网络/网关/终端
     */
    @ApiModelProperty(value = "角色：网络/网关/终端")
    private Integer role;

    /**
     * 主分类
     */
    @ApiModelProperty(value = "主分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty(value = "子分类")
    private Integer subCategory;

    /**
     * 虚拟类型
     */
    @ApiModelProperty(value = "虚拟类型")
    private Integer virtualType;

    /**
     * 网关信息
     */
    @ApiModelProperty(value = "网关信息")
    private ExtGateway extGateway;

    /**
     * 扩展的虚拟机信息
     */
    @ApiModelProperty(value = "虚拟类型")
    private ExtVm extVm;

    /**
     * 接入方式
     */
    @ApiModelProperty(value = "接入方式")
    private List<AccessProtocol> accessProtocolList;

    /**
     * 系统信息
     */
    @ApiModelProperty(value = "系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 系统镜像
     */
    @ApiModelProperty(value = "系统镜像")
    private ExtImageInfo extImageInfo;
}
