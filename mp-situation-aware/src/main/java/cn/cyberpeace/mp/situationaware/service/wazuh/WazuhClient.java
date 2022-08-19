package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.situationaware.config.SituationCacheConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.constant.WazuhUrlConstant;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.NetAddr;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@NoArgsConstructor
public class WazuhClient extends RequestProcessor{
    @Value("${mp.situationaware.wazuh.username}")
    private String wazuhUsername;

    @Value("${mp.situationaware.wazuh.password}")
    private String wazuhPassword;

    @Value("${mp.situationaware.wazuh.base-url}")
    private String wazuhUrl;

    public WazuhClient(String wazuhUrl, String wazuhUsername, String wazuhPassword) {
        this.wazuhUrl = wazuhUrl;
        this.wazuhUsername = wazuhUsername;
        this.wazuhPassword = wazuhPassword;
    }

    public static final String CACHE_KEY_AGENTS = "agents";

    // expires in 14 minutes
    private static TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(14 * 60 * 1000);

    /**
     * 查询wazuhToken防止token失效
     * @return
     */
    public String authenticate() {
        var requestUrl = wazuhUrl + WazuhUrlConstant.AUTHENTICATE_URL;
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, wazuhUsername, wazuhPassword);
    }

    /**
     * 查询所有agent信息
     * @param agentRequest
     * @return
     */
    @Cacheable(cacheManager = SituationCacheConfig.CACHE_MANAGER, value = CACHE_KEY_AGENTS)
    public ListObjResult<Agent> pageAgents(AgentRequest agentRequest) {
        agentRequest.ensure();
        var requestUrl = wazuhUrl + WazuhUrlConstant.ALL_AGENT_URL;
        List<NameValuePair> params = this.processRequestParams(agentRequest);
        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
        BaseResponse<Agent> result = JSON.parseObject(res, new TypeReference<BaseResponse<Agent>>() {
        });
        List<Agent> agents = result.getData().getAffectedItems();

        return new ListObjResult<>(agents, result.getData().getTotalAffectedItems());
    }

    /**
     * 查询端口
     *
     * @return 端口列表
     */
    public ListObjResult<Port> pagePorts(CollectorBaseRequest collectorBaseRequest) {
        String agentId = this.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isEmpty(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        var requestUrl = wazuhUrl + String.format(WazuhUrlConstant.PORT_INFO_URL, agentId);

        List<NameValuePair> params = super.processRequestParams(collectorBaseRequest);

        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
        BaseResponse<Port> result = JSON.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Port> ports = result.getData().getAffectedItems();

        return new ListObjResult<>(ports, result.getData().getTotalAffectedItems());
    }

    /**
     * 查询网卡列表
     *
     * @return 网卡集合
     */
    public ListObjResult<Netiface> pageNetifaces(CollectorBaseRequest collectorBaseRequest) {
        String agentId = this.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        var requestUrl = wazuhUrl + String.format(WazuhUrlConstant.NETIFACE_INFO_URL, agentId);

        List<NameValuePair> params = super.processRequestParams(collectorBaseRequest);

        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Netiface> result = JSON.parseObject(res, new TypeReference<>() {
        });
        List<Netiface> netifaces = result.getData().getAffectedItems();

        netifaces.forEach(n -> {
            List<NetAddr> netAddrs = listNetAddrs(agentId, n.getName(), "ipv4");
            if (CollectionUtils.isNotEmpty(netAddrs)) {
                n.setIp(netAddrs.get(0).getAddress());
            }
        });

        return new ListObjResult<>(netifaces, result.getData().getTotalAffectedItems());
    }

    /**
     * 查询操作系统信息
     * @param agentName
     * @return
     */
    public List<OS> getOS(String agentName) {
        String agentId = this.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return new ArrayList<>();
        }

        var requestUrl = wazuhUrl + String.format(WazuhUrlConstant.OS_INFO_URL, agentId);

        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        BaseResponse<OS> result = JSON.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        return result.getData().getAffectedItems();
    }

    private String getWazuhTokenWithUrl(String wazuhUrl) {
        if (Objects.isNull(TIMED_CACHE.get(wazuhUrl))) {
            String wazuhToken = this.authenticate();
            TIMED_CACHE.put(wazuhUrl, wazuhToken);
            return wazuhToken;
        } else {
            return TIMED_CACHE.get(wazuhUrl);
        }
    }

    /**
     * 查询网卡ip地址
     *
     * @return 网卡ip地址集合
     */
    private List<NetAddr> listNetAddrs(String agentId, String iface, String proto) {
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null");
            return Collections.emptyList();
        }
        var requestUrl = wazuhUrl + String.format(WazuhUrlConstant.NETADDR_INFO_URL, agentId);

        List<NameValuePair> params = processRequestParams(new BaseRequest());
        if (StringUtils.isNotBlank(iface)) {
            params.add(new BasicNameValuePair("iface", URLUtil.encode(iface)));
        }

        if (StringUtils.isNotBlank(proto)) {
            params.add(new BasicNameValuePair("proto", URLUtil.encode(proto)));
        }

        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<NetAddr> result = JSON.parseObject(res, new TypeReference<>() {
        });
        if (Objects.isNull(result.getData()) || CollectionUtils.isEmpty(result.getData().getAffectedItems())) {
            return Collections.emptyList();
        }
        return result.getData().getAffectedItems();
    }

    /**
     * 查询指定agentName的agentId，agentName需要唯一
     *
     * @param agentName
     * @return
     */
    private String getSingleAgentId(String agentName) {
        if (StringUtils.isNotBlank(agentName)) {
            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setName(agentName);
            List<Agent> agents = listAgents(agentRequest);

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

    private List<Agent> listAgents(AgentRequest agentRequest) {
        var result = pageAgents(agentRequest);
        return result.getObjs();
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
