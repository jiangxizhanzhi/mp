package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 微软云虚拟机系统磁盘信息列表
 * @create: 2022-01-04 14:05
 **/
@Data
@Table("mp_azure_vm_os_disk")
public class AzureVmOsDisk extends BaseModel {
    @ApiModelProperty(value = "磁盘类型名称")
    private String name;
    @ApiModelProperty(value = "性能等级名称")
    private String size;
    @ApiModelProperty(value = "磁盘最大容量")
    private String maxSizeGiB;
    @ApiModelProperty(value = "磁盘最大容量")
    private String maxSize;
    @ApiModelProperty(value = "磁盘容量单位")
    private String maxSizeUnit;
    @ApiModelProperty(value = "IOPS")
    private String iops;
    @ApiModelProperty(value = "扩展IOPS")
    private String iopsPerformancePlus;
    @ApiModelProperty(value = "磁盘吞吐量")
    private String bandwidthMbps;
    @ApiModelProperty(value = "扩展吞吐量")
    private String bandwidthMbpsPerformancePlus;
    @ApiModelProperty(value = "最大突发IOPS")
    private String maxBurstIops;
    @ApiModelProperty(value = "最大突发吞吐量")
    private String maxBurstBandwidthMbps;
    @ApiModelProperty(value = "最大突发持续时间")
    private String maxBurstDurationInMin;


}