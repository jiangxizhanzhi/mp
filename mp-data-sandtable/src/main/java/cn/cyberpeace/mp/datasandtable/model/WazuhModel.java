package cn.cyberpeace.mp.datasandtable.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WazuhModel {

    @JSONField(name = "MemoryTotal")
    private long memoryTotal;
    @JSONField(name = "MemoryFree")
    private long memoryFree;
    @JSONField(name = "MemoryPercent")
    private int memoryPercent;
    @JSONField(name = "CPUTotalPercent")
    private double CPUTotalPercent;
    @JSONField(name = "CPUCount")
    private int CPUCount;
    @JSONField(name = "DiskSpace")
    private long diskSpace;

    private String agentId;

    private String macId;
}
