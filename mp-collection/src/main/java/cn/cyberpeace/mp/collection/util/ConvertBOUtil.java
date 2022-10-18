package cn.cyberpeace.mp.collection.util;

import cn.cyberpeace.mp.collection.bo.event.*;
import cn.cyberpeace.mp.collection.bo.host.*;
import cn.cyberpeace.mp.collection.core.model.es.event.Event;
import cn.cyberpeace.mp.collection.core.model.es.event.EventCount;
import cn.cyberpeace.mp.collection.core.model.es.event.RuleCount;
import cn.cyberpeace.mp.collection.core.model.es.event.WazuhProbeCommon;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhHostState;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhNetworkConnection;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhProcess;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhSystemUser;
import cn.cyberpeace.mp.collection.core.model.es.vulnerability.WazuhVulnerabilityInfo;
import cn.cyberpeace.mp.collection.core.model.es.vulnerability.WazuhVulnerabilityInfoCount;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConvertBOUtil {
    private ConvertBOUtil(){

    }

    public static List<EventBO> convertEventBO(List<Event> eventList) {
        List<EventBO> eventBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(eventList)) {
            return eventBOList;
        }
        eventList.forEach(event -> {
            EventBO eventBO = new EventBO();
            eventBO.setAgentName(event.getAgentName());
            eventBO.setAgentIp(event.getAgentIp());
            eventBO.setDescription(event.getDescription());
            eventBO.setLevel(event.getLevel());
            eventBO.setMessage(event.getMessage());
            eventBO.setRuleId(event.getRuleId());
            eventBO.setTime(event.getUpdateTime());
            eventBOList.add(eventBO);
        });
        return eventBOList;
    }

    public static List<ModifyEventBO> convertModifyEventBO(List<Event> eventList) {
        List<ModifyEventBO> modifyEventBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(eventList)) {
            return modifyEventBOList;
        }
        eventList.forEach(event -> {
            ModifyEventBO modifyEventBO = new ModifyEventBO();
            modifyEventBO.setAgentName(event.getAgentName());
            modifyEventBO.setAgentIp(event.getAgentIp());
            modifyEventBO.setTime(event.getUpdateTime());
            modifyEventBO.setAction(event.getAction());
            modifyEventBO.setMd5Before(event.getMd5Before());
            modifyEventBO.setMd5After(event.getMd5After());
            modifyEventBO.setPath(event.getFilePath());
            modifyEventBO.setSize(event.getSize());
            modifyEventBO.setUser(event.getUser());
            modifyEventBOList.add(modifyEventBO);
        });
        return modifyEventBOList;
    }

    public static List<CustomProbeEventBO> convertCustomProbeEventBO(List<WazuhProbeCommon> wazuhProbeCommonList) {
        List<CustomProbeEventBO> eventBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wazuhProbeCommonList)) {
            return eventBOList;
        }
        wazuhProbeCommonList.forEach(wazuhProbeCommon -> {
            CustomProbeEventBO eventBO = new CustomProbeEventBO();
            eventBO.setAgentName(wazuhProbeCommon.getAgentName());
            eventBO.setAgentIp(wazuhProbeCommon.getAgentIp());
            eventBO.setTime(wazuhProbeCommon.getTime());
            eventBO.setRawOutPut(wazuhProbeCommon.getRawOutPut());
            eventBO.setProbeId(wazuhProbeCommon.getProbeId());
            eventBOList.add(eventBO);
        });
        return eventBOList;
    }

    public static List<VulnerabilityEventBO> convertVulnerabilityEventBO(List<WazuhVulnerabilityInfo> vulnerabilityInfoList) {
        List<VulnerabilityEventBO> eventBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(vulnerabilityInfoList)) {
            return eventBOList;
        }
        vulnerabilityInfoList.forEach(vulnerabilityInfo -> {
            VulnerabilityEventBO eventBO = new VulnerabilityEventBO();
            eventBO.setAgentName(vulnerabilityInfo.getAgentName());
            eventBO.setAgentIp(vulnerabilityInfo.getAgentIp());
            eventBO.setTime(vulnerabilityInfo.getTime());
            if (Objects.nonNull(vulnerabilityInfo.getVulnerability())) {
                eventBO.setCve(vulnerabilityInfo.getVulnerability().getCve());
                eventBO.setTitle(vulnerabilityInfo.getVulnerability().getTitle());
                eventBO.setPublished(vulnerabilityInfo.getVulnerability().getPublished());
                eventBO.setUpdated(vulnerabilityInfo.getVulnerability().getUpdated());
                eventBO.setRationale(vulnerabilityInfo.getVulnerability().getRationale());
                eventBO.setReferences(vulnerabilityInfo.getVulnerability().getReferences());
                eventBO.setSeverity(vulnerabilityInfo.getVulnerability().getSeverity());
            }
            eventBOList.add(eventBO);
        });
        return eventBOList;
    }

    public static List<VulnerabilityCountBO> convertVulnerabilityCountBO(List<WazuhVulnerabilityInfoCount> infoCountList) {
        List<VulnerabilityCountBO> countBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(infoCountList)) {
            return countBOList;
        }
        infoCountList.forEach(infoCount -> {
            VulnerabilityCountBO countBO = new VulnerabilityCountBO();
            countBO.setFieldName(infoCount.getFieldName());
            countBO.setCount(infoCount.getCount());
            countBOList.add(countBO);
        });
        return countBOList;
    }

    public static List<HostStateBO> convertHostStateBO(List<WazuhHostState> wazuhHostStateList) {
        List<HostStateBO> hostStateBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wazuhHostStateList)) {
            return hostStateBOList;
        }
        wazuhHostStateList.forEach(wazuhHostState -> {
            HostStateBO hostStateBO = new HostStateBO();
            hostStateBO.setAgentIp(wazuhHostState.getAgentIp());
            hostStateBO.setAgentName(wazuhHostState.getAgentName());
            hostStateBO.setTime(wazuhHostState.getTime());
            hostStateBO.setCpuCount(wazuhHostState.getCpuCount());
            hostStateBO.setCpuFreq(wazuhHostState.getCpuFreq());
            hostStateBO.setCpuTotalPercent(wazuhHostState.getCpuTotalPercent());
            hostStateBO.setDiskPercent(wazuhHostState.getDiskPercent());
            hostStateBO.setDiskReadBytes(wazuhHostState.getDiskReadBytes());
            hostStateBO.setDiskTotal(wazuhHostState.getDiskTotal());
            hostStateBO.setDiskUnused(wazuhHostState.getDiskUnused());
            hostStateBO.setDiskUsed(wazuhHostState.getDiskUsed());
            hostStateBO.setDiskWriteBytes(wazuhHostState.getDiskWriteBytes());
            hostStateBO.setDownloadSpeed(wazuhHostState.getDownloadSpeed());
            hostStateBO.setMemoryFree(wazuhHostState.getMemoryFree());
            hostStateBO.setMemoryPercent(wazuhHostState.getMemoryPercent());
            hostStateBO.setMemoryTotal(wazuhHostState.getMemoryTotal());
            hostStateBO.setUploadSpeed(wazuhHostState.getUploadSpeed());
            hostStateBOList.add(hostStateBO);
        });
        return hostStateBOList;
    }

    public static List<NetworkConnectionBO> convertNetworkConnectionBO(List<WazuhNetworkConnection> networkConnectionList) {
        List<NetworkConnectionBO> connectionBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(networkConnectionList)) {
            return connectionBOList;
        }
        networkConnectionList.forEach(networkConnection -> {
            NetworkConnectionBO connectionBO = new NetworkConnectionBO();
            connectionBO.setAgentIp(networkConnection.getAgentIp());
            connectionBO.setAgentName(networkConnection.getAgentName());
            connectionBO.setTime(networkConnection.getTime());
            connectionBO.setLocalIp(networkConnection.getLaddrIp());
            connectionBO.setLocalPort(networkConnection.getLaddrPort());
            connectionBO.setPid(networkConnection.getPid());
            connectionBO.setProtocol(networkConnection.getProto());
            connectionBO.setRemoteIp(networkConnection.getRaddrIp());
            connectionBO.setRemotePort(networkConnection.getRaddrPort());
            connectionBO.setStatus(networkConnection.getStatus());
            connectionBOList.add(connectionBO);
        });
        return connectionBOList;
    }

    public static List<ProcessBO> convertProcessBO(List<WazuhProcess> wazuhProcessList) {
        List<ProcessBO> processBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wazuhProcessList)) {
            return processBOList;
        }
        wazuhProcessList.forEach(wazuhProcess -> {
            ProcessBO processBO = new ProcessBO();
            processBO.setAgentIp(wazuhProcess.getAgentIp());
            processBO.setAgentName(wazuhProcess.getAgentName());
            processBO.setTime(wazuhProcess.getTime());
            processBO.setCommand(wazuhProcess.getCommand());
            processBO.setCreateTime(wazuhProcess.getCreateTime());
            processBO.setCwd(wazuhProcess.getCwd());
            processBO.setExe(wazuhProcess.getExe());
            processBO.setName(wazuhProcess.getName());
            processBO.setNumThreads(wazuhProcess.getNumThreads());
            processBO.setParentId(wazuhProcess.getParentId());
            processBO.setParentName(wazuhProcess.getParentName());
            processBO.setPid(wazuhProcess.getPid());
            processBO.setPriority(wazuhProcess.getPriority());
            processBO.setRss(wazuhProcess.getRss());
            processBO.setStatus(wazuhProcess.getStatus());
            processBO.setUsername(wazuhProcess.getUsername());
            processBO.setVms(wazuhProcess.getVms());
            processBOList.add(processBO);
        });
        return processBOList;
    }

    public static List<SystemUserBO> convertSystemUserBO(List<WazuhSystemUser> wazuhSystemUserList) {
        List<SystemUserBO> systemUserBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wazuhSystemUserList)) {
            return systemUserBOList;
        }
        wazuhSystemUserList.forEach(wazuhSystemUser -> {
            SystemUserBO systemUserBO = new SystemUserBO();
            systemUserBO.setAgentIp(wazuhSystemUser.getAgentIp());
            systemUserBO.setAgentName(wazuhSystemUser.getAgentName());
            systemUserBO.setTime(wazuhSystemUser.getTime());
            systemUserBO.setComment(wazuhSystemUser.getComment());
            systemUserBO.setDir(wazuhSystemUser.getDir());
            systemUserBO.setGroupId(wazuhSystemUser.getGroupId());
            systemUserBO.setGroupName(wazuhSystemUser.getGroupName());
            systemUserBO.setId(wazuhSystemUser.getId());
            systemUserBO.setName(wazuhSystemUser.getName());
            systemUserBO.setShell(wazuhSystemUser.getShell());
            systemUserBOList.add(systemUserBO);
        });
        return systemUserBOList;
    }

    public static List<PortBO> convertPortBO(List<Port> portList) {
        List<PortBO> portBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(portList)) {
            return portBOList;
        }
        portList.forEach(port -> {
            PortBO portBO = new PortBO();
            portBO.setScanTime(port.getScan().getTime());
            portBO.setInode(port.getInode());
            portBO.setLocalIp(port.getLocal().getIp());
            portBO.setLocalPort(port.getLocal().getPort());
            portBO.setPid(port.getPid());
            portBO.setProcess(port.getProcess());
            portBO.setProtocol(port.getProtocol());
            portBO.setRemoteIp(port.getRemote().getIp());
            portBO.setRemotePort(port.getRemote().getPort());
            portBO.setRxQueue(port.getRxQueue());
            portBO.setTxQueue(port.getTxQueue());
            portBO.setState(port.getState());
            portBOList.add(portBO);
        });
        return portBOList;
    }

    public static List<NetifaceBO> convertNetifaceBO(List<Netiface> netifaceList) {
        List<NetifaceBO> netifaceBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(netifaceList)) {
            return netifaceBOList;
        }
        netifaceList.forEach(netiface -> {
            NetifaceBO netifaceBO = new NetifaceBO();
            netifaceBO.setIp(netiface.getIp());
            netifaceBO.setMac(netiface.getMac());
            netifaceBO.setMtu(netiface.getMtu());
            netifaceBO.setName(netiface.getName());
            netifaceBO.setReceiveBytes(netiface.getRx().getBytes());
            netifaceBO.setReceiveErrors(netiface.getRx().getErrors());
            netifaceBO.setReceiveLost(netiface.getRx().getDropped());
            netifaceBO.setReceivePackets(netiface.getRx().getPackets());
            netifaceBO.setSendBytes(netiface.getTx().getBytes());
            netifaceBO.setSendErrors(netiface.getTx().getErrors());
            netifaceBO.setSendLost(netiface.getTx().getDropped());
            netifaceBO.setSendPackets(netiface.getTx().getPackets());
            netifaceBO.setState(netiface.getState());
            netifaceBO.setType(netiface.getType());
            netifaceBO.setScanTime(netiface.getScan().getTime());
            netifaceBOList.add(netifaceBO);
        });
        return netifaceBOList;
    }

    public static List<OperatingSystemBO> convertOperatingSystemBO(List<OS> osList) {
        List<OperatingSystemBO> operatingSystemBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(osList)) {
            return operatingSystemBOList;
        }
        osList.forEach(os -> {
            OperatingSystemBO operatingSystemBO = new OperatingSystemBO();
            operatingSystemBO.setArchitecture(os.getArchitecture());
            operatingSystemBO.setHostname(os.getHostname());
            operatingSystemBO.setMajor(os.getOs().getMajor());
            operatingSystemBO.setMinor(os.getOs().getMinor());
            operatingSystemBO.setName(os.getOs().getName());
            operatingSystemBO.setPlatform(os.getOs().getPlatform());
            operatingSystemBO.setRelease(os.getRelease());
            operatingSystemBO.setSystemName(os.getSysname());
            operatingSystemBO.setVersion(os.getVersion());
            operatingSystemBO.setVersionNumber(os.getOs().getVersion());
            operatingSystemBO.setTime(os.getScan().getTime());
            operatingSystemBOList.add(operatingSystemBO);
        });
        return operatingSystemBOList;
    }

    public static List<EventCountBO> convertEventCountBO(List<EventCount> eventCountList) {
        List<EventCountBO> eventCountBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(eventCountList)) {
            return eventCountBOList;
        }
        eventCountList.forEach(eventCount -> {
            EventCountBO eventCountBO = new EventCountBO();
            eventCountBO.setAgentName(eventCount.getAgentName());
            eventCountBO.setEventCount(eventCount.getCount());
            eventCountBOList.add(eventCountBO);
        });
        return eventCountBOList;
    }

    public static List<RuleCountBO> convertRuleCountBO(List<RuleCount> ruleCountList) {
        List<RuleCountBO> ruleCountBOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(ruleCountList)) {
            return ruleCountBOList;
        }
        ruleCountList.forEach(ruleCount -> {
            RuleCountBO eventCountBO = new RuleCountBO();
            eventCountBO.setRuleId(ruleCount.getRuleId());
            eventCountBO.setCount(ruleCount.getCount());
            ruleCountBOList.add(eventCountBO);
        });
        return ruleCountBOList;
    }
}
