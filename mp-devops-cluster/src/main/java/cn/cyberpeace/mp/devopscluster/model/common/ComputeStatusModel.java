package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor

public final class ComputeStatusModel implements Serializable {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskData implements Serializable{

        private String total;

        private String free;

        private String percent;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskIo implements Serializable{

        
        private String test;

    }

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetIo implements Serializable{

        
        private String test;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssistStorage implements Serializable{

        private String total;

        private String used;

    }
}
