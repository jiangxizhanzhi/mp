package cn.cyberpeace.mp.collection.core.model.es.hostmonitor;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WazuhHostState {
    private long memoryTotal;

    private long memoryFree;

    private double memoryPercent;

    private double cpuTotalPercent;

    private double cpuFreq;

    private int cpuCount;

    private long diskTotal;

    private long diskUsed;

    private long diskUnused;

    private double diskPercent;

    private String downloadSpeed;

    private String uploadSpeed;

    private long diskReadBytes;

    private long diskWriteBytes;

    private String agentId;

    private String agentName;

    private String agentIp;

    private LocalDateTime time;
}
