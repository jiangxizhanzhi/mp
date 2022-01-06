package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorComputeStatusCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 磁盘信息
     */
    private DiskData diskData;

    /**
     * 磁盘IO
     */
    private HashMap<String,Object> diskIo;

    /**
     * 内存信息
     */
    private MemoryData memoryData;

    /**
     * CPU信息
     */
    private CpuData cpuData;

    /**
     * 网络IO
     */
    private HashMap<String,Object> netIo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DiskData implements Serializable {
        /**
         *
         */
        private String total;

        /**
         *
         */
        private String free;

        /**
         *
         */
        private String percent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DiskIo{

        /**
         * read_count/write_count
         */
        private BigDecimal rcWc;

        /**
         * read_bytes/write_bytes
         */
        private BigDecimal rbWb;

        /**
         * read_time/write_time
         */
        private BigDecimal rtWt;

        /**
         * read_merged_count/write_merged_count
         */
        private BigDecimal rmcWmc;

        /**
         *
         */
        private Integer busyTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class MemoryData implements Serializable{
        /**
         *
         */
        private String total;

        /**
         *
         */
        private String free;

        /**
         *
         */
        private String percent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CpuData implements Serializable{

        private Integer cpuCount;

        private BigDecimal cpuUse;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NetIo{

        /**
         * bytes_sent/bytes_recv
         */
        private BigDecimal bsBr;

        /**
         * packets_sent/packets_recv
         */
        private BigDecimal packetsSentPacketsRecv;
    }
}
