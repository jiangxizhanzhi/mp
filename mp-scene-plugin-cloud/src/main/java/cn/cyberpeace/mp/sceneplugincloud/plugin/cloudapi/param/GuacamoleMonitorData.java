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
