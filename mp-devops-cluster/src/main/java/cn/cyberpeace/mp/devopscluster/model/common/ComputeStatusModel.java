 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ComputeStatusModel.java.
 // File size 1655.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName ComputeStatusModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
//@AllArgsConstructor
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

        // todo
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

        // todo
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
