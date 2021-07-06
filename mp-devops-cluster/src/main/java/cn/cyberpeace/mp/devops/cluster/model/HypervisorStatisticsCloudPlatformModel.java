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
    private HypervisorStatistics hypervisor_statistics;

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
        private Long memory_mb;

        /**
         * 磁盘总大小
         */
        private Long local_gb;

        /**
         * 已使用CPU核数
         */
        private Long vcpus_used;

        /**
         * 已使用内存大小
         */
        private Long memory_mb_used;

        /**
         * 已使用磁盘大小
         */
        private Long local_gb_used;

        /**
         * 空闲内存大小
         */
        private Long free_ram_mb;

        /**
         * 空闲磁盘大小
         */
        private Long free_disk_gb;

        /**
         * 当前工作负载
         */
        private Long current_workload;

        /**
         * 运行中的虚拟机数量
         */
        private Integer running_vms;

        /**
         * 真实的空闲磁盘大小
         */
        private Long disk_available_least;
    }

    
}
