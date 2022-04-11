package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneRoleBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigPieceBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author maojun
 * @Date 2021/12/13
 */
@Slf4j
public class CsceneDataModifier {
    /**
     * 端口中补充协议
     */
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
                // 当前节点所有端口上指定协议数量不为0则跳出此次
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
                    // 取端口
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
                        // 无指定协议则补充
                        AccessMode mode = new AccessMode();
                        mode.setProtocol(protocol);
                        Map<String, Object> protocolMap = JsonCommon.beanToSnakeMap(mode);
                        // 避免数据丢失,需要从element中获取原始的access_modes
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
                // 当前节点所有端口上指定协议数量不为0则跳出此次
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
                    // 取端口
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
                        // 无指定协议则补充
                        AccessMode mode = new AccessMode();
                        mode.setProtocol(protocol);
                        Map<String, Object> protocolMap = JsonCommon.beanToSnakeMap(mode);
                        // 避免数据丢失,需要从element中获取原始的access_modes
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

    /**
     * 创建时去除多余的eid
     * @param csceneBO
     */
    public static void removeEidWhenCreate(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            SceneConfigBO sceneConfigBO = csceneBO.getSceneConfig().getTopology().getScene();
            if (sceneConfigBO != null && sceneConfigBO.getEid() == null) {
                // 属于新增 需要去除多余的eid
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

    /**
     * 纠正创建和更新场景模板时fdfs的地址,移除前缀
     * @param csceneBO
     */
    public static void removeFilePathPrefix(CsceneBO csceneBO) {
        BaseAppConfig baseAppConfig = BeanCommon.getBean(BaseAppConfig.class);
        // 纠正fdfs的地址
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            SceneConfigBO sceneConfig = csceneBO.getSceneConfig().getTopology().getScene();
            if (sceneConfig != null && StringUtils.isNotEmpty(sceneConfig.getFile()) && sceneConfig.getFile().startsWith(baseAppConfig.getFdfsOuterHttpServer())) {
                sceneConfig.setFile(StringUtils.replaceOnce(sceneConfig.getFile(), baseAppConfig.getFdfsOuterHttpServer(), StringUtils.EMPTY));
                if (sceneConfig.getFile().startsWith("/")) {
                    sceneConfig.setFile(StringUtils.replaceOnce(sceneConfig.getFile(), "/", StringUtils.EMPTY));
                }
            }
            List<SceneConfigElementBO> elements = csceneBO.getSceneConfig().getTopology().getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                elements.stream().forEach(e -> {
                    if (StringUtils.isNotEmpty(e.getFile()) && e.getFile().startsWith(baseAppConfig.getFdfsOuterHttpServer())) {
                        e.setFile(StringUtils.replaceOnce(e.getFile(), baseAppConfig.getFdfsOuterHttpServer(), StringUtils.EMPTY));
                        if (e.getFile().startsWith("/")) {
                            e.setFile(StringUtils.replaceOnce(e.getFile(), "/", StringUtils.EMPTY));
                        }
                    }
                });
            }
        }
    }

    public static void assembleInitCommands(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            csceneBO.getSceneConfig().getTopology().getElements().stream().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getInitCommands())) {
                    List<String> s = e.getInitCommands().stream().map(c -> {
                        // TODO: 临时兼容业务界面
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
        addFilePathPrefix(csceneInstanceBO.getScene());
        handlerNodeCommand(csceneInstanceBO.getScene());
        csceneInstanceBO.setEnableRecording(getOutputEnableRecording(csceneInstanceBO.getScene()));
    }

    public static void correctName(String csceneId, String referenceMetaData, CsceneInstanceBO csceneInstance, CsceneBO cscene) {
        if (StringUtils.isEmpty(referenceMetaData)) {
            return;
        }
        try {
            // 兼容name
            if (String.valueOf(csceneInstance.getReferenceType()).startsWith("3")) {
                // 竞赛
                if (cscene.getSceneConfig() != null && cscene.getSceneConfig().getTopology() != null && cscene.getSceneConfig().getTopology().getScene() != null) {
                    csceneInstance.setName(cscene.getSceneConfig().getTopology().getScene().getName());
                }
            } else {
                // 靶场和实训
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
                            // 暂时只有一个命令
                            sceneElement.setNodeCommand(nodeCommands.get(0));
                        }
                    }
                });
            }
        }
    }

    /**
     * 纠正返回场景模板时fdfs的地址,增加前缀
     * @param baseSceneDataBO
     */
    private static void addFilePathPrefix(BaseSceneDataBO baseSceneDataBO) {
        BaseAppConfig baseAppConfig = BeanCommon.getBean(BaseAppConfig.class);
        // 纠正fdfs的地址
        if (baseSceneDataBO.getTopology() != null) {
            BaseSceneBO scene = baseSceneDataBO.getTopology().getScene();
            if (scene != null && StringUtils.isNotEmpty(scene.getFile()) && !StringUtils.startsWith(scene.getFile(), baseAppConfig.getFdfsOuterHttpServer())) {
                if (!StringUtils.startsWith(scene.getFile(), "/")) {
                    scene.setFile("/" + scene.getFile());
                }
                scene.setFile(baseAppConfig.getFdfsOuterHttpServer() + scene.getFile());
            }
            if (CollectionUtils.isNotEmpty(baseSceneDataBO.getTopology().getElements())) {
                baseSceneDataBO.getTopology().getElements().forEach(e -> {
                    BaseSceneElementBO sceneElement = (BaseSceneElementBO) e;
                    if (StringUtils.isNotEmpty(sceneElement.getFile()) && !StringUtils.startsWith(sceneElement.getFile(), baseAppConfig.getFdfsOuterHttpServer())) {
                        if (!StringUtils.startsWith(sceneElement.getFile(), "/")) {
                            sceneElement.setFile("/" + sceneElement.getFile());
                        }
                        sceneElement.setFile(baseAppConfig.getFdfsOuterHttpServer() + sceneElement.getFile());
                    }
                });
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

    public static void checkVlanId(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                elements.values().forEach(e -> {
                    if (e.get("vlan_id") == null || e.get("vlan_id").equals(StringUtils.EMPTY)) {
                        e.put("vlan_id", 0);
                    }
                });
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
                        });
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
}
