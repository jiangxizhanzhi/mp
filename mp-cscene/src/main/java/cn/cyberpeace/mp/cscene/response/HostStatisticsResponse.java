 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HostStatisticsResponse.java.
 // File size 1658.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.response;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

import java.util.List;

/**
 * @ClassName HostStatisticsResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data

public class HostStatisticsResponse extends BaseModel {

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
        private int vmCount;
        private int containerCount;

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
        private int total;
        private int used;
        private double percent;
    }



}
