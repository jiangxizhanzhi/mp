package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.bo.base.*;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class CsceneDataModifier {

    public static final String ENABLE_PROBE_COLLECT = "enable_probe_collect";
    public static final String CIDR = "cidr";

    
    public static void supplyProtocol(CsceneInstanceBO csceneInstance, String protocol) {
        ScenePluginDataBO cloudPlugin = Optional.ofNullable(csceneInstance.getScene().getPlugins()).orElse(new HashMap<>()).get("cpcloud");
        if (cloudPlugin == null) {
            return;
        }
        Map<String, Map<String, Object>> cloudElements = cloudPlugin.getElements();
        if (cloudElements != null) {
            for (SceneElementBO elementBO : csceneInstance.getScene().getTopology().getElements()) {
                if (CollectionUtils.isEmpty(elementBO.getPorts())) {
                    continue;
                }
                Map<String, Object> cloudElement = cloudElements.get(elementBO.getId());
                if (cloudElement == null) {
                    continue;
                }
                Object virtualType = cloudElement.get("virtual_type");
                if (virtualType == null) {
                    continue;
                }
                int intVirtualType = Integer.parseInt(virtualType.toString());
                if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE != intVirtualType &&
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER != intVirtualType) {
                    continue;
                }
                
                boolean hasPro = false;
                for (SceneElementPortBO portBO : elementBO.getPorts()) {
                    hasPro = portBO.getAccessModes().stream().filter(m -> protocol.equals(m.getProtocol())).collect(Collectors.toList()).size() > 0;
                    if (hasPro) {
                        break;
                    }
                }
                if (hasPro) {
                    continue;
                }
                for (SceneElementPortBO portBO : elementBO.getPorts()) {
                    
                    if (cloudPlugin.getElementPorts() == null) {
                        continue;
                    }
                    Map<String, Object> portMap = cloudPlugin.getElementPorts().get(portBO.getId());
                    if (portMap == null) {
                        continue;
                    }
                    CpcloudPortData cpcloudPortData = JsonCommon.snakeMapToBean(portMap, CpcloudPortData.class);
                    List<AccessMode> modes = Optional.ofNullable(cpcloudPortData.getAccessModes()).orElse(new ArrayList<>());
                    boolean hasProtocol = modes.stream().filter(mode -> protocol.equals(mode.getProtocol())).collect(Collectors.toList()).size() > 0;
                    if (!hasProtocol) {
                        
                        AccessMode mode = new AccessMode();
                        mode.setProtocol(protocol);
                        Map<String, Object> protocolMap = JsonCommon.beanToSnakeMap(mode);
                        
                        Object originModes = portMap.getOrDefault("access_modes", new LinkedList<>());
                        List originModeList = (List) originModes;
                        originModeList.add(protocolMap);
                        portMap.put("access_modes", originModes);
                        portBO.getAccessModes().add(mode);
                        break;
                    }
                }
            }
        }
    }

    public static void handleAccessModePath(CsceneBO csceneBO) {
        try {
            if (csceneBO == null || csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getTopology() == null) {
                return;
            }
            if (CollectionUtils.isEmpty(csceneBO.getSceneConfig().getTopology().getElements())) {
                return;
            }
            for (var e : csceneBO.getSceneConfig().getTopology().getElements()) {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (var p : e.getPorts()) {
                        if (CollectionUtils.isNotEmpty(p.getAccessModes())) {
                            for (var a : p.getAccessModes()) {
                                if (AccessMode.Protocol.HTTP.equals(a.getProtocol()) || AccessMode.Protocol.HTTPS.equals(a.getProtocol())) {
                                    if (StringUtils.isEmpty(a.getPath()) && StringUtils.isNotEmpty(a.getDesc()) && a.getDesc().startsWith("/")) {
                                        a.setPath(a.getDesc());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (csceneBO.getSceneConfig().getPlugins() == null || !csceneBO.getSceneConfig().getPlugins().containsKey(PluginConstant.PLUGIN_KEY)) {
                return;
            }
            var plugin = csceneBO.getSceneConfig().getPlugins().get(PluginConstant.PLUGIN_KEY);
            if (plugin.getElementPorts() == null) {
                return;
            }
            for (var portMap : plugin.getElementPorts().values()) {
                if (portMap.get("access_modes") != null) {
                    List<Map> accessModes = (List<Map>) portMap.get("access_modes");
                    accessModes.forEach(accessMode -> {
                        if (accessMode.containsKey("protocol")) {
                            String protocol = (String) accessMode.get("protocol");
                            if (AccessMode.Protocol.HTTP.equals(protocol) || AccessMode.Protocol.HTTPS.equals(protocol)) {
                                if (accessMode.get("desc") != null) {
                                    String desc = (String) accessMode.get("desc");
                                    if (accessMode.get("path") == null || StringUtils.isEmpty(accessMode.get("path").toString())) {
                                        if (StringUtils.isNotEmpty(desc) && desc.startsWith("/")) {
                                            accessMode.put("path", desc);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            
        }
    }

    public static void supplyProtocolForCoreRouter(CsceneInstanceBO csceneInstance, String protocol) {
        ScenePluginDataBO cloudPlugin = Optional.ofNullable(csceneInstance.getScene().getPlugins()).orElse(new HashMap<>()).get("cpcloud");
        if (cloudPlugin == null) {
            return;
        }
        Map<String, Map<String, Object>> cloudElements = cloudPlugin.getElements();
        if (cloudElements != null) {
            for (SceneElementBO elementBO : csceneInstance.getScene().getTopology().getElements()) {
                if (CollectionUtils.isEmpty(elementBO.getPorts())) {
                    continue;
                }
                Map<String, Object> cloudElement = cloudElements.get(elementBO.getId());
                if (cloudElement == null) {
                    continue;
                }
                Object role = cloudElement.get("role");
                Object imageType = cloudElement.get("image_type");
                if (role == null || imageType == null) {
                    continue;
                }
                if (Integer.parseInt(role.toString()) != BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER
                        || !imageType.equals(BaseCloudPluginSceneElementModel.ImageType.VM)) {
                    continue;
                }
                Object virtualType = cloudElement.get("virtual_type");
                if (virtualType == null) {
                    continue;
                }
                int intVirtualType = Integer.parseInt(virtualType.toString());
                if (BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE != intVirtualType &&
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER != intVirtualType) {
                    continue;
                }
                
                boolean hasPro = false;
                for (SceneElementPortBO portBO : elementBO.getPorts()) {
                    hasPro = portBO.getAccessModes().stream().filter(m -> protocol.equals(m.getProtocol())).collect(Collectors.toList()).size() > 0;
                    if (hasPro) {
                        break;
                    }
                }
                if (hasPro) {
                    continue;
                }
                for (SceneElementPortBO portBO : elementBO.getPorts()) {
                    
                    if (cloudPlugin.getElementPorts() == null) {
                        continue;
                    }
                    Map<String, Object> portMap = cloudPlugin.getElementPorts().get(portBO.getId());
                    if (portMap == null) {
                        continue;
                    }
                    CpcloudPortData cpcloudPortData = JsonCommon.snakeMapToBean(portMap, CpcloudPortData.class);
                    List<AccessMode> modes = Optional.ofNullable(cpcloudPortData.getAccessModes()).orElse(new ArrayList<>());
                    boolean hasProtocol = modes.stream().filter(mode -> protocol.equals(mode.getProtocol())).collect(Collectors.toList()).size() > 0;
                    if (!hasProtocol) {
                        
                        AccessMode mode = new AccessMode();
                        mode.setProtocol(protocol);
                        Map<String, Object> protocolMap = JsonCommon.beanToSnakeMap(mode);
                        
                        Object originModes = portMap.getOrDefault("access_modes", new LinkedList<>());
                        List originModeList = (List) originModes;
                        originModeList.add(protocolMap);
                        portMap.put("access_modes", originModes);
                        portBO.getAccessModes().add(mode);
                        break;
                    }
                }
            }
        }
    }

    
    public static void removeEidWhenCreate(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            SceneConfigBO sceneConfigBO = csceneBO.getSceneConfig().getTopology().getScene();
            if (sceneConfigBO != null && sceneConfigBO.getEid() == null) {
                
                List<SceneConfigElementBO> elementBOS = csceneBO.getSceneConfig().getTopology().getElements();
                List<SceneConfigElementPortConnectionBO> connectionBOS = csceneBO.getSceneConfig().getTopology().getConnections();
                List<SceneConfigAreaBO> areaBOS = csceneBO.getSceneConfig().getTopology().getAreas();
                List<SceneConfigPieceBO> pieceBOS = csceneBO.getSceneConfig().getTopology().getPieces();
                List<CsceneRoleBO> roles = csceneBO.getRoles();
                if (CollectionUtils.isNotEmpty(elementBOS)) {
                    elementBOS.forEach(bo -> {
                        bo.setEid(null);
                        if (CollectionUtils.isNotEmpty(bo.getPorts())) {
                            bo.getPorts().forEach(port -> port.setEid(null));
                        }
                    });
                }
                if (CollectionUtils.isNotEmpty(connectionBOS)) {
                    connectionBOS.forEach(conn -> conn.setEid(null));
                }
                if (CollectionUtils.isNotEmpty(areaBOS)) {
                    areaBOS.forEach(area -> area.setEid(null));
                }
                if (CollectionUtils.isNotEmpty(pieceBOS)) {
                    pieceBOS.forEach(piece -> piece.setEid(null));
                }
                if (CollectionUtils.isNotEmpty(roles)) {
                    roles.forEach(r -> {
                        r.setId(null);
                        if (CollectionUtils.isNotEmpty(r.getChildren())) {
                            r.getChildren().forEach(ch -> {
                                ch.setId(null);
                            });
                        }
                    });
                }
            }
        }
    }

    
    public static void removeFilePathPrefix(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            SceneConfigBO sceneConfig = csceneBO.getSceneConfig().getTopology().getScene();
            if (sceneConfig != null && StringUtils.isNotEmpty(sceneConfig.getFile())) {
                sceneConfig.setFile(FsCommon.getFileKey(sceneConfig.getFile()));
            }
            List<SceneConfigElementBO> elements = csceneBO.getSceneConfig().getTopology().getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                elements.stream().forEach(e -> {
                    if (StringUtils.isNotEmpty(e.getFile())) {
                        e.setFile(FsCommon.getFileKey(e.getFile()));
                    }
                    if (StringUtils.isNotEmpty(e.getIcon2())) {
                        e.setIcon2(FsCommon.getFileKey(e.getIcon2()));
                    }



                    if (CollectionUtils.isNotEmpty(e.getPorts())) {
                        e.getPorts().forEach(port -> {
                            if (port != null) {
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    accessModes.forEach(accessMode -> {
                                        accessMode.setPrivateKeyPath(FsCommon.getFileKey(accessMode.getPrivateKeyPath()));
                                    });
                                }
                            }
                        });
                    }
                });
            }
            if (csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    scenePluginDataBO.getElementPorts().values().forEach(p -> {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            accessModes.forEach(accessMode -> {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.getFileKey(accessMode.get("private_key_path").toString()));
                                }
                            });
                        }
                    });
                }
            }
            if (csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    scenePluginDataBO.getElements().values().forEach(e -> {
                        if (e.get("probes") != null) {
                            List<Map> probes = (List<Map>) e.get("probes");
                            probes.forEach(probe -> {
                                if (probe.get("script_url") != null) {
                                    probe.put("script_url", FsCommon.getFileKey(probe.get("script_url").toString()));
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public static void removeFilePathPrefix(CsceneUpdateBO csceneBO) {
        if (csceneBO != null) {
            removeFilePathPrefix(csceneBO.getUpdateScene());
        }
    }

    public static void removeFilePathPrefix(ElementModifyBO elementModifyBO) {
        if (elementModifyBO != null) {
            removeFilePathPrefix(elementModifyBO.getUpdate());
        }
    }

    private static void removeFilePathPrefix(SceneDataBO sceneDataBO) {
        if (sceneDataBO != null && sceneDataBO.getTopology() != null) {
            SceneBO sceneConfig = sceneDataBO.getTopology().getScene();
            if (sceneConfig != null && StringUtils.isNotEmpty(sceneConfig.getFile())) {
                sceneConfig.setFile(FsCommon.getFileKey(sceneConfig.getFile()));
            }
            List<SceneElementBO> elements = sceneDataBO.getTopology().getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                elements.stream().forEach(e -> {
                    if (StringUtils.isNotEmpty(e.getFile())) {
                        e.setFile(FsCommon.getFileKey(e.getFile()));
                    }
                    if (StringUtils.isNotEmpty(e.getIcon2())) {
                        e.setIcon2(FsCommon.getFileKey(e.getIcon2()));
                    }



                    if (CollectionUtils.isNotEmpty(e.getPorts())) {
                        e.getPorts().forEach(port -> {
                            if (port != null) {
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    accessModes.forEach(accessMode -> {
                                        accessMode.setPrivateKeyPath(FsCommon.getFileKey(accessMode.getPrivateKeyPath()));
                                    });
                                }
                            }
                        });
                    }
                });
            }
            if (sceneDataBO.getPlugins() != null && sceneDataBO.getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = sceneDataBO.getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    scenePluginDataBO.getElementPorts().values().forEach(p -> {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            accessModes.forEach(accessMode -> {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.getFileKey(accessMode.get("private_key_path").toString()));
                                }
                            });
                        }
                    });
                }
            }
            if (sceneDataBO.getPlugins() != null && sceneDataBO.getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = sceneDataBO.getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    scenePluginDataBO.getElements().values().forEach(e -> {
                        if (e.get("probes") != null) {
                            List<Map> probes = (List<Map>) e.get("probes");
                            probes.forEach(probe -> {
                                if (probe.get("script_url") != null) {
                                    probe.put("script_url", FsCommon.getFileKey(probe.get("script_url").toString()));
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public static void assembleInitCommands(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            csceneBO.getSceneConfig().getTopology().getElements().stream().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getInitCommands())) {
                    List<String> s = e.getInitCommands().stream().map(c -> {
                        
                        if (c.startsWith("o:") || c.startsWith("s:") || c.startsWith("n:")) {
                            return c;
                        } else {
                            return "s:" + c;
                        }
                    }).collect(Collectors.toList());
                    e.setInitCommands(s);
                }
                if (StringUtils.isNotBlank(e.getNodeCommand())) {
                    e.getInitCommands().add("n:" + e.getNodeCommand());
                }
            });
        }
    }

    public static void handlerGns3Router(CsceneBO csceneBO) {
        if (csceneBO == null || csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getPlugins() == null || csceneBO.getSceneConfig().getPlugins().get("cpcloud") == null) {
            return;
        }
        if (csceneBO.getSceneConfig().getTopology().getElements() == null) {
            return;
        }
        ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
        if (scenePluginDataBO.getElements() == null) {
            return;
        }
        List<String> gns3Routers = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : scenePluginDataBO.getElements().entrySet()) {
            var nodeId = entry.getKey();
            var cloudElement = entry.getValue();
            if (cloudElement.containsKey("image_uuid") &&
                    cloudElement.get("image_uuid").toString().equals(getGns3RouterImageUuid())
                    || "854f7c24-060f-4dbd-b771-68eab207a072".equals(cloudElement.get("image_uuid"))
                    || "854f7c24-060f-4dbd-b771-68eab207a073".equals(cloudElement.get("image_uuid"))) {
                gns3Routers.add(nodeId);
            }
        }
        if (gns3Routers.size() == 0) {
            return;
        }
        
        ScenePluginDataBO gns3Plugin = new ScenePluginDataBO();
        gns3Plugin.setScene(new HashMap<>());
        gns3Plugin.setElements(new HashMap<>());
        for (String gns3Router : gns3Routers) {
            var cloudElement = scenePluginDataBO.getElements().get(gns3Router);
            var gns3ImageInfo = getGns3ImageInfos().get(cloudElement.get("image_uuid"));
            if (gns3ImageInfo != null) {
                Map<String, Object> content = new HashMap<>();
                content.put("static_routes", cloudElement.get("static_routes"));
                content.put("image", gns3ImageInfo.getImage());
                content.put("platform", gns3ImageInfo.getPlatform());
                content.put("ram", gns3ImageInfo.getRam());
                gns3Plugin.getElements().put(gns3Router, content);
            }
        }
        csceneBO.getSceneConfig().getPlugins().put("cpgns3", gns3Plugin);
    }

    public static void handlerInputEnableRecording(CsceneBO csceneBO) {
        if (csceneBO == null || csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getPlugins() == null || csceneBO.getSceneConfig().getPlugins().get("cpcloud") == null) {
            return;
        }
        CsceneAppConfig csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        boolean enableRecording = Optional
                .ofNullable(csceneBO.getEnableRecording())
                .orElse(csceneAppConfig.getSceneEnableRecording());
        ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
        var cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
                .orElse(new HashMap<>());
        cpcloudPluginScene.put("enable_recording", enableRecording);
        scenePluginDataBO.setScene(cpcloudPluginScene);
    }

    public static void handlerEnableRate(CsceneBO csceneBO) {
        if (csceneBO == null || csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getPlugins() == null || csceneBO.getSceneConfig().getPlugins().get("cpcloud") == null) {
            return;
        }
        boolean enableRate = csceneBO.getReferenceType() == null ? false : (BaseCsceneModel.ReferenceType.READ_VS_BLUE == csceneBO.getReferenceType()
                || BaseCsceneModel.ReferenceType.TEAM_TRAINING == csceneBO.getReferenceType()
                || BaseCsceneModel.ReferenceType.RANGE_TEST == csceneBO.getReferenceType());
        ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
        var cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
                .orElse(new HashMap<>());
        cpcloudPluginScene.put("enable_rate", enableRate);
        scenePluginDataBO.setScene(cpcloudPluginScene);
    }

    public static boolean getOutputEnableRecording(BaseSceneDataBO baseSceneDataBO) {
        CsceneAppConfig csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        if (baseSceneDataBO == null || baseSceneDataBO.getPlugins() == null || baseSceneDataBO.getPlugins().get("cpcloud") == null) {
            return csceneAppConfig.getSceneEnableRecording();
        }
        ScenePluginDataBO scenePluginDataBO = (ScenePluginDataBO) baseSceneDataBO.getPlugins().get("cpcloud");
        if (scenePluginDataBO.getScene() == null) {
            return csceneAppConfig.getSceneEnableRecording();
        }
        Object enableRecording = scenePluginDataBO.getScene().get("enable_recording");
        if (enableRecording != null) {
            return (boolean) enableRecording;
        } else {
            return csceneAppConfig.getSceneEnableRecording();
        }
    }

    public static void correctReturnCsceneData(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        addFilePathPrefix(csceneBO);
        addFilePathPrefix(csceneBO.getSceneConfig());
        handlerNodeCommand(csceneBO.getSceneConfig());
        correctVlanId(csceneBO);
        csceneBO.setEnableRecording(getOutputEnableRecording(csceneBO.getSceneConfig()));
    }

    public static void correctVlanId(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                elements.values().forEach(e -> {
                    if (e.get("vlan_id") != null && e.get("vlan_id").equals(0)) {
                        e.put("vlan_id", StringUtils.EMPTY);
                    }
                });
            }
        }
    }

    public static void correctReturnCsceneData(CsceneInstanceBO csceneInstanceBO) {
        correctReturnCsceneData(csceneInstanceBO, true);
    }

    public static void correctReturnCsceneData(CsceneInstanceBO csceneInstanceBO, boolean handlerHidden) {
        addFilePathPrefix(csceneInstanceBO.getScene());
        handlerNodeCommand(csceneInstanceBO.getScene());
        if (handlerHidden) {
            handlerHiddenElement(csceneInstanceBO.getScene());
        }
        csceneInstanceBO.setEnableRecording(getOutputEnableRecording(csceneInstanceBO.getScene()));
    }

    
    public static void handlerHiddenElement(SceneDataBO sceneDataBO) {
        if (sceneDataBO.getTopology() != null && sceneDataBO.getTopology().getElements() != null) {
            List<String> hiddenElements = new ArrayList<>();
            List<String> hiddenPorts = new ArrayList<>();
            for (var element : sceneDataBO.getTopology().getElements()) {
                if (Boolean.TRUE.equals(element.getIsAuxiliary())) {
                    
                    hiddenElements.add(element.getId());
                    if (CollectionUtils.isNotEmpty(element.getPorts())) {
                        element.getPorts().forEach(p -> hiddenPorts.add(p.getId()));
                    }
                }
            }

            
            Set<String> auxiliaryPortIds = new HashSet<>();
            for (var e : sceneDataBO.getTopology().getElements()) {
                List<SceneElementPortBO> auxiliaryPorts = new ArrayList<>();
                if (Boolean.TRUE.equals(e.getIsAuxiliary())) {
                    
                    if (e.getPorts() != null) {
                        e.getPorts().forEach(p -> auxiliaryPorts.add(p));
                    }
                } else if (e.getPorts() != null) {
                    
                    e.getPorts().forEach(p -> {
                        if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                            auxiliaryPorts.add(p);
                        }
                    });
                }
                auxiliaryPorts.forEach(p -> auxiliaryPortIds.add(p.getId()));
            }
            if (!auxiliaryPortIds.isEmpty() && sceneDataBO.getTopology().getConnections() != null) {
                var connectIterator = sceneDataBO.getTopology().getConnections().iterator();
                while (connectIterator.hasNext()) {
                    var c = connectIterator.next();
                    if (auxiliaryPortIds.contains(c.getSource()) || auxiliaryPortIds.contains(c.getTarget())) {
                        connectIterator.remove();
                    }
                }
            }

            var elementIterator = sceneDataBO.getTopology().getElements().iterator();
            while (elementIterator.hasNext()) {
                var element = elementIterator.next();
                if (Boolean.TRUE.equals(element.getIsAuxiliary())) {
                    
                    elementIterator.remove();
                    if (sceneDataBO.getPlugins() == null) {
                        continue;
                    }
                    
                    sceneDataBO.getPlugins().values().forEach(plugin -> {
                        
                        if (plugin.getElements() != null) {
                            plugin.getElements().remove(element.getId());
                        }
                        
                        if (CollectionUtils.isNotEmpty(element.getPorts())) {
                            element.getPorts().forEach(p -> {
                                if (plugin.getElementPorts() != null) {
                                    plugin.getElementPorts().remove(p.getId());
                                }
                            });
                        }
                    });
                } else if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    
                    var portIterator = element.getPorts().iterator();
                    while (portIterator.hasNext()) {
                        var port = portIterator.next();
                        if (Boolean.TRUE.equals(port.getIsAuxiliary())) {
                            
                            portIterator.remove();
                            if (sceneDataBO.getPlugins() == null) {
                                continue;
                            }
                            
                            sceneDataBO.getPlugins().values().forEach(plugin -> {
                                if (plugin.getElementPorts() != null) {
                                    plugin.getElementPorts().remove(port.getId());
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public static void correctName(String csceneId, String referenceMetaData, CsceneInstanceBO csceneInstance, CsceneBO cscene, CsceneBO dbCscene, String name) {
        if (csceneInstance == null) {
            return;
        }
        try {
            
            if (String.valueOf(csceneInstance.getReferenceType()).startsWith("3")) {
                
                if (StringUtils.isNotEmpty(name)) {
                    csceneInstance.setName(name);
                } else if (cscene != null && cscene.getSceneConfig() != null && cscene.getSceneConfig().getTopology() != null && cscene.getSceneConfig().getTopology().getScene() != null) {
                    csceneInstance.setName(cscene.getSceneConfig().getTopology().getScene().getName());
                } else if (dbCscene != null && dbCscene.getSceneConfig() != null && dbCscene.getSceneConfig().getTopology() != null && dbCscene.getSceneConfig().getTopology().getScene() != null) {
                    csceneInstance.setName(dbCscene.getSceneConfig().getTopology().getScene().getName());
                }
            } else {
                
                CsceneDataModifier.NameParseResult result = JsonCommon.snakeParse(referenceMetaData, CsceneDataModifier.NameParseResult.class);
                if (result.getRoomName() != null) {
                    csceneInstance.setName(result.getRoomName());
                    csceneInstance.getScene().getTopology().getScene().setName(result.getRoomName());
                } else if (result.getSceneName() != null) {
                    csceneInstance.setName(result.getSceneName());
                    csceneInstance.getScene().getTopology().getScene().setName(result.getSceneName());
                } else if (result.getBusinessName() != null) {
                    csceneInstance.setName(result.getBusinessName());
                    csceneInstance.getScene().getTopology().getScene().setName(result.getBusinessName());
                }
            }
        } catch (Exception e) {
            log.error("parse name error,csceneId is {} , reference data is {},", csceneId, referenceMetaData, e);
        }
    }

    private static void handlerNodeCommand(BaseSceneDataBO baseSceneDataBO) {
        if (baseSceneDataBO.getTopology() != null) {
            if (CollectionUtils.isNotEmpty(baseSceneDataBO.getTopology().getElements())) {
                baseSceneDataBO.getTopology().getElements().stream().forEach(e -> {
                    BaseSceneElementBO sceneElement = (BaseSceneElementBO) e;
                    if (CollectionUtils.isNotEmpty(sceneElement.getInitCommands())) {
                        List<String> nodeCommands = new ArrayList<>();
                        List<String> initCommands = new ArrayList<>();
                        sceneElement.getInitCommands().forEach(c -> {
                            if (c != null) {
                                if (c.toString().startsWith("n:")) {
                                    nodeCommands.add(StringUtils.replaceOnce(c.toString(), "n:", ""));
                                } else if (c.toString().startsWith("s:")) {
                                    initCommands.add(StringUtils.replaceOnce(c.toString(), "s:", ""));
                                } else {
                                    initCommands.add(c.toString());
                                }
                            }
                        });
                        sceneElement.setInitCommands(initCommands);
                        if (CollectionUtils.isNotEmpty(nodeCommands)) {
                            
                            sceneElement.setNodeCommand(nodeCommands.get(0));
                        }
                    }
                });
            }
        }
    }

    private static void addFilePathPrefix(CsceneBO csceneBO) {
        if (csceneBO != null && StringUtils.isNotEmpty(csceneBO.getLogo())) {
            csceneBO.setLogo(FsCommon.genHttpUrl(csceneBO.getLogo()));
        }
    }

    
    private static void addFilePathPrefix(BaseSceneDataBO baseSceneDataBO) {
        if (baseSceneDataBO.getTopology() != null) {
            BaseSceneBO scene = baseSceneDataBO.getTopology().getScene();
            if (scene != null && StringUtils.isNotEmpty(scene.getFile())) {
                scene.setFile(FsCommon.genHttpUrl(scene.getFile()));
            }
            if (CollectionUtils.isNotEmpty(baseSceneDataBO.getTopology().getElements())) {
                baseSceneDataBO.getTopology().getElements().forEach(e -> {
                    BaseSceneElementBO sceneElement = (BaseSceneElementBO) e;
                    sceneElement.setFile(FsCommon.genHttpUrl(sceneElement.getFile()));
                    if (StringUtils.isNotEmpty(sceneElement.getIcon2()) && sceneElement.getIcon2().startsWith("/api/mp") && !sceneElement.getIcon2().startsWith("/api/mp/file")) {
                        List<LocalFileMappingModel> mappingModelList = DefaultImgUtil.listByGroups(Collections.singletonList(DefaultImgDirEnum.TARGET.getGroup()));
                        var icon2 = DefaultImgUtil.pickPathToFdfsUrl(sceneElement.getIcon2(), mappingModelList);
                        sceneElement.setIcon2(icon2);
                    } else {
                        sceneElement.setIcon2(FsCommon.genHttpUrl(sceneElement.getIcon2()));
                    }

                    if (CollectionUtils.isNotEmpty(sceneElement.getPorts())) {
                        sceneElement.getPorts().forEach(p -> {
                            if (p != null) {
                                BaseSceneElementPortBO port = (BaseSceneElementPortBO) p;
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    accessModes.forEach(accessMode -> {
                                        String secretPath = FsCommon.genHttpUrl(accessMode.getPrivateKeyPath());
                                        accessMode.setPrivateKeyPath(secretPath);
                                    });
                                }
                            }
                        });
                    }
                });
            }
            if (baseSceneDataBO.getPlugins() != null && baseSceneDataBO.getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = (ScenePluginDataBO) baseSceneDataBO.getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    scenePluginDataBO.getElementPorts().values().forEach(p -> {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            accessModes.forEach(accessMode -> {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.genHttpUrl(accessMode.get("private_key_path").toString()));
                                }
                            });
                        }
                    });
                }
            }
            if (baseSceneDataBO.getPlugins() != null && baseSceneDataBO.getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = (ScenePluginDataBO) baseSceneDataBO.getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    scenePluginDataBO.getElements().values().forEach(p -> {
                        if (p.get("probes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("probes");
                            accessModes.forEach(accessMode -> {
                                if (accessMode.get("script_url") != null) {
                                    accessMode.put("script_url", FsCommon.genHttpUrl(accessMode.get("script_url").toString()));
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public static void checkFlowNum(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpflow") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpflow").getElements();
            if (elements != null && elements.size() > 0) {
                elements.values().forEach(e -> {
                    if (e.containsKey("flows")) {
                        Object flows = e.get("flows");
                        if (JsonCommon.stringify(flows).getBytes(StandardCharsets.UTF_8).length > 65535) {
                            throw new MpRuntimeException(CsceneErrorCode.PARAMS_FLOWS_DATA_TOO_LONG);
                        }
                    }
                });
            }
        }
    }

    public static void checkSameCidrForCpcollection(CsceneBO csceneBO) {
        SceneConfigDataBO sceneConfigDataBO = csceneBO.getSceneConfig();
        if (sceneConfigDataBO != null && sceneConfigDataBO.getPlugins() != null && sceneConfigDataBO.getPlugins().get(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY) != null) {
            ScenePluginDataBO collctionPluginDataBO = csceneBO.getSceneConfig().getPlugins().get(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY);
            var sceneSwitchMap = collctionPluginDataBO.getScene();
            boolean checkCidr = sceneSwitchMap.get(ENABLE_PROBE_COLLECT) != null && Boolean.TRUE.toString().equals(sceneSwitchMap.get(ENABLE_PROBE_COLLECT).toString());
            if(!checkCidr){
                var elements = collctionPluginDataBO.getElements();
                if (elements != null && !elements.isEmpty()) {
                    for(Map<String, Object> map : elements.values()){
                        if (map.get(ENABLE_PROBE_COLLECT) != null && Boolean.TRUE.toString().equals(map.get(ENABLE_PROBE_COLLECT).toString())) {
                            checkCidr = true;
                            break;
                        }
                    }
                }
            }
            if(!checkCidr){
                return;
            }
            
            ScenePluginDataBO cloudPluginDataBO = csceneBO.getSceneConfig().getPlugins().get(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            var elements = cloudPluginDataBO.getElements();
            Set<String> cidrSet = new HashSet();
            if (elements != null && !elements.isEmpty()) {
                for(Map<String, Object> map : elements.values()){
                    if (!ObjectUtils.isEmpty(map.get(CIDR))) {
                       String cidr = String.valueOf(map.get(CIDR));
                       if(cidrSet.contains(cidr)){
                           MpRuntimeException ex = new MpRuntimeException(CsceneErrorCode.PARAMS_SAME_CIDR_FOR_COLLECTION);
                           ex.setDetail("conflict cidr: ".concat(map.get(CIDR).toString()));
                           throw ex;
                       }
                       cidrSet.add(cidr);
                    }
                }
            }
        }
    }


    public static void checkFirewallRules(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var ports = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElementPorts();
            if (ports != null && ports.size() > 0) {
                ports.values().forEach(p -> {
                    if (p.containsKey("firewall_rules")) {
                        Object flows = p.get("firewall_rules");
                        if (flows != null && flows instanceof List && ((List) flows).size() > 99) {
                            throw new MpRuntimeException(CsceneErrorCode.PARAMS_FIRE_RULES_OUT_OF_LIMIT);
                        }
                    }
                });
            }
        }
    }

    public static void checkCloudElement(CsceneBO csceneBO) {
        if (csceneBO == null || csceneBO.getSceneConfig() == null || csceneBO.getSceneConfig().getPlugins() == null || csceneBO.getSceneConfig().getPlugins().get("cpcloud") == null) {
            return;
        }
        checkVirtualUsb(csceneBO);
        checkEntityUsb(csceneBO);
    }

    public static void checkVirtualUsb(CsceneBO csceneBO) {
        var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
        if (elements != null && elements.size() > 0) {
            
            Set<String> existsUsbs = new HashSet<>();
            for (var entry : elements.entrySet()) {
                CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
                if (CollectionUtils.isNotEmpty(elementBO.getVirtualUsbs())) {
                    int maxUsbNums = 4;
                    if (elementBO.getVirtualUsbs().size() > maxUsbNums) {
                        String name = "";
                        for (var srcElement : csceneBO.getSceneConfig().getTopology().getElements()) {
                            if (srcElement.getId().equals(entry.getKey())) {
                                name = srcElement.getName();
                            }
                        }
                        throw new MpRuntimeException(CsceneErrorCode.VIRTUAL_USB_OUT_OF_RANGE.format(name, String.valueOf(maxUsbNums)));
                    }
                    elementBO.getVirtualUsbs().forEach(usb -> {
                        if (existsUsbs.contains(usb.getUuid())) {
                            throw new MpRuntimeException(CsceneErrorCode.SAME_USB_HAS_USED.format(usb.getName()));
                        } else {
                            existsUsbs.add(usb.getUuid());
                        }
                    });
                }
            }
        }
    }

    public static void checkEntityUsb(CsceneBO csceneBO) {
        var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
        if (MapUtils.isEmpty(elements)) {
            return;
        }
        
        Set<String> existsUsbs = new HashSet<>();
        for (var entry : elements.entrySet()) {
            CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
            if (CollectionUtils.isEmpty(elementBO.getEntityUsbs())) {
                continue;
            }
            
            Set<String> hosts = new HashSet<>();
            elementBO.getEntityUsbs().forEach(usb -> {
                if (existsUsbs.contains(usb.getUuid())) {
                    throw new MpRuntimeException(CsceneErrorCode.ENTITY_USB_HAS_USD.format(usb.getName()));
                } else {
                    existsUsbs.add(usb.getUuid());
                    hosts.add(usb.getHost());
                }
            });
            if (hosts.size() > 1) {
                throw new MpRuntimeException(CsceneErrorCode.ENTITY_USB_HOST_NOT_SAME);
            }
        }
    }

    public static void checkVlanId(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                elements.values().forEach(e -> {
                    if (e.get("vlan_id") == null || e.get("vlan_id").equals(StringUtils.EMPTY)) {
                        e.put("vlan_id", 0);
                    }
                    Object vlanId = e.get("vlan_id");
                    if (Integer.parseInt(vlanId.toString()) > 0) {
                        e.put("vlan", true);
                    } else {
                        e.put("vlan", false);
                    }
                });
            }
        }
    }

    public static void check(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null && csceneBO.getSceneConfig().getTopology().getScene() != null) {
            var name = csceneBO.getSceneConfig().getTopology().getScene().getName();
            var description = csceneBO.getSceneConfig().getTopology().getScene().getDescription();

            if (name != null && name.length() > 255) {
                throw new MpRuntimeException(CsceneErrorCode.PARAMS_NAME_TOO_LONG);
            }

            if (description != null && description.length() > 2048) {
                throw new MpRuntimeException(CsceneErrorCode.PARAMS_DESCRIPTION_TOO_LONG);
            }
        }
    }

    public static void checkIngressAndEgress(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null && csceneBO.getSceneConfig().getTopology().getElements() != null) {
            var elements = csceneBO.getSceneConfig().getTopology().getElements();
            if (elements != null && elements.size() > 0) {
                elements.forEach(e -> {
                    if (e.getPorts() != null && e.getPorts().size() > 0) {
                        e.getPorts().forEach(p -> {
                            if (p.getIngress() == null) {
                                p.setIngress(-1);
                            }
                            if (p.getEgress() == null) {
                                p.setEgress(-1);
                            }
                            if (p.getDelay() == null) {
                                p.setDelay(-1);
                            }
                            if (p.getLoss() == null) {
                                p.setLoss(-1);
                            }
                        });
                    }
                });
            }
        }
    }

    public static void correctQuickCreateSupport(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
            if (elements.getElements() != null) {
                elements.getElements().values().forEach(e -> {
                    
                    if (e.get("quick_create_params") != null) {
                        Collection list = (Collection) e.get("quick_create_params");
                        if (list.size() > 0) {
                            e.put("quick_create_support", true);
                        }
                    }
                });
            }
        }
    }

    static private class CpcloudPortData {
        private List<AccessMode> accessModes;

        public List<AccessMode> getAccessModes() {
            return accessModes;
        }

        public void setAccessModes(List<AccessMode> accessModes) {
            this.accessModes = accessModes;
        }
    }

    private static class NameParseResult {
        private String sceneName;
        private String roomName;
        private String businessName;

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }
    }

    public static void removeUnUsePlugin(CsceneBO csceneBO) {
        if (csceneBO != null && csceneBO.getSceneConfig() != null && csceneBO.getReferenceType() != null) {
            if (csceneBO.getReferenceType() != BaseCsceneModel.ReferenceType.TEAM_TRAINING &&
                    csceneBO.getReferenceType() != BaseCsceneModel.ReferenceType.READ_VS_BLUE) {
                if (csceneBO.getSceneConfig().getPlugins() != null) {
                    csceneBO.getSceneConfig().getPlugins().remove("cpflow");
                    csceneBO.getSceneConfig().getPlugins().remove("cpcollection");
                }
            }
        }
    }

    private static Map<String, Gns3ImageInfo> getGns3ImageInfos() {
        Map<String, Gns3ImageInfo> result = new HashMap<>();
        Gns3ImageInfo c2691Router = new Gns3ImageInfo();
        c2691Router.setImage("c2691-ik9s-mz.123-22.image");
        c2691Router.setPlatform("c2691");
        c2691Router.setRam(198);
        result.put(getGns3RouterImageUuid(), c2691Router);

        Gns3ImageInfo c3725Router = new Gns3ImageInfo();
        c3725Router.setImage("c3725-adventerprisek9-mz.124-7.bin");
        c3725Router.setPlatform("c3725");
        c3725Router.setRam(198);
        result.put("854f7c24-060f-4dbd-b771-68eab207a072", c3725Router);

        Gns3ImageInfo c2600Router = new Gns3ImageInfo();
        c2600Router.setImage("c2600-ik9s-mz.123-19.bin");
        c2600Router.setPlatform("c2600");
        c2600Router.setRam(198);
        result.put("854f7c24-060f-4dbd-b771-68eab207a073", c2600Router);

        return result;
    }

    private static String getGns3RouterImageUuid() {
        return Gns3Singleton.gns3AppConfig.getGns3ImageUuids().get(0);
    }

    @Getter
    @Setter
    private static class Gns3ImageInfo {
        private String image;
        private String platform;
        private Integer ram;
    }
}
