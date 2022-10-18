package cn.cyberpeace.mp.collection.core.client.wazuh;

import cn.cyberpeace.mp.collection.core.constant.CollectionErrorCodeConstant;
import cn.cyberpeace.mp.collection.core.exception.MpCollectionRuntimeException;
import cn.cyberpeace.mp.collection.core.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.NetAddr;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.core.request.wazuh.*;
import cn.cyberpeace.mp.collection.core.result.ListObjResult;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class WazuhClient {
    private String wazuhUsername;

    private String wazuhPassword;

    private String wazuhUrl;

    private WazuhClient(String wazuhUrl, String wazuhUsername, String wazuhPassword) {
        this.wazuhUrl = wazuhUrl;
        this.wazuhUsername = wazuhUsername;
        this.wazuhPassword = wazuhPassword;
    }

    public static class WazuhClientBuilder {
        private String wazuhUrl;
        private String wazuhUsername;
        private String wazuhPassword;

        public void configWazuh(String wazuhUrl, String wazuhUsername, String wazuhPassword) {
            this.wazuhUrl = wazuhUrl;
            this.wazuhUsername = wazuhUsername;
            this.wazuhPassword = wazuhPassword;
        }

        public WazuhClient build() {
            return new WazuhClient(wazuhUrl, wazuhUsername, wazuhPassword);
        }
    }

    public static WazuhClientBuilder builder() {
        return new WazuhClientBuilder();
    }

    // expires in 10 minutes
    private static final TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000L);

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

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
        BaseResponse<Port> result = JSON.parseObject(res, new TypeReference<>() {
        });
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

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
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
     *
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

        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, wazuhToken);
        BaseResponse<OS> result = JSON.parseObject(res, new TypeReference<>() {
        });
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
     * 查询wazuhToken防止token失效
     *
     * @return
     */
    private String authenticate() {
        var requestUrl = wazuhUrl + WazuhUrlConstant.AUTHENTICATE_URL;
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, wazuhUsername, wazuhPassword);
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

        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
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
                throw new MpCollectionRuntimeException(CollectionErrorCodeConstant.WAZUH, "agent size is not unique");
            }

            return agents.get(0).getId();
        }

        return null;
    }

    /**
     * 查询所有agent信息
     *
     * @param agentRequest
     * @return
     */
    private ListObjResult<Agent> pageAgents(AgentRequest agentRequest) {
        agentRequest.ensure();
        var requestUrl = wazuhUrl + WazuhUrlConstant.ALL_AGENT_URL;
        List<NameValuePair> params = this.processAgentRequestParams(agentRequest);
        String wazuhToken = getWazuhTokenWithUrl(wazuhUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params, wazuhToken);
        BaseResponse<Agent> result = JSON.parseObject(res, new TypeReference<BaseResponse<Agent>>() {
        });
        List<Agent> agents = result.getData().getAffectedItems();

        return new ListObjResult<>(agents, result.getData().getTotalAffectedItems());
    }

    private List<Agent> listAgents(AgentRequest agentRequest) {
        var result = pageAgents(agentRequest);
        return result.getList();
    }

    private List<NameValuePair> processAgentRequestParams(AgentRequest agentRequest) {

        List<NameValuePair> params = processRequestParams(agentRequest);

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

    private List<NameValuePair> processRequestParams(BasePageRequest pageRequest) {

        List<NameValuePair> params = new ArrayList<>();
        if (Objects.isNull(pageRequest)) {
            params = defaultParams();
            return params;
        }

        if (Objects.isNull(pageRequest.getPageSize()) || pageRequest.getPageSize() <= 0) {
            pageRequest.setPageSize(10);
        }

        if (Objects.isNull(pageRequest.getPage()) || pageRequest.getPage() < 1) {
            pageRequest.setPage(1);
        }

        if (Objects.nonNull(pageRequest.getPageSize())) {
            params.add(new BasicNameValuePair("limit", String.valueOf(pageRequest.getPageSize())));
            Integer offset = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
            params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        }

        if (ArrayUtil.isNotEmpty(pageRequest.getSelect())) {
            params.add(new BasicNameValuePair("select", Joiner.on(",").join(pageRequest.getSelect())));
        }

        if (ArrayUtil.isNotEmpty(pageRequest.getSort())) {
            params.add(new BasicNameValuePair("sort", Joiner.on(",").join(pageRequest.getSelect())));
        }

        return params;
    }

    private List<NameValuePair> processRequestParams(BaseRequest baseRequest) {
        List<NameValuePair> params = defaultParams();

        if (Objects.isNull(baseRequest)) {
            return params;
        }

        if (Objects.nonNull(baseRequest.getLimit())) {
            params.add(new BasicNameValuePair("limit", String.valueOf(baseRequest.getLimit())));
        }

        if (Objects.nonNull(baseRequest.getOffset())) {
            params.add(new BasicNameValuePair("offset", String.valueOf(baseRequest.getOffset())));
        }

        if (StringUtils.isNotBlank(baseRequest.getSelect())) {
            params.add(new BasicNameValuePair("select", String.valueOf(baseRequest.getSelect())));
        }

        return params;
    }

    private List<NameValuePair> defaultParams() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("limit", "10"));
        return params;
    }
}
