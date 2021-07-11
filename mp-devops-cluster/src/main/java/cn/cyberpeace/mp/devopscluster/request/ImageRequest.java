package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageRequest extends BaseRequest {

    
    @ApiModelProperty(value = "UUID")
    private String uuid;

    
    @ApiModelProperty(value = "logo文件流")
    private MultipartFile logoFile;

    
    @ApiModelProperty(value = "镜像文件流")
    private MultipartFile imageFile;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "图标")
    private String logo;

    
    @ApiModelProperty(value = "角色：网络/网关/终端")
    private Integer role;


    
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    
    @ApiModelProperty(value = "二级分类")
    private Integer secondCategory;

    
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty(value = "虚拟类型")
    private String imageType;

    
    @ApiModelProperty(value = "网关信息")
    private ExtGateway extGateway;

    
    @ApiModelProperty(value = "虚拟类型")
    private ExtVm extVm;

    
    @ApiModelProperty(value = "接入方式")
    private List<AccessProtocol> accessProtocolList;

    
    @ApiModelProperty(value = "系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty(value = "系统镜像")
    private ExtImageInfo extImageInfo;

    
    @ApiModelProperty(value = "Docker信息")
    private ExtDocker extDocker;

    @ApiModelProperty(value = "是否隐藏")
    private Boolean isHide = Boolean.FALSE;
}
