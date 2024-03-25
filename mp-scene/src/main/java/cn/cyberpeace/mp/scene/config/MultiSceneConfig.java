package cn.cyberpeace.mp.scene.config;


import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "mp.multi-scene")
@Data
public class MultiSceneConfig {

    @Value("${mp.multi-scene.manager-center-flag:false}")
    private boolean managerCenterFlag;

    @Value("${mp.multi-scene.manager-center-ips:10.100.7.1}")
    private String managerCenterIps;

    @Value("${mp.multi-scene.manager-center-ports:8001}")
    private String managerCenterPorts;

    @Value("${mp.multi-scene.heartbeat-interval:60}")
    private long heartbeatInterval;

    @Value("${mp.multi-scene.scan-interval:600}")
    private long scanInterval;

    @Value("${mp.multi-scene.sub-cyber-range-flag}")
    private boolean subCyberRangeFlag;

    @Value("${mp.multi-scene.cyber-range-tag}")
    private String cyberRangeTag;

    @Value("${mp.multi-scene.cyber-range-ip}")
    private String cyberRangeIp;

    @Value("${mp.multi-scene.cyber-range-port}")
    private String cyberRangePort;

    @Value("${mp.multi-scene.vlanIds}")
    private String vlanIds;

    private List<String> manageServers;

    private List<String> manageServersWithoutSelf;

    private List<Integer> vlanIdList;

    public List<Integer> getAllVlanResources() {
        if (CollectionUtil.isNotEmpty(vlanIdList)) {
            return vlanIdList;
        } else {
            if (StringUtils.isNotEmpty(vlanIds)) {
                this.vlanIdList = Arrays.stream(this.vlanIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
                return vlanIdList;
            } else {
                return Collections.emptyList();
            }
        }

    }
    public List<String> getManageCenterServerArray() {
        if (Objects.isNull(manageServers)) {
            String[] ips = getManagerCenterIpArray();
            String[] ports = getManagerCenterPortArray();

            if (ips.length != ports.length) {
                throw new MpRuntimeException("配置的多靶场ip和端口数目不对应");
            } else {
                manageServers = new ArrayList<>();
                for (int i = 0; i < ips.length; i++) {
                    manageServers.add(String.format("%s:%s", ips[i], ports[i]));
                }
                return manageServers;
            }
        } else {
            return manageServers;
        }
    }



    public List<String> getManageServersWithoutSelf() {
        if (Objects.isNull(this.manageServersWithoutSelf)) {
            List<String> allServers = getManageCenterServerArray();
            List<String> serversWithSelf = new ArrayList<>(allServers);
            serversWithSelf.remove(String.format("%s:%s", cyberRangeIp, cyberRangePort));
            this.manageServersWithoutSelf = serversWithSelf;
            return serversWithSelf;
        } else {
            return manageServersWithoutSelf;
        }

    }

    private String[] getManagerCenterIpArray() {
        return getArrayFromStringWithComma(this.getManagerCenterIps());
    }

    private String[] getManagerCenterPortArray() {
        return getArrayFromStringWithComma(this.getManagerCenterPorts());
    }

    private String[] getArrayFromStringWithComma(String inputStr) {
        if (StringUtils.isNotEmpty(inputStr)) {
            return Arrays.stream(inputStr.split(",")).toArray(String[]::new);
        } else {
            return new String[] {};
        }
    }




}
