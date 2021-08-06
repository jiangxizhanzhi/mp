package cn.cyberpeace.mp.devops.cluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HypervisorPageConvertModel extends BasePageConvertModel{

    /**
     * 列表
     */
    private List<Model> modelList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class Model {

        /**
         * 节点名称
         */
        private String name;

        /**
         * 节点IP
         */
        private String ip;

        /**
         * 服务状态
         */
        private String status;

        /**
         * 节点管理
         */
        private String state;

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
        private Integer cpuUsed;

        /**
         * 已使用内存大小
         */
        private Long ramUsed;

        /**
         * 已使用磁盘大小
         */
        private Long diskUsed;

        /**
         * CPU使用率
         */
        private BigDecimal cpuPercent;

        /**
         * 内存使用率
         */
        private BigDecimal ramPercent;

        /**
         * 磁盘使用率
         */
        private BigDecimal diskPercent;

        /**
         * 虚拟机数量
         */
        private Integer totalInstances;

        /**
         * 容器数量
         */
        private Integer totalContainers;

        /**
         * 权重
         */
        private Integer weight;

        /**
         * 虚拟机数量限制
         */
        private Integer maxInstances;
    }

}
