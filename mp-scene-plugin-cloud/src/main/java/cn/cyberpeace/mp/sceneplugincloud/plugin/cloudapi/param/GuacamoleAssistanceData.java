package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuacamoleAssistanceData implements CloudHttpData {
    private List<AssistanceInfo> data;
    private Boolean success;
    private String requestId;

    private String message;

    @Data
    public static class AssistanceInfo {
        private String connectionId;
        private String username;
        private String assistanceUrl;
    }
}
