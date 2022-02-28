package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentPortsModel;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Hardware;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/23 16:55
 */
@Service
@Slf4j
public class SysCollectorService extends RequestProcessor  {


    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;

    @Autowired
    private AgentService agentService;

    /**
     * 查询端口
     * @return  端口号
     */
    public List<Port> listPorts(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            return Collections.emptyList();
        }

        var requestUri = "/syscollector/" + agentId + "/ports";
        var requestUrl = wazuhUrl + requestUri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        try {
            log.info("requestUrl:{}", requestUrl);
            var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
            BaseResponse<Port> result = JSONObject.parseObject(res, new TypeReference<>(){});
            log.info("{}", result);
            List<Port> ports = result.getData().getAffectedItems();
            return ports;
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR.getMessage());
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    /**
     * 查询进程
     * @return  进程集合
     */
    public List<Process> listProcesses(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            return Collections.emptyList();
        }

        var requestUri = "/syscollector/" + agentId + "/processes";
        var requestUrl = wazuhUrl + requestUri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        try {
            log.info("requestUrl:{}", requestUrl);
            var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
            BaseResponse<Process> result = JSONObject.parseObject(res, new TypeReference<>(){});
            log.info("{}", result);
            List<Process> processes = result.getData().getAffectedItems();
            return processes;
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PROCESS_ERROR.getMessage());
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PROCESS_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     *
     * @param
     * @return
     */
    public List<Hardware> getHardware(String agentName) {

        String agentId = agentService.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            return null;
        }

        var requestUrl = wazuhUrl + "/syscollector/" + agentId + "/hardware";

        try {
            String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
            BaseResponse<Hardware> result = JSONObject.parseObject(res, new TypeReference<>(){});
            log.info("{}", result);
            List<Hardware> hardwares = result.getData().getAffectedItems();
            return hardwares;
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_HARDWARE_ERROR.getMessage());
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_HARDWARE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询agentPort
     * @param agents agents
     * @return  端口号
     */
    @Deprecated
    public List<WazuhAgentPortsModel> queryAgentsPorts(List<String> agents) {
        List<WazuhAgentPortsModel> agentPortsModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return agentPortsModels;
        }
        List<String> agentIds = agentService.listAgentIds(agents);
        var requestUri = "/experimental/syscollector/ports";
        var requestUrl = wazuhUrl + requestUri + "?agents_list=" + String.join(",", agentIds);

        try {
            var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
            JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
            agentPortsModels = agentArray.toJavaList(WazuhAgentPortsModel.class);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR.getMessage());
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR, ExceptionUtils.getStackTrace(e));
        }
        return agentPortsModels;
    }

    public List<WazuhAgentProcessModel> queryAgentsProcesses(List<String> agents) {
        List<WazuhAgentProcessModel> wazuhAgentProcessModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return wazuhAgentProcessModels;
        }
        var res = "";
        List<String> agentIds = agentService.listAgentIds(agents);
        var requestUri = "/experimental/syscollector/processes";
        var requestUrl = wazuhUrl + requestUri + "?agents_list=" + String.join(",", agentIds);
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
            JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
            wazuhAgentProcessModels = agentArray.toJavaList(WazuhAgentProcessModel.class);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR.getMessage());
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_PORT_ERROR, ExceptionUtils.getStackTrace(e));
        }

        return wazuhAgentProcessModels;
    }



    /**
     * 查询hardware信息
     * @param agentId
     * @return
     * @throws IOException
     */
    @Deprecated
    public WazuhRamRoot queryRamByAgentId(String agentId) {
        var requestUrl = wazuhUrl + "/syscollector/" + agentId + "/hardware";
        String res;
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        } catch (Exception e) {
            log.error("查询ram信息失败:" + e);
            throw new MpRuntimeException("查询ram信息失败");
        }
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhRamRoot.class);
    }

    @Override
    public List<NameValuePair> processRequestParams(BaseRequest baseRequest) {
        return super.processRequestParams(baseRequest);
    }
}
