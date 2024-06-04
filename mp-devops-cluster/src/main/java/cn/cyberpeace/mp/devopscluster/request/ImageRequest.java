package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
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
    @ApiModelProperty(value = "UUID")
    private String uuid;

    /**
     * logo文件流
     */
    @ApiModelProperty(value = "logo文件流")
    private MultipartFile logoFile;

    /**
     * 镜像文件流
     */
    @ApiModelProperty(value = "镜像文件流")
    private MultipartFile imageFile;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 版本
     */
    @ApiModelProperty(value = "版本")
    private String deviceVersion;

    /**
     * 父靶标名称
     */
    @ApiModelProperty(value = "父靶标名称")
    private String parentName;

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
     * 唯一标识靶标类型,兼容运维类型
     */
    private Integer roleId;


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
    private String imageType;

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

    /**
     * Docker信息
     */
    @ApiModelProperty(value = "Docker信息")
    private ExtDocker extDocker;

    @ApiModelProperty(value = "是否隐藏")
    private Boolean isHide = Boolean.FALSE;


    @ApiModelProperty(value = "操作系统版本")
    private String osVersion;

    @ApiModelProperty(value = "网卡数量")
    private Integer netIfCnt;

    @ApiModelProperty(value = "镜像位置")
    private String imagePath;


    @ApiModelProperty(value = "是否支持用户行为")
    private Boolean behaveSupport;

    @ApiModelProperty(value = "靶标类型")
    private Integer deviceIdentify;

    @ApiModelProperty(value = "管理员用户密码")
    private String rootPassword;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty("内置应用")
    private String innerApps;

    /**
     * 创建用户名
     */
    private String username;

    @ApiModelProperty("模型ID")
    private String modelId;

    @ApiModelProperty("模型Code")
    private String modelCode;

    @ApiModelProperty("是否是攻防靶标模拟器")
    private Boolean attackMockDeviceFlag;
}

