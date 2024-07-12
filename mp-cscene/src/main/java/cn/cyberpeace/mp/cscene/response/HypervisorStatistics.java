 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HypervisorStatistics.java.
 // File size 1368.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName HypervisorStatistics
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HypervisorStatistics implements Serializable {

    /**
     * 监视器总数
     */
    private Integer count;

    /**
     * CPU总核数
     */
    private Integer vcpus;

    /**
     * 内存总大小
     */
    private Long memoryMb;

    /**
     * 磁盘总大小
     */
    private Long localGb;

    /**
     * 已使用CPU核数
     */
    private Long vcpusUsed;

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

    /**
     * 当前工作负载
     */
    private Long currentWorkload;

    /**
     * 运行中的虚拟机数量
     */
    private Integer runningVms;

    /**
     * 真实的空闲磁盘大小
     */
    private Long diskAvailableLeast;
}

