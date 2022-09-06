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
import java.util.List;

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
    private String cpInterval;

    private String bizInterval;

    private String cpServerList1;

    private String cpServerList2;

    private String bizServerList;

    private List<String> cpRestartRestrict1;

    private List<String> cpRestartRestrict2;

    private List<String> bizRestartRestrict;

    // 云平台服务监控
    public static List<MonitorProbe> cpSrvMonitors;

    // 业务服务监控
    public static List<MonitorProbe> bizSrvMonitors;

    @PostConstruct
    public void init() {
        cpSrvMonitors = JSON.parseArray(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.CP_SRV_MONITORS), MonitorProbe.class);
        bizSrvMonitors = JSON.parseArray(SituationAwareCommonConfigUtil.getConfigString(SituationAwareCommonConfigUtil.BIZ_SRV_MONITORS), MonitorProbe.class);
        if (StringUtils.isNotEmpty(cpInterval)) {
            cpSrvMonitors.forEach(monitorProbe -> monitorProbe.setInterval(cpInterval));
        }

        if (StringUtils.isNotEmpty(bizInterval)) {
            bizSrvMonitors.forEach(monitorProbe -> monitorProbe.setInterval(bizInterval));
        }

        if (StringUtils.isNotEmpty(cpServerList1)) {
            cpSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("001")) {
                    monitorProbe.setServiceList(cpServerList1);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(cpRestartRestrict1)) {
            cpSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("001")) {
                    monitorProbe.setRestartRestrictList(cpRestartRestrict1);
                }
            });
        }

        if (StringUtils.isNotEmpty(cpServerList2)) {
            cpSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("002")) {
                    monitorProbe.setServiceList(cpServerList2);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(cpRestartRestrict2)) {
            cpSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("002")) {
                    monitorProbe.setRestartRestrictList(cpRestartRestrict2);
                }
            });
        }

        if (StringUtils.isNotEmpty(bizServerList)) {
            bizSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("003")) {
                    monitorProbe.setServiceList(bizServerList);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(bizRestartRestrict)) {
            cpSrvMonitors.forEach(monitorProbe -> {
                if (monitorProbe.getLaunchId().contains("003")) {
                    monitorProbe.setRestartRestrictList(bizRestartRestrict);
                }
            });
        }
    }

    @Data
    public static class MonitorProbe {

        private String probeName;

        private String launchId;

        private String interval;

        /**
         * 服务列表
         */
        private String serviceList;

        /**
         * 该列表中的服务：正常状态下不允许重启，异常状态下才允许重启
         */
        private List<String> restartRestrictList;
    }

}
