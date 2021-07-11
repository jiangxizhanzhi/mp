package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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

    public String getContainerConsoleUrl(String containerId) {
        ContainerGetResult result = getHttpClient().get(String.format(CloudUrlConstant.CONTAINER_GET_URL, containerId), null, ContainerGetResult.class);
        return result.getData() == null ? null : result.getData().getUrl();
    }

    public OriginIpsResult resetCpcloudAllIps(ResetCplcoudAllIpsData request) {
        OriginIpsResult result = getHttpClient().post(CloudUrlConstant.RESET_CPCLOUD_ALL_IPS, request, null, OriginIpsResult.class);
        return result;
    }

    public OriginIpsResult getCpcloudOriginIps() {
        OriginIpsResult result = getHttpClient().get(CloudUrlConstant.GET_CPCLOUD_ALL_ORIGIN_IPS, null, OriginIpsResult.class);
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

    public String enableRecording(String connectionId, String sceneName,String sceneId, String nodeName,String nodeId, String username,
                                  String department) {
        var enableGuacamoleRecordingData = new EnableGuacamoleRecordingData();
        enableGuacamoleRecordingData.setConnectionId(connectionId);
        enableGuacamoleRecordingData.setUsername(username == null ? StringUtils.EMPTY : username);
        enableGuacamoleRecordingData.setDepartment(department == null ? StringUtils.EMPTY : department);
        enableGuacamoleRecordingData.setScene(sceneName == null ? StringUtils.EMPTY : sceneName);
        enableGuacamoleRecordingData.setHost(nodeName == null ? StringUtils.EMPTY : nodeName);
        enableGuacamoleRecordingData.setSceneId(sceneId);
        enableGuacamoleRecordingData.setHostId(nodeId);
        var result = getHttpClient().put(CloudUrlConstant.ENABLE_RECORDING_URL,
                enableGuacamoleRecordingData, null,
                EnableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            if (result.getMessages() != null && result.getMessages().toLowerCase().contains("storage space for screen")) {
                throw new MpRuntimeException(CloudErrorCode.NO_SPACE_RECORDING_ERROR);
            } else {
                throw new MpRuntimeException(CloudErrorCode.ENABLE_RECORDING_ERROR.format(result.getMessages()));
            }
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

    public String convertRecording(String connectionId) {
        var result = getHttpClient().put(CloudUrlConstant.CONVERT_RECORDING_URL,
                new DisableGuacamoleRecordingData(connectionId), null,
                DisableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            
            return StringUtils.EMPTY;
        }
        return result.getRecordingName();
    }

    public void deleteRecords(List<String> recordingNames) {
        var deleteGuacamoleRecordingData = new DeleteGuacamoleRecordingData();
        deleteGuacamoleRecordingData.setRecordingFile(recordingNames);
        var result = getHttpClient().delete(CloudUrlConstant.DELETE_RECORDING_URL, deleteGuacamoleRecordingData, null
                , DeleteGuacamoleRecordingResultData.class);
    }

    public List<String> getRecords(List<String> connectionIds) {
        List<String> recordFiles = new ArrayList<>();
        var getGuacamoleRecordsData = new GetGuacamoleRecordsData();
        getGuacamoleRecordsData.setConnectionId(connectionIds);
        var result = getHttpClient().post(CloudUrlConstant.GET_RECORDING_NAME_URL, getGuacamoleRecordsData,
                null, GetGuacamoleRecordingResult.class);
        if (result == null) {
            return new ArrayList<>();
        }
        result.getData().forEach(record -> {
            if (record.getRecordingFile() != null) {
                recordFiles.add(record.getRecordingFile());
            }
        });
        return recordFiles;
    }

    public boolean changeVlanRange(String vlanRange) {
        if (StringUtils.isEmpty(vlanRange)) {
            return false;
        }
        VlanRangeData vlanRangeData = new VlanRangeData();
        vlanRangeData.setVlans(vlanRange);
        VlanRangeResult result = getHttpClient().post(CloudUrlConstant.VLAN_RANGE, vlanRangeData, null, VlanRangeResult.class);
        return result.getStatus() == null ? false : result.getStatus();
    }

    public boolean expireAllConnect(String userId) {
        GuacamoleLogoutData guacamoleLogoutData = new GuacamoleLogoutData();
        guacamoleLogoutData.setUsername(userId);
        GuacamoleLogoutResult result = getHttpClient().post(CloudUrlConstant.EXPIRE_ALL_CONNECT, guacamoleLogoutData, null, GuacamoleLogoutResult.class);
        if (result != null && Boolean.TRUE.equals(result.getSuccess())) {
            return true;
        } else {
            log.error("failed to exec guacamole logout , userId is {} , result is {}", userId, JsonCommon.stringify(result));
            return false;
        }
    }

    public RecordRestoreLimitConfigResult getRecordRestoreLimitConfig() {
        RecordRestoreLimitConfigResult result = getHttpClient().get(CloudUrlConstant.GET_RECORD_CLEAN_CONFIG, null, RecordRestoreLimitConfigResult.class);
        return result;
    }

    public void updateRecordRestoreLimitConfig(RecordRestoreLimitConfigData data) {
        getHttpClient().put(CloudUrlConstant.UPDATE_RECORD_CLEAN_CONFIG, data, null, Object.class);
    }

    public void createFlavor() {

    }

    
    public boolean addFlowPorts(List<String> portIds) {
        log.info("Remote.addFlowPorts begin. portIds:{}", portIds);
        boolean result = true;
        NetworkFlowRateData networkFlowRateData = new NetworkFlowRateData();
        networkFlowRateData.setPorts(portIds);
        log.info("Call cpclould begin. posturl:{}, params:{}", CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateData);
        CloudResponse<Object> response = cloudPost(CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateData, null, new TypeReference<CloudResponse<Object>>() {}, true);
        log.info("Call cpclould end. posturl:{}, response:{}", CloudUrlConstant.NETWORK_FLOW_RATE, response);
        if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
            result = true;
        } else {
            log.error("failed to addFlowPorts");
            result = false;
        }
        log.info("Remote.addFlowPorts end. result:{}", result);
        return result;
    }

    
    public boolean deleteFlowPorts(List<String> portIds) {
        log.info("Remote.deleteFlowPorts begin. portIds:{}", portIds);
        boolean result = true;
        NetworkFlowRateData networkFlowRateData = new NetworkFlowRateData();
        networkFlowRateData.setPorts(portIds);
        log.info("Call cpclould begin. deleteurl:{}, params:{}", CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateData);
        CloudResponse<Object> response = cloudDelete(CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateData, null, new TypeReference<CloudResponse<Object>>() {}, true);
        log.info("Call cpclould end. deleteurl:{}, response:{}", CloudUrlConstant.NETWORK_FLOW_RATE, response);
        if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
            result = true;
        } else {
            log.error("failed to deleteFlowPorts");
            result = false;
        }
        log.info("Remote.deleteFlowPorts end. result:{}", result);
        return result;
    }

    
    public NetworkFlowRateResult networkFlowRate(List<String> portIds, Long time) {
        log.info("Remote.networkFlowRate begin. portIds:{}, time:{}", portIds, time);
        NetworkFlowRateParam networkFlowRateParam = new NetworkFlowRateParam();
        networkFlowRateParam.setPorts(portIds);
        networkFlowRateParam.setTime(time);
        log.info("Call cpclould begin. geturl:{}, params:{}", CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateParam);
        CloudResponse<NetworkFlowRateResult> response = cloudGet(CloudUrlConstant.NETWORK_FLOW_RATE, networkFlowRateParam, new TypeReference<CloudResponse<NetworkFlowRateResult>>() {}, true);
        log.info("Call cpclould end. geturl:{}, response:{}", CloudUrlConstant.NETWORK_FLOW_RATE, response);
        if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
            return response.getData();
        } else {
            log.error("failed to get networkFlowRate.");
            return null;
        }
    }

    public List<String> activeConnection(List<String> connectionIdList) {
        log.info("Remote.activeConnection begin. connectionIdList:{},", connectionIdList);
        ActiveConnectionParam activeConnectionParam = new ActiveConnectionParam();
        activeConnectionParam.setConnectionIds(connectionIdList);
        log.info("Call cpclould begin. url:{}, params:{}", CloudUrlConstant.ACTIVE_CONNECTION_URL, activeConnectionParam);
        ActiveConnectionData activeConnectionData = getHttpClient().post(CloudUrlConstant.ACTIVE_CONNECTION_URL, activeConnectionParam, null, ActiveConnectionData.class);
        log.info("Call cpclould end. url:{}, response:{}", CloudUrlConstant.ACTIVE_CONNECTION_URL, activeConnectionData);
        if (activeConnectionData != null && Boolean.TRUE.equals(activeConnectionData.getSuccess()) && CollectionUtils.isNotEmpty(activeConnectionData.getCurrentConnections())) {
            return activeConnectionData.getCurrentConnections();
        } else {
            log.error("failed to get activeConnection.");
            return new ArrayList<>();
        }
    }
}
