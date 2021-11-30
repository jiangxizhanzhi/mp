package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentModel;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentPortsModel;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhNetifaceRoot;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * wazuh的数据层操作封装dao
 */
@Repository
@Slf4j
public class WazuhRepository {

    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;

    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    /**
     * 查询wazuhToken防止token失效
     * @return
     * @throws IOException
     */
    public String queryWazuhToken() throws IOException {
        var requestUrl = wazuhUrl + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, wazuhUsername, wazuhPassword);
    }


    /**
     * 查询ram信息
     * @param agentId
     * @return
     * @throws IOException
     */
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


    /**
     * 查询文件监控
     * @param requestUri requestUri
     * @return WazuhFileRoot
     */
    public WazuhFileRoot queryFileEvents(String requestUri){
        var requestUrl = wazuhUrl + requestUri;
        String res;
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        } catch (Exception e) {
            log.error("查询文件预警是失败 requestUri {}: stack: {}" , requestUri, e);
            throw new MpRuntimeException("查询文件预警是失败 requestUri : " + requestUri);
        }
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhFileRoot.class);
    }

    /**
     * 查询机器所有的mac地址关系 这里如果遇到性能瓶颈，可做guava缓存，或者缓存查询单个
     * @return
     */
    public WazuhNetifaceRoot queryAllAgentMac() {
        var requestUri = "/experimental/syscollector/ports";
        var requestUrl = wazuhUrl + requestUri;
        String res;
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        } catch (Exception e) {
            log.error("查询文件预警是失败 requestUri {}: stack: {}" , requestUri, e);
            throw new MpRuntimeException("查询文件预警是失败 requestUri : " + requestUri);
        }
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhNetifaceRoot.class);
    }

    /**
     * 查询agentPort
     * @param agents agents
     * @return  端口号
     */
    public List<WazuhAgentPortsModel> queryAgentsPorts(List<String> agents) {
        List<WazuhAgentPortsModel> agentPortsModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return agentPortsModels;
        }
        List<String> agentIds = queryAgentIdByAgentName(agents);
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

    /**
     * 查询所有agent信息
     * @return
     */
    public List<WazuhAgentModel> queryAllAgents() {
        List<WazuhAgentModel> collectAgents = Lists.newArrayList();

        var requestUri = "/agents";
        var requestUrl = wazuhUrl + requestUri;
        String res = null;
        try {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
            JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
            collectAgents = agentArray.toJavaList(WazuhAgentModel.class);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_ERROR.getMessage() + "{}",ExceptionUtils.getMessage(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_ERROR, ExceptionUtils.getStackTrace(e));
        }

        return collectAgents;
    }


    public List<String> queryAgentIdByAgentName(List<String> agentNames) {
        List<WazuhAgentModel> collectAgents = queryAllAgents();
        return collectAgents.stream().filter(agent -> agentNames.contains(agent.getName())).map(WazuhAgentModel::getId).collect(Collectors.toList());
    }
}
