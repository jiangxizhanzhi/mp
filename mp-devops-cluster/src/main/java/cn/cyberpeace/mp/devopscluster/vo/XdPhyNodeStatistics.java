package cn.cyberpeace.mp.devopscluster.vo;

import lombok.Data;

@Data
public class XdPhyNodeStatistics {

    private String nodeName;
    private Double cpuPercent = 0D;
    private Double memoryPercent = 0D;
    private Double fileSystemPercent = 0D;

}
