package cn.cyberpeace.mp.collection.bo.host;

import cn.cyberpeace.mp.collection.bo.base.BaseEventBO;
import lombok.Data;

@Data
public class HostStateBO extends BaseEventBO {
    /**
     * 总内存
     */
    private long memoryTotal;

    /**
     * 空闲内存
     */
    private long memoryFree;

    /**
     * 内存利用率
     */
    private double memoryPercent;

    /**
     * CPU利用率
     */
    private double cpuTotalPercent;

    /**
     * CPU主频
     */
    private double cpuFreq;

    /**
     * CPU核数
     */
    private int cpuCount;

    /**
     * 磁盘总空间
     */
    private long diskTotal;

    /**
     * 磁盘已用空间
     */
    private long diskUsed;

    /**
     * 磁盘剩余空间
     */
    private long diskUnused;

    /**
     * 磁盘使用率
     */
    private double diskPercent;

    /**
     * 下载速度
     */
    private String downloadSpeed;

    /**
     * 上传速度
     */
    private String uploadSpeed;

    /**
     * 磁盘读取速度 Bytes
     */
    private long diskReadBytes;

    /**
     * 磁盘写速度 Bytes
     */
    private long diskWriteBytes;
}
