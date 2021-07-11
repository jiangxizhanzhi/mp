package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.config.ServerMonitorConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ServiceMonitorDataCollectionService extends CollectionBaseService {

    @Autowired
    private DataCollectionService dataCollectionService;



    
    public List<ServiceMonitorDto> queryCPServiceMonitorData(List<String> hostnames) {
        List<String> launchIds = ServerMonitorConfig.cpCloudSrvMonitors.stream().map(ServerMonitorConfig.MonitorProbe::getLaunchId).collect(Collectors.toList());
        List<ServiceMonitorDto> dtos = new ArrayList<>();
        for (String hostname : hostnames) {
            var result = doQueryServiceMonitorDto(hostname, launchIds) ;
            dtos.addAll(result);
        }
        return dtos;
    }


    
    public List<ServiceMonitorDto> queryBizServiceMonitorData(List<String> hostnames) {
        List<String> launchIds = ServerMonitorConfig.businessSrvMonitors.stream().map(ServerMonitorConfig.MonitorProbe::getLaunchId).collect(Collectors.toList());
        List<ServiceMonitorDto> dtos = new ArrayList<>();
        for (String hostname : hostnames) {
            dtos.addAll(doQueryServiceMonitorDto(hostname, launchIds));
        }
        return dtos;
    }

    
    public List<ServiceMonitorDto> queryServiceMonitorData(WazuhMonitorEventsRequest wazuhMonitorEventsRequest, List<String> launchIds) {
        if (isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return Collections.emptyList();
        }

        List<ServiceMonitorDto> dtos = new ArrayList<>();
        for (String launchId : launchIds) {
            wazuhMonitorEventsRequest.setPageSize(1);
            wazuhMonitorEventsRequest.setStartTime(LocalDateTime.now().minusMinutes(10));
            SearchResponse response = dataCollectionService.querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, launchId);
            JSONObject jsonObject = dataCollectionService.processBaseRawOutput(response);
            if (Objects.isNull(jsonObject)) {
                continue;
            }

            dtos.addAll(jsonObj2ServiceMonitorDtos(jsonObject, launchId));
        }

        return dtos;
    }


    private List<ServiceMonitorDto> doQueryServiceMonitorDto(String hostname, List<String> launchIds) {
        WazuhMonitorEventsRequest wazuhRequest = genDefaultWazuhRequest(hostname);
        List<ServiceMonitorDto> dtos = queryServiceMonitorData(wazuhRequest, launchIds);
        for (ServiceMonitorDto dto : dtos) {
            dto.setHostname(hostname);
        }
        return dtos;
    }



    private List<ServiceMonitorDto> jsonObj2ServiceMonitorDtos(JSONObject jsonObject, String launchId) {
        List<ServiceMonitorDto> dtos = new ArrayList<>();
        if (Objects.nonNull(jsonObject)) {

            LocalDateTime scanTime = null;
            if (Objects.nonNull(jsonObject.get("time"))) {
                scanTime = (LocalDateTime) jsonObject.get("time");
            }

            for (String name : jsonObject.keySet()) {
                if (Objects.equals(name, "time")) {
                    continue;
                }
                JSONObject subObj = jsonObject.getJSONObject(name);
                String ip = subObj.getString("ip");
                String status = subObj.getString("status");

                Long uptimeSec = 0L;
                String uptime = subObj.getString("uptime_timestamp");
                if (StringUtils.isNotBlank(uptime)) {
                    uptime = StringUtils.substringBefore(uptime, ".");
                    if (StringUtils.isNotBlank(uptime)) {
                        uptimeSec = Long.valueOf(uptime);
                    }
                }

                ServiceMonitorDto dto = ServiceMonitorDto.builder()
                        .name(name)
                        .serviceName(name)
                        .launchId(launchId)
                        .uptime(uptimeSec)
                        .ip(ip)
                        .state(status)
                        .scanTime(scanTime)
                        .build();

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
        wazuhRequest.setStartTime(endTime.minusHours(12));
        wazuhRequest.setEndTime(endTime);
        return wazuhRequest;
    }


}
