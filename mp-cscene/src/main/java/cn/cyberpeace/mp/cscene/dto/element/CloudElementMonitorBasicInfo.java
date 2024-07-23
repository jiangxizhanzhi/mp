package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 云节点监控统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "云节点基本信息")
public class CloudElementMonitorBasicInfo {

    @ApiModelProperty(value = "服务类型")
    private String serviceType;

    @ApiModelProperty(value = "云服务类型")
    private String cloudServiceType;

    @ApiModelProperty(value = "镜像类型")
    private String vmType;

    @ApiModelProperty(value = "虚拟机大小")
    private String vmSizeType;

    @ApiModelProperty(value = "虚拟机系统拓展磁盘类型")
    private String vmExpansionDiskType;

    @ApiModelProperty(value = "cpu数量")
    private Integer cpu;

    @ApiModelProperty(value = "拓展磁盘大小")
    private Integer expansionDisk;

    @ApiModelProperty(value = "内存大小")
    private Integer ram;

    @ApiModelProperty(value = "是否扩容过")
    private Boolean expansion;
}
