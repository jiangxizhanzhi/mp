 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SnmpInterfaceResourceCloudPlatform.java.
 // File size 912.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SnmpInterfaceResourceCloudPlatform
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPlatformResult
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpInterfaceResourceCloudPlatform extends BaseCloudPlatformResult {

    private Boolean status;

    private List<Interfaces> result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Interfaces implements Serializable {

        private String interfaceName;

        private Long inTraffic;

        private Long outTraffic;
    }
}
