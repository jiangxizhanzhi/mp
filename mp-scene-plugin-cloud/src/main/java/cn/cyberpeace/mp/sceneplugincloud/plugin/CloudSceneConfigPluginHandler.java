package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.mptool.Ipv4Util;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;
import cn.cyberpeace.mp.scene.config.MultiSceneConfig;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.BaseCloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ConfigHandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudSceneConfigPluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.validation.CloudScenePluginValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@ClassFieldClasses({
        CloudPluginSceneConfigBO.class,
        CloudPluginSceneConfigElementBO.class,
        CloudPluginSceneConfigElementPortBO.class,
        Object.class,
})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class CloudSceneConfigPluginHandler extends SceneConfigPluginHandler<CloudPluginSceneConfigBO,
        CloudPluginSceneConfigElementBO, CloudPluginSceneConfigElementPortBO, Object> {

    private CloudSceneConfigPluginModelHandler pluginModelHandler;

    private ConfigHandlerUtils handlerUtils;

    private void correctElementRoleRelated(SceneConfigElementBO srcElement, CloudPluginSceneConfigElementBO cloudElement,
                                           Integer role, String imageType) {
        if (role == null) {
            return;
        }

        // 网元类型暂时无法准确纠正
        var isForceCorrectElementType = srcElement.getType() == null
                || srcElement.getType().equals(BaseSceneElementModel.Type.OTHER);
        // 虚拟化类型可能无法准确纠正，但必须纠正，不然无法正常虚拟化

        if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟二层网关就是默认交换机
                srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_GATEWAY)) {
            if (imageType != null && imageType.equals(BaseCloudPluginSceneElementModel.ImageType.SIMPLE)) {
                if (isForceCorrectElementType) {
                    // TODO 暂定 - 简易虚拟三层网关就是默认路由
                    srcElement.setType(BaseSceneElementModel.Type.ROUTER);
                }
                cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
            } else {
                if (isForceCorrectElementType) {
                    // TODO 暂定 - 其它虚拟三层网关就是仿真路由
                    srcElement.setType(BaseSceneElementModel.Type.ROUTER);
                }
            }
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            // 虚拟外部网络
            srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
            srcElement.setType(BaseSceneElementModel.Type.TERMINAL);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TRANSPARENT_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟透明网关就是默认防火墙
                srcElement.setType(BaseSceneElementModel.Type.FIREWALL);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_FIREWALL);
            cloudElement.setTransparent(true);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 虚拟网关就是仿真路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_IMAGE_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 三层镜像网关就是仿真路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
            srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)) {
            srcElement.setType(BaseSceneElementModel.Type.SWITCH);
            // 简单三层虚拟交换机用虚拟默认路由实现
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
            srcElement.setType(BaseSceneElementModel.Type.FIREWALL);
            // 简单三层虚拟防火墙用虚拟默认路由 + 防火墙实现
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_GATEWAY)) {
            if (isForceCorrectElementType) {
                // TODO 暂定 - 实体网关就是实体路由
                srcElement.setType(BaseSceneElementModel.Type.ROUTER);
            }
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_TERMINAL)) {
            srcElement.setType(BaseSceneElementModel.Type.TERMINAL);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.NPC)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.NPC);
        }
    }

    private void correctElementImageTypeRelated(SceneConfigElementBO srcElement, CloudPluginSceneConfigElementBO cloudElement,
                                                String imageType) {
        if (imageType == null) {
            return;
        }

        if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.SIMPLE)) {
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.VM)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.CONTAINER)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.ENTITY)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.DYNAMIPS)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS);
        }
    }

    @Override
    public void correctData() {
        getElementMap().forEach((nodeId, element) -> {
            var srcElement = getSrcElement(nodeId);
            // 纠正 role imageType 对应 type virtualType
            Integer role = element.getRole();
            String imageType = element.getImageType();
            correctElementRoleRelated(srcElement, element, role, imageType);
            correctElementImageTypeRelated(srcElement, element, imageType);
        });
    }

    @Override
    public void validate() {
        for (var srcPort : getSceneHandler().getElementPortMap().values()) {
            // 验证ip是否在cidr内
            var ip = srcPort.getIp();
            if (!StringUtils.isEmpty(ip)) {
                // TODO: 这里未初始化辅助网络，判断不准
                var auxiliaryNetworkCloudElement = handlerUtils.getPortAuxiliaryNetworkCloudElement(srcPort.getId());
                if (auxiliaryNetworkCloudElement != null) {
                    String cidr;
                    var role = auxiliaryNetworkCloudElement.getRole();
                    if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
                        cidr = CloudSingleton.cloudAppConfig.getExternalNetCidr();
                    } else {
                        cidr = auxiliaryNetworkCloudElement.getCidr();
                    }
                    if (!StringUtils.isEmpty(cidr) && !IpUtils.newIsInCidr(ip, cidr)) {
                        throw new MpRuntimeException(CloudErrorCode.INVALID_IP_ON_CIDR.format(ip, cidr));
                    }
                }
            }
        }
        if (getElementPortMap() != null) {
            getElementPortMap().values().forEach(p -> {
                if (p.getFirewallRules() != null && JsonCommon.stringify(p.getFirewallRules()).length() >= 65535) {
                    throw new MpRuntimeException(CloudErrorCode.FIRE_RULES_DATA_TOO_LONG);
                }
            });
        }
        // 是否有节点连接了外部网络 而且外部网络设置了平台的地址
        var u = URLUtil.url(CloudSingleton.cloudAppConfig.getCpcloudServer());
        for (var element : getSceneHandler().getElementMap().values()) {
            if (CollectionUtils.isNotEmpty(element.getPorts())) {
                for (var port : element.getPorts()) {
                    if (StringUtils.isNotEmpty(port.getIp()) && port.getIp().equals(u.getHost())) {
                        var connectElement = getSceneHandler().getPortConnectedElement(port.getId());
                        if (connectElement != null) {
                            var cloudElement = getElement(connectElement.getId());
                            if (cloudElement.getRole() != null && BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK == cloudElement.getRole()) {
                                throw new MpRuntimeException(CloudErrorCode.IP_CONFLICT_WITH_SERVER);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CloudSceneConfigPluginModelHandler(this);
        handlerUtils = new ConfigHandlerUtils(this);
    }

    @Override
    public void reloadData() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds()
                .toArray(new String[0])));
    }

    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

    @Override
    public ScenePluginDataBO toData() {
        try {
            getElementPortMap().forEach((portId, cloudPort) -> {
                var auxiliaryNetworkCloudElement = handlerUtils.getPortAuxiliaryNetworkCloudElement(portId);
                if (auxiliaryNetworkCloudElement != null && auxiliaryNetworkCloudElement.getRole() != null && auxiliaryNetworkCloudElement.getRole().equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
                    cloudPort.setConnToExtNet(true);
                } else {
                    cloudPort.setConnToExtNet(false);
                }
            });
        } catch (Exception e) {
            log.error("failed to set connToExtNet , scene id is {}", getSceneHandler().getSceneId(), e);
        }
        return super.toData();
    }

    @Override
    protected void validateData(BaseScenePluginDataBO validateData) {
        var validateResult = CloudScenePluginValidation.validate(validateData);
        CloudScenePluginValidation.handleValidateResult(validateResult);
    }

    /**
     * 多靶场需要预先初始化靶场直接连线。cidr, ip, gateway
     */
    @Override
    public void initiate() {
        Set<String> existCidrList = new HashSet<>();
        existCidrList.addAll(
                this.getElementMap().values().stream().map(BaseCloudPluginSceneElementBO::getCidr)
                        .filter(StringUtils::isNotBlank).collect(Collectors.toSet()));

        Set<String> manualSetIps = this.getSceneHandler().getElementPortMap().values().stream()
                .map(BaseSceneElementPortBO::getIp).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        existCidrList.addAll(manualSetIps.stream().map(IpUtils::getIpDefault24Cidr).collect(Collectors.toSet()));


        this.getElementMap().forEach((nodeId, pluginElement) -> {
            boolean switchFlag = pluginElement.getSrcElement().getType() == BaseSceneElementModel.Type.SWITCH;
            if (switchFlag) {
                //设置网络
                String cidr = pluginElement.getCidr();
                if (StringUtils.isNotEmpty(cidr)) {
                    //根据cidr，给连接的端口设置ip
                    processSwitchConnectPortIpAndGateWay(nodeId, cidr, pluginElement);
                } else {
                    //1. 分配一个cidr
                    cidr = getCidrFromElementAndConnectPorts(nodeId, existCidrList);
                    existCidrList.add(cidr);
                    processSwitchConnectPortIpAndGateWay(nodeId, cidr, pluginElement);
                }

            }
        });

        this.getElementMap().forEach((nodeId, pluginElement) -> {
            boolean gatewayTypeFlag = gatewayTypeFlag(nodeId);
            if (gatewayTypeFlag) {
                //对于路由器（非核心）和防火墙来说，在同一默认网络的都是同一个cidr，其他用户设置了ip的，都是由ip计算cidr
                String defaultRandomCidr = getRandomCidrs(1, new ArrayList<>(existCidrList)).get(0);
                List<String> defaultRandomCidrExcludeIps = new ArrayList<>();
                String defaultFromIp = "";

                for (SceneConfigElementPortBO port : pluginElement.getSrcElement().getPorts()) {
                    if (StringUtils.isNotEmpty(port.getGateway())) {
                        continue;
                    } else {
                        SceneConfigElementPortBO connectedPort = getSceneHandler().getPortConnectedPort(port.getId());
                        if (Objects.isNull(connectedPort)) {
                            continue;
                        }

                        String fromPortIp = port.getIp();
                        String toPortIp = connectedPort.getIp();

                        //完全随机生成cidr
                        if (StringUtils.isEmpty(fromPortIp) && StringUtils.isEmpty(toPortIp)) {
                            if (!ElementTypeUtils.isCoreRouter(pluginElement.getImage())) {

                                if (StringUtils.isEmpty(defaultFromIp)) {
                                    existCidrList.add(defaultRandomCidr);
                                    List<String> computedIpPool = IpUtils.newRandomIps(defaultRandomCidr, 2, defaultRandomCidrExcludeIps, null, false);
                                    defaultFromIp = computedIpPool.remove(0);
                                    port.setIp(defaultFromIp);
                                    connectedPort.setIp(computedIpPool.remove(0));

                                    defaultRandomCidrExcludeIps.add(port.getIp());
                                    defaultRandomCidrExcludeIps.add(connectedPort.getIp());

                                } else {
                                    List<String> computedIpPool = IpUtils.newRandomIps(defaultRandomCidr, 1, defaultRandomCidrExcludeIps, null, false);
                                    connectedPort.setIp(computedIpPool.remove(0));
                                    port.setIp(defaultFromIp);

                                    defaultRandomCidrExcludeIps.add(connectedPort.getIp());
                                }

                            } else {
                                String randomCidr = getRandomCidrs(1, new ArrayList<>(existCidrList)).get(0);
                                existCidrList.add(randomCidr);
                                List<String> computedIpPool = IpUtils.newRandomIps(randomCidr, 2, new ArrayList<>(), null, false);
                                port.setIp(computedIpPool.remove(0));
                                connectedPort.setIp(computedIpPool.remove(0));
                            }

                        } else if (StringUtils.isEmpty(fromPortIp)) {
                            String cidrFromIp = IpUtils.getIpDefault24Cidr(toPortIp);
                            String randomFromPortIp = IpUtils.newRandomIps(cidrFromIp, 1, Collections.singletonList(toPortIp), null, false).get(0);
                            port.setIp(randomFromPortIp);
                        } else if (StringUtils.isEmpty(toPortIp)) {
                            String cidrFromIp = IpUtils.getIpDefault24Cidr(fromPortIp);
                            String randomToPortIp = IpUtils.newRandomIps(cidrFromIp, 1, Collections.singletonList(toPortIp), null, false).get(0);
                            connectedPort.setIp(randomToPortIp);
                        }

                        port.setGateway(fromPortIp);
                        connectedPort.setGateway(fromPortIp);
                    }
                }
            }
        });

        Set<String> partSceneConnectIds = getPartSceneConnects();
        List<Integer> allVlanResourceIds = BeanCommon.getBean(MultiSceneConfig.class).getAllVlanResources().stream().distinct().collect(Collectors.toList());
        partSceneConnectIds.forEach(connectId -> {
            this.getSceneHandler().getElementPortConnectionMap().forEach((key, connectionBO) -> {
                if (connectId.equals(connectionBO.getId())) {
                    String sourcePortId = connectionBO.getSource();
                    String targetPortId = connectionBO.getTarget();
                    SceneConfigElementPortBO sourcePort = this.getSceneHandler().getPort(sourcePortId);
                    SceneConfigElementPortBO targetPort = this.getSceneHandler().getPort(targetPortId);

                    Integer sourceElementVlanId = getElementMap().get(sourcePort.getElement().getId()).getVlanId();
                    Integer targetElementVlanId = getElementMap().get(targetPort.getElement().getId()).getVlanId();
                    if (validVlan(sourceElementVlanId)) {
                        sourcePort.setVlanNumber(sourceElementVlanId);
                        targetPort.setVlanNumber(sourceElementVlanId);
                        allVlanResourceIds.remove(sourceElementVlanId);
                    } else if (validVlan(targetElementVlanId)) {
                        sourcePort.setVlanNumber(targetElementVlanId);
                        targetPort.setVlanNumber(targetElementVlanId);
                        allVlanResourceIds.remove(targetElementVlanId);
                    } else {
                        Integer vlan = allVlanResourceIds.remove(0);
                        sourcePort.setVlanNumber(vlan);
                        targetPort.setVlanNumber(vlan);
                    }
                }
            });

        });
    }

    private Set<String> getPartSceneConnects() {
        Set<String> partSceneConnectIdSet = new HashSet<>();
        this.getSceneHandler().getElementPortConnectionMap().values().forEach(connect -> {

            SceneConfigElementBO sourceElement = Optional.ofNullable(this.getSceneHandler().getElementPortMap().get(connect.getSource())).map(SceneConfigElementPortBO::getElement).orElse(null);
            SceneConfigElementBO targetElement = Optional.ofNullable(this.getSceneHandler().getElementPortMap().get(connect.getTarget())).map(SceneConfigElementPortBO::getElement).orElse(null);
            if (Objects.nonNull(sourceElement) && Objects.nonNull(targetElement)) {

                if (StringUtils.isNotEmpty(sourceElement.getRangeTag()) && StringUtils.isNotEmpty(targetElement.getRangeTag())
                        && !sourceElement.getRangeTag().equals(targetElement.getRangeTag())) {
                    partSceneConnectIdSet.add(connect.getId());
                }
            }
        });

        return partSceneConnectIdSet;
    }

    private String getCidrFromElementAndConnectPorts(String nodeId, Set<String> existCidrList) {
        List<SceneConfigElementPortBO> currentNodePorts = getElement(nodeId).getSrcElement().getPorts();
        List<String> cidrSetFromCurrentNodeIp = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(currentNodePorts)) {
            cidrSetFromCurrentNodeIp = currentNodePorts.stream().map(SceneConfigElementPortBO::getIp).filter(StringUtils::isNotBlank)
                    .map(IpUtils::getIpDefault24Cidr).collect(Collectors.toList());

        }

        List<String> cidrSetFromConnectIp = getElementConnectedSrcPorts(nodeId).stream().map(BaseSceneElementPortBO::getIp).filter(StringUtils::isNotBlank)
                .map(IpUtils::getIpDefault24Cidr).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(cidrSetFromConnectIp)) {
            return cidrSetFromConnectIp.get(0);
        } else if (CollectionUtils.isNotEmpty(cidrSetFromCurrentNodeIp)) {
            return cidrSetFromCurrentNodeIp.get(0);
        } else {
            return getRandomCidrs(1, new ArrayList<>(existCidrList)).get(0);
        }
    }

    private void processSwitchConnectPortIpAndGateWay(String nodeId, String cidr, CloudPluginSceneConfigElementBO pluginElement) {
        checkSwitchConnectPortInCidr(nodeId, cidr, pluginElement.getSrcElement());
        List<String> ipPool = getSwitchTypeConnectPortIpPool(nodeId, cidr);
        setSwitchIpAndGateway(nodeId, ipPool);
    }

    private void checkSwitchConnectPortInCidr(String nodeId, String cidr, SceneConfigElementBO sceneConfigElementBO) {
        List<SceneConfigElementPortBO> ports = this.getElementConnectedSrcPorts(nodeId);
        String maskBit = cidr.split("/")[1];
        if (CollectionUtils.isNotEmpty(ports)) {
            boolean manualIpNotInSwitchCidr = ports.stream().map(BaseSceneElementPortBO::getIp).filter(StringUtils::isNotBlank).map(ip -> IpUtils.getIpCidrByMaskBit(ip, maskBit))
                    .anyMatch(computedCidr -> !computedCidr.equals(cidr));
            if (manualIpNotInSwitchCidr) {
                throw new MpRuntimeException(String.format("节点%s连接的端口ip配置和该节点cidr配置冲突", sceneConfigElementBO.getName()));
            }
        }

    }

    private boolean validVlan(Integer vlanNumber) {
        return Objects.nonNull(vlanNumber) && vlanNumber > 0;
    }

    private List<String> getRandomCidrs(int count, List<String> existCidrs) {
        List<String> cidrPool = CloudSingleton.cloudAppConfig.getCidrPool();
        return IpUtils.randomCidrs(cidrPool, count, existCidrs);
    }

    private List<String> getSwitchTypeConnectPortIpPool(String nodeId, String cidr) {
        List<SceneConfigElementPortBO> elementPortList = this.getElementConnectedSrcPorts(nodeId);

        List<String> excludeIps = new ArrayList<>();
        List<String> manualSetIps = elementPortList.stream().map(BaseSceneElementPortBO::getIp).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        excludeIps.addAll(manualSetIps);

        String gateway = this.getElement(nodeId).getGateway();

        long notSetIpCount = elementPortList.stream().filter(port -> StringUtils.isEmpty(port.getIp())).count() + 1;

        List<String> ipPool = new ArrayList<>();

        if (StringUtils.isNotEmpty(gateway)) {
            if (!manualSetIps.contains(gateway)) {
                notSetIpCount = notSetIpCount - 1;
                ipPool.add(gateway);
            }
            excludeIps.add(gateway);
        }


        List<String> computedIpPool = IpUtils.newRandomIps(cidr, (int) notSetIpCount, excludeIps, null, false);
        ipPool.addAll(computedIpPool);
        return ipPool;
    }

    private void setSwitchIpAndGateway(String nodeId, List<String> ipPool) {
        if (CollectionUtils.isNotEmpty(ipPool)) {
            ipPool.sort((o1, o2) -> (int) (Ipv4Util.ipv4ToLong(o1) - Ipv4Util.ipv4ToLong(o2)));
        }

        List<SceneConfigElementPortBO> elementPortList = this.getElementConnectedSrcPorts(nodeId);
        String gatewayIp = null;

        for (SceneConfigElementPortBO connectPort : elementPortList) {
            if (StringUtils.isNotEmpty(connectPort.getIp())) {
                return;
            } else {
                if (gatewayTypeFlag(connectPort.getElement().getId())) {
                    connectPort.setIp(ipPool.remove(ipPool.size() - 1));
                    if (StringUtils.isEmpty(gatewayIp)) {
                        gatewayIp = connectPort.getIp();
                    }
                } else {
                    connectPort.setIp(ipPool.remove(0));
                }
            }
        }

        List<SceneConfigElementPortBO> ports = getElement(nodeId).getSrcElement().getPorts();

        if (CollectionUtils.isNotEmpty(ports)) {
            ports.forEach(port -> port.setIp(ipPool.get(0)));
            if (StringUtils.isEmpty(gatewayIp)) {
                gatewayIp = ipPool.get(0);
            }
        }

        gatewayIp = StringUtils.isEmpty(getElement(nodeId).getGateway()) ? gatewayIp : getElement(nodeId).getGateway();

        for (SceneConfigElementPortBO connectPort : elementPortList) {
            connectPort.setGateway(gatewayIp);
        }
        for (SceneConfigElementPortBO port : ports) {
            port.setGateway(gatewayIp);
        }
    }

    public boolean gatewayTypeFlag(String nodeId) {
        return getElement(nodeId).getSrcElement().getType() == BaseSceneElementModel.Type.FIREWALL || BaseSceneElementModel.Type.ROUTER == getElement(nodeId).getSrcElement().getType();
    }

}
