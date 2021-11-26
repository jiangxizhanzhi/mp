package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点资源配置信息(cpu，内存，磁盘等")
public class ElementResource {

//    @ApiModelProperty(value = "节点资源-CPU")
//    private int nodeCPUs;
//
//    @ApiModelProperty(value = "节点资源-内存 (单位GB)")
//    private double memSize;
//
//    @ApiModelProperty(value = "节点资源-硬盘 (单位GB)")
//    private double diskSize;

    @ApiModelProperty("总内存(MB)")
    private Integer memoryTotal;

    @ApiModelProperty("空闲内存(MB)")
    private Integer memoryFree;

    @ApiModelProperty("内存使用率")
    private Double memoryPercent;

    @ApiModelProperty("cpu使用率")
    private Double cpuPercent;

    @ApiModelProperty("CPU核数")
    private Integer cpuCount;

    @ApiModelProperty("磁盘空间(GB)")
    private Integer diskSpace;

}
