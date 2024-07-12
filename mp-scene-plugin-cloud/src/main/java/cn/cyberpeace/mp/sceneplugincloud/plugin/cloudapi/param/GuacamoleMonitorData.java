 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GuacamoleMonitorData.java.
 // File size 814.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @ClassName GuacamoleMonitorData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleMonitorData implements CloudHttpData {
    private List<MonitorInfo> data;
    private Boolean success;
    private String requestId;

    private String message;

    @Data
    public static class MonitorInfo {
        private String connectionId;
        private String username;
        private String monitorUrl;
    }
}
