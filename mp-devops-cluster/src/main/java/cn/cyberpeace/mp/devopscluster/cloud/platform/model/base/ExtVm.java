package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("虚拟机信息")
public class ExtVm implements Serializable {

    @ApiModelProperty("flavor信息")
    private String flavor;

    
    @ApiModelProperty("支持初始化")
    private Boolean initSupport;

    
    @ApiModelProperty("支持快速创建")
    private Boolean quickCreateSupport;

    
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;

    
    private List<QuickCreateParam> quickCreateParams;

    
    @ApiModelProperty("扩展信息")
    private Object extInfo;

    
    @ApiModelProperty("实例是否存在")
    private Boolean instanceExist;

    @ApiModelProperty("快照数量")
    private Integer snapshotCount;

    @ApiModelProperty("实例ID")
    private String instanceUuid;

    
    @ApiModelProperty("实例状态")
    private Integer instanceState;

    @ApiModelProperty("CPU核数")
    private Integer cpu;

    @ApiModelProperty("内存大小")
    private Integer ram;

    @ApiModelProperty("磁盘大小")
    private Integer disk;

    @ApiModelProperty("是否挂载ISO文件")
    private Boolean isoAttach;
    @ApiModelProperty("ISO文件名称")
    private String isoAttachFilename;
    @ApiModelProperty("ISO临时文件地址")
    private String isoTmpdirUuid;
}
