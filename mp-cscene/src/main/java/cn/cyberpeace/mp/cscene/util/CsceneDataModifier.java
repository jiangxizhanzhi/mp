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
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
            // 忽略错误
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
     *
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
     *
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
                    if (CollectionUtils.isNotEmpty(e.getPorts())) {
                        e.getPorts().forEach(port -> {
                            if (port != null) {
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    accessModes.forEach(accessMode -> {
                                        accessMode.setPrivateKeyPath(removePrefix(accessMode.getPrivateKeyPath(), baseAppConfig.getFdfsOuterHttpServer()));
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
                                    accessMode.put("private_key_path", removePrefix(accessMode.get("private_key_path").toString(), baseAppConfig.getFdfsOuterHttpServer()));
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
                                    probe.put("script_url", removePrefix(probe.get("script_url").toString(), baseAppConfig.getFdfsOuterHttpServer()));
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private static String removePrefix(String path, String prefix) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        if (path.startsWith(prefix)) {
            path = StringUtils.replaceOnce(path, prefix, StringUtils.EMPTY);
            if (path.startsWith("/")) {
                path = StringUtils.replaceOnce(path, "/", StringUtils.EMPTY);
            }
        }
        return path;
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
        // 补充gns3插件
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
        handlerHiddenElement(csceneInstanceBO.getScene());
        csceneInstanceBO.setEnableRecording(getOutputEnableRecording(csceneInstanceBO.getScene()));
    }

    /**
     * 处理需要隐藏的节点
     *
     * @param sceneDataBO
     */
    public static void handlerHiddenElement(SceneDataBO sceneDataBO) {
        if (sceneDataBO.getTopology() != null && sceneDataBO.getTopology().getElements() != null) {
            List<String> hiddenElements = new ArrayList<>();
            List<String> hiddenPorts = new ArrayList<>();
            for (var element : sceneDataBO.getTopology().getElements()) {
                if (Boolean.TRUE.equals(element.getIsAuxiliary())) {
                    // 辅助节点,节点和端口都要隐藏
                    hiddenElements.add(element.getId());
                    if (CollectionUtils.isNotEmpty(element.getPorts())) {
                        element.getPorts().forEach(p -> hiddenPorts.add(p.getId()));
                    }
                }
            }

            // 隐藏连线
            Set<String> auxiliaryPortIds = new HashSet<>();
            for (var e : sceneDataBO.getTopology().getElements()) {
                List<SceneElementPortBO> auxiliaryPorts = new ArrayList<>();
                if (Boolean.TRUE.equals(e.getIsAuxiliary())) {
                    // 辅助节点,所有端口都是辅助端口
                    if (e.getPorts() != null) {
                        e.getPorts().forEach(p -> auxiliaryPorts.add(p));
                    }
                } else if (e.getPorts() != null) {
                    // 非辅助节点,需要判断字段
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
                    // 辅助节点整个隐藏
                    elementIterator.remove();
                    if (sceneDataBO.getPlugins() == null) {
                        continue;
                    }
                    // 隐藏插件节点
                    sceneDataBO.getPlugins().values().forEach(plugin -> {
                        // 处理节点
                        if (plugin.getElements() != null) {
                            plugin.getElements().remove(element.getId());
                        }
                        // 处理端口
                        if (CollectionUtils.isNotEmpty(element.getPorts())) {
                            element.getPorts().forEach(p -> {
                                if (plugin.getElementPorts() != null) {
                                    plugin.getElementPorts().remove(p.getId());
                                }
                            });
                        }
                    });
                } else if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    // 其他节点隐藏辅助端口
                    var portIterator = element.getPorts().iterator();
                    while (portIterator.hasNext()) {
                        var port = portIterator.next();
                        if (Boolean.TRUE.equals(port.getIsAuxiliary())) {
                            // 辅助端口隐藏
                            portIterator.remove();
                            if (sceneDataBO.getPlugins() == null) {
                                continue;
                            }
                            // 处理插件中的端口
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
            // 兼容name
            if (String.valueOf(csceneInstance.getReferenceType()).startsWith("3")) {
                // 竞赛
                if (StringUtils.isNotEmpty(name)) {
                    csceneInstance.setName(name);
                } else if (cscene != null && cscene.getSceneConfig() != null && cscene.getSceneConfig().getTopology() != null && cscene.getSceneConfig().getTopology().getScene() != null) {
                    csceneInstance.setName(cscene.getSceneConfig().getTopology().getScene().getName());
                } else if (dbCscene != null && dbCscene.getSceneConfig() != null && dbCscene.getSceneConfig().getTopology() != null && dbCscene.getSceneConfig().getTopology().getScene() != null) {
                    csceneInstance.setName(dbCscene.getSceneConfig().getTopology().getScene().getName());
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
     *
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
                    if (CollectionUtils.isNotEmpty(sceneElement.getPorts())) {
                        sceneElement.getPorts().forEach(p -> {
                            if (p != null) {
                                BaseSceneElementPortBO port = (BaseSceneElementPortBO) p;
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    accessModes.forEach(accessMode -> {
                                        String secretPath = addFilePrefix(accessMode.getPrivateKeyPath(), baseAppConfig.getFdfsOuterHttpServer());
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
                                    accessMode.put("private_key_path", addFilePrefix(accessMode.get("private_key_path").toString(), baseAppConfig.getFdfsOuterHttpServer()));
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
                                    accessMode.put("script_url", addFilePrefix(accessMode.get("script_url").toString(), baseAppConfig.getFdfsOuterHttpServer()));
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private static String addFilePrefix(String path, String prefix) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        if (!StringUtils.startsWith(path, prefix)) {
            if (!StringUtils.startsWith(path, "/")) {
                path = "/" + path;
            }
            path = prefix + path;
        }
        return path;
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

    public static void roleChangeAndAuxiliaryRouterOffSet(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                elements.values().forEach(e -> {
                    if (e.get("role") != null && BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH == Integer.parseInt(e.get("role").toString())) {
                        e.put("role", BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY);
                        e.put("auxiliary_router_off", true);
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
                                p.setDelay(0);
                            }
                            if (p.getLoss() == null) {
                                p.setLoss(0);
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
                    // 有软件需要安装的 quick_create_support 设置为true
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
