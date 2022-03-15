package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2022/2/23
 */
public class BatchSceneHandler {
    private List<String> sceneIds;

    private List<String> nodeIds;

    private LocalDateTime earliestStartTime;

    private List<String> flowCollectors = new ArrayList<>();

    private List<String> probeCollectors = new ArrayList<>();

    private Set<String> flowCollectEnabledMacAddrs = new HashSet<>();

    private Set<String> flowCollectEnabledIpAddrs = new HashSet<>();

    private Map<String, SceneElementBO> allMac2ElementMap = new HashMap<>();

    private Map<String, SceneElementBO> allIp2ElementMap = new HashMap<>();

    private Map<String, SceneElementBO> allEid2ElementMap = new HashMap<>();

    private Map<String, List<String>> nodeId2Ips = new HashMap<>();

    private Map<String, String> allMac2SceneIdMap = new HashMap<>();

    private Map<String, String> allIp2SceneIdMap = new HashMap<>();

    private Map<String, String> probeEnabledNodeId2EidMap = new HashMap<>();

    private Map<String, String> nodeId2NodenameMap = new HashMap<>();

    public BatchSceneHandler(List<String> sceneIds, List<String> nodeIds) {
        this.sceneIds = sceneIds;
        this.nodeIds = nodeIds;
        this.init();
    }

    public LocalDateTime getEarliestStartTime() {
        return earliestStartTime;
    }

    public List<String> getFlowCollectors() {
        return flowCollectors;
    }

    public List<String> getProbeCollectors() {
        return probeCollectors;
    }

    public SceneElementBO getNodeByMacAddr(String macAddr) {
        return this.allMac2ElementMap.get(macAddr);
    }

    public Map<String, SceneElementBO> getAllMac2ElementMap() {
        return allMac2ElementMap;
    }

    public Map<String, SceneElementBO> getAllIp2ElementMap() {
        return allIp2ElementMap;
    }

    public Map<String, String> getAllIp2SceneIdMap() {
        return allIp2SceneIdMap;
    }

    public Map<String, String> getAllMac2SceneIdMap() {
        return allMac2SceneIdMap;
    }

    public void setAllMac2SceneIdMap(Map<String, String> allMac2SceneIdMap) {
        this.allMac2SceneIdMap = allMac2SceneIdMap;
    }

    public SceneElementBO getNodeByIpAddr(String ip) {
        return this.allIp2ElementMap.get(ip);
    }

    public String getSceneIdByIpAddr(String ip) {
        return this.allIp2SceneIdMap.get(ip);
    }

    public List<String> getAllMacAddrs() {
        return new ArrayList<>(this.allMac2ElementMap.keySet());
    }

    public List<String> getAllIpAddrs() {
        return new ArrayList<>(this.allIp2ElementMap.keySet());
    }

    public List<String> getFlowCollectedMacAddrs() {
        return new ArrayList<>(this.flowCollectEnabledMacAddrs);
    }

    public List<String> getFlowCollectedIpAddrs() {
        return new ArrayList<>(this.flowCollectEnabledIpAddrs);
    }

    public Map<String, String> getProbeEnabledNodeId2EidMap() {
        return probeEnabledNodeId2EidMap;
    }

    public Map<String, String> getNodeId2NodenameMap() {
        return nodeId2NodenameMap;
    }

    public SceneElementBO getNodeByEid(String eid) {
        return this.allEid2ElementMap.get(eid);
    }

    public List<String> getAllIps() {
        return new ArrayList<>(this.allIp2ElementMap.keySet());
    }

    public List<String> getIpsByNodeId(String nodeId) {
        return this.nodeId2Ips.get(nodeId);
    }

    private void init() {
        for (String sceneId : sceneIds) {
            SceneHandler sceneHandler = new SceneHandler(sceneId, null, false, null);
            if (earliestStartTime == null || sceneHandler.getScene().getCreateTime().isBefore(earliestStartTime)) {
                earliestStartTime = sceneHandler.getScene().getCreateTime();
            }
            CollectionScenePluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
            if (pluginHandler != null && pluginHandler.getScene() != null) {
                if (StringUtils.isNotEmpty(pluginHandler.getScene().getFlowCollector())) {
                    this.flowCollectors.add(pluginHandler.getScene().getFlowCollector());
                }
                if (StringUtils.isNotEmpty(pluginHandler.getScene().getProbeCollector())) {
                    this.probeCollectors.add(pluginHandler.getScene().getProbeCollector());
                }
            }
            List<SceneElementBO> srcElements = getSrcElements(sceneHandler, nodeIds);
            this.handlerElement(sceneId, srcElements, pluginHandler);
        }
    }

    private void handlerElement(String sceneId, List<SceneElementBO> srcElements, CollectionScenePluginHandler pluginHandler) {
        srcElements.forEach(srcElement -> {
            this.allEid2ElementMap.put(srcElement.getEid(), srcElement);
            var srcPorts = srcElement.getPorts();
            if (srcPorts == null) {
                return;
            }
            if (pluginHandler != null && pluginHandler.getElementMap() != null && pluginHandler.getElementMap().get(srcElement.getId()) != null
                    && pluginHandler.getElementMap().get(srcElement.getId()).getEnableProbeCollect() != null &&
                    pluginHandler.getElementMap().get(srcElement.getId()).getEnableProbeCollect()) {
                this.probeEnabledNodeId2EidMap.put(srcElement.getId(), srcElement.getEid());
            }
            this.nodeId2NodenameMap.put(srcElement.getId(), srcElement.getName());
            srcPorts.forEach(srcPort -> {
                String macAddr = srcPort.getMacAddr();
                String ipAddr = srcPort.getIp();
                boolean flowCollectedEnabled = false;
                if (pluginHandler != null && pluginHandler.getPort(srcPort.getId()) != null && Boolean.TRUE.equals(pluginHandler.getPort(srcPort.getId()).getEnableFlowCollect())) {
                    flowCollectedEnabled = true;
                }
                if (StringUtils.isNotEmpty(macAddr)) {
                    this.allMac2SceneIdMap.put(macAddr, sceneId);
                    this.allMac2ElementMap.put(macAddr, srcElement);
                    if (flowCollectedEnabled) {
                        this.flowCollectEnabledMacAddrs.add(macAddr);
                    }
                }
                if (StringUtils.isNotEmpty(ipAddr)) {
                    this.allIp2SceneIdMap.put(ipAddr, sceneId);
                    this.allIp2ElementMap.put(ipAddr, srcElement);
                    List<String> ips = this.nodeId2Ips.getOrDefault(ipAddr, new ArrayList<>());
                    ips.add(ipAddr);
                    this.nodeId2Ips.put(srcElement.getId(), ips);
                    if (flowCollectedEnabled) {
                        this.flowCollectEnabledIpAddrs.add(ipAddr);
                    }
                }
            });
        });
    }

    private List<SceneElementBO> getSrcElements(SceneHandler sceneHandler, List<String> nodeIds) {
        List<SceneElementBO> srcElements;
        if (nodeIds == null || nodeIds.isEmpty()) {
            srcElements = new ArrayList<>(sceneHandler.getElementMap().values());
        } else {
            srcElements = new ArrayList<>();
            nodeIds.forEach(nodeId -> {
                var srcElement = sceneHandler.getElement(nodeId);
                if (srcElement != null) {
                    srcElements.add(srcElement);
                }
            });
        }
        return srcElements;
    }
}
