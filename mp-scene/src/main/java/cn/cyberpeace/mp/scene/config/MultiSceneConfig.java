package cn.cyberpeace.mp.scene.config;


import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "mp.multi-scene")
public class MultiSceneConfig {

    @Getter
    @Value("${mp.multi-scene.manager-center-flag:false}")
    private boolean managerCenterFlag;

    @Getter
    @Value("${mp.multi-scene.manager-center-ips:10.100.7.1}")
    private String managerCenterIps;

    @Getter
    @Value("${mp.multi-scene.manager-center-ports:8001}")
    private String managerCenterPorts;

    @Getter
    @Value("${mp.multi-scene.heartbeat-interval:60}")
    private long heartbeatInterval;

    @Getter
    @Value("${mp.multi-scene.scan-interval:600}")
    private long scanInterval;

    @Getter
    @Value("${mp.multi-scene.sub-cyber-range-flag}")
    private boolean subCyberRangeFlag;

    @Getter
    @Value("${mp.multi-scene.cyber-range-tag}")
    private String cyberRangeTag;

    @Getter
    @Value("${mp.multi-scene.cyber-range-ip}")
    private String cyberRangeIp;


    @Getter
    @Value("${mp.multi-scene.cyber-range-port}")
    private String cyberRangePort;

    @Getter
    @Value("${mp.multi-scene.vlanIds}")
    private String vlanIds;

    private List<String> manageServers;

    private List<String> manageServersWithoutSelf;

    private List<Integer> vlanIdList;

    public void setManagerCenterFlag(boolean managerCenterFlag) {
        this.managerCenterFlag = managerCenterFlag;
    }

    public void setManagerCenterIps(String managerCenterIps) {
        this.managerCenterIps = managerCenterIps;
    }

    public void setManagerCenterPorts(String managerCenterPorts) {
        this.managerCenterPorts = managerCenterPorts;
    }

    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval;
    }

    public void setSubCyberRangeFlag(boolean subCyberRangeFlag) {
        this.subCyberRangeFlag = subCyberRangeFlag;
    }

    public void setCyberRangeTag(String cyberRangeTag) {
        this.cyberRangeTag = cyberRangeTag;
    }

    public void setCyberRangeIp(String cyberRangeIp) {
        this.cyberRangeIp = cyberRangeIp;
    }

    public void setCyberRangePort(String cyberRangePort) {
        this.cyberRangePort = cyberRangePort;
    }

    public void setVlanIds(String vlanIds) {
        this.vlanIds = vlanIds;
    }

    public void setManageServers(List<String> manageServers) {
        this.manageServers = manageServers;
    }

    public void setManageServersWithoutSelf(List<String> manageServersWithoutSelf) {
        this.manageServersWithoutSelf = manageServersWithoutSelf;
    }

    public void setVlanIdList(List<Integer> vlanIdList) {
        this.vlanIdList = vlanIdList;
    }


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
