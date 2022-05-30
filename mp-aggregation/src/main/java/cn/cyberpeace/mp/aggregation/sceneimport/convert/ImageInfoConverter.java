package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageInfoParseResult;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 写入images目录中解析出来的镜像信息
 * @Author maojun
 * @Date 2021/10/12
 */
class ImageInfoConverter implements IConverter {

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(container.getNetworkElements())) {
            this.doConvert(container.getNetworkElements(), container);
        }
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
            this.doConvert(container.getNodeElements(), container);
        }
    }

    private void doConvert(List<SceneConfigElementBO> list, ConvertDataContainer container) {
        Map<String, CloudPluginSceneConfigElementBO> pluginElements = container.getCloudPluginElements();
        Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts = container.getCloudPluginPorts();
        Map<String, ImageInfoParseResult> imageInfos = container.getImageInfos();

        for (SceneConfigElementBO element : list) {
            CloudPluginSceneConfigElementBO cloudPluginElement = pluginElements.get(element.getId());
            if (cloudPluginElement == null || cloudPluginElement.getImage() == null) {
                continue;
            }
            ImageInfoParseResult imageInfo = imageInfos.get(cloudPluginElement.getImage());
            if (imageInfo == null) {
                // 无镜像信息 增加role init_support proxy的默认值
                if (cloudPluginElement.getRole() == null) {
                    cloudPluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
                }
                if (cloudPluginElement.getVirtualType() == null) {
                    cloudPluginElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
                }
                if (cloudPluginElement.getInitSupport() == null) {
                    cloudPluginElement.setInitSupport(true);
                }
                if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    element.getPorts().forEach(port -> {
                        if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                            port.getAccessModes().forEach(mode -> {
                                if (mode.getProxy() == null) {
                                    mode.setProxy(true);
                                }
                            });
                        }
                        CloudPluginSceneConfigElementPortBO pluginPort = pluginPorts.get(port.getId());
                        if (pluginPort != null && CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                            pluginPort.getAccessModes().forEach(mode -> {
                                if (mode.getProxy() == null) {
                                    mode.setProxy(true);
                                }
                            });
                        }
                    });
                }
            } else {
                // 有镜像信息 增加role init_support proxy的默认值,还要读取其他值
                if (CollectionUtils.isNotEmpty(imageInfo.getInitCommands())) {
                    if (element.getInitCommands() == null) {
                        element.setInitCommands(imageInfo.getInitCommands());
                    } else {
                        element.getInitCommands().addAll(imageInfo.getInitCommands());
                    }
                }
                if (cloudPluginElement.getVirtualType() == null) {
                    cloudPluginElement.setVirtualType(imageInfo.getVirtualType());
                }
                if (imageInfo.getRole() != null) {
                    cloudPluginElement.setRole(imageInfo.getRole());
                }
                if (cloudPluginElement.getRole() == null) {
                    cloudPluginElement.setRole(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL);
                }
                if (cloudPluginElement.getImageType() == null && imageInfo.getImageType() != null) {
                    cloudPluginElement.setImageType(imageInfo.getImageType());
                }
                if (CollectionUtils.isEmpty(element.getPorts())) {
                    SceneConfigElementPortBO portBO = new SceneConfigElementPortBO();
                    portBO.setId(IConverter.getPortId(element.getId(), element.getType(), element.getPorts().size()));
                    element.setPorts(new ArrayList<>());
                    element.getPorts().add(portBO);
                    CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
                    container.putPluginElementPort(portBO.getId(), pluginPort);
                }
                SceneConfigElementPortBO portBO = element.getPorts().get(0);
                portBO.setAccessModes(this.getAccessModes(imageInfo.getAccessModes()));
                portBO.getAccessModes().forEach(mode -> {
                    if (mode.getProxy() == null) {
                        mode.setProxy(true);
                    }
                });
                CloudPluginSceneConfigElementPortBO pluginPort = pluginPorts.get(portBO.getId());
                pluginPort.setNat(imageInfo.getNat());
                pluginPort.setAccessModes(this.getCloudAccessModes(imageInfo.getAccessModes()));
                pluginPort.getAccessModes().forEach(mode -> {
                    if (mode.getProxy() == null) {
                        mode.setProxy(true);
                    }
                });
            }
        }
    }

    private List<AccessMode> getAccessModes(List<AccessModeParseResult> results) {
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        return results.stream()
                .map(result -> {
                    AccessMode accessMode = new AccessMode();
                    this.loadAccessModeInfo(accessMode, result);
                    return accessMode;
                }).collect(Collectors.toList());
    }

    private List<CloudAccessMode> getCloudAccessModes(List<AccessModeParseResult> results) {
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        return results.stream()
                .map(result -> {
                    CloudAccessMode accessMode = new CloudAccessMode();
                    this.loadAccessModeInfo(accessMode, result);
                    return accessMode;
                }).collect(Collectors.toList());
    }

    private void loadAccessModeInfo(AccessMode accessMode, AccessModeParseResult result) {
        accessMode.setUsername(result.getUsername());
        accessMode.setProtocol(result.getProtocol());
        accessMode.setDesc(result.getDesc());
        accessMode.setProxy(result.getIsproxy() == null ? true : Boolean.valueOf(result.getIsproxy()));
        accessMode.setPassword(result.getPassword());
        if (NumberUtils.isCreatable(result.getPort())) {
            accessMode.setPort(Integer.valueOf(result.getPort()));
        }
    }
}
