package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.mptool.URLUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ConfigHandlerUtils;
import cn.cyberpeace.mp.sceneplugincloud.plugin.modelhandler.CloudSceneConfigPluginModelHandler;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.sceneplugincloud.validation.CloudScenePluginValidation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
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

    private static final String[] disallowedUsernames = {"administrator", "admin", "user", "user1", "test", "user2", "test1", "user3", "admin1", "1", "123", "a", "actuser", "adm", "admin2", "aspnet", "backup", "console", "david", "guest", "john", "owner", "root", "server", "sql", "support", "support_388945a0", "sys", "test2", "test3", "user4", "user5"};
    private static final Pattern validUsernamePattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_-]{0,63}");
    private static final String[] disallowedPasswords = {"abc@123", "P@$$w0rd", "P@ssw0rd", "P@ssword123", "Pa$$word", "pass@word1", "Password!", "Password1", "Password22", "iloveyou!"};
    private static final Pattern validPasswordPattern = Pattern.compile("^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{12,72}$");

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
        } else if (role.equals(BaseCloudPluginSceneElementModel.Role.LOAD)) {
            srcElement.setType(BaseSceneElementModel.Type.LOAD);
        }
    }

    private void correctElementImageTypeRelated(SceneConfigElementBO srcElement, CloudPluginSceneConfigElementBO cloudElement,
                                                String imageType) {
        if (imageType == null) {
            return;
        }

        if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.SIMPLE)) {
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.VM) || imageType.equals(BaseCloudPluginSceneElementModel.ImageType.LAAS_VM)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.CONTAINER) || imageType.equals(BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.ENTITY)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.DYNAMIPS)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS);
        } else if (imageType.equals(BaseCloudPluginSceneElementModel.ImageType.LOAD)) {
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.LOAD);
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
        Integer cloudType = getSceneHandler().getScene().getCloudType();

//        存进过递归方法的Vent节点 避免重复校验
        List<String> elementIdBycollectCidrPools = new ArrayList<>();


        Map<String, CloudPluginSceneConfigElementBO> elementMap = getElementMap();
        //            根据微软云创建虚拟机规则验证用户名、密码
        if (cloudType != null && cloudType != BaseSceneModel.CloudType.DEFAULT) {
            Map<String, List<SceneConfigElementPortBO>> elementPortMap = getSceneHandler().getElementPortMap().values().stream()
                    .collect(Collectors.groupingBy(
                            element -> element.getElement().getId()
                    ));

            elementMap.forEach((nodeId, element) -> {
                if (StringUtils.isNotBlank(element.getUsername())) {
                    boolean isUsernameAllowed = Arrays.stream(disallowedUsernames)
                            .noneMatch(element.getUsername()::equalsIgnoreCase);
                    boolean matches = !validUsernamePattern.matcher(element.getUsername()).matches();
                    if (!isUsernameAllowed) {
                        throw new MpRuntimeException(CloudErrorCode.USERNAME_NOT_ALLOWED.format(element.getUsername()));
                    } else if (!validUsernamePattern.matcher(element.getUsername()).matches()) {
                        throw new MpRuntimeException(CloudErrorCode.INVALID_USERNAME.format(element.getUsername()));
                    }
                }
                if (StringUtils.isNotBlank(element.getPassword())) {
                    boolean isPasswordAllowed = Arrays.stream(disallowedPasswords)
                            .noneMatch(element.getPassword()::equalsIgnoreCase);
                    if (!isPasswordAllowed) {
                        throw new MpRuntimeException(CloudErrorCode.PASSWORD_NOT_ALLOWED.format(element.getPassword()));
                    } else if (!validPasswordPattern.matcher(element.getPassword()).matches()) {
                        throw new MpRuntimeException(CloudErrorCode.INVALID_PASSWORD.format(element.getPassword()));
                    }
                }
                if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(element.getImageType())) {
//                VNet节点子网格式校验
                    List<String> cidrPools = element.getCidrPools();
                    List<String> validateCidrPools = IpUtils.validateCidrPools(cidrPools);
                    if (validateCidrPools.size() > 0) {
                        throw new MpRuntimeException(CloudErrorCode.INVALID_CIDR_POOLS.format(validateCidrPools.toString()));
                    }
//                对等互联子网冲突验证
//                查Vent子网+连接节点的子网
//                查连接连接Vent节点下 Vent子网+连接节点的子网
//                判断是否有冲突
                    if (!elementIdBycollectCidrPools.contains(element.getSrcElement().getId())) {
                        HashMap<String, Set<String>> nodeCidrPools = new HashMap<>();
                        HashSet<String> cidrPoolSet = new HashSet<>();
                        cidrPoolSet.addAll(cidrPools);
                        nodeCidrPools.put(element.getSrcElement().getId(), cidrPoolSet);
//                    连接的VNet 需递归查询所有相连的VNet
                        collectCidrPools(element, elementMap, elementPortMap, nodeCidrPools, elementIdBycollectCidrPools);

                        Set<String> allCidrs = new HashSet<>();
                        Set<String> duplicates = new HashSet<>();
                        for (Map.Entry<String, Set<String>> nodeCidrPoolList : nodeCidrPools.entrySet()) {
                            for (String cidr : nodeCidrPoolList.getValue()) {
                                if (!allCidrs.add(cidr) && StringUtils.isNotEmpty(cidr)) {
                                    duplicates.add(cidr);
                                }
                            }
                        }
                        if (duplicates.size() > 0) {
                            throw new MpRuntimeException(CloudErrorCode.CONFLICT_CIDR_POOLS.format(duplicates.toString()));
                        }
                    }
                }
            });
        }
        Map<String, SceneConfigElementPortBO> elementPortMap = getSceneHandler().getElementPortMap();
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
//                公有云场景需要验证ip是否在保留ip段内
                if (null != getSceneHandler().getScene().getCloudType() && BaseSceneModel.CloudType.DEFAULT != getSceneHandler().getScene().getCloudType()) {
                    if (IpUtils.isLastOctetInReservedRange(ip)) {
                        throw new MpRuntimeException(CloudErrorCode.IP_CONFLICT_WITH_RESERVED_IP.format(ip));
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

    private void collectCidrPools(CloudPluginSceneConfigElementBO element, Map<String, CloudPluginSceneConfigElementBO> elementMap,
                                  Map<String, List<SceneConfigElementPortBO>> elementPortMap, HashMap<String, Set<String>> nodeCidrPools,
                                  List<String> elementIdBycollectCidrPools) {
        elementIdBycollectCidrPools.add(element.getSrcElement().getId());
        for (SceneConfigElementPortBO port : element.getSrcElement().getPorts()) {
            SceneConfigElementPortBO portConnectedPort = getSceneHandler().getPortConnectedPort(port.getId());
            if (null != portConnectedPort) {
                String id = portConnectedPort.getElement().getId();
                CloudPluginSceneConfigElementBO cloudPluginSceneConfigElementBO = elementMap.get(id);
                if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(cloudPluginSceneConfigElementBO.getImageType())) {
                    List<SceneConfigElementPortBO> sceneConfigElementPortBOList = elementPortMap.get(id);
                    List<String> cidrs = sceneConfigElementPortBOList.stream()
                            .flatMap(element1 -> element1.getElement().getPorts().stream().map(port1 -> IpUtils.formatIpAddressWithDefaultMask(port1.getIp())))
                            .collect(Collectors.toList());
                    nodeCidrPools.get(element.getSrcElement().getId()).addAll(cidrs);
                } else if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(cloudPluginSceneConfigElementBO.getImageType())) {
                    Set set = nodeCidrPools.get(cloudPluginSceneConfigElementBO.getSrcElement().getId());
                    if (set != null) continue;
                    set = new HashSet<>();
                    set.addAll(cloudPluginSceneConfigElementBO.getCidrPools());
                    nodeCidrPools.put(cloudPluginSceneConfigElementBO.getSrcElement().getId(), set);
                    collectCidrPools(cloudPluginSceneConfigElementBO, elementMap, elementPortMap, nodeCidrPools, elementIdBycollectCidrPools);
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

    private static List<String> findDuplicates(List<String> cidrs, Set<String> uniqueCidrs) {
        List<String> duplicates = new ArrayList<>();
        for (String cidr : cidrs) {
            if (!uniqueCidrs.add(cidr)) {
                duplicates.add(cidr);
            }
        }
        // Remove the first occurrence of each duplicate as it was already added to the set.
        Set<String> toRemove = new HashSet<>(duplicates);
        duplicates.retainAll(toRemove);
        return duplicates;
    }
}
