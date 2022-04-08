package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: wazuh agent api
 * @Author zhouxinghuai
 * @Date：2022/2/23 16:12
 */
@Service
@Slf4j
public class AgentService extends RequestProcessor {

    public static final String CACHE_KEY_AGENTS = "agents";

    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;


    /**
     * 查询所有agent信息
     *
     * @return
     */
    @Cacheable(cacheManager = "situationCacheManager", value = CACHE_KEY_AGENTS)
    public PageInfo<Agent> pageAgents(AgentRequest agentRequest) {
        agentRequest.ensure();
        log.info("agentRequest:{}", agentRequest);
        var requestUri = "/agents";
        var requestUrl = wazuhUrl + requestUri;
        List<NameValuePair> params = processRequestParams(agentRequest);
        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Agent> result = JSONObject.parseObject(res, new TypeReference<BaseResponse<Agent>>() {
        });
        log.info("{}", result);
        List<Agent> agents = result.getData().getAffectedItems();

        PageInfo pageInfo = new PageInfo(agents);
        pageInfo.setTotal(result.getData().getTotalAffectedItems());

        return pageInfo;
    }

    public List<Agent> listAgents(AgentRequest agentRequest) {
        PageInfo<Agent> agentPageInfo = pageAgents(agentRequest);
        return agentPageInfo.getList();
    }

    /**
     * 查询所有agent信息
     * @return
     */
    /*public List<Agent> listAgents(List<String> agentNames) {
        log.info("agentNames:{}", agentNames);
        var requestUri = "/agents";
        if (CollectionUtil.isNotEmpty(agentNames)) {
            String names = agentNames.stream().collect(Collectors.joining(","));
            requestUri = requestUri + "?name=" + names;
        }
        var requestUrl = wazuhUrl + requestUri;
        try {
            log.info("requestUrl:{}", requestUrl);
            var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
            BaseResponse<Agent> result = JSONObject.parseObject(res, new TypeReference<BaseResponse<Agent>>(){});
            log.info("{}", result);
            List<Agent> agents = result.getData().getAffectedItems();
            return agents;
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_ERROR.getMessage() + "{}", ExceptionUtils.getMessage(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }*/


    /**
     * 查询指定agentName的agentId
     *
     * @param agentNames
     * @return
     */
    public List<String> listAgentIds(List<String> agentNames) {
        log.info("agentNames:{}", agentNames);
        AgentRequest agentRequest = new AgentRequest();
        if (CollectionUtil.isNotEmpty(agentNames)) {
            agentRequest.setName(agentNames.stream().collect(Collectors.joining(",")));
        }
        List<Agent> agents = listAgents(agentRequest);

        if (CollectionUtil.isEmpty(agents)) {
            return null;
        }

        List<String> ids = agents.stream().map(a -> a.getId()).collect(Collectors.toList());
        return ids;
    }


    /**
     * 查询指定agentName的agentId，agentName需要唯一
     *
     * @param agentName
     * @return
     */
    public String getSingleAgentId(String agentName) {
        log.info("agentName:{}", agentName);
        if (StringUtils.isNotBlank(agentName)) {
            List<Agent> agents = listAgents(new AgentRequest(agentName));

            if (CollectionUtil.isEmpty(agents)) {
                return null;
            }

            if (agents.size() > 1) {
                log.error("agentName:{} is not unique! {}", agentName, agents);
                throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AGENTS_QUERY_ERROR);
            }

            return agents.get(0).getId();
        }

        return null;

    }


    private List<NameValuePair> processRequestParams(AgentRequest agentRequest) {

        List<NameValuePair> params = super.processRequestParams(agentRequest);

        if (StringUtils.isNotBlank(agentRequest.getName())) {
            params.add(new BasicNameValuePair("name", agentRequest.getName()));

        }

        return params;

    }

}
