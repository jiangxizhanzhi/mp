package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.utils.PublicWazuhHttpsUtils;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SituationAwareConfig situationAwareConfig;


    /**
     * 查询所有agent信息
     *
     * @return
     */
    @Cacheable(cacheManager = SituationCacheConfig.CACHE_MANAGER, value = CACHE_KEY_AGENTS)
    public PageInfo<Agent> pageAgents(AgentRequest agentRequest, Integer cloudType) {
        agentRequest.ensure();
        var requestUri = "/agents";
        String requestUrl = null;
        if (null != cloudType && BaseSceneModel.CloudType.PUBLIC == cloudType) {
            requestUrl = situationAwareConfig.getPublicWazuhBaseUrl() + requestUri;
        } else {
            requestUrl = situationAwareConfig.getWazuhBaseUrl() + requestUri;
        }
        List<NameValuePair> params = processRequestParams(agentRequest);
        log.info("requestUrl:{}", requestUrl);
        String res = "";
        if (null != cloudType && BaseSceneModel.CloudType.PUBLIC == cloudType) {
            res = PublicWazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        } else {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        }
        BaseResponse<Agent> result = JSONObject.parseObject(res, new TypeReference<BaseResponse<Agent>>() {
        });
        log.info("{}", result);
        List<Agent> agents = result.getData().getAffectedItems();

        PageInfo pageInfo = new PageInfo(agents);
        pageInfo.setTotal(result.getData().getTotalAffectedItems());

        return pageInfo;
    }

    public List<Agent> listAgents(AgentRequest agentRequest, Integer cloudType) {
        PageInfo<Agent> agentPageInfo = pageAgents(agentRequest, cloudType);
        return agentPageInfo.getList();
    }


    /**
     * 查询指定agentName的agentId
     *
     * @param agentNames
     * @return
     */
    public List<String> listAgentIds(List<String> agentNames) {
        AgentRequest agentRequest = new AgentRequest();
        if (CollectionUtils.isNotEmpty(agentNames)) {
            agentRequest.setName(agentNames.stream().collect(Collectors.joining(",")));
        }
        List<Agent> agents = listAgents(agentRequest, null);

        if (CollectionUtils.isEmpty(agents)) {
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
    public String getSingleAgentId(String agentName, Integer cloudType) {
        if (StringUtils.isNotBlank(agentName)) {
            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setName(agentName);
            List<Agent> agents = listAgents(agentRequest, cloudType);

            if (CollectionUtils.isEmpty(agents)) {
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

        if (StringUtils.isNotBlank(agentRequest.getIp())) {
            params.add(new BasicNameValuePair("ip", agentRequest.getIp()));
        }

        if (StringUtils.isNotBlank(agentRequest.getStatus())) {
            params.add(new BasicNameValuePair("status", agentRequest.getStatus()));
        }

        return params;
    }

}
