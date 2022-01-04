package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Remote extends CloudClient {

    public GuacamoleCreateConnectionResult createConnection(GuacamoleCreateConnectionData createConnectionData) {
        GuacamoleCreateConnectionResult result = getHttpClient().post(CloudUrlConstant.CREATE_CONNECTION_URL,
                createConnectionData, null, GuacamoleCreateConnectionResult.class);
        if (result == null) {
            return null;
        }

        return result;
    }

    public GuacamoleGetConnectionUrlResult getConnectionUrl(String connectionId, String userId) {
        GuacamoleGetConnectionUrlResult result = getHttpClient().get(CloudUrlConstant.GET_CONNECTION_URL,
                new GuacamoleGetConnectionUrlParam(connectionId, userId), GuacamoleGetConnectionUrlResult.class);
        if (result == null) {
            return null;
        }

        return result;
    }

    public String removeConnection() {
        return null;
    }

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

    public ListObjResult<GuacamoleRecordingData.RecordingInfo> getRecordings(GetGuacamoleRecordingData queryData) {
        GuacamoleRecordingData result = getHttpClient().post(CloudUrlConstant.GET_RECORDING_URL, queryData, null,
                GuacamoleRecordingData.class);
        if (result == null || result.getData() == null) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }

        int total = result.getTotalnum() == null ? 0 : result.getTotalnum();
        return new ListObjResult<>(result.getData(), total);
    }

    public String enableRecording(String connectionId, String sceneName, String nodeName, String username, String department) {
        var enableGuacamoleRecordingData = new EnableGuacamoleRecordingData();
        enableGuacamoleRecordingData.setConnectionId(connectionId);
        enableGuacamoleRecordingData.setUsername(username == null ? StringUtils.EMPTY : username);
        enableGuacamoleRecordingData.setDepartment(department == null ? StringUtils.EMPTY : department);
        enableGuacamoleRecordingData.setScene(sceneName == null ? StringUtils.EMPTY : sceneName);
        enableGuacamoleRecordingData.setHost(nodeName == null ? StringUtils.EMPTY : nodeName);
        var result = getHttpClient().put(CloudUrlConstant.ENABLE_RECORDING_URL,
                enableGuacamoleRecordingData, null,
                EnableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            throw new MpRuntimeException(result.getMessgaes());
        }
        return result.getData().getRecordingName();
    }

    public String disableRecording(String connectionId) {
        var result = getHttpClient().put(CloudUrlConstant.DISABLE_RECORDING_URL,
                new DisableGuacamoleRecordingData(connectionId), null,
                DisableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            throw new MpRuntimeException(result.getMessages());
        }
        return result.getRecordingName();
    }
}
