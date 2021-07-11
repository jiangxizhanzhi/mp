package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "镜像分页返回信息")
public class ImagesPageVO extends BaseVO {

    
    private String uuid;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "图标地址")
    private String logo;

    
    @ApiModelProperty("分类")
    private Integer mainCategory;

    
    @ApiModelProperty("子分类")
    private Integer subCategory;

    
    @ApiModelProperty("子分类")
    private Integer thirdCategory;

    
    @ApiModelProperty("虚拟类型：vm/container")
    private String imageType;

    
    @ApiModelProperty("操作系统类型")
    private Integer systemType;

    
    @ApiModelProperty("大小")
    private String flavor;

    
    @ApiModelProperty("描述")
    private String description;

    
    @ApiModelProperty("0:私有，1：自定义，2：公开")
    private PublicEnum isPublic;

    
    @ApiModelProperty("授权人数")
    private Integer authTotal;

    @ApiModelProperty("镜像状态")
    private Integer imageStatus;

    
    @ApiModelProperty("是否导入")
    private Boolean isImport;

    
    @ApiModelProperty("是否默认靶标")
    private Boolean isInternal;

    
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty("镜像信息")
    private ExtImageInfo extImageInfo;

    
    @ApiModelProperty("虚拟机信息")
    private ExtVm extVm;

    
    @ApiModelProperty("网关信息")
    private ExtGateway extGateway;

    @ApiModelProperty("容器信息")
    private ExtDocker extDocker;

    @ApiModelProperty("是否允许导出")
    private Boolean allowExport;

    @ApiModelProperty("靶标创建用户名称")
    private String createUserName;
}
