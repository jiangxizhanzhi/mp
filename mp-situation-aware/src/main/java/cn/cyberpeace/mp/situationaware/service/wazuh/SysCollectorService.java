package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.config.WazuhConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentPortsModel;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.*;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.*;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/23 16:55
 */
@Service
@Slf4j
public class SysCollectorService extends RequestProcessor {

    @Autowired
    private WazuhConfig wazuhConfig;

    @Autowired
    private AgentService agentService;

    /**
     * 查询端口
     *
     * @return 端口号
     */
    public PageInfo<Port> pagePorts(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new PageInfo<>(new ArrayList<>());
        }
        var uri = "/syscollector/" + agentId + "/ports";
        var requestUrl = wazuhConfig.getBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Port> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Port> ports = result.getData().getAffectedItems();

        PageInfo pageInfo = new PageInfo(ports);
        pageInfo.setTotal(result.getData().getTotalAffectedItems());

        return pageInfo;
    }


    /**
     * 查询进程
     *
     * @return 进程集合
     */
    public PageInfo<Process> pageProcesses(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new PageInfo<>(new ArrayList<>());
        }
        var uri = "/syscollector/" + agentId + "/processes";
        var requestUrl = wazuhConfig.getBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Process> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Process> processes = result.getData().getAffectedItems();
        PageInfo pageInfo = new PageInfo(processes);
        pageInfo.setTotal(result.getData().getTotalAffectedItems());

        return pageInfo;
    }


    /**
     * 查询网卡列表
     *
     * @return 进程集合
     */
    public PageInfo<Netiface> pageNetifaces(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new PageInfo<>(new ArrayList<>());
        }
        var uri = "/syscollector/" + agentId + "/netiface";
        var requestUrl = wazuhConfig.getBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Netiface> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Netiface> netifaces = result.getData().getAffectedItems();

        netifaces.forEach(n -> {
            List<NetAddr> netAddrs = listNetAddrs(agentId, n.getName(), "ipv4");
            if (CollectionUtil.isNotEmpty(netAddrs)) {
                n.setIp(netAddrs.get(0).getAddress());
            }
        });

        PageInfo pageInfo = new PageInfo(netifaces);
        pageInfo.setTotal(result.getData().getTotalAffectedItems());

        return pageInfo;
    }

    /**
     * 查询网卡列表
     *
     * @return 进程集合
     */
    public List<NetAddr> listNetAddrs(String agentId, String iface, String proto) {
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null");
            return Collections.emptyList();
        }
        var uri = "/syscollector/" + agentId + "/netaddr";
        var requestUrl = wazuhConfig.getBaseUrl() + uri;


        List<NameValuePair> params = processRequestParams(new BaseRequest());
        if (StringUtils.isNotBlank(iface)) {
            params.add(new BasicNameValuePair("iface", URLUtil.encode(iface)));
        }

        if (StringUtils.isNotBlank(proto)) {
            params.add(new BasicNameValuePair("proto", URLUtil.encode(proto)));
        }

        log.info("requestUrl:{}, params:{}", requestUrl, params);

        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<NetAddr> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        if (Objects.isNull(result.getData()) || CollectionUtil.isEmpty(result.getData().getAffectedItems())) {
            return Collections.emptyList();
        }
        List<NetAddr> netAddrs = result.getData().getAffectedItems();

        return netAddrs;
    }


    /**
     * @param
     * @return
     */
    public List<Hardware> getHardware(String agentName) {

        String agentId = agentService.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return null;
        }

        var requestUrl = wazuhConfig.getBaseUrl() + "/syscollector/" + agentId + "/hardware";

        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        BaseResponse<Hardware> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Hardware> hardwares = result.getData().getAffectedItems();
        return hardwares;
    }



    /**
     * @param
     * @return
     */
    public List<OS> getOS(String agentName) {

        String agentId = agentService.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return null;
        }

        var requestUrl = wazuhConfig.getBaseUrl() + "/syscollector/" + agentId + "/os";

        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        BaseResponse<OS> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<OS> oslist = result.getData().getAffectedItems();
        return oslist;
    }

    /**
     * 查询agentPort
     *
     * @param agents agents
     * @return 端口号
     */
    @Deprecated
    public List<WazuhAgentPortsModel> queryAgentsPorts(List<String> agents) {
        List<WazuhAgentPortsModel> agentPortsModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return agentPortsModels;
        }
        List<String> agentIds = agentService.listAgentIds(agents);
        var uri = "/experimental/syscollector/ports";
        var requestUrl = wazuhConfig.getBaseUrl() + uri + "?agents_list=" + String.join(",", agentIds);

        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
        agentPortsModels = agentArray.toJavaList(WazuhAgentPortsModel.class);
        return agentPortsModels;
    }

    @Deprecated
    public List<WazuhAgentProcessModel> queryAgentsProcesses(List<String> agents) {
        List<WazuhAgentProcessModel> wazuhAgentProcessModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return wazuhAgentProcessModels;
        }
        List<String> agentIds = agentService.listAgentIds(agents);
        var uri = "/experimental/syscollector/processes";
        var requestUrl = wazuhConfig.getBaseUrl() + uri + "?agents_list=" + String.join(",", agentIds);
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
        wazuhAgentProcessModels = agentArray.toJavaList(WazuhAgentProcessModel.class);

        return wazuhAgentProcessModels;
    }


    /**
     * 查询hardware信息
     *
     * @param agentId
     * @return
     * @throws IOException
     */
    @Deprecated
    public WazuhRamRoot queryRamByAgentId(String agentId) {
        var requestUrl = wazuhConfig.getBaseUrl() + "/syscollector/" + agentId + "/hardware";
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhRamRoot.class);
    }

    @Override
    public List<NameValuePair> processRequestParams(BaseRequest baseRequest) {
        return super.processRequestParams(baseRequest);
    }

}
