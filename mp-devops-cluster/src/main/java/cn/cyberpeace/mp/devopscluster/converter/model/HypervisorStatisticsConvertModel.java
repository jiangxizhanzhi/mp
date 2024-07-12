 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorStatisticsConvertModel.java.
 // File size 1048.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName HypervisorStatisticsConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
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
