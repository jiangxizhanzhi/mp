package cn.cyberpeace.mp.devopscluster.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public final class ComputeStatusVO implements Serializable {

    /**
     * 磁盘信息
     */
    private DiskData diskData;

    /**
     * 磁盘IO
     */
    //private DiskIo diskIo;

    /**
     * 内存信息
     */
    private MemoryData memoryData;

    /**
     * cpu信息
     */
    private CpuData cpuData;

    /**
     * 网络IO
     */
    //private NetIo netIo;

    /**
     * 辅助存储信息
     */
    private AssistStorage assistStorage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskData implements Serializable{

        private String total;

        private String free;

        private String percent;

    }

    /*@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskIo implements Serializable{

        // read_count/write_count 读/写 Count 占比
        private Double readWriteCountRatio;

        // read_bytes/write_bytes 读/写 bytes 占比
        private Double readWriteBytesRatio;

        // read_time/write_time 读/写 time 占比
        private Double readWriteTimeRatio;

        // read_merged_count/write_merged_count 读/写 time 占比
        private Double readWriteMergedCountRatio;

        // busy_time
        private Long busytime;

    }*/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryData implements Serializable{

        private String total;

        private String free;

        private String percent;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CpuData implements Serializable{

        private Integer cpuCount;

        private Double cpuUse;

    }

    /*@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetIo implements Serializable{

        // bytes_sent/bytes_recv 发送/接受 字节占比
        private Double sentRecvBytesRatio;

        // packets_sent/packets_recv 发送/接受 包占比
        private Double sentRecvPacketsRatio;
    }*/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssistStorage implements Serializable{

        private String total;

        private String used;

    }
}
