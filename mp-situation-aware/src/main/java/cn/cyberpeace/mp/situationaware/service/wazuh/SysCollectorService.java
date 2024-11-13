package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentPortsModel;
import cn.cyberpeace.mp.situationaware.model.WazuhAgentProcessModel;
import cn.cyberpeace.mp.situationaware.model.WazuhRamRoot;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseResponse;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.*;
import cn.cyberpeace.mp.situationaware.utils.*;
import com.alibaba.fastjson.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private AgentService agentService;

    /**
     * 查询端口
     *
     * @return 端口号
     */
    public ListObjServiceResult<Port> pagePorts(CollectorBaseRequest collectorBaseRequest, Integer cloudType) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName(), cloudType);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        var uri = "/syscollector/" + agentId + "/ports";
        String requestUrl = CloudAwareWazuhHttpClient.getRequestUrl(situationAwareConfig, cloudType, uri);
        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        String res = CloudAwareWazuhHttpClient.getRes(cloudType, requestUrl, params);
        BaseResponse<Port> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        List<Port> ports = result.getData().getAffectedItems();

        return new ListObjServiceResult(new ListObjResult<>(ports, result.getData().getTotalAffectedItems()).asResult()).asResult();

    }


    /**
     * 查询进程
     *
     * @return 进程集合
     */
    public ListObjServiceResult<Process> pageProcesses(CollectorBaseRequest collectorBaseRequest) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName(), null);
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


    /**
     * 查询网卡列表
     *
     * @return 进程集合
     */
    public ListObjServiceResult<Netiface> pageNetifaces(CollectorBaseRequest collectorBaseRequest, Integer cloudType) {
        String agentId = agentService.getSingleAgentId(collectorBaseRequest.getAgentName(), cloudType);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", collectorBaseRequest.getAgentName());
            return new ListObjServiceResult<>(new ListObjResult<>(Collections.emptyList(), 0));
        }
        var uri = "/syscollector/" + agentId + "/netiface";
        String requestUrl = CloudAwareWazuhHttpClient.getRequestUrl(situationAwareConfig, cloudType, uri);

        List<NameValuePair> params = processRequestParams(collectorBaseRequest);

        log.info("requestUrl:{}", requestUrl);
        String res = CloudAwareWazuhHttpClient.getRes(cloudType, requestUrl, params);
        BaseResponse<Netiface> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("{}", result);
        List<Netiface> netifaces = result.getData().getAffectedItems();

        netifaces.forEach(n -> {
            List<NetAddr> netAddrs = listNetAddrs(agentId, n.getName(), "ipv4");
            if (!CollectionUtils.isEmpty(netAddrs)) {
                n.setIp(netAddrs.get(0).getAddress());
            }
        });

        return new ListObjServiceResult(new ListObjResult<>(netifaces, result.getData().getTotalAffectedItems()).asResult()).asResult();
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
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + uri;


        List<NameValuePair> params = processRequestParams(new BaseRequest());
        if (StringUtils.isNotBlank(iface)) {
            params.add(new BasicNameValuePair("iface", URLEncoder.encode(iface, StandardCharsets.UTF_8)));
        }

        if (StringUtils.isNotBlank(proto)) {
            params.add(new BasicNameValuePair("proto", URLEncoder.encode(proto, StandardCharsets.UTF_8)));
        }

        log.info("requestUrl:{}, params:{}", requestUrl, params);

        var res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl, params);
        BaseResponse<NetAddr> result = JSONObject.parseObject(res, new TypeReference<>() {
        });
        log.info("listNetAddrs result: {}", result);
        if (Objects.isNull(result.getData()) || CollectionUtils.isEmpty(result.getData().getAffectedItems())) {
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

        String agentId = agentService.getSingleAgentId(agentName, null);
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


    /**
     * @param
     * @return
     */
    public List<OS> getOS(String agentName, Integer cloudType) {

        String agentId = agentService.getSingleAgentId(agentName, cloudType);
        if (StringUtils.isBlank(agentId)) {
            log.error("agentId is null, agentName:{}", agentName);
            return null;
        }
        String res = "";
        String requestUrl = CloudAwareWazuhHttpClient.getRequestUrl(situationAwareConfig, cloudType, "/syscollector/" + agentId + "/os");
        if (cloudType != null) {
            switch (cloudType) {
                case BaseSceneModel.CloudType.PUBLIC:
                    res = PublicWazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
                    break;
                case BaseSceneModel.CloudType.PRIVATE:
                    res = PrivateWazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
                    break;
                case BaseSceneModel.CloudType.MIX:
                    res = MixWazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
                    break;
                default:
                    res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
                    break;
            }
        } else {
            res = WazuhHttpsUtils.sendGetWithAuthToken(requestUrl);
        }

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


    /**
     * 查询hardware信息
     *
     * @param agentId
     * @return
     * @throws IOException
     */
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
