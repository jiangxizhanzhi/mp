package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleAssistanceParams;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetGuacamoleMonitorParams;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleAssistanceData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleMonitorData;

import java.util.HashMap;
import java.util.Map;

public class Remote extends CloudClient {

    public GuacamoleMonitorData getMonitor(String connectionId) {
        var params = new GetGuacamoleMonitorParams(connectionId);
        GuacamoleMonitorData result = getHttpClient().get(CloudUrlConstant.GET_MONITOR_URL, params,
                GuacamoleMonitorData.class);
        if (result == null) {
            return null;
        }
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            return null;
        }
        return result;
    }

    public Map<String, GuacamoleMonitorData> getMonitors(String[] connectionIds) {
        Map<String, GuacamoleMonitorData> result = new HashMap<>();
        for (var connectionId : connectionIds) {
            var monitorData = getMonitor(connectionId);
            if (monitorData != null) {
                result.put(connectionId, monitorData);
            }
        }
        return result;
    }

    public GuacamoleAssistanceData getAssistance(String connectionId) {
        var params = new GetGuacamoleAssistanceParams(connectionId);
        GuacamoleAssistanceData result = getHttpClient().get(CloudUrlConstant.GET_ASSISTANCE_URL, params,
                GuacamoleAssistanceData.class);
        if (result == null) {
            return null;
        }
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            return null;
        }
        return result;
    }

    public Map<String, GuacamoleAssistanceData> getAssistances(String[] connectionIds) {
        Map<String, GuacamoleAssistanceData> result = new HashMap<>();
        for (var connectionId : connectionIds) {
            var assistanceData = getAssistance(connectionId);
            if (assistanceData != null) {
                result.put(connectionId, assistanceData);
            }
        }
        return result;
    }
}
