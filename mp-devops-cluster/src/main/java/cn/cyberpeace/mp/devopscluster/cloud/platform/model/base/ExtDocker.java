package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtDocker implements Serializable {

    @ApiModelProperty("flavor信息")
    private String flavor;

    @ApiModelProperty("CPU核数")
    private Integer cpu;

    @ApiModelProperty("内存大小")
    private Integer ram;

    @ApiModelProperty("磁盘大小")
    private Integer disk;

    
    @ApiModelProperty(value = "文件地址")
    private String file;

    
    @ApiModelProperty(value = "启动参数",hidden = true)
    private String settings;

    
    @ApiModelProperty(value = "状态")
    private Integer syncStatus;

    
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;


    @ApiModelProperty("用户文件名称")
    private String userFilename;


    @ApiModelProperty("实例ID")
    private String instanceUuid;

    
    @ApiModelProperty("实例状态")
    private Integer instanceState;

    
    @ApiModelProperty("支持初始化")
    private Boolean initSupport;

    
    @ApiModelProperty("支持快速创建")
    private Boolean quickCreateSupport;

    @ApiModelProperty("快照数量")
    private Integer snapshotCount;

    
    @ApiModelProperty("扩展信息")
    private Object extInfo;


    
    @ApiModelProperty("接入方式")
    private List<AccessProtocol> accessProtocols;
}
