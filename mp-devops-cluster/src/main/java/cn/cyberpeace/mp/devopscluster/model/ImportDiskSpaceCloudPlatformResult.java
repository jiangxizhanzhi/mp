package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.*;

import java.io.Serializable;

/**
 * @ClassName ImportDiskSpaceCloudPlatformResult
 * @Description 磁盘剩余空间
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ImportDiskSpaceCloudPlatformResult extends BaseCloudPlatformResult implements Serializable {

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
