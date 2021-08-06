package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorStatisticsConvertModel {

    /**
     * CPU核数
     */
    private Integer cpus;

    /**
     * 内存大小
     */
    private Long memoryMb;

    /**
     * 磁盘总大小
     */
    private Long localGb;

    /**
     * 已使用CPU核数
     */
    private Long cpusUsed;

    /**
     * 已使用内存大小
     */
    private Long memoryMbUsed;

    /**
     * 已使用磁盘大小
     */
    private Long localGbUsed;

    /**
     * 空闲内存大小
     */
    private Long freeRamMb;

    /**
     * 空闲磁盘大小
     */
    private Long freeDiskGb;

}
