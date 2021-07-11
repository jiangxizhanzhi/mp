package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.AccessProtocol;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtGateway;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "根据ID查询返回信息")
public class ImagesByIdVO extends BaseVO {

    
    @ApiModelProperty(value = "UUID")
    private String uuid;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "一级分类")
    private Integer category;

    
    @ApiModelProperty(value = "二级分类")
    private Integer secondCategory;

    
    @ApiModelProperty(value = "三级分类")
    private Integer thirdCategory;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "图标")
    private String logo;

    
    @ApiModelProperty(value = "角色：网络/网关/终端")
    private Integer role;

    
    @ApiModelProperty(value = "虚拟类型")
    private String imageType;

    
    @ApiModelProperty(value = "是否导入")
    private Boolean isImport;

    
    @ApiModelProperty(value = "是否默认靶标")
    private Boolean isInternal;


    
    @ApiModelProperty(value = "是否初始靶标")
    private Boolean isInit;

    @ApiModelProperty(value = "是否允许变更iso文件")
    private Boolean allowChangeIso;

    
    @ApiModelProperty(value = "接入方式")
    private List<AccessProtocol> accessProtocolList;


    
    @ApiModelProperty(value = "系统信息")
    private ExtSystemInfo extSystemInfo;

    
    @ApiModelProperty(value = "镜像信息")
    private ExtImageInfo extImageInfo;

    
    @ApiModelProperty(value = "虚拟信息")
    private ExtVm extVm;

    
    @ApiModelProperty(value = "网关信息")
    private ExtGateway extGateway;

    
    @ApiModelProperty(value = "容器信息")
    private ExtDocker extDocker;
}
