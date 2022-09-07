package cn.cyberpeace.mp.situationaware.config;

import cn.cyberpeace.mp.situationaware.utils.SituationAwareCommonConfigUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 14:58
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.devops.monitor", ignoreInvalidFields = true)
public class MonitorConfig {
    private String cpCloudInterval;

    private String businessInterval;

    private List<String> cpCloudServerList;

    private List<String> businessServerList;

    private List<String> cpCloudRestartRestrict;

    private List<String> businessRestartRestrict;

    private static final Integer PART_SIZE = 30;

    // 云平台服务监控
    public static List<MonitorProbe> cpCloudSrvMonitors;

    // 业务服务监控
    public static List<MonitorProbe> businessSrvMonitors;

    @PostConstruct
    public void init() {
        var cpCloudMonitor = JSON.parseObject(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.CP_SRV_MONITORS), MonitorProbe.class);
        var businessMonitor = JSON.parseObject(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.BIZ_SRV_MONITORS), MonitorProbe.class);
        if (StringUtils.isNotEmpty(cpCloudInterval)) {
            cpCloudMonitor.setInterval(cpCloudInterval);
        }
        if (StringUtils.isNotEmpty(businessInterval)) {
            businessMonitor.setInterval(businessInterval);
        }

        if (CollectionUtils.isNotEmpty(cpCloudServerList)) {
            cpCloudMonitor.setServiceList(cpCloudServerList);
        }
        if (CollectionUtils.isNotEmpty(cpCloudRestartRestrict)) {
            cpCloudMonitor.setRestartRestrictList(cpCloudRestartRestrict);
        }

        if (CollectionUtils.isNotEmpty(businessServerList)) {
            businessMonitor.setServiceList(businessServerList);
        }
        if (CollectionUtils.isNotEmpty(businessRestartRestrict)) {
            businessMonitor.setRestartRestrictList(businessServerList);
        }

        cpCloudSrvMonitors = new ArrayList<>();
        businessSrvMonitors = new ArrayList<>();
        //  云平台服务每30个截一次
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

        /**
         * 服务列表
         */
        private List<String> serviceList;

        /**
         * 该列表中的服务：正常状态下不允许重启，异常状态下才允许重启
         */
        private List<String> restartRestrictList;
    }

}
