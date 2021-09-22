package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 镜像新增请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageRequest extends BaseRequest {

    /**
     * UUID
     */
    private String uuid;

    /**
     * logo文件流
     */
    private MultipartFile logoFile;

    /**
     * 镜像文件流
     */
    private MultipartFile imageFile;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String logo;

    /**
     * 角色：网络/网关/终端
     */
    @ApiModelProperty(value = "角色：网络/网关/终端")
    private Integer role;


    /**
     * 分类
     */
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    /**
     * 二级分类
     */
    @ApiModelProperty(value = "二级分类")
    private Integer secondCategory;

    /**
     * 三级分类
     */
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    /**
     * 虚拟类型
     */
    @ApiModelProperty(value = "虚拟类型")
    private String virtualType;

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
