package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.config.MonitorConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 20:49
 */
@Slf4j
@Service
public class ServiceMonitorDataCollectionService extends CollectionBaseService {

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private MonitorConfig monitorConfig;


    /**
     * 查询平台服务状态列表
     * @param hostname
     * @return
     */
    public List<ServiceMonitorDto>  queryCPServiceMonitorData(String hostname) {
        List<String> launchIds = monitorConfig.getCpSrvMonitors().stream().map(MonitorConfig.MonitorProbe::getLaunchId).collect(Collectors.toList());
        WazuhMonitorEventsRequest wazuhRequest = genDefaultWazuhRequest(hostname);
        return queryServiceMonitorData(wazuhRequest, launchIds);
    }


    /**
     * 查询业务服务状态列表
     * @param hostname
     * @return
     */
    public List<ServiceMonitorDto>  queryBizServiceMonitorData(String hostname) {
        List<String> launchIds = monitorConfig.getBizSrvMonitors().stream().map(MonitorConfig.MonitorProbe::getLaunchId).collect(Collectors.toList());
        WazuhMonitorEventsRequest wazuhRequest = genDefaultWazuhRequest(hostname);
        return queryServiceMonitorData(wazuhRequest, launchIds);
    }

    /**
     * 查询服务监控
     * @param
     * @return
     */
    public List<ServiceMonitorDto> queryServiceMonitorData(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, List<String> launchIds) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }

        //List<MonitorConfig.MonitorProbe> cpMonitorProbes = monitorConfig.getCpSrvMonitors();

        // 只取一条数据，做假分页处理
        List<ServiceMonitorDto> dtos = new ArrayList<>();
        for (String launchId : launchIds) {
            wazuhMonitorEventsRequest.setPageSize(1);
            SearchResponse response = dataCollectionService.querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, launchId);
            JSONObject jsonObject = dataCollectionService.processBaseRawOutput(response);
            if (Objects.isNull(jsonObject)) {
                return Collections.emptyList();
            }

            dtos.addAll(jsonObj2ServiceMonitorDtos(jsonObject, launchId));
        }

        return dtos;
    }



    private List<ServiceMonitorDto> jsonObj2ServiceMonitorDtos(JSONObject jsonObject, String launchId) {
        List<ServiceMonitorDto> dtos = new ArrayList<>();
        if (Objects.nonNull(jsonObject)) {
            for (String name : jsonObject.keySet()) {
                JSONObject subObj = jsonObject.getJSONObject(name);
                String uptime = subObj.getString("uptime_timestamp");
                String ip = subObj.getString("ip");
                String status = subObj.getString("status");
                uptime = StringUtils.substringBefore(uptime, ".");
                Long uptimeSec = 0L;
                if (StringUtils.isNotBlank(uptime)) {
                    uptimeSec = Long.valueOf(uptime);
                }
                ServiceMonitorDto dto = ServiceMonitorDto.builder().name(name).launchId(launchId).uptime(uptimeSec).ip(ip).state(status).build();
                dtos.add(dto);
            }
        }

        return dtos;
    }


    private WazuhMonitorEventsRequest genDefaultWazuhRequest(String hostname) {
        WazuhMonitorEventsRequest wazuhRequest = new WazuhMonitorEventsRequest();
        wazuhRequest.setAgentNames(Arrays.asList(hostname));
        wazuhRequest.setPageSize(1);
        LocalDateTime endTime = LocalDateTime.now();
        wazuhRequest.setStartTime(endTime.minusHours(2));
        wazuhRequest.setEndTime(endTime);
        return wazuhRequest;
    }


}
