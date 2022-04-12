package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.constant.CommonConstant;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.*;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.TargetSceneConvertModel;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
            this.doConvert(container.getNetworkElements(), container.getCloudPluginElements(), container.getCloudPluginPorts());
        }
        if (CollectionUtils.isNotEmpty(container.getNodeElements())) {
            this.doConvert(container.getNodeElements(), container.getCloudPluginElements(), container.getCloudPluginPorts());
        }
    }

    private void doConvert(List<SceneConfigElementBO> list, Map<String, CloudPluginSceneConfigElementBO> pluginElements, Map<String, CloudPluginSceneConfigElementPortBO> pluginPorts) {
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
            TargetSceneConvertModel targetSceneConvertModel = this.targetConverter.allTarget(request);
            if (Objects.isNull(targetSceneConvertModel) || CollectionUtils.isEmpty(targetSceneConvertModel.getDeviceList())) {
                List<Device> devices = this.targetConverter.getCommonTargets(request.getName());
                if (CollectionUtils.isEmpty(devices)) {
                    log.info("import scene ,no device find , image is {}", request.getName());
                    continue;
                } else {
                    Params params = new Params(element, pluginElements.get(element.getId()), pluginPorts);
                    this.loadDeviceInfo(params, devices.get(0));
                }
            } else {
                List<Device> devices = targetSceneConvertModel.getDeviceList();
                Params params = new Params(element, pluginElements.get(element.getId()), pluginPorts);
                this.loadDeviceInfo(params, devices.get(0));
            }
        }
    }

    private void loadDeviceInfo(Params params, Device device) {
        SceneConfigElementBO element = params.element;
        CloudPluginSceneConfigElementBO pluginElement = params.pluginElement;

        if (StringUtils.isNotEmpty(device.getLogo())) {
            if (!device.getLogo().startsWith("/fdfs") && !device.getLogo().startsWith(CommonConstant.DefaultImagePrefix)) {
                element.setIcon2(CommonConstant.DefaultImagePrefix + device.getLogo());
            } else {
                element.setIcon2(device.getLogo());
            }
        }
        element.setDescription(device.getDescription());
        pluginElement.setCategory(device.getCategory());
        pluginElement.setCategory2(device.getSecondCategory());
        pluginElement.setCategory3(device.getL3Category());
        pluginElement.setImageType(device.getImageType());
        // 先用内置靶标来判断 is_build_in
        pluginElement.setIsBuildIn(device.getIsInternal());
        // 云平台数据库大小写不敏感,这里名字不能覆盖
//        pluginElement.setImage(device.getName());
        pluginElement.setRole(device.getRole());

        if (device.getExtSystemInfo() != null) {
            this.loadExtSystemInfo(params.element, device.getExtSystemInfo());
        }

        if (device.getExtDocker() != null) {
            pluginElement.setInitSupport(device.getExtDocker().getInitSupport());
            pluginElement.setQuickCreateSupport(device.getExtDocker().getInitSupport());
        }

        if (CollectionUtils.isNotEmpty(device.getExtEntityAccess())) {
            this.loadExtEntityAccesses(params, device.getExtEntityAccess());
        }

        if (device.getExtGateway() != null && CollectionUtils.isNotEmpty(device.getExtGateway().getPortConfigs())) {
            this.loadExtGatewayPortConfigs(params, device.getExtGateway().getPortConfigs());
        }

        if (device.getExtVm() != null) {
            this.loadExtVm(params, device.getExtVm());
        }
    }

    private void loadExtEntityAccesses(Params params, List<ExtEntityAccess> accesses) {
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
            port.setName(access.getPortName());
            pluginPort.setEntitySwitchName(access.getSwitchName());
            pluginPort.setEntitySwitchPort(access.getSwitchPort());
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
