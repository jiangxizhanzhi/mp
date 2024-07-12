 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Remote.java.
 // File size 17551.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * @ClassName Remote
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class Remote extends CloudClient {

    /**
     * createConnection
     *
     * @param createConnectionData
     * @return
     */
    public GuacamoleCreateConnectionResult createConnection(GuacamoleCreateConnectionData createConnectionData) {
        GuacamoleCreateConnectionResult result = getHttpClient().post(CloudUrlConstant.CREATE_CONNECTION_URL,
                createConnectionData, null, GuacamoleCreateConnectionResult.class);
        if (result == null) {
            return null;
        }

        return result;
    }

    /**
     * deleteConnection
     *
     * @param connectionId
     * @return boolean
     */
    public boolean deleteConnection(String connectionId) {
        var result = getHttpClient().delete(CloudUrlConstant.DELETE_CONNECTION_URL, new DisableGuacamoleRecordingData(connectionId),
                null, DisableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            log.error("delete connection error,connection id:{},message: {}", connectionId, result.getMessages());
        }
        return result.getSuccess();
    }

    /**
     * getConnectionUrl
     *
     * @param connectionId
     * @param userId
     * @return
     */
    @Deprecated
    public GuacamoleGetConnectionUrlResult getConnectionUrl(String connectionId, String userId) {
        GuacamoleGetConnectionUrlResult result = getHttpClient().get(CloudUrlConstant.GET_CONNECTION_URL,
                new GuacamoleGetConnectionUrlParam(connectionId, userId), GuacamoleGetConnectionUrlResult.class);
        if (result == null) {
            return null;
        }

        return result;
    }

    /**
     * getNoVncConnectionUrl
     *
     * @param param
     * @return
     */
    public GetConnectionUrlResult getNoVncConnectionUrl(GetConnectionUrlParam param) {
        var res = getHttpClient().get(CloudUrlConstant.VIRTUAL_MACHINE_ACCESS_URL,
                param, GetConnectionUrlResult.class);
        return res;
    }

    /**
     * getContainerConnectionUrl
     *
     * @param param
     * @return
     */
    public GetConnectionUrlResult getContainerConnectionUrl(GetConnectionUrlParam param) {
        var res = getHttpClient().post(CloudUrlConstant.CONTAINER_ACTION_URL.replace("{container_id}", param.getVmId())
                .replace("{action}", "console"), null, null, GetConnectionUrlResult.class);
        return res;
    }

    /**
     * getConnectionFile
     *
     * @param param
     * @param response
     */
    public void getConnectionFile(GetConnectionUrlParam param, HttpServletResponse response) {
//        BufferedInputStream bin = null;
        OutputStream os = null;
        try {
            URL url = new URL(CloudSingleton.cloudAppConfig.getCpcloudServer() + CloudUrlConstant.VIRTUAL_MACHINE_ACCESS_URL +
                    "?vm_id=" + param.getVmId() + "&console_type=" + param.getConsoleType());
            // 建立连接
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL引用的资源的通信链接
            httpURLConnection.connect();
//            bin = new BufferedInputStream(httpURLConnection.getInputStream());
            os = response.getOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024 * 5];

            try (BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream())) {
                while ((len = bin.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (Exception e) {
            log.error("download connection file error", e);
        } finally {
//            IOUtils.closeQuietly(bin);
            IOUtils.closeQuietly(os);
        }
    }

    /**
     * getContainerConsoleUrl
     *
     * @param containerId
     * @return
     */
    public String getContainerConsoleUrl(String containerId) {
        ContainerGetResult result = getHttpClient().get(String.format(CloudUrlConstant.CONTAINER_GET_URL, containerId), null, ContainerGetResult.class);
        return result.getData() == null ? null : result.getData().getUrl();
    }

    /**
     * resetCpcloudAllIps
     *
     * @param request
     * @return
     */
    public OriginIpsResult resetCpcloudAllIps(ResetCplcoudAllIpsData request) {
        OriginIpsResult result = getHttpClient().post(CloudUrlConstant.RESET_CPCLOUD_ALL_IPS, request, null, OriginIpsResult.class);
        return result;
    }

    /**
     * getCpcloudOriginIps
     *
     * @return
     */
    public OriginIpsResult getCpcloudOriginIps() {
        OriginIpsResult result = getHttpClient().get(CloudUrlConstant.GET_CPCLOUD_ALL_ORIGIN_IPS, null, OriginIpsResult.class);
        return result;
    }

    /**
     * removeConnection
     *
     * @return
     */
    public String removeConnection() {
        return null;
    }

    /**
     * getMonitors
     *
     * @param connectionIds
     * @return
     */
    public Map<String, GuacamoleMonitorData.MonitorInfo> getMonitors(String[] connectionIds) {
        GuacamoleMonitorData result = getHttpClient().post(CloudUrlConstant.GET_MONITOR_URL,
                new GetGuacamoleMonitorData(Arrays.asList(connectionIds)), null, GuacamoleMonitorData.class);
        if (result == null || result.getData() == null) {
            return null;
        }

        Map<String, GuacamoleMonitorData.MonitorInfo> map = new HashMap<>();
        for (GuacamoleMonitorData.MonitorInfo m : result.getData()) {
            if (map.put(m.getConnectionId(), m) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    /**
     * getAssistances
     *
     * @param connectionIds
     * @return
     */
    public Map<String, GuacamoleAssistanceData.AssistanceInfo> getAssistances(String[] connectionIds) {
        GuacamoleAssistanceData result = getHttpClient().post(CloudUrlConstant.GET_ASSISTANCE_URL,
                new GetGuacamoleAssistanceData(Arrays.asList(connectionIds)), null,
                GuacamoleAssistanceData.class);
        if (result == null || result.getData() == null) {
            return null;
        }

        Map<String, GuacamoleAssistanceData.AssistanceInfo> map = new HashMap<>();
        for (GuacamoleAssistanceData.AssistanceInfo m : result.getData()) {
            if (map.put(m.getConnectionId(), m) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    /**
     * getRecordings
     *
     * @param queryData
     * @return
     */
    public ListObjResult<GuacamoleRecordingData.RecordingInfo> getRecordings(GetGuacamoleRecordingData queryData) {
        GuacamoleRecordingData result = getHttpClient().post(CloudUrlConstant.GET_RECORDING_URL, queryData, null,
                GuacamoleRecordingData.class);
        if (result == null || result.getData() == null) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }

        int total = result.getTotalnum() == null ? 0 : result.getTotalnum();
        return new ListObjResult<>(result.getData(), total);
    }

    /**
     * enableRecording
     *
     * @param connectionId
     * @param sceneName
     * @param sceneId
     * @param nodeName
     * @param nodeId
     * @param username
     * @param department
     * @return
     */
    public String enableRecording(String connectionId, String sceneName,String sceneId, String nodeName,String nodeId, String username,
                                  String department) {
        EnableGuacamoleRecordingData enableGuacamoleRecordingData = new EnableGuacamoleRecordingData();
        enableGuacamoleRecordingData.setConnectionId(connectionId);
        enableGuacamoleRecordingData.setUsername(username == null ? StringUtils.EMPTY : username);
        enableGuacamoleRecordingData.setDepartment(department == null ? StringUtils.EMPTY : department);
        enableGuacamoleRecordingData.setScene(sceneName == null ? StringUtils.EMPTY : sceneName);
        enableGuacamoleRecordingData.setHost(nodeName == null ? StringUtils.EMPTY : nodeName);
        enableGuacamoleRecordingData.setSceneId(sceneId);
        enableGuacamoleRecordingData.setHostId(nodeId);
        EnableGuacamoleRecordingResult result = getHttpClient().put(CloudUrlConstant.ENABLE_RECORDING_URL,
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

    /**
     * disableRecording
     *
     * @param connectionId
     * @return
     */
    public String disableRecording(String connectionId) {
        DisableGuacamoleRecordingResult result = getHttpClient().put(CloudUrlConstant.DISABLE_RECORDING_URL,
                new DisableGuacamoleRecordingData(connectionId), null,
                DisableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            throw new MpRuntimeException(result.getMessages());
        }
        return result.getRecordingName();
    }

    /**
     * convertRecording
     *
     * @param connectionId
     * @return
     */
    public String convertRecording(String connectionId) {
        DisableGuacamoleRecordingResult result = getHttpClient().put(CloudUrlConstant.CONVERT_RECORDING_URL,
                new DisableGuacamoleRecordingData(connectionId), null,
                DisableGuacamoleRecordingResult.class);
        if (!Boolean.TRUE.equals(result.getSuccess())) {
            // 不再抛出异常
            return StringUtils.EMPTY;
        }
        return result.getRecordingName();
    }

    /**
     * deleteRecords
     *
     * @param recordingNames
     */
    public void deleteRecords(List<String> recordingNames) {
        DeleteGuacamoleRecordingData deleteGuacamoleRecordingData = new DeleteGuacamoleRecordingData();
        deleteGuacamoleRecordingData.setRecordingFile(recordingNames);
        DeleteGuacamoleRecordingResultData result = getHttpClient().delete(CloudUrlConstant.DELETE_RECORDING_URL, deleteGuacamoleRecordingData, null
                , DeleteGuacamoleRecordingResultData.class);
    }

    /**
     * getRecords
     *
     * @param connectionIds
     * @return
     */
    public List<String> getRecords(List<String> connectionIds) {
        List<String> recordFiles = new ArrayList<>();
        GetGuacamoleRecordsData getGuacamoleRecordsData = new GetGuacamoleRecordsData();
        getGuacamoleRecordsData.setConnectionId(connectionIds);
        GetGuacamoleRecordingResult result = getHttpClient().post(CloudUrlConstant.GET_RECORDING_NAME_URL, getGuacamoleRecordsData,
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

    /**
     * changeVlanRange
     *
     * @param vlanRange
     * @return boolean
     */
    public boolean changeVlanRange(String vlanRange) {
        if (StringUtils.isEmpty(vlanRange)) {
            return false;
        }
        VlanRangeData vlanRangeData = new VlanRangeData();
        vlanRangeData.setVlans(vlanRange);
        VlanRangeResult result = getHttpClient().post(CloudUrlConstant.VLAN_RANGE, vlanRangeData, null, VlanRangeResult.class);
        return result.getStatus() == null ? false : result.getStatus();
    }

    /**
     * expireAllConnect
     *
     * @param userId
     * @return boolean
     */
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

    /**
     * getRecordRestoreLimitConfig
     *
     * @return
     */
    public RecordRestoreLimitConfigResult getRecordRestoreLimitConfig() {
        RecordRestoreLimitConfigResult result = getHttpClient().get(CloudUrlConstant.GET_RECORD_CLEAN_CONFIG, null, RecordRestoreLimitConfigResult.class);
        return result;
    }

    /**
     * updateRecordRestoreLimitConfig
     *
     * @param data
     */
    public void updateRecordRestoreLimitConfig(RecordRestoreLimitConfigData data) {
        getHttpClient().put(CloudUrlConstant.UPDATE_RECORD_CLEAN_CONFIG, data, null, Object.class);
    }


    /**
     * 查询环境总速率
     * @param portIds
     * @param time
     * @return
     */
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

    /**
     * activeConnection
     *
     * @param connectionIdList
     * @return
     */
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
