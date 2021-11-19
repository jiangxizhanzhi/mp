package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtEntityAccess;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtVm;
import cn.cyberpeace.mp.devops.cluster.model.common.PortConfig;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description 写入设备信息
 * @Author maojun
 * @Date 2021/10/12
 */
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
            List<Device> devices = this.targetConverter.allTarget(request);
            if (CollectionUtils.isEmpty(devices)) {
                continue;
            }
            Params params = new Params(element, pluginElements.get(element.getId()), pluginPorts);
            this.loadDeviceInfo(params, devices.get(0));
        }
    }

    private void loadDeviceInfo(Params params, Device device) {
        SceneConfigElementBO element = params.element;
        CloudPluginSceneConfigElementBO pluginElement = params.pluginElement;

        element.setIcon2(device.getLogo());
        element.setDescription(device.getDescription());
        pluginElement.setCategory(device.getCategory());
        pluginElement.setImageType(device.getImageType());
        pluginElement.setImage(device.getName());
        // TODO 20211030 目前资管导入的靶标无正确的角色信息
//        pluginElement.setRole(device.getRole());

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
                this.port.setId(TextUtils.uuid());
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
