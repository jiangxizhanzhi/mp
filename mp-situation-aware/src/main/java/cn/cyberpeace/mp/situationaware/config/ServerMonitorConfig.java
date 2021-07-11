package cn.cyberpeace.mp.situationaware.config;

import cn.cyberpeace.mp.situationaware.utils.SituationAwareCommonConfigUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServerMonitorConfig {
    @Resource
    private SituationAwareConfig situationAwareConfig;

    private static final Integer PART_SIZE = 30;

    
    public static List<MonitorProbe> cpCloudSrvMonitors;

    
    public static List<MonitorProbe> businessSrvMonitors;

    @PostConstruct
    public void init() {
        var cpCloudMonitor = JSON.parseObject(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.CP_SRV_MONITORS), MonitorProbe.class);
        var businessMonitor = JSON.parseObject(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.BIZ_SRV_MONITORS), MonitorProbe.class);
        if (StringUtils.isNotEmpty(situationAwareConfig.getMonitorCpCloudInterval())) {
            cpCloudMonitor.setInterval(situationAwareConfig.getMonitorCpCloudInterval());
        }
        if (StringUtils.isNotEmpty(situationAwareConfig.getMonitorBusinessInterval())) {
            businessMonitor.setInterval(situationAwareConfig.getMonitorBusinessInterval());
        }

        if (CollectionUtils.isNotEmpty(situationAwareConfig.getMonitorCpCloudServerList())) {
            cpCloudMonitor.setServiceList(situationAwareConfig.getMonitorCpCloudServerList());
        }
        if (CollectionUtils.isNotEmpty(situationAwareConfig.getMonitorCpCloudRestartRestrict())) {
            cpCloudMonitor.setRestartRestrictList(situationAwareConfig.getMonitorCpCloudRestartRestrict());
        }

        if (CollectionUtils.isNotEmpty(situationAwareConfig.getMonitorBusinessServerList())) {
            businessMonitor.setServiceList(situationAwareConfig.getMonitorBusinessServerList());
        }
        if (CollectionUtils.isNotEmpty(situationAwareConfig.getMonitorBusinessRestartRestrict())) {
            businessMonitor.setRestartRestrictList(situationAwareConfig.getMonitorBusinessRestartRestrict());
        }

        cpCloudSrvMonitors = new ArrayList<>();
        businessSrvMonitors = new ArrayList<>();
        
        int size = cpCloudMonitor.getServiceList().size();
        List<List<String>> cpCloudServers = new ArrayList<>();
        if (size > PART_SIZE) {
            for (int i = 0; (i * PART_SIZE) < size; i++) {
                int startIndex = PART_SIZE * i;
                int endIndex = Math.min(size, PART_SIZE * (i + 1));
                cpCloudServers.add(cpCloudMonitor.getServiceList().subList(startIndex, endIndex));
            }
        } else {
            cpCloudServers.add(cpCloudMonitor.getServiceList());
        }
        int i = 0;
        for (var cpCloudServer : cpCloudServers) {
            i++;
            MonitorProbe cpCloud = new MonitorProbe();
            cpCloud.setInterval(cpCloudMonitor.getInterval());
            cpCloud.setLaunchId(cpCloudMonitor.getLaunchId() + i);
            cpCloud.setProbeName(cpCloudMonitor.getProbeName());
            cpCloud.setServiceList(cpCloudServer);
            cpCloud.setRestartRestrictList(cpCloudMonitor.getRestartRestrictList().stream()
                    .filter(r -> cpCloudServer.contains(r))
                    .collect(Collectors.toList()));
            cpCloudSrvMonitors.add(cpCloud);
        }

        businessSrvMonitors.add(businessMonitor);
    }

    @Data
    public static class MonitorProbe {

        private String probeName;

        private String launchId;

        private String interval;

        
        private List<String> serviceList;

        
        private List<String> restartRestrictList;
    }
}
