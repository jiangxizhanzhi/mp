package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorStatisticsCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 资源统计
     */
    private HypervisorStatistics hypervisorStatistics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class HypervisorStatistics{

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

    
}
