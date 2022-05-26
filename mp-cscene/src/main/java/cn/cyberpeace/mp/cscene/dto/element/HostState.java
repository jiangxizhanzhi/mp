package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: 节点监控统计信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点状态信息(cpu，内存，磁盘等")
public class HostState {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String elementId;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总内存(MB)")
    private Long memoryTotal;

    @ApiModelProperty("空闲内存(MB)")
    private Long memoryFree;

    @ApiModelProperty("内存使用率")
    private Double memoryPercent;

    @ApiModelProperty("cpu主频")
    private Double cpuFreq;

    @ApiModelProperty("cpu使用率")
    private Double cpuPercent;

    @ApiModelProperty("CPU核数")
    private Integer cpuCount;

    @ApiModelProperty("磁盘空间(GB)")
    private Long diskSpace;

    //@ApiModelProperty("磁盘已使用空间")
    //private Long diskUsed;

    @ApiModelProperty("磁盘未使用空间")
    private Long diskUnused;

    @ApiModelProperty("磁盘使用率")
    private Double diskPercent;

}
