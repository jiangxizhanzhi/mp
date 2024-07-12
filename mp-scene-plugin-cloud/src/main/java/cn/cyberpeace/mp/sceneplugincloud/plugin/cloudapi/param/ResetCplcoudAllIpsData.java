 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ResetCplcoudAllIpsData.java.
 // File size 932.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ResetCplcoudAllIpsData
 * @Description
 * @Author zhanglin
 * @Date 2023/3/21
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class ResetCplcoudAllIpsData implements CloudHttpData {
    private Controller controller;
    private List<Compute> compute;
    private Office office;

    @Data
    public static class Controller {
        private String ip;
        private String netmask;
        private String gateway;
        private String allocationPools;
    }

    @Data
    public static class Compute {
        private String name;
        private boolean status;
        private String ip;
    }

    @Data
    public static class Office {
        private String ip;
    }
}
