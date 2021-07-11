package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentPortsModel;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Hardware;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.NetAddr;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class SysCollectorService extends RequestProcessor {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private AgentService agentService;

    
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        var uri = "/syscollector/" + agentId + "/ports";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Port> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        List<Port> ports = result.getData().getAffectedItems();

        return new ListObjServiceResult(new ListObjResult<>(ports, result.getData().getTotalAffectedItems()).asResult()).asResult();

    }


    
    public ListObjServiceResult<Process> pageProcesses(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        var uri = "/syscollector/" + agentId + "/processes";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<Process> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Process> processes = result.getData().getAffectedItems();

        return new ListObjServiceResult(new ListObjResult<>(processes, result.getData().getTotalAffectedItems()).asResult()).asResult();

    }


    
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName());
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        var uri = "/syscollector/" + agentId + "/netiface";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;

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

        return new ListObjServiceResult(new ListObjResult<>(netifaces, result.getData().getTotalAffectedItems()).asResult()).asResult();
    }

    
    public List<NetAddr> listNetAddrs(String agentId, String iface, String proto) {
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null");
            return Collections.emptyList();
        }
        var uri = "/syscollector/" + agentId + "/netaddr";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;


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


    
    public List<Hardware> getHardware(String agentName) {

        String agentId = agentService.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return null;
        }

        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/syscollector/" + agentId + "/hardware";

        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        BaseResponse<Hardware> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Hardware> hardwares = result.getData().getAffectedItems();
        return hardwares;
    }



    
    public List<OS> getOS(String agentName) {

        String agentId = agentService.getSingleAgentId(agentName);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return null;
        }

        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/syscollector/" + agentId + "/os";

        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        BaseResponse<OS> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<OS> oslist = result.getData().getAffectedItems();
        return oslist;
    }

    
    @Deprecated
    public List<WazuhAgentPortsModel> queryAgentsPorts(List<String> agents) {
        List<WazuhAgentPortsModel> agentPortsModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(agents)) {
            return agentPortsModels;
        }
        List<String> agentIds = agentService.listAgentIds(agents);
        var uri = "/experimental/syscollector/ports";
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri + "?agents_list=" + String.join(",", agentIds);

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
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri + "?agents_list=" + String.join(",", agentIds);
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        JSONArray agentArray = (JSONArray) JSONPath.eval(JSON.parse(res), "$.data.affected_items");
        wazuhAgentProcessModels = agentArray.toJavaList(WazuhAgentProcessModel.class);

        return wazuhAgentProcessModels;
    }


    
    @Deprecated
    public WazuhRamRoot queryRamByAgentId(String agentId) {
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/syscollector/" + agentId + "/hardware";
        String res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        return JSONObject.toJavaObject(JSON.parseObject(res), WazuhRamRoot.class);
    }

    @Override
    public List<NameValuePair> processRequestParams(BaseRequest baseRequest) {
        return super.processRequestParams(baseRequest);
    }

}
