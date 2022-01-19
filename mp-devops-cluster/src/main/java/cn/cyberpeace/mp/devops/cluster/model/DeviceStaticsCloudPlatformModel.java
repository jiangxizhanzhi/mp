package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 靶标统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceStaticsCloudPlatformModel extends BaseCloudPlatformModel {

    /**
     * 靶标统计信息
     */
    private Map<String, Map<String,Integer>> data;
}
