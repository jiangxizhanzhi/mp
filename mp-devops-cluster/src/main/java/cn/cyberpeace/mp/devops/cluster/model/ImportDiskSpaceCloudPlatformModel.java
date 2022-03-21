package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.io.Serializable;

/**
 * 磁盘剩余空间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ImportDiskSpaceCloudPlatformModel extends BaseCloudPlatformModel implements Serializable {

    /**
     * 磁盘剩余空间
     */
    private DiskSpace data;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static final class DiskSpace implements Serializable {

        /**
         * 虚拟机空间
         */
        private Long vmSpace;

    }
}
