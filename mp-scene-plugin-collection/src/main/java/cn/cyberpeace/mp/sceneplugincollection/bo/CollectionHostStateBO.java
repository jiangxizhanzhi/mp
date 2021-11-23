package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionHostStateBO {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总内存")
    private Long memoryTotal;

    @ApiModelProperty("空闲内存")
    private Long memoryFree;

    @ApiModelProperty("内存使用率")
    private Integer memoryPercent;

    @ApiModelProperty("cpu使用率")
    private Double cpuPercent;

    @ApiModelProperty("CPU核数")
    private Integer cpuCount;

    @ApiModelProperty("磁盘空间")
    private Long diskSpace;

}
