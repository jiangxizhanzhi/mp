package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhModel {

    @JSONField(name = "MemoryTotal")
    @ApiModelProperty("总内存")
    private long memoryTotal;

    @JSONField(name = "MemoryFree")
    @ApiModelProperty("空闲内存")
    private long memoryFree;

    @JSONField(name = "MemoryPercent")
    @ApiModelProperty("内存利用率")
    private double memoryPercent;

    @JSONField(name = "CPUTotalPercent")
    @ApiModelProperty("CPU利用率")
    private double cpuTotalPercent;

    @JSONField(name = "CPUCount")
    @ApiModelProperty("CPU核数")
    private int cpuCount;

    @ApiModelProperty("磁盘总空间")
    private long diskTotal;

    @ApiModelProperty("磁盘已用空间")
    private long diskUsed;

    @ApiModelProperty("磁盘使用率")
    private double diskPercent;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @ApiModelProperty("macId")
    private List<String> macIds;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
