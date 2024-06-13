package cn.cyberpeace.mp.cscene.response;

import lombok.Data;

import java.util.List;

@Data

public class HostStatisticsResponse {

    private List<HostData> data;
    private Integer total;
    private HostCount count;



    @Data
    public static class HostData {
        private String id;
        private String name;
        private String hostname;
        private String comment;
        private String description;
        private String address;
        private String status;
        private ResourceUseInfo cpu;
        private ResourceUseInfo memory;
        private ResourceUseInfo disk;
        private Integer vmCount;
        private Integer container_count;

    }

    @Data
    public static class HostCount {
        private Integer vmCount;
        private Integer containersCount;
        private Integer physicalCount;
        private Integer protocolCount;
        private Double hostCpuUsage;
        private Double hostMemoryUsage;
        private Double hostDiskUsage;
        private Integer cpuUsedAbs;
        private Integer memoryUsedAbs;
        private Integer localDiskUsedAbs;
        private Integer cpuResidueAbs;
        private Integer memoryResidueAbs;
        private Integer localDiskResidueAbs;
    }

    @Data
    public static class ResourceUseInfo {
        private Integer total;
        private Integer used;
        private Double percent;
    }



}
