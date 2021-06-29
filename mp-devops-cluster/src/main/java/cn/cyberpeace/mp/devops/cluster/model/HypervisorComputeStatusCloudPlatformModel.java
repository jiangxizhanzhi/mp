package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class HypervisorComputeStatusCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 磁盘信息
     */
    private DiskData disk_data;

    /**
     * 磁盘IO
     */
    private Map<String,Object> disk_io;

    /**
     * 内存信息
     */
    private MemoryData memory_data;

    /**
     * CPU信息
     */
    private CpuData cpu_data;

    /**
     * 网络IO
     */
    private Map<String,Object> net_io;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class DiskData{
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
        private BigDecimal rc_wc;

        /**
         * read_bytes/write_bytes
         */
        private BigDecimal rb_wb;

        /**
         * read_time/write_time
         */
        private BigDecimal rt_wt;

        /**
         * read_merged_count/write_merged_count
         */
        private BigDecimal rmc_wmc;

        /**
         *
         */
        private Integer busy_time;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class MemoryData{
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
    public static final class CpuData{

        private Integer cpu_count;

        private BigDecimal cpu_use;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class NetIo{

        /**
         * bytes_sent/bytes_recv
         */
        private BigDecimal bs_br;

        /**
         * packets_sent/packets_recv
         */
        private BigDecimal packets_sent_packets_recv;
    }
}
