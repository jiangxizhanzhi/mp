package cn.cyberpeace.mp.datasandtable.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

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
    private int memoryPercent;

    @JSONField(name = "CPUTotalPercent")
    @ApiModelProperty("CPU利用率")
    private double cpuTotalPercent;

    @JSONField(name = "CPUCount")
    @ApiModelProperty("CPU核数")
    private int cpuCount;

    @JSONField(name = "DiskSpace")
    @ApiModelProperty("磁盘空间")
    private long diskSpace;

    @ApiModelProperty("agentId")
    private String agentId;

    @ApiModelProperty("agentName")
    private String agentName;

    @ApiModelProperty("macId")
    private String macId;

    @JSONField(name = "timestamp")
    @ApiModelProperty("时间")
    private LocalDateTime time;
}
