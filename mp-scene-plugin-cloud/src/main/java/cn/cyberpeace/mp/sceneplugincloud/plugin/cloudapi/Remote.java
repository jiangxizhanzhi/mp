package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleAssistanceData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleMonitorData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleAssistanceData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleMonitorData;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Remote extends CloudClient {

    public Map<String, GuacamoleMonitorData.MonitorInfo> getMonitors(String[] connectionIds) {
        GuacamoleMonitorData result = getHttpClient().post(CloudUrlConstant.GET_MONITOR_URL,
                new GetGuacamoleMonitorData(Arrays.asList(connectionIds)), null, GuacamoleMonitorData.class);
        if (result == null || result.getData() == null) {
            return null;
        }

        return result.getData().stream().collect(Collectors.toMap(m -> m.getConnectionId(), m -> m));
    }

    public Map<String, GuacamoleAssistanceData.AssistanceInfo> getAssistances(String[] connectionIds) {
        GuacamoleAssistanceData result = getHttpClient().post(CloudUrlConstant.GET_ASSISTANCE_URL,
                new GetGuacamoleAssistanceData(Arrays.asList(connectionIds)), null,
                GuacamoleAssistanceData.class);
        if (result == null || result.getData() == null) {
            return null;
        }

        return result.getData().stream().collect(Collectors.toMap(m -> m.getConnectionId(), m -> m));
    }
}
