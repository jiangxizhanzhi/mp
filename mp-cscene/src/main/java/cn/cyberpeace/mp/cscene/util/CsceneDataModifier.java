package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.auth.constant.DefaultImgDirEnum;
import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneRoleBO;
import cn.cyberpeace.mp.cscene.bo.CsceneUpdateBO;
import cn.cyberpeace.mp.cscene.bo.ElementModifyBO;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.scene.core.bo.SceneBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigPieceBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.EntityUsb;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.VirtualUsb;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

    public static final String ENABLE_PROBE_COLLECT = "enable_probe_collect";
    public static final String CIDR = "cidr";

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
                    List<AccessMode> list = new ArrayList<>();
                    for (AccessMode m : portBO.getAccessModes()) {
                        if (protocol.equals(m.getProtocol())) {
                            list.add(m);
                        }
                    }
                    hasPro = list.size() > 0;
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
                    List<AccessMode> list = new ArrayList<>();
                    for (AccessMode accessMode : modes) {
                        if (protocol.equals(accessMode.getProtocol())) {
                            list.add(accessMode);
                        }
                    }
                    boolean hasProtocol = list.size() > 0;
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
            for (SceneConfigElementBO e : csceneBO.getSceneConfig().getTopology().getElements()) {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (SceneConfigElementPortBO p : e.getPorts()) {
                        if (CollectionUtils.isNotEmpty(p.getAccessModes())) {
                            for (AccessMode a : p.getAccessModes()) {
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
            ScenePluginDataBO plugin = csceneBO.getSceneConfig().getPlugins().get(PluginConstant.PLUGIN_KEY);
            if (plugin.getElementPorts() == null) {
                return;
            }
            for (Map<String, Object> portMap : plugin.getElementPorts().values()) {
                if (portMap.get("access_modes") != null) {
                    List<Map> accessModes = (List<Map>) portMap.get("access_modes");
                    for (Map accessMode : accessModes) {
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
                    }
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
                    List<AccessMode> list = new ArrayList<>();
                    for (AccessMode m : portBO.getAccessModes()) {
                        if (protocol.equals(m.getProtocol())) {
                            list.add(m);
                        }
                    }
                    hasPro = list.size() > 0;
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
                    List<AccessMode> list = new ArrayList<>();
                    for (AccessMode accessMode : modes) {
                        if (protocol.equals(accessMode.getProtocol())) {
                            list.add(accessMode);
                        }
                    }
                    boolean hasProtocol = list.size() > 0;
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
                    for (SceneConfigElementBO bo : elementBOS) {
                        bo.setEid(null);
                        if (CollectionUtils.isNotEmpty(bo.getPorts())) {
                            for (SceneConfigElementPortBO port : bo.getPorts()) {
                                port.setEid(null);
                            }
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(connectionBOS)) {
                    for (SceneConfigElementPortConnectionBO conn : connectionBOS) {
                        conn.setEid(null);
                    }
                }
                if (CollectionUtils.isNotEmpty(areaBOS)) {
                    for (SceneConfigAreaBO area : areaBOS) {
                        area.setEid(null);
                    }
                }
                if (CollectionUtils.isNotEmpty(pieceBOS)) {
                    for (SceneConfigPieceBO piece : pieceBOS) {
                        piece.setEid(null);
                    }
                }
                if (CollectionUtils.isNotEmpty(roles)) {
                    for (CsceneRoleBO r : roles) {
                        r.setId(null);
                        if (CollectionUtils.isNotEmpty(r.getChildren())) {
                            for (CsceneRoleBO ch : r.getChildren()) {
                                ch.setId(null);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 纠正创建和更新场景模板时 文件字段 的地址 去除文件字段的前缀，只保留文件对应的key值
     *
     * @param csceneBO
     */
    public static void removeFilePathPrefix(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            SceneConfigBO sceneConfig = csceneBO.getSceneConfig().getTopology().getScene();
            if (sceneConfig != null && StringUtils.isNotEmpty(sceneConfig.getFile())) {
                sceneConfig.setFile(FsCommon.getFileKey(sceneConfig.getFile()));
            }
            List<SceneConfigElementBO> elements = csceneBO.getSceneConfig().getTopology().getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                for (SceneConfigElementBO e : elements) {
                    if (StringUtils.isNotEmpty(e.getFile())) {
                        e.setFile(FsCommon.getFileKey(e.getFile()));
                    }
                    if (StringUtils.isNotEmpty(e.getIcon2())) {
                        e.setIcon2(FsCommon.getFileKey(e.getIcon2()));
                    }
//                    if (StringUtils.isNotEmpty(e.getIcon3())) {
//                        e.setIcon3(FsCommon.getFileKey(e.getIcon3()));
//                    }
                    if (CollectionUtils.isNotEmpty(e.getPorts())) {
                        for (SceneConfigElementPortBO port : e.getPorts()) {
                            if (port != null) {
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    for (AccessMode accessMode : accessModes) {
                                        accessMode.setPrivateKeyPath(FsCommon.getFileKey(accessMode.getPrivateKeyPath()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    for (Map<String, Object> p : scenePluginDataBO.getElementPorts().values()) {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            for (Map accessMode : accessModes) {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.getFileKey(accessMode.get("private_key_path").toString()));
                                }
                            }
                        }
                    }
                }
            }
            if (csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = csceneBO.getSceneConfig().getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    for (Map<String, Object> e : scenePluginDataBO.getElements().values()) {
                        if (e.get("probes") != null) {
                            List<Map> probes = (List<Map>) e.get("probes");
                            for (Map probe : probes) {
                                if (probe.get("script_url") != null) {
                                    probe.put("script_url", FsCommon.getFileKey(probe.get("script_url").toString()));
                                }
                            }
                        }
                    }
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
                for (SceneElementBO e : elements) {
                    if (StringUtils.isNotEmpty(e.getFile())) {
                        e.setFile(FsCommon.getFileKey(e.getFile()));
                    }
                    if (StringUtils.isNotEmpty(e.getIcon2())) {
                        e.setIcon2(FsCommon.getFileKey(e.getIcon2()));
                    }
//                    if (StringUtils.isNotEmpty(e.getIcon3())) {
//                        e.setIcon3(FsCommon.getFileKey(e.getIcon3()));
//                    }
                    if (CollectionUtils.isNotEmpty(e.getPorts())) {
                        for (SceneElementPortBO port : e.getPorts()) {
                            if (port != null) {
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    for (AccessMode accessMode : accessModes) {
                                        accessMode.setPrivateKeyPath(FsCommon.getFileKey(accessMode.getPrivateKeyPath()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (sceneDataBO.getPlugins() != null && sceneDataBO.getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = sceneDataBO.getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    for (Map<String, Object> p : scenePluginDataBO.getElementPorts().values()) {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            for (Map accessMode : accessModes) {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.getFileKey(accessMode.get("private_key_path").toString()));
                                }
                            }
                        }
                    }
                }
            }
            if (sceneDataBO.getPlugins() != null && sceneDataBO.getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = sceneDataBO.getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    for (Map<String, Object> e : scenePluginDataBO.getElements().values()) {
                        if (e.get("probes") != null) {
                            List<Map> probes = (List<Map>) e.get("probes");
                            for (Map probe : probes) {
                                if (probe.get("script_url") != null) {
                                    probe.put("script_url", FsCommon.getFileKey(probe.get("script_url").toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void assembleInitCommands(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null) {
            for (SceneConfigElementBO e : csceneBO.getSceneConfig().getTopology().getElements()) {
                if (CollectionUtils.isNotEmpty(e.getInitCommands())) {
                    List<String> s = new ArrayList<>();
                    for (String c : e.getInitCommands()) {
                        String string = getString(c);
                        s.add(string);
                    }
                    e.setInitCommands(s);
                }
                if (StringUtils.isNotBlank(e.getNodeCommand())) {
                    e.getInitCommands().add("n:" + e.getNodeCommand());
                }
            }
        }
    }

    private static String getString(String c) {
        // TODO: 临时兼容业务界面
        if (c.startsWith("o:") || c.startsWith("s:") || c.startsWith("n:")) {
            return c;
        } else {
            return "s:" + c;
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
        Map<String, Object> cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
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
        Map<String, Object> cpcloudPluginScene = Optional.ofNullable(scenePluginDataBO.getScene())
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
        csceneBO.calHardware();
        csceneBO.setResourceOccupiedNums(NodeLimitUtils.calLimitedNodesNum(csceneBO));
        Set<String> nodeIds = new HashSet<>();
        csceneBO.getSceneConfig().getTopology().getElements().forEach(e -> nodeIds.add(e.getId()));
        var list = csceneBO.getSceneConfig().getTopology().getConnections();
        var it = list.iterator();
        while (it.hasNext()){
            var d = it.next();
            if(!nodeIds.contains(d.getSource()) || !nodeIds.contains(d.getTarget())){
                it.remove();
            }
        }
    }

    public static void correctVlanId(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                for (Map<String, Object> e : elements.values()) {
                    if (e.get("vlan_id") != null && e.get("vlan_id").equals(0)) {
                        e.put("vlan_id", StringUtils.EMPTY);
                    }
                }
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

    /**
     * 处理需要隐藏的节点
     *
     * @param sceneDataBO
     */
    public static void handlerHiddenElement(SceneDataBO sceneDataBO) {
        if (sceneDataBO.getTopology() != null && sceneDataBO.getTopology().getElements() != null) {
            List<String> hiddenElements = new ArrayList<>();
            List<String> hiddenPorts = new ArrayList<>();
            for (SceneElementBO element : sceneDataBO.getTopology().getElements()) {
                if (Boolean.TRUE.equals(element.getIsAuxiliary())) {
                    // 辅助节点,节点和端口都要隐藏
                    hiddenElements.add(element.getId());
                    if (CollectionUtils.isNotEmpty(element.getPorts())) {
                        for (SceneElementPortBO p : element.getPorts()) {
                            hiddenPorts.add(p.getId());
                        }
                    }
                }
            }

            // 隐藏连线
            Set<String> auxiliaryPortIds = new HashSet<>();
            for (SceneElementBO e : sceneDataBO.getTopology().getElements()) {
                List<SceneElementPortBO> auxiliaryPorts = new ArrayList<>();
                if (Boolean.TRUE.equals(e.getIsAuxiliary())) {
                    // 辅助节点,所有端口都是辅助端口
                    if (e.getPorts() != null) {
                        for (SceneElementPortBO p : e.getPorts()) {
                            auxiliaryPorts.add(p);
                        }
                    }
                } else if (e.getPorts() != null) {
                    // 非辅助节点,需要判断字段
                    for (SceneElementPortBO p : e.getPorts()) {
                        if (Boolean.TRUE.equals(p.getIsAuxiliary())) {
                            auxiliaryPorts.add(p);
                        }
                    }
                }
                for (SceneElementPortBO p : auxiliaryPorts) {
                    auxiliaryPortIds.add(p.getId());
                }
            }
            if (!auxiliaryPortIds.isEmpty() && sceneDataBO.getTopology().getConnections() != null) {
                Iterator<SceneElementPortConnectionBO> connectIterator = sceneDataBO.getTopology().getConnections().iterator();
                while (connectIterator.hasNext()) {
                    SceneElementPortConnectionBO c = connectIterator.next();
                    if (auxiliaryPortIds.contains(c.getSource()) || auxiliaryPortIds.contains(c.getTarget())) {
                        connectIterator.remove();
                    }
                }
            }

            Iterator<SceneElementBO> elementIterator = sceneDataBO.getTopology().getElements().iterator();
            while (elementIterator.hasNext()) {
                SceneElementBO element = elementIterator.next();
                if (Boolean.TRUE.equals(element.getIsAuxiliary())) {
                    // 辅助节点整个隐藏
                    elementIterator.remove();
                    if (sceneDataBO.getPlugins() == null) {
                        continue;
                    }
                    // 隐藏插件节点
                    for (ScenePluginDataBO plugin : sceneDataBO.getPlugins().values()) {// 处理节点
                        if (plugin.getElements() != null) {
                            plugin.getElements().remove(element.getId());
                        }
                        // 处理端口
                        if (CollectionUtils.isNotEmpty(element.getPorts())) {
                            for (SceneElementPortBO p : element.getPorts()) {
                                if (plugin.getElementPorts() != null) {
                                    plugin.getElementPorts().remove(p.getId());
                                }
                            }
                        }
                    }
                } else if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    // 其他节点隐藏辅助端口
                    Iterator<SceneElementPortBO> portIterator = element.getPorts().iterator();
                    while (portIterator.hasNext()) {
                        SceneElementPortBO port = portIterator.next();
                        if (Boolean.TRUE.equals(port.getIsAuxiliary())) {
                            // 辅助端口隐藏
                            portIterator.remove();
                            if (sceneDataBO.getPlugins() == null) {
                                continue;
                            }
                            // 处理插件中的端口
                            for (ScenePluginDataBO plugin : sceneDataBO.getPlugins().values()) {
                                if (plugin.getElementPorts() != null) {
                                    plugin.getElementPorts().remove(port.getId());
                                }
                            }
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
                baseSceneDataBO.getTopology().getElements().forEach(e -> {
                    extracted((BaseSceneElementBO) e);
                });
            }
        }
    }

    private static void extracted(BaseSceneElementBO e) {
        BaseSceneElementBO sceneElement = e;
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
    }

    private static void addFilePathPrefix(CsceneBO csceneBO) {
        if (csceneBO != null && StringUtils.isNotEmpty(csceneBO.getLogo())) {
            csceneBO.setLogo(FsCommon.genHttpUrl(csceneBO.getLogo()));
        }
    }

    /**
     * 纠正返回场景模板时文件的地址,增加前缀
     *
     * @param baseSceneDataBO
     */
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
                        String icon2 = DefaultImgUtil.pickPathToFdfsUrl(sceneElement.getIcon2(), mappingModelList);
                        sceneElement.setIcon2(icon2);
                    } else {
                        sceneElement.setIcon2(FsCommon.genHttpUrl(sceneElement.getIcon2()));
                    }
//                    sceneElement.setIcon3(FsCommon.genHttpUrl(sceneElement.getIcon3()));
                    if (CollectionUtils.isNotEmpty(sceneElement.getPorts())) {
                        sceneElement.getPorts().forEach(p -> {
                            if (p != null) {
                                BaseSceneElementPortBO port = (BaseSceneElementPortBO) p;
                                if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                                    List<AccessMode> accessModes = port.getAccessModes();
                                    for (AccessMode accessMode : accessModes) {
                                        String secretPath = FsCommon.genHttpUrl(accessMode.getPrivateKeyPath());
                                        accessMode.setPrivateKeyPath(secretPath);
                                    }
                                }
                            }
                        });
                    }
                });
            }
            if (baseSceneDataBO.getPlugins() != null && baseSceneDataBO.getPlugins().get("cpcloud") != null) {
                ScenePluginDataBO scenePluginDataBO = (ScenePluginDataBO) baseSceneDataBO.getPlugins().get("cpcloud");
                if (scenePluginDataBO.getElementPorts() != null) {
                    for (Map<String, Object> p : scenePluginDataBO.getElementPorts().values()) {
                        if (p.get("access_modes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("access_modes");
                            for (Map accessMode : accessModes) {
                                if (accessMode.get("private_key_path") != null) {
                                    accessMode.put("private_key_path", FsCommon.genHttpUrl(accessMode.get("private_key_path").toString()));
                                }
                            }
                        }
                    }
                }
            }
            if (baseSceneDataBO.getPlugins() != null && baseSceneDataBO.getPlugins().get("cpcollection") != null) {
                ScenePluginDataBO scenePluginDataBO = (ScenePluginDataBO) baseSceneDataBO.getPlugins().get("cpcollection");
                if (scenePluginDataBO.getElements() != null) {
                    for (Map<String, Object> p : scenePluginDataBO.getElements().values()) {
                        if (p.get("probes") != null) {
                            List<Map> accessModes = (List<Map>) p.get("probes");
                            for (Map accessMode : accessModes) {
                                if (accessMode.get("script_url") != null) {
                                    accessMode.put("script_url", FsCommon.genHttpUrl(accessMode.get("script_url").toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void checkFlowNum(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpflow") != null) {
            Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpflow").getElements();
            if (elements != null && elements.size() > 0) {
                for (Map<String, Object> e : elements.values()) {
                    if (e.containsKey("flows")) {
                        Object flows = e.get("flows");
                        if (JsonCommon.stringify(flows).getBytes(StandardCharsets.UTF_8).length > 65535) {
                            throw new MpRuntimeException(CsceneErrorCode.PARAMS_FLOWS_DATA_TOO_LONG);
                        }
                    }
                }
            }
        }
    }

    public static void checkSameCidrForCpcollection(CsceneBO csceneBO) {
        SceneConfigDataBO sceneConfigDataBO = csceneBO.getSceneConfig();
        if (sceneConfigDataBO != null && sceneConfigDataBO.getPlugins() != null && sceneConfigDataBO.getPlugins().get(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY) != null) {
            ScenePluginDataBO collctionPluginDataBO = csceneBO.getSceneConfig().getPlugins().get(cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant.PLUGIN_KEY);
            Map<String, Object> sceneSwitchMap = collctionPluginDataBO.getScene();
            boolean checkCidr = sceneSwitchMap.get(ENABLE_PROBE_COLLECT) != null && Boolean.TRUE.toString().equals(sceneSwitchMap.get(ENABLE_PROBE_COLLECT).toString());
            if(!checkCidr){
                Map<String, Map<String, Object>> elements = collctionPluginDataBO.getElements();
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
            //检查cpcloud下是否有相同cidr
            ScenePluginDataBO cloudPluginDataBO = csceneBO.getSceneConfig().getPlugins().get(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
            Map<String, Map<String, Object>> elements = cloudPluginDataBO.getElements();
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
            Map<String, Map<String, Object>> ports = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElementPorts();
            if (ports != null && ports.size() > 0) {
                for (Map<String, Object> p : ports.values()) {
                    if (p.containsKey("firewall_rules")) {
                        Object flows = p.get("firewall_rules");
                        if (flows != null && flows instanceof List && ((List) flows).size() > 99) {
                            throw new MpRuntimeException(CsceneErrorCode.PARAMS_FIRE_RULES_OUT_OF_LIMIT);
                        }
                    }
                }
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
        Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
        if (elements != null && elements.size() > 0) {
            // 校验虚拟usb 规则: 单个节点usb不能超过4个 , 单个场景的虚拟usb不能重复
            Set<String> existsUsbs = new HashSet<>();
            for (Map.Entry<String, Map<String, Object>> entry : elements.entrySet()) {
                CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
                if (CollectionUtils.isNotEmpty(elementBO.getVirtualUsbs())) {
                    int maxUsbNums = 4;
                    if (elementBO.getVirtualUsbs().size() > maxUsbNums) {
                        String name = "";
                        for (SceneConfigElementBO srcElement : csceneBO.getSceneConfig().getTopology().getElements()) {
                            if (srcElement.getId().equals(entry.getKey())) {
                                name = srcElement.getName();
                            }
                        }
                        throw new MpRuntimeException(CsceneErrorCode.VIRTUAL_USB_OUT_OF_RANGE.format(name, String.valueOf(maxUsbNums)));
                    }
                    for (VirtualUsb usb : elementBO.getVirtualUsbs()) {
                        if (existsUsbs.contains(usb.getUuid())) {
                            throw new MpRuntimeException(CsceneErrorCode.SAME_USB_HAS_USED.format(usb.getName()));
                        } else {
                            existsUsbs.add(usb.getUuid());
                        }
                    }
                }
            }
        }
    }

    public static void checkEntityUsb(CsceneBO csceneBO) {
        Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
        if (MapUtils.isEmpty(elements)) {
            return;
        }
        //所有节点上usb集合
        Set<String> existsUsbs = new HashSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : elements.entrySet()) {
            CloudPluginSceneConfigElementBO elementBO = JsonCommon.snakeMapToBean(entry.getValue(), CloudPluginSceneConfigElementBO.class);
            if (CollectionUtils.isEmpty(elementBO.getEntityUsbs())) {
                continue;
            }
            //1，同一个实体USB不能在一个场景中出现两次；2，同一个节点上挂的USB的host要一样
            Set<String> hosts = new HashSet<>();
            for (EntityUsb usb : elementBO.getEntityUsbs()) {
                if (existsUsbs.contains(usb.getUuid())) {
                    throw new MpRuntimeException(CsceneErrorCode.ENTITY_USB_HAS_USD.format(usb.getName()));
                } else {
                    existsUsbs.add(usb.getUuid());
                    hosts.add(usb.getHost());
                }
            }
            if (hosts.size() > 1) {
                throw new MpRuntimeException(CsceneErrorCode.ENTITY_USB_HOST_NOT_SAME);
            }
        }
    }

    public static void checkVlanId(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && elements.size() > 0) {
                for (Map<String, Object> e : elements.values()) {
                    if (e.get("vlan_id") == null || e.get("vlan_id").equals(StringUtils.EMPTY)) {
                        e.put("vlan_id", 0);
                    }
                    Object vlanId = e.get("vlan_id");
                    if (Integer.parseInt(vlanId.toString()) > 0) {
                        e.put("vlan", true);
                    } else {
                        e.put("vlan", false);
                    }
                }
            }
        }
    }

    public static void check(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getTopology() != null && csceneBO.getSceneConfig().getTopology().getScene() != null) {
            String name = csceneBO.getSceneConfig().getTopology().getScene().getName();
            String description = csceneBO.getSceneConfig().getTopology().getScene().getDescription();

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
            List<SceneConfigElementBO> elements = csceneBO.getSceneConfig().getTopology().getElements();
            if (elements != null && elements.size() > 0) {
                for (SceneConfigElementBO e : elements) {
                    if (e.getPorts() != null && e.getPorts().size() > 0) {
                        for (SceneConfigElementPortBO p : e.getPorts()) {
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
                        }
                    }
                }
            }
        }
    }

    public static void correctQuickCreateSupport(CsceneBO csceneBO) {
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            ScenePluginDataBO elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud");
            if (elements.getElements() != null) {
                for (Map<String, Object> e : elements.getElements().values()) {// 有软件需要安装的 quick_create_support 设置为true
                    if (e.get("quick_create_params") != null) {
                        Collection list = (Collection) e.get("quick_create_params");
                        if (list.size() > 0) {
                            e.put("quick_create_support", true);
                        }
                    }
                }
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
