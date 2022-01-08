package cn.cyberpeace.mp.sceneplugincloud.plugin;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.ScenePluginDataBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


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
        }  else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_IMAGE_GATEWAY)) {
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
                var auxiliaryNetworkCloudElement =  handlerUtils.getPortAuxiliaryNetworkCloudElement(srcPort.getId());
                if (auxiliaryNetworkCloudElement != null) {
                    String cidr;
                    var role = auxiliaryNetworkCloudElement.getRole();
                    if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
                        cidr = CloudSingleton.cloudAppConfig.getExternalNetCidr();
                    } else {
                        cidr = auxiliaryNetworkCloudElement.getCidr();
                    }
                    if (!StringUtils.isEmpty(cidr) && !IpUtils.isIpInCidr(ip, cidr)) {
                        throw new MpRuntimeException(CloudErrorCode.INVALID_IP_ON_CIDR.format(ip, cidr));
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
}
