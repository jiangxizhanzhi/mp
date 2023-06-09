package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntityAccess;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.PortConfig;
import cn.cyberpeace.mp.devopscluster.converter.TargetConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description 写入设备信息
 * @Author maojun
 * @Date 2021/10/12
 */
@Slf4j
class DeviceInfoConverter implements IConverter {

    private TargetConverter targetConverter;

    public DeviceInfoConverter(TargetConverter targetConverter) {
        this.targetConverter = targetConverter;
    }

    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(container.getNetworkElements())) {
            this.doConvert(container.getNetworkElements(), container.getCloudPluginElements(), container.getCloudPluginPorts(), container);
        }
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
            this.doConvert(container.getNodeElements(), container.getCloudPluginElements(), container.getCloudPluginPorts(), container);
        }
    }

    private void doConvert(List<SceneConfigElementBO> list, Map<String, CloudPluginSceneConfigElementBO> pluginElements, Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts, ConvertDataContainer container) {
        for (SceneConfigElementBO element : list) {
            CloudPluginSceneConfigElementBO elementBO = pluginElements.get(element.getId());
            if (elementBO == null || elementBO.getImage() == null) {
                continue;
            }
            TargetSceneRequest request = new TargetSceneRequest();
            request.setName(elementBO.getImage());
            request.setIgnoreUser(true);
            // 精确搜索
            request.setLikeName(false);
            //不过滤隐藏靶标
            request.setIsHide(Boolean.TRUE);
            BasePageBO<Device> targetSceneConvertModel = this.targetConverter.allTarget(new ImagePageDTO(request));
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getList())) {
                List<Device> devices = this.targetConverter.getCommonTargets(request.getName());
                if (CollectionUtils.isEmpty(devices)) {
                    log.info("import scene ,no device find , image is {}", request.getName());
                } else {
                    Params params = new Params(element, pluginElements.get(element.getId()), pluginPorts);
                    this.loadDeviceInfo(params, devices.get(0), container);
                }
            } else {
                List<Device> devices = targetSceneConvertModel.getList();
                Params params = new Params(element, pluginElements.get(element.getId()), pluginPorts);
                this.loadDeviceInfo(params, devices.get(0), container);
            }
        }
    }

    private void loadDeviceInfo(Params params, Device device, ConvertDataContainer container) {
        SceneConfigElementBO element = params.element;
        CloudPluginSceneConfigElementBO pluginElement = params.pluginElement;

        if (StringUtils.isNotEmpty(device.getLogo())) {
            String logo = FsCommon.getFileKey(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
            element.setIcon2(logo);
        }
        element.setDescription(device.getDescription());
        pluginElement.setImageUuid(device.getUuid());
        pluginElement.setCategory(device.getCategory());
        pluginElement.setCategory2(device.getSecondCategory());
        pluginElement.setCategory3(device.getL3Category());
        pluginElement.setImageType(device.getImageType());
        // 先用内置靶标来判断 is_build_in
        pluginElement.setIsBuildIn(device.getIsInternal());
        // 云平台数据库大小写不敏感,这里名字不能覆盖
//        pluginElement.setImage(device.getName());
        // 默认交换机使用默认角色
//        if (!Boolean.TRUE.equals(device.getIsInternal()) || !TargetConstant.DefaultTargetName.DEFAULT_NETWORK.equals(device.getName())) {
//            pluginElement.setRole(device.getRole());
//        }

        if (device.getExtSystemInfo() != null) {
            this.loadExtSystemInfo(params.element, device.getExtSystemInfo());
        }

        if (device.getExtDocker() != null) {
            pluginElement.setInitSupport(device.getExtDocker().getInitSupport());
            pluginElement.setQuickCreateSupport(device.getExtDocker().getInitSupport());
        }

        if (CollectionUtils.isNotEmpty(device.getExtEntityAccess())) {
            this.loadExtEntityAccesses(params, device.getExtEntityAccess(), container);
        }

        if (device.getExtGateway() != null && CollectionUtils.isNotEmpty(device.getExtGateway().getPortConfigs())) {
            this.loadExtGatewayPortConfigs(params, device.getExtGateway().getPortConfigs());
        }

        if (device.getExtVm() != null) {
            this.loadExtVm(params, device.getExtVm());
        }
    }

    private void loadExtEntityAccesses(Params params, List<ExtEntityAccess> accesses, ConvertDataContainer container) {
        SceneConfigElementBO element = params.element;
        Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts = params.pluginPorts;

        int originPortsNum = element.getPorts().size();
        for (int i = 0; i < accesses.size(); i++) {
            PortParams portParams = new PortParams(i, originPortsNum, params);
            SceneConfigElementPortBO port = portParams.getPort();
            CloudPluginSceneConfigElementPortBO pluginPort = portParams.getPluginPort();
            ExtEntityAccess access = accesses.get(i);

            port.setIp(access.getIp());
            port.setMacAddr(access.getMac());
            port.setName(access.getSwitchPort());
            pluginPort.setEntitySwitchName(access.getSwitchName());
            pluginPort.setEntitySwitchPort(access.getSwitchPort());
            container.getEntityPortId2Name().put(port.getId(), port.getName());
            if (CollectionUtils.isNotEmpty(access.getAccessProtocols())) {
                if (port.getAccessModes() == null) {
                    port.setAccessModes(new ArrayList<>());
                }
                if (pluginPort.getAccessModes() == null) {
                    pluginPort.setAccessModes(new ArrayList<>());
                }
                for (var protocol : access.getAccessProtocols()) {
                    var accessMode = new AccessMode();
                    accessMode.setUsername(protocol.getUsername());
                    accessMode.setPassword(protocol.getPassword());
                    accessMode.setProtocol(protocol.getProtocol());
                    accessMode.setDesc(protocol.getDescription());
                    accessMode.setProxy(true);
                    accessMode.setPort(protocol.getPort());
                    accessMode.setPrivateKeyEnable(protocol.getPrivateKeyEnable());
                    accessMode.setPrivateKeyPath(protocol.getPrivateKeyPath());
                    accessMode.setPrivateKeyFileName(protocol.getPrivateKeyFileName());
                    port.getAccessModes().add(accessMode);

                    var pluginAccessMode = new CloudAccessMode();
                    pluginAccessMode.setUsername(protocol.getUsername());
                    pluginAccessMode.setPassword(protocol.getPassword());
                    pluginAccessMode.setProtocol(protocol.getProtocol());
                    pluginAccessMode.setDesc(protocol.getDescription());
                    pluginAccessMode.setProxy(true);
                    pluginAccessMode.setPort(protocol.getPort());
                    pluginAccessMode.setPrivateKeyEnable(protocol.getPrivateKeyEnable());
                    pluginAccessMode.setPrivateKeyPath(protocol.getPrivateKeyPath());
                    pluginAccessMode.setPrivateKeyFileName(protocol.getPrivateKeyFileName());
                    pluginAccessMode.setAccessIp(access.getIp());
                    pluginAccessMode.setAccessPort(protocol.getPort());
                    pluginPort.getAccessModes().add(pluginAccessMode);
                }
            }
        }
    }

    private void loadExtGatewayPortConfigs(Params params, List<PortConfig> portConfigs) {
        SceneConfigElementBO element = params.element;

        for (int i = 0; i < portConfigs.size(); i++) {
            PortConfig portConfig = portConfigs.get(i);
            PortParams portParams = new PortParams(i, element.getPorts().size(), params);
            SceneConfigElementPortBO port = portParams.getPort();
            CloudPluginSceneConfigElementPortBO pluginPort = portParams.getPluginPort();

            port.setName(portConfig.getPortName());
            pluginPort.setGatewayPortIps(portConfig.getPortIps());
            pluginPort.setGatewayPortType(portConfig.getPortType());
        }
    }

    private void loadExtSystemInfo(SceneConfigElementBO element, ExtSystemInfo extSystemInfo) {
        // language_name systemTypeName systemTypeName 都是null?
        element.setSystemLanguage(extSystemInfo.getLanguage());
        element.setSystemArchitecture(extSystemInfo.getSystemArchitecture());
        element.setSystemType(extSystemInfo.getSystemType() == null ? StringUtils.EMPTY : String.valueOf(extSystemInfo.getSystemType()));
    }

    private void loadExtVm(Params params, ExtVm extVm) {
        SceneConfigElementBO element = params.element;
        CloudPluginSceneConfigElementBO pluginElement = params.pluginElement;

        element.setCpu(extVm.getCpu());
        element.setDisk(extVm.getDisk());
        element.setRam(extVm.getRam());

        pluginElement.setInitSupport(extVm.getInitSupport());
        pluginElement.setQuickCreateSupport(extVm.getQuickCreateSupport());

    }

    private static class Params {
        SceneConfigElementBO element;
        CloudPluginSceneConfigElementBO pluginElement;
        Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts;

        public Params(SceneConfigElementBO element, CloudPluginSceneConfigElementBO pluginElement, Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts) {
            this.element = element;
            this.pluginElement = pluginElement;
            this.pluginPorts = pluginPorts;
        }
    }

    private static class PortParams {
        SceneConfigElementPortBO port;
        CloudPluginSceneConfigElementPortBO pluginPort;

        PortParams(int index, int originPortNum, Params params) {
            SceneConfigElementBO element = params.element;
            Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts = params.pluginPorts;

            if (originPortNum > index) {
                this.port = element.getPorts().get(index);
                this.pluginPort = pluginPorts.get(port.getId());
            } else {
                this.port = new SceneConfigElementPortBO();
                this.pluginPort = new CloudPluginSceneConfigElementPortBO();
                this.port.setId(IConverter.getPortId(element.getId(), element.getType(), element.getPorts().size()));
                element.getPorts().add(this.port);
                pluginPorts.put(this.port.getId(), this.pluginPort);
            }
        }

        public SceneConfigElementPortBO getPort() {
            return port;
        }

        public CloudPluginSceneConfigElementPortBO getPluginPort() {
            return pluginPort;
        }
    }
}
