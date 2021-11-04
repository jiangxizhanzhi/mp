package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageInfoParseResult;
import cn.cyberpeace.mp.base.util.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
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
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
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
                continue;
            }
            element.setInitCommands(imageInfo.getInitCommands());
            if (CollectionUtils.isEmpty(element.getPorts())) {
                SceneConfigElementPortBO portBO = new SceneConfigElementPortBO();
                portBO.setId(TextUtils.uuid());
                element.setPorts(new ArrayList<>());
                element.getPorts().add(portBO);
                CloudPluginSceneConfigElementPortBO pluginPort = new CloudPluginSceneConfigElementPortBO();
                container.putPluginElementPort(portBO.getId(), pluginPort);
            }
            SceneConfigElementPortBO portBO = element.getPorts().get(0);
            portBO.setAccessModes(this.getAccessModes(imageInfo.getAccessModes()));
            CloudPluginSceneConfigElementPortBO pluginPort = pluginPorts.get(portBO.getId());
            pluginPort.setNat(imageInfo.getNat());
            pluginPort.setAccessModes(this.getCloudAccessModes(imageInfo.getAccessModes()));
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
        accessMode.setProxy(Boolean.valueOf(result.getIsproxy()));
        if (NumberUtils.isCreatable(result.getPort())) {
            accessMode.setPort(Integer.valueOf(result.getPort()));
        }
    }
}
