package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorOpenStackModel extends BaseOpenStackModel{

    /**
     * 资源统计
     */
    private List<Hypervisor> hypervisors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Hypervisor{

        /**
         * ID
         */
        private String id;

        /**
         * 主机IP
         */
        private String host_ip;

        /**
         * 主机名称
         */
        private String hostname;

        /**
         * 类型
         */
        private String type;

        /**
         * CPU核数
         */
        private Integer cpu;

        /**
         * 内存大小
         */
        private Long ram;

        /**
         * 磁盘大小
         */
        private Long disk;

        /**
         * 已使用CPU核数
         */
        private Integer cpu_used;

        /**
         * 已使用内存大小
         */
        private Long ram_used;

        /**
         * 已使用磁盘大小
         */
        private Long disk_used;

        /**
         * 运行中的虚拟机数量
         */
        private Integer running_vms;

        /**
         * 服务器
         */
        private List<Server> servers;

        /**
         * 状态
         */
        private String status;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static final class Server{

            /**
             * UUID
             */
            private String uuid;

            /**
             * 名称
             */
            private String name;
        }
    }

    
}
