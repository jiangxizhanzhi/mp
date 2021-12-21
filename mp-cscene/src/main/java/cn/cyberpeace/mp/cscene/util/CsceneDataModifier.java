package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
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
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
public class CsceneDataModifier {
    /**
     * 端口中补充协议
     */
    public static void supplyProtocol(CsceneInstanceBO csceneInstance, String protocol) {
        ScenePluginDataBO cloudPlugin = Optional.ofNullable(csceneInstance.getScene().getPlugins()).orElse(new HashMap<>()).get("cpcloud");
        csceneInstance.getScene().getTopology().getElements().get(0).getPorts().get(0).getId();
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
                        BaseCloudPluginSceneElementModel.VirtualType.CONTAINER != intVirtualType &&
                        BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE != intVirtualType) {
                    continue;
                }
                // 当前节点所有端口上指定协议数量不为0则跳出此次
                int proCNum = 0;
                for (SceneElementPortBO portBO : elementBO.getPorts()) {
                    proCNum = proCNum + portBO.getAccessModes().stream().filter(m -> protocol.equals(m.getProtocol())).collect(Collectors.toList()).size();
                }
                if (proCNum > 0) {
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
                });
            }
        }
    }

    public static void correctReturnCsceneData(CsceneBO csceneBO) {
        addFilePathPrefix(csceneBO.getSceneConfig());
    }

    public static void correctReturnCsceneData(CsceneInstanceBO csceneInstanceBO) {
        addFilePathPrefix(csceneInstanceBO.getScene());
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

}
