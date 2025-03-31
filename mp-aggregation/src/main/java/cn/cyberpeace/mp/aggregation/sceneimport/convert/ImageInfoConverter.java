 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageInfoConverter.java.
 // File size 12911.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.AccessModeParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image.ImageInfoParseResult;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ImageInfoConverter
 * @Description 写入images目录中解析出来的镜像信息
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 * @see IConverter
 */
class ImageInfoConverter implements IConverter {

    /**
     * convert2BO
     *
     * @param container
     */
    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(container.getNetworkElements())) {
            this.doConvert(container.getNetworkElements(), container);
        }
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
            this.doConvert(container.getNodeElements(), container);
        }
    }

    /**
     * doConvert
     *
     * @param list
     * @param container
     */
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
                    for (SceneConfigElementPortBO port : element.getPorts()) {
                        if (CollectionUtils.isNotEmpty(port.getAccessModes())) {
                            for (AccessMode mode : port.getAccessModes()) {
                                if (mode.getProxy() == null) {
                                    mode.setProxy(true);
                                }
                            }
                        }
                        CloudPluginSceneConfigElementPortBO pluginPort = pluginPorts.get(port.getId());
                        if (pluginPort != null && CollectionUtils.isNotEmpty(pluginPort.getAccessModes())) {
                            for (CloudAccessMode mode : pluginPort.getAccessModes()) {
                                if (mode.getProxy() == null) {
                                    mode.setProxy(true);
                                }
                            }
                        }
                    }
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
                if (portBO.getAccessModes() == null) {
                    portBO.setAccessModes(this.getManageAccessModes(imageInfo.getManageAccessModes()));
                } else {
                    portBO.getAccessModes().addAll(this.getManageAccessModes(imageInfo.getManageAccessModes()));
                }
                portBO.getAccessModes().addAll(this.getAccessModes(imageInfo.getAccessModes(),portBO.getAccessModes()));
                for (AccessMode accessMode : portBO.getAccessModes()) {
                    if (accessMode.getProxy() == null) {
                        accessMode.setProxy(true);
                    }
                }
                CloudPluginSceneConfigElementPortBO pluginPort = pluginPorts.get(portBO.getId());
                pluginPort.setNat(imageInfo.getNat());
                if (pluginPort.getAccessModes() == null) {
                    pluginPort.setAccessModes(this.getManageCloudAccessModes(imageInfo.getManageAccessModes()));
                } else {
                    pluginPort.getAccessModes().addAll(this.getManageCloudAccessModes(imageInfo.getManageAccessModes()));
                }
                pluginPort.getAccessModes().addAll(this.getCloudAccessModes(imageInfo.getAccessModes(), pluginPort.getAccessModes()));
                for (CloudAccessMode mode : pluginPort.getAccessModes()) {
                    if (mode.getProxy() == null) {
                        mode.setProxy(true);
                    }
                }

                var flavor = imageInfo.calFlavor();
                if (flavor != null) {
                    element.setCpu(flavor.getCpu());
                    element.setDisk(flavor.getDisk());
                    element.setRam(flavor.getRam());
                }
            }
        }
    }

    /**
     * getManageAccessModes
     *
     * @param results
     * @return
     */
    private List<AccessMode> getManageAccessModes(List<AccessModeParseResult> results) {
        List<AccessMode> result = getAccessModes(results, new ArrayList<>());
        for (AccessMode r : result) {
            r.setManageMode(true);
        }
        return result;
    }

    /**
     * getAccessModes
     *
     * @param results
     * @param existAccessMode
     * @return
     */
    private List<AccessMode> getAccessModes(List<AccessModeParseResult> results, List<AccessMode> existAccessMode) {
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        List<AccessMode> filterResults = new ArrayList<>();
        List<AccessMode> list = new ArrayList<>();
        for (AccessModeParseResult accessModeParseResult : results) {
            AccessMode accessMode = new AccessMode();
            this.loadAccessModeInfo(accessMode, accessModeParseResult);
            AccessMode apply = accessMode;
            list.add(apply);
        }
        Set<String> existAccessModeKey = new HashSet<>();
        if (CollectionUtils.isNotEmpty(existAccessMode)) {
            for (AccessMode m : existAccessMode) {
                existAccessModeKey.add(StringUtils.joinWith(":", m.getProtocol(), m.getPort(), m.getUsername(), m.getPassword()));
            }
        }
        for (AccessMode accessMode : list) {
            if (accessMode.getProtocol() != null) {
                String key = StringUtils.joinWith(":", accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(), accessMode.getPassword());
                if (!existAccessModeKey.contains(key)) {
                    filterResults.add(accessMode);
                }
            }
        }
        return filterResults;
    }

    /**
     * getManageCloudAccessModes
     *
     * @param results
     * @return
     */
    private List<CloudAccessMode> getManageCloudAccessModes(List<AccessModeParseResult> results) {
        List<CloudAccessMode> result = getCloudAccessModes(results, new ArrayList<>());
        for (CloudAccessMode r : result) {
            r.setManageMode(true);
        }
        return result;
    }

    /**
     * getCloudAccessModes
     *
     * @param results
     * @param existAccessMode
     * @return
     */
    private List<CloudAccessMode> getCloudAccessModes(List<AccessModeParseResult> results, List<CloudAccessMode> existAccessMode) {
        if (CollectionUtils.isEmpty(results)) {
            return new ArrayList<>();
        }
        List<CloudAccessMode> filterResults = new ArrayList<>();
        List<CloudAccessMode> list = new ArrayList<>();
        for (AccessModeParseResult accessModeParseResult : results) {
            CloudAccessMode accessMode = new CloudAccessMode();
            this.loadAccessModeInfo(accessMode, accessModeParseResult);
            CloudAccessMode apply = accessMode;
            list.add(apply);
        }
        Set<String> existAccessModeKey = new HashSet<>();
        if (CollectionUtils.isNotEmpty(existAccessMode)) {
            for (CloudAccessMode m : existAccessMode) {
                existAccessModeKey.add(StringUtils.joinWith(":", m.getProtocol(), m.getPort(), m.getUsername(), m.getPassword()));
            }
        }
        for (CloudAccessMode accessMode : list) {
            if (accessMode.getProtocol() != null) {
                String key = StringUtils.joinWith(":", accessMode.getProtocol(), accessMode.getPort(), accessMode.getUsername(), accessMode.getPassword());
                if (!existAccessModeKey.contains(key)) {
                    filterResults.add(accessMode);
                }
            }
        }
        return filterResults;
    }

    /**
     * loadAccessModeInfo
     *
     * @param accessMode
     * @param result
     */
    private void loadAccessModeInfo(AccessMode accessMode, AccessModeParseResult result) {
        accessMode.setUsername(result.getUsername());
        accessMode.setProtocol(result.getProtocol());
        accessMode.setDesc(result.getDesc());
        accessMode.setPath(result.getPath());
        accessMode.setProxy(result.getIsproxy() == null ? true : Boolean.valueOf(result.getIsproxy()));
        accessMode.setPath(result.getPath());
        if (StringUtils.isNotEmpty(result.getPrivateKey())) {
            Path path = Paths.get(result.getPrivateKey());
            if (Files.exists(path)) {
                String fdfsPath = FsCommon.upload(result.getPrivateKeyPath());
                accessMode.setPrivateKeyEnable(true);
                accessMode.setPrivateKeyPath(fdfsPath);
                accessMode.setPrivateKeyFileName(result.getPrivateKey());
            }
        } else {
            accessMode.setPassword(result.getPassword());
        }
        if (NumberUtils.isCreatable(result.getPort())) {
            accessMode.setPort(Integer.valueOf(result.getPort()));
        }
    }
}
