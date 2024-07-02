package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.BoolUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.mptool.Ipv4Util;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.core.event.base.SceneEventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.AllocationPool;
import cn.cyberpeace.mp.sceneplugincloud.bo.base.CloudAccessMode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.CoreType;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.InitiateTaskPool;
import cn.cyberpeace.mp.sceneplugincloud.util.ElementTypeUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InitiateHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final SceneEventProcess INITIATE_BASE_PROPERTIES = new SceneEventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties", "init", 1);
        public static final SceneEventProcess INITIATE_IP_POOL = new SceneEventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool", "init", 2);
        public static final SceneEventProcess INITIATE_PORTS = new SceneEventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports", "init", 3);
        public static final SceneEventProcess PORT_CHECK = new SceneEventProcess("port_check", "sceneplugincloud.event.port_check", "init", 4);
        public static final SceneEventProcess REALLOCATE_IPS = new SceneEventProcess("reallocate_ips", "sceneplugincloud.event.reallocate_ips", "init", 5);
    }

    public static class ElementProcess {
        public static final EventProcess INITIATE_BASE_PROPERTIES = new EventProcess("initiate_base_properties", "sceneplugincloud.event.initiate_base_properties");
        public static final EventProcess INITIATE_IP_POOL = new EventProcess("initiate_ip_pool", "sceneplugincloud.event.initiate_ip_pool");
        public static final EventProcess INITIATE_PORTS = new EventProcess("initiate_ports", "sceneplugincloud.event.initiate_ports");
    }

    private List<String> existCidrs;

    /**
     * InitiateHandler
     *
     * @param pluginHandler
     */
    public InitiateHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, InitiateTaskPool.getInstance(), SceneModel.Event.INITIATE);
    }

    /**
     * initCoreType
     *
     * @param element
     */
    public void initCoreType(CloudPluginSceneElementBO element) {
        Integer role = element.getRole();
        CoreType coreType = CoreType.NETWORK;
        if (role != null) {
            if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY)
                    || role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)
            ) {
                coreType = CoreType.NETWORK;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_SWITCH)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK;
                } else {
                    coreType = CoreType.NETWORK_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else if (BaseCloudPluginSceneElementModel.ImageType.SIMPLE.equals(element.getImageType())) {
                    coreType = CoreType.ROUTER;
                } else {
                    coreType = CoreType.ROUTER_L2;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_FIREWALL)) {
                if (BaseCloudPluginSceneElementModel.ImageType.ENTITY.equals(element.getImageType())) {
                    coreType = CoreType.NETWORK_0;
                } else {
                    coreType = CoreType.FIREWALL;
                }
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.NPC)) {
                coreType = CoreType.TERMINAL;
            } else if (role.equals(BaseCloudPluginSceneElementModel.Role.ENTITY_TERMINAL)) {
                coreType = CoreType.TERMINAL;
            }
        }
        element.setCoreType(coreType);
    }

    /**
     * initiate
     */
    public void initiate() {
        this.prepareEventProcess();

        getPluginHandler().getSceneHandler().addProgressPercentage(5);

        // 标记已设置的ip的端口 后续需要区分端口ip是来自参数还是自动分配
        markIpSetedPorts();
        // 将节点排序 排序的不用会导致网络分配的不同,因此需要有统一的排序
        List<CloudPluginSceneElementBO> allCloudElements = getOrderedAllCloudElements();

        this.afterOrderElements(allCloudElements);

        // 部分启动和动态更新 需要区分出哪些节点不需要处理
        List<CloudPluginSceneElementBO> cloudElements = new ArrayList<>();
        List<CloudPluginSceneElementBO> noEventElements = new ArrayList<>();
        for (CloudPluginSceneElementBO e : allCloudElements) {
            if (getPluginHandler().getSceneHandler().isHandleNode(e.getSrcElement().getId())) {
                cloudElements.add(e);
            } else {
                noEventElements.add(e);
            }
        }

        // 初始化节点属性
        List<Runnable> initiateElementTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : cloudElements) {
            Runnable runnable = () -> initiateElementBasePropertiesProcess(cloudPluginSceneElementBO);
            initiateElementTasks.add(runnable);
        }
        if (!initiateElementTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_BASE_PROPERTIES, () -> {
                initiateElementTasks.forEach(task -> task.run());
            });
        }

        getPluginHandler().createElementNetworks();

        // 初始化ip池
        List<Runnable> initiateElementIpPoolTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudElement : cloudElements) {
            if (!cloudElement.getDeriveNetworks().isEmpty()) {
                Runnable runnable = () -> initiateElementIpPoolProcess(cloudElement);
                initiateElementIpPoolTasks.add(runnable);
            }
        }
        if (!initiateElementIpPoolTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_IP_POOL, () -> {
                initiateElementIpPoolTasks.forEach(task -> task.run());
            });
        }

        // 初始化端口, 先初始化路由端口, 再初始化其它端口
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        loadInitaitePortsTasks(cloudElements, initiateRouterElementPortsTasks, initiateOtherElementPortsTasks);
        if (!initiateRouterElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateRouterElementPortsTasks));
        }
        if (!initiateOtherElementPortsTasks.isEmpty()) {
            runSceneProgress(SceneProcess.INITIATE_PORTS, () -> runTasks(initiateOtherElementPortsTasks));
        }

        // 存在需要重新分配的ip，重新分配ip
        List<CloudPluginSceneElementBO> networkElements = new ArrayList<>();
        for (CloudPluginSceneElementBO c : cloudElements) {
            if (CollectionUtils.isNotEmpty(c.getDeriveNetworks())) {
                networkElements.add(c);
            }
        }
        if (!Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getEnableReallocateIpPorts())) {
            if (CollectionUtils.isNotEmpty(networkElements)) {
                runSceneProgress(SceneProcess.PORT_CHECK, () -> {
                    portCheck(networkElements);
                });
            }
        }
        if (CollectionUtils.isNotEmpty(networkElements)) {
            runSceneProgress(SceneProcess.REALLOCATE_IPS, () -> {
                reallocateIpPorts(networkElements);
            });
        }

        // 分配 fwdnsInterfaceIp
        setFwdnsInterfaceIps(cloudElements);
//        this.initiateForSingleNode(noEventElements);
    }

    /**
     * initiateForSingleNode
     *
     * @param noEventElements
     */
    public void initiateForSingleNode(List<CloudPluginSceneElementBO> noEventElements) {
        List<CloudPluginSceneElementBO> cloudElements = noEventElements
                .stream()
                .filter(e -> {
                    List<SceneElementBO> connectElements = getHandlerUtils().getEleConnectedOpaqueSrcElements(e.getSrcElement().getId());
                    if (CollectionUtils.isEmpty(connectElements)) {
                        return true;
                    } else {
                        boolean hasConnectNet = false;
                        for (SceneElementBO connectElement : connectElements) {
                            CloudPluginSceneElementBO cloudElement = getPluginHandler().getElement(connectElement.getId());
                            if (cloudElement != null && CoreType.TERMINAL != cloudElement.getCoreType()) {
                                hasConnectNet = true;
                            }
                        }
                        return !hasConnectNet;
                    }
                })
                .collect(Collectors.toList());

        // 使用节点name排序一下,这样节点类型相同时可以有个相对固定的初始化顺序,节点名称相同的情况暂不处理
        // 使初始化结果不受数据库数据顺序的影响
        Collections.sort(cloudElements, (a, b) -> {
            if (a.getSrcElement().getName() == null || b.getSrcElement().getName() == null) {
                return 0;
            }
            return a.getSrcElement().getName().compareTo(b.getSrcElement().getName());
        });

        cloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
        // 根据节点的类型排序,这个顺序直接影响组网的结果
        Collections.sort(cloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));

        // 初始化节点属性
        List<Runnable> initiateElementTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : cloudElements) {
            Runnable runnable = () -> initiateElementBaseProperties(cloudPluginSceneElementBO);
            initiateElementTasks.add(runnable);
        }
        if (!initiateElementTasks.isEmpty()) {
            initiateElementTasks.forEach(task -> task.run());
        }

        try {
            getPluginHandler().createElementNetworks();
        } catch (Exception e) {
            log.error("failed to create network ", e);
        }

        // 初始化ip池
        List<Runnable> initiateElementIpPoolTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : cloudElements) {
            if (!cloudPluginSceneElementBO.getDeriveNetworks().isEmpty()) {
                Runnable runnable = () -> initIpPool(cloudPluginSceneElementBO);
                initiateElementIpPoolTasks.add(runnable);
            }
        }
        if (!initiateElementIpPoolTasks.isEmpty()) {
            initiateElementIpPoolTasks.forEach(task -> task.run());
        }

        // 初始化端口, 先初始化路由端口, 再初始化其它端口
        List<Runnable> initiateRouterElementPortsTasks = new ArrayList<>();
        List<Runnable> initiateOtherElementPortsTasks = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudElement : cloudElements) {
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType != null
                    && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH))) {
                Runnable task = () -> initiateElementPorts(cloudElement);
                Integer type = cloudElement.getSrcElement().getType();
                // 防火墙也看作路由
                if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER)
                        || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
                    initiateRouterElementPortsTasks.add(task);
                } else {
                    initiateOtherElementPortsTasks.add(task);
                }
            } else if (Boolean.TRUE.equals(cloudElement.getSrcElement().getIsAuxiliary())) {
                // 辅助节点也需要处理下端口
                initiateOtherElementPortsTasks.add(() -> initiateVirtualPort(cloudElement));
            }
        }
        if (!initiateRouterElementPortsTasks.isEmpty()) {
            runTasks(initiateRouterElementPortsTasks);
        }
        if (!initiateOtherElementPortsTasks.isEmpty()) {
            runTasks(initiateOtherElementPortsTasks);
        }

        // 存在需要重新分配的ip，重新分配ip
        List<CloudPluginSceneElementBO> networkElements = new ArrayList<>();
        for (CloudPluginSceneElementBO c : cloudElements) {
            if (CollectionUtils.isNotEmpty(c.getDeriveNetworks())) {
                networkElements.add(c);
            }
        }
        if (!Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getEnableReallocateIpPorts())) {
            if (CollectionUtils.isNotEmpty(networkElements)) {
                portCheck(networkElements);
            }
        }
        if (CollectionUtils.isNotEmpty(networkElements)) {
            reallocateIpPorts(networkElements);
        }

        // 分配 fwdnsInterfaceIp
        setFwdnsInterfaceIps(cloudElements);
    }

    /**
     * initiateElementBasePropertiesProcess
     *
     * @param cloudElement
     */
    public void initiateElementBasePropertiesProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_BASE_PROPERTIES,
                () -> initiateElementBaseProperties(cloudElement));
    }

    /**
     * initiateElementIpPoolProcess
     *
     * @param cloudElement
     */
    public void initiateElementIpPoolProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_IP_POOL,
                () -> initIpPool(cloudElement));
    }

    /**
     * initiateElementPortsProcess
     *
     * @param cloudElement
     */
    public void initiateElementPortsProcess(CloudPluginSceneElementBO cloudElement) {
        runElementProgress(cloudElement.getSrcElement().getId(), ElementProcess.INITIATE_PORTS,
                () -> initiateElementPorts(cloudElement));
    }

    /**
     * ip2Cidr24
     *
     * @param ip
     * @return
     */
    public String ip2Cidr24(String ip) {
        return ip.substring(0, ip.lastIndexOf(".")) + ".0/24";
    }

    /**
     * prepareEventProcess
     */
    protected void prepareEventProcess() {
        prepareEventProcess(SceneModel.Event.INITIATE, SceneProcess.class, ElementProcess.class);
    }

    /**
     * resetResourceId
     *
     * @return boolean
     */
    protected boolean resetResourceId() {
        return true;
    }

    /**
     * 初始化节点
     * @param cloudElement 网元节点
     */
    protected void initiateElementBaseProperties(CloudPluginSceneElementBO cloudElement) {
        // 外部网络预先初始化
        Integer role = cloudElement.getRole();
        if (role != null && role.equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK)) {
            cloudElement.setExternal(true);
            cloudElement.setVirtualType(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH);
            cloudElement.setAuxiliaryNetworkOn(true);
            cloudElement.setAuxiliaryNetworkId(CloudSingleton.cloudAppConfig.getExternalNetId());

            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
            network.setExternal(true);
            network.setCidr(CloudSingleton.cloudAppConfig.getExternalNetCidr());
            network.setResourceId(CloudSingleton.cloudAppConfig.getExternalNetId());
            ConnectedNetworkBO connectNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
            List<SceneElementPortBO> ports = Optional
                    .ofNullable(cloudElement.getSrcElement().getPorts())
                    .orElse(new ArrayList<>());
            for (SceneElementPortBO port : ports) {
                SceneElementPortBO connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
                connectNetwork.addPort(port, connectPort, true);
            }
            cloudElement.getDeriveNetworks().add(connectNetwork);
        } else if (Boolean.TRUE.equals(cloudElement.getExternal()) && role != null && role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
            CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
            this.loadProperties2Network(network, cloudElement);
            network.setExternal(true);
            network.setResourceId(cloudElement.getResourceId());
            ConnectedNetworkBO connectNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
            List<SceneElementPortBO> ports = Optional
                    .ofNullable(cloudElement.getSrcElement().getPorts())
                    .orElse(new ArrayList<>());
            for (SceneElementPortBO port : ports) {
                SceneElementPortBO connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
                connectNetwork.addPort(port, connectPort, true);
            }
            cloudElement.getDeriveNetworks().add(connectNetwork);
        }

        if (Boolean.TRUE.equals(cloudElement.getExternal()) && !ElementTypeUtils.isExternalTerminal(cloudElement)) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        if (virtualType == null) {
            log.warn("Element({}) {} has no virtual type", cloudElement.getSrcElement().getEid(),
                    cloudElement.getSrcElement().getId());
            return;
        }

        // 核心路由 处理下ip
        if (ElementTypeUtils.isCoreRouter(cloudElement)) {
            if (CollectionUtils.isNotEmpty(cloudElement.getSrcElement().getPorts())) {
                cloudElement.getSrcElement().getPorts().forEach(p -> {
                    if (StringUtils.isNotEmpty(p.getIp())) {
                        return;
                    }
                    CloudPluginSceneElementPortBO pluginPort = getPluginHandler().getPort(p.getId());
                    if (pluginPort == null) {
                        return;
                    }
                    if (StringUtils.isNotEmpty(pluginPort.getOspfIp())) {
                        p.setIp(pluginPort.getOspfIp().split("/")[0]);
                    } else if (StringUtils.isNotEmpty(pluginPort.getBgpIp())) {
                        p.setIp(pluginPort.getBgpIp().split("/")[0]);
                    } else if (StringUtils.isNotEmpty(pluginPort.getRipIp())) {
                        p.setIp(pluginPort.getRipIp().split("/")[0]);
                    }
                });
            }
        }

        // 非外部资源 重置资源id
        if (!Boolean.TRUE.equals(cloudElement.getExternal())) {
            if (this.resetResourceId()) {
                cloudElement.setResourceId(StringUtils.EMPTY);
            }
        }
        // defaultConnectedNetwork
        CloudPluginSceneElementNetworkBO network = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
        this.loadProperties2Network(network, cloudElement);
        ConnectedNetworkBO defaultConnectedNetwork = new ConnectedNetworkBO(network, getHandlerUtils());
        // 需要的网络
        List<SceneElementPortBO> ports = Optional
                .ofNullable(cloudElement.getSrcElement().getPorts())
                .orElse(new ArrayList<>());

        // 如果连线的对端节点,有相同cidr的网络,那么这个网络就是默认网络
        boolean defaultNetwordIsDerive = true;
        for (SceneElementPortBO port : ports) {
            SceneElementPortBO connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            if (connectPort != null) {
                // 有连线 且 对端端口已归属某个网络 则本端口接入即可
                CloudPluginSceneElementNetworkBO connectNetwork = getHandlerUtils().getNetworkByPort(connectPort);
                if (connectNetwork != null) {
                    if (StringUtils.equals(connectNetwork.getCidr(), defaultConnectedNetwork.getNetwork().getCidr())) {
                        defaultConnectedNetwork = new ConnectedNetworkBO(connectNetwork, getHandlerUtils());
                        defaultNetwordIsDerive = false;
                    }
                }
            }
        }

        Map<String, ConnectedNetworkBO> cidr2ConnectedNetwork = new HashMap<>();
        List<ConnectedNetworkBO> noCidrNetwork = new ArrayList<>();
        for (SceneElementPortBO port : ports) {
            SceneElementPortBO connectPort = getHandlerUtils().getPortConnectedOpaqueSrcPort(port.getId());
            if (connectPort != null) {
                // 有连线 且 对端端口已归属某个网络 则本端口接入即可
                CloudPluginSceneElementNetworkBO connectNetwork = getHandlerUtils().getNetworkByPort(connectPort);
                if (connectNetwork != null) {
                    ConnectedNetworkBO connectedNetwork = new ConnectedNetworkBO(connectNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, false);
                    cloudElement.getAccessNetworks().add(connectedNetwork);
                    continue;
                }
            }
            // 需要自己处理网络的端口
            String cidrDivideIp = null;
            if (StringUtils.isNotEmpty(port.getIp())) {
                // 自己的端口设置了ip,用自己的端口分网络
                cidrDivideIp = port.getIp();
            } else if (connectPort != null && StringUtils.isNotEmpty(connectPort.getIp())) {
                // 自己的端口无ip,但是连线另一端的端口设置了ip,用对端端口ip分网络
                cidrDivideIp = connectPort.getIp();
            }
            if (StringUtils.isEmpty(cidrDivideIp)) {
                // 未设置ip值,使用默认网段的网络
                boolean isCoreRouter = this.isCoreRouter(cloudElement);
                boolean isDynamips = this.isDynamips(cloudElement);
                if (isCoreRouter) {
                    // 如果是核心路由器 每个端口分配一个网络
                    CloudPluginSceneElementNetworkBO createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    ConnectedNetworkBO connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, true);
                    noCidrNetwork.add(connectedNetwork);
                } else if (isDynamips && connectPort != null) {
                    // 如果是dynamips 每个连了线的端口分配一个网络
                    CloudPluginSceneElementNetworkBO createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                    ConnectedNetworkBO connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                    connectedNetwork.addPort(port, connectPort, true);
                    noCidrNetwork.add(connectedNetwork);
                } else {
                    // 否则加入默认网络即可
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                }
            } else {
                // 设置了ip值,根据ip值计算网段
                String cidr = this.ip2Cidr24(cidrDivideIp);
                if (cidr.equals(defaultConnectedNetwork.getNetwork().getCidr())) {
                    // 和默认网段相同,则也加入默认网络
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                } else if (this.isInCidr(defaultConnectedNetwork.getNetwork().getCidr(), cidrDivideIp)) {
                    defaultConnectedNetwork.addPort(port, connectPort, true);
                } else {
                    // 和默认网段不同,构建网络
                    ConnectedNetworkBO connectedNetwork = cidr2ConnectedNetwork.get(cidr);
                    if (connectedNetwork == null) {
                        CloudPluginSceneElementNetworkBO createNetwork = new CloudPluginSceneElementNetworkBO(getPluginHandler().getSourceSceneId(), cloudElement.getSrcElement().getId());
                        createNetwork.setCidr(cidr);
                        connectedNetwork = new ConnectedNetworkBO(createNetwork, getHandlerUtils());
                        cidr2ConnectedNetwork.put(cidr, connectedNetwork);
                    }
                    connectedNetwork.addPort(port, connectPort, true);
                }
            }
        }
        if (this.isCoreRouter(cloudElement)) {
            // 核心路由 每个端口都接网络
            if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getFromPorts())) {
                if (defaultNetwordIsDerive) {
                    cloudElement.getDeriveNetworks().add(defaultConnectedNetwork);
                }
            }
            cloudElement.getDeriveNetworks().addAll(cidr2ConnectedNetwork.values());
            cloudElement.getDeriveNetworks().addAll(noCidrNetwork);
        } else {
            // 其余 有连线才接网络
            List<ConnectedNetworkBO> deriveNetworks = new ArrayList<>();
            for (ConnectedNetworkBO n : noCidrNetwork) {
                if (CollectionUtils.isNotEmpty(n.getToPorts())) {
                    deriveNetworks.add(n);
                }
            }
            for (ConnectedNetworkBO n : cidr2ConnectedNetwork.values()) {
                if (CollectionUtils.isNotEmpty(n.getToPorts())) {
                    deriveNetworks.add(n);
                }
            }
            if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getToPorts())) {
                if (defaultNetwordIsDerive) {
                    deriveNetworks.add(defaultConnectedNetwork);
                }
            }
            // 至少有个网络
            if (CollectionUtils.isEmpty(cloudElement.getAccessNetworks()) && CollectionUtils.isEmpty(deriveNetworks)) {
                if (CollectionUtils.isNotEmpty(defaultConnectedNetwork.getFromPorts())) {
                    if (defaultNetwordIsDerive) {
                        deriveNetworks.add(defaultConnectedNetwork);
                    }
                }
                // 如果虚拟终端没有默认网络 那么加一个
                if (CollectionUtils.isEmpty(deriveNetworks) && cloudElement.getVirtualType() != null &&
                        (
                                cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE
                                        || cloudElement.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER
                        )
                ) {
                    for (ConnectedNetworkBO n : cidr2ConnectedNetwork.values()) {
                        if (CollectionUtils.isNotEmpty(n.getFromPorts())) {
                            deriveNetworks.add(n);
                            break;
                        }
                    }
                }
            }
            cloudElement.setDeriveNetworks(deriveNetworks);
        }

        initiateAuxiliaryNetwork(cloudElement);
    }

    /**
     * allocateCidrs
     *
     * @param count
     * @return
     */
    private synchronized List<String> allocateCidrs(int count) {
        List<String> cidrs = getHandlerUtils().randomCidrs(count, existCidrs);
        existCidrs.addAll(cidrs);
        return cidrs;
    }

    /**
     * prepareExistCidrs
     */
    private void prepareExistCidrs() {
        if (existCidrs == null) {
            existCidrs = getHandlerUtils().statExistCidrs();
        }
    }

    /**
     * isCoreRouter
     *
     * @param cloudElement
     * @return boolean
     */
    private boolean isCoreRouter(CloudPluginSceneElementBO cloudElement) {
        boolean isCoreRouter = (cloudElement.getRole() != null && cloudElement.getRole().equals(BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER))
                && BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType());
        return isCoreRouter;
    }

    /**
     * isDynamips
     *
     * @param cloudElement
     * @return boolean
     */
    private boolean isDynamips(CloudPluginSceneElementBO cloudElement) {
        boolean isDynamips = (BaseCloudPluginSceneElementModel.ImageType.DYNAMIPS.equals(cloudElement.getImageType())
                && cloudElement.getCoreType() != null
                && !cloudElement.getCoreType().equals(CoreType.TERMINAL));
        return isDynamips;
    }

    /**
     * 初始化辅助网络
     * @param cloudElement 网元节点
     */
    private void initiateAuxiliaryNetwork(CloudPluginSceneElementBO cloudElement) {
        List<CloudPluginSceneElementNetworkBO> networks = new ArrayList<>();
        for (ConnectedNetworkBO n : cloudElement.getDeriveNetworks()) {
            CloudPluginSceneElementNetworkBO cloudPluginSceneElementNetworkBO = n.network;
            networks.add(cloudPluginSceneElementNetworkBO);
        }
        for (CloudPluginSceneElementNetworkBO network : networks) {
            Integer role = cloudElement.getRole();
            if (Boolean.TRUE.equals(network.getExternal())) {
                // 外网不处理
                continue;
            } else if (Boolean.TRUE.equals(cloudElement.getExternal()) && role != null && role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_2_GATEWAY) {
                // 外部已存在的网络设置网络id
                network.setResourceId(cloudElement.getResourceId());
                continue;
            } else if (getHandlerUtils().getCreatedNetworks().contains(network.getId())) {
                continue;
            }
            network.setResourceId(StringUtils.EMPTY);
            if (StringUtils.isEmpty(network.getCidr())) {
                prepareExistCidrs();
                network.setCidr(allocateCidrs(1).get(0));
            }
        }
        Integer virtualType = cloudElement.getVirtualType();
        for (ConnectedNetworkBO connectNetwork : cloudElement.getDeriveNetworks()) {
            if (!Boolean.TRUE.equals(connectNetwork.network.getVlan())) {
                // 是否vlan模式(是否连接了实体节点)
                boolean vlan = false;
                if (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                    // 本节点是实体节点
                    vlan = true;
                } else {
                    // 连接节点有实体节点
                    for (SceneElementPortBO p : connectNetwork.getFromPorts()) {
                        SceneElementBO connectedCloudElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(p.getId());
                        CloudPluginSceneElementBO pluginConnectedCloudElement = connectedCloudElement == null ? null : getPluginHandler().getElement(connectedCloudElement.getId());
                        Integer connectedVirtualType = pluginConnectedCloudElement == null ? null : pluginConnectedCloudElement.getVirtualType();
                        if (connectedVirtualType != null
                                && connectedVirtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                            vlan = true;
                            break;
                        }
                    }
                }
                connectNetwork.network.setVlan(vlan);
            }

            // 未显式关闭
            if (!Boolean.TRUE.equals(cloudElement.getAuxiliaryRouterOff())) {
                // 是否开启辅助路由(是否未连接默认路由)
                boolean routerOn = true;
                if (this.isGateway(cloudElement.getCoreType(), cloudElement.getVirtualType(), cloudElement.getSrcElement().getType())) {
                    routerOn = false;
                } else if (ElementTypeUtils.isEntityGateway(cloudElement)) {
                    routerOn = false;
                } else {
                    for (SceneElementPortBO p : connectNetwork.getFromPorts()) {
                        SceneElementBO connectedCloudElement = getHandlerUtils().getPortConnectedOpaqueSrcElement(p.getId());
                        CloudPluginSceneElementBO pluginConnectedCloudElement = connectedCloudElement == null ? null : getPluginHandler().getElement(connectedCloudElement.getId());
                        if (pluginConnectedCloudElement != null) {
                            if (this.isGateway(pluginConnectedCloudElement.getCoreType(), pluginConnectedCloudElement.getVirtualType(), pluginConnectedCloudElement.getSrcElement().getType())) {
                                routerOn = false;
                                break;
                            }
                        }
                    }
                }
                connectNetwork.network.setAuxiliaryNetworkRouterOn(routerOn);
            } else {
                connectNetwork.network.setAuxiliaryNetworkRouterOn(false);
            }
        }
    }

    /**
     * isGateway
     *
     * @param coreType
     * @param virtualType
     * @param type
     * @return boolean
     */
    private boolean isGateway(CoreType coreType, Integer virtualType, Integer type) {
        if (this.isRouter(coreType, virtualType)) {
            return true;
        } else if (type != null) {
            String gatewayTypes = CloudSingleton.cloudAppConfig.getGatewayTypes();
            if (StringUtils.isNotEmpty(gatewayTypes)) {
                String[] gatewayTypeStr = gatewayTypes.split(",");
                for (String gatewayType : gatewayTypeStr) {
                    if (type.toString().equals(gatewayType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * isRouter
     *
     * @param coreType
     * @param virtualType
     * @return boolean
     */
    private boolean isRouter(CoreType coreType, Integer virtualType) {
        return (CoreType.isRouter(coreType)) || (virtualType != null && virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER));
    }

    /**
     * 初始化辅助网络节点的ip池
     * @param element 辅助网络节点
     */
    private void initIpPool(CloudPluginSceneElementBO element) {
        element.getDeriveNetworks().forEach(connectedNetwork -> {
            CloudPluginSceneElementNetworkBO network = connectedNetwork.network;
            String cidr = network.getCidr();
            if (StringUtils.isEmpty(cidr)) {
                return;
            }
            Set<String> existIps = new HashSet<>();
            connectedNetwork.getFromPorts().forEach(port -> {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    existIps.add(port.getIp());
                }
            });
            connectedNetwork.getToPorts().forEach(port -> {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    existIps.add(port.getIp());
                }
            });

            // 处理网关
            if (StringUtils.isEmpty(network.getGateway())) {
                String gatewayIp = null;
                if (ElementTypeUtils.isVirtualGateway(element)
                        || ElementTypeUtils.isCoreRouter(element) || ElementTypeUtils.isEntityGateway(element)) {
                    for (SceneElementPortBO p : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isNotEmpty(p.getIp())) {
                            gatewayIp = p.getIp();
                            break;
                        }
                    }
                }
                if (gatewayIp == null) {
                    String gateway = null;
                    List<String> excludeIps = new ArrayList<>(existIps);
                    for (int i = 0; i < 5; i++) {
                        gateway = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), 1, excludeIps, element.getAllocationPools()).get(0);
                        excludeIps.add(gateway);
                        List<String> possibleDhcpIps = this.getPossibleDhcpIps(gateway, network.getCidr());
                        if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                            possibleDhcpIps = new ArrayList<>();
                        }
                        boolean conflict = false;
                        for (String possibleDhcpIp : possibleDhcpIps) {
                            if (existIps.contains(possibleDhcpIp)) {
                                conflict = true;
                                break;
                            }
                        }
                        if (!conflict) {
                            break;
                        }
                    }
                    network.setGateway(gateway);
                } else {
                    network.setGateway(gatewayIp);
                }
            }
            existIps.add(network.getGateway());
            if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate()) && !Boolean.FALSE.equals(element.getDhcp())) {
                List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getGateway(), network.getCidr());
                for (String possibleDhcpIp : possibleDhcpIps) {
                    if (!existIps.contains(possibleDhcpIp) && !possibleDhcpIp.endsWith("255")) {
                        network.setDhcpIp(possibleDhcpIp);
                        existIps.add(possibleDhcpIp);
                        break;
                    }
                }
                if (StringUtils.isEmpty(network.getDhcpIp())) {
                    List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), 1, new ArrayList<>(existIps), new ArrayList<>(), BoolUtils.bool(element.getDhcp()));
                    if (CollectionUtils.isNotEmpty(ipPool)) {
                        network.setDhcpIp(ipPool.get(0));
                        existIps.add(ipPool.get(0));
                    }
                }
            } else {
                existIps.add(IpUtils.nextIp(network.getGateway()));
            }

            Set<String> portIps = new HashSet<>();
            int requireCount = 0;
            int gatewayCount = 0;
            boolean fromPortsAdd = false;
            // 网络的from端口最多分一个
            for (SceneElementPortBO port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    if (!fromPortsAdd) {
                        if (ElementTypeUtils.isVirtualGateway(element)) {
                            gatewayCount++;
                        } else {
                            requireCount++;
                        }
                    }
                    fromPortsAdd = true;
                } else {
                    portIps.add(port.getIp());
                }
            }
            for (SceneElementPortBO port : connectedNetwork.getToPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    if (ElementTypeUtils.isVirtualGateway(getPluginHandler().getElement(port.getElement().getId()))) {
                        gatewayCount++;
                    } else {
                        requireCount++;
                    }
                } else {
                    portIps.add(port.getIp());
                }
            }
            if (Boolean.TRUE.equals(connectedNetwork.network.getAuxiliaryNetworkRouterOn())) {
                gatewayCount++;
            }
            List<String> gatewayIpPool = IpUtils.newAllocateDefaultRouterIps(network.getCidr(), gatewayCount, new ArrayList<>(existIps), element.getAllocationPools());
            if (Boolean.TRUE.equals(connectedNetwork.network.getAuxiliaryNetworkRouterOn())) {
                connectedNetwork.network.setAuxiliaryNetworkRouterIp(connectedNetwork.getNetwork().getGateway());
            }
            if (CollectionUtils.isNotEmpty(gatewayIpPool)) {
                existIps.addAll(gatewayIpPool);
            }
            List<String> ipPool = IpUtils.newRandomIps(network.getCidr(), requireCount, new ArrayList<>(existIps), element.getAllocationPools(), BoolUtils.bool(element.getDhcp()));
            ipPool.addAll(gatewayIpPool);
            if (!portIps.contains(network.getGateway())) {
                // 网关未分配 则放入地址池的最后等待分配
                if (!Boolean.TRUE.equals(network.getGatewayOff())) {
                    ipPool.add(network.getGateway());
                }
            }
            network.getExcloudIps().addAll(existIps);
            network.getExcloudIps().addAll(ipPool);
            network.setIpPool(ipPool);
            if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH) {
                connectedNetwork.getFromPorts().forEach(port -> port.setIp(network.getGateway()));
            }
        });
    }

    /**
     * 初始化虚拟网络端口
     * @param cloudElement 网元节点
     */
    private void initiateVirtualPort(CloudPluginSceneElementBO cloudElement) {
        List<ConnectedNetworkBO> connectedNetworks = cloudElement.getDeriveNetworks();
        for (ConnectedNetworkBO connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            // 先处理连接的端口
            List<SceneElementPortBO> toPorts = connectedNetwork.getToPorts();
            for (SceneElementPortBO port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                CloudPluginSceneElementBO pluginElement = getPluginHandler().getElement(port.getElement().getId());
                if (ElementTypeUtils.isVirtualGateway(pluginElement)) {
                    synchronized (ipPool) {
                        port.setIp(ipPool.remove(ipPool.size() - 1));
                    }
                } else {
                    synchronized (ipPool) {
                        port.setIp(ipPool.remove(0));
                    }
                }
            }

            // 处理自己的端口
            List<ConnectedNetworkBO.ConnectPort> connectPorts = connectedNetwork.connectPorts;
            String fromIp = null;
            boolean noConnect = true;
            for (ConnectedNetworkBO.ConnectPort connectPort : connectPorts) {
                SceneElementPortBO port = connectPort.from;
                if (connectPort.to != null) {
                    noConnect = false;
                }
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                } else {
                    if (fromIp == null) {
                        CloudPluginSceneElementBO pluginElement = getPluginHandler().getElement(port.getElement().getId());
                        if (ElementTypeUtils.isVirtualGateway(pluginElement) || this.isCoreRouter(cloudElement)) {
                            synchronized (ipPool) {
                                if (fromIp == null) {
                                    fromIp = ipPool.remove(ipPool.size() - 1);
                                }
                            }
                        } else {
                            synchronized (ipPool) {
                                if (fromIp == null) {
                                    fromIp = ipPool.remove(0);
                                }
                            }
                        }
                    }
                    if (connectPort.to != null) {
                        port.setIp(fromIp);
                    }
                }
            }
            if (noConnect) {
                // 网络没有任何连线
                if (ElementTypeUtils.isVirtualGateway(cloudElement)) {
                    // 路由或网络处理多个口
                    for (SceneElementPortBO port : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isEmpty(port.getIp())) {
                            port.setIp(fromIp);
                        }
                    }
                } else {
                    // 其他处理一个口即可
                    for (SceneElementPortBO port : connectedNetwork.getFromPorts()) {
                        if (StringUtils.isEmpty(port.getIp())) {
                            port.setIp(fromIp);
                            break;
                        }
                    }
                }
            }
            for (SceneElementPortBO port : connectedNetwork.getFromPorts()) {
                if (StringUtils.isEmpty(port.getIp())) {
                    port.setUsing(false);
                }
            }
        }
    }

    /**
     * initiateVirtualToPort
     *
     * @param cloudElement
     */
    private void initiateVirtualToPort(CloudPluginSceneElementBO cloudElement) {
        List<ConnectedNetworkBO> connectedNetworks = cloudElement.getDeriveNetworks();
        for (ConnectedNetworkBO connectedNetwork : connectedNetworks) {
            List<String> ipPool = connectedNetwork.network.getIpPool();
            // 先处理连接的端口
            List<SceneElementPortBO> toPorts = connectedNetwork.getToPorts();
            for (SceneElementPortBO port : toPorts) {
                if (StringUtils.isNotEmpty(port.getIp())) {
                    continue;
                }
                CloudPluginSceneElementBO pluginElement = getPluginHandler().getElement(port.getElement().getId());
                if (ElementTypeUtils.isVirtualGateway(pluginElement)) {
                    synchronized (ipPool) {
                        if (ipPool.size() > 0) {
                            port.setIp(ipPool.remove(ipPool.size() - 1));
                        } else {
                            log.error("failed to get ip from ip pool , port eid:{}", port.getEid());
                        }
                    }
                } else {
                    synchronized (ipPool) {
                        if (ipPool.size() > 0) {
                            port.setIp(ipPool.remove(0));
                        } else {
                            log.error("failed to get ip from ip pool , port eid:{}", port.getEid());
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化网络端口
     * @param cloudElement 网元节点
     */
    private void initiateElementPorts(CloudPluginSceneElementBO cloudElement) {
        List<SceneElementPortBO> srcPorts = cloudElement.getSrcElement().getPorts();
        if (srcPorts == null) {
            return;
        }

        Integer virtualType = cloudElement.getVirtualType();
        boolean isVirtualElement = ElementTypeUtils.isVirtualNode(cloudElement);

        for (SceneElementPortBO srcPort : srcPorts) {
            CloudPluginSceneElementPortBO cloudPort = getPluginHandler().getPort(srcPort.getId());
            if (cloudPort != null) {
                // 初始化accessModeId
                List<CloudAccessMode> cloudAccessModes = cloudPort.getAccessModes();
                if (cloudAccessModes != null) {
                    cloudAccessModes.forEach(cloudAccessMode -> {
                        if (StringUtils.isEmpty(cloudAccessMode.getId())) {
                            cloudAccessMode.setId(TextUtils.uuid());
                        }
                    });
                }
            }
        }

        if (isVirtualElement) {
            initiateVirtualPort(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
            // 实体节点的网络需要初始化对端端口
            initiateVirtualToPort(cloudElement);
        } else if (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)) {
            // 30s底层不能自动分ip，所以这里特殊处理，外部网络连接的端口也由中台分一个ip，否则接口报错
            // 二层网络节点的网络、而且非外部网络需要初始化对端端口
//            if (cloudElement.getRole() != null && cloudElement.getRole() != BaseCloudPluginSceneElementModel.Role.VIRTUAL_EXTERNAL_NETWORK) {
//                initiateVirtualToPort(cloudElement);
//            }
            initiateVirtualToPort(cloudElement);
        }
    }

    /**
     * loadProperties2Network
     *
     * @param network
     * @param element
     */
    private void loadProperties2Network(CloudPluginSceneElementNetworkBO network, CloudPluginSceneElementBO element) {
        network.setAllocationPools(element.getAllocationPools());
        network.setDnsNameservers(element.getDnsNameservers());
        network.setGateway(element.getGateway());
        network.setGatewayOff(element.getGatewayOff());
        network.setCidr(element.getCidr());
        network.setHostRoutes(element.getHostRoutes());
        network.setVlan(element.getVlan());
        network.setVlanId(element.getVlanId());
        network.setDhcp(element.getDhcp());
        network.setNetMode(element.getNetMode());
    }

    /**
     * afterOrderElements
     *
     * @param allElements
     */
    protected void afterOrderElements(List<CloudPluginSceneElementBO> allElements) {

    }

    /**
     * reallocateIpPorts
     *
     * @param networkElements
     */
    private void reallocateIpPorts(List<CloudPluginSceneElementBO> networkElements) {
        for (CloudPluginSceneElementBO element : networkElements) {
            for (ConnectedNetworkBO network : element.getDeriveNetworks()) {
                try {
                    if (Boolean.TRUE.equals(network.getNetwork().getExternal())) {
                        continue;
                    }
                    List<SceneElementPortBO> ports = new ArrayList<>();
                    ports.addAll(network.getFromPorts());
                    ports.addAll(network.getToPorts());
                    if (CollectionUtils.isEmpty(ports)) {
                        continue;
                    }
                    List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getNetwork().getGateway(), network.getNetwork().getAllocationPools(),network.getNetwork().getCidr());
                    if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                        possibleDhcpIps = new ArrayList<>();
                    }
                    Map<String, String> changeIp = new HashMap<>();
                    List<String> excludeIps = new ArrayList<>(network.getNetwork().getExcloudIps());
                    excludeIps.addAll(possibleDhcpIps);
                    for (SceneElementPortBO port : ports) {
                        if (StringUtils.isNotEmpty(port.getIp()) && possibleDhcpIps.contains(port.getIp())) {
                            // 和dhcp冲突 需要重新分配
                            String newIp = changeIp.get(port.getIp());
                            if (StringUtils.isEmpty(newIp)) {
                                CloudPluginSceneElementBO portElement = getPluginHandler().getElement(port.getElement().getId());
                                if (portElement != null) {
                                    newIp = this.getNewReallocateIp(portElement, network, excludeIps);
                                }
                            }
                            if (StringUtils.isNotEmpty(newIp)) {
                                log.info("Port {} reallocate ip {} to {}", port.getId(), port.getIp(), newIp);
                                changeIp.put(port.getIp(), newIp);
                                excludeIps.add(newIp);
                                port.setIp(newIp);
                            }
                        }
                    }
                    if (ElementTypeUtils.isCoreRouter(element)) {
                        // 核心路由的网络其网关为核心路由的ip
                        if (CollectionUtils.isEmpty(element.getDeriveNetworks())) {
                            continue;
                        }
                        element.getDeriveNetworks().forEach(net -> {
                            if (CollectionUtils.isNotEmpty(net.getFromPorts())) {
                                String gatewayIp = null;
                                for (SceneElementPortBO p : net.getFromPorts()) {
                                    if (StringUtils.isNotEmpty(p.getIp())) {
                                        gatewayIp = p.getIp();
                                        break;
                                    }
                                }
                                if (gatewayIp != null) {
                                    net.network.setGateway(gatewayIp);
                                }
                            }
                        });
                    }
                } catch (MpRuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("reallocate ip error , network is {} ", JsonCommon.stringify(network), e);
                }
            }
        }
    }

    /**
     * portCheck
     *
     * @param networkElements
     */
    private void portCheck(List<CloudPluginSceneElementBO> networkElements) {
        for (CloudPluginSceneElementBO element : networkElements) {
            for (ConnectedNetworkBO network : element.getDeriveNetworks()) {
                try {
                    if (Boolean.TRUE.equals(network.getNetwork().getExternal())) {
                        continue;
                    }
                    List<SceneElementPortBO> ports = new ArrayList<>();
                    ports.addAll(network.getFromPorts());
                    ports.addAll(network.getToPorts());
                    if (CollectionUtils.isEmpty(ports)) {
                        continue;
                    }
                    List<String> possibleDhcpIps = this.getPossibleDhcpIps(network.getNetwork().getGateway(), network.getNetwork().getAllocationPools(), network.getNetwork().getCidr());
                    if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getDhcpIpAllocate())) {
                        possibleDhcpIps = new ArrayList<>();
                    }
                    for (SceneElementPortBO port : ports) {
                        if (StringUtils.isNotEmpty(port.getIp()) && possibleDhcpIps.contains(port.getIp())) {
                            if (getHandlerUtils().isManualConfigIpPort(port)) {
                                // 如果人工配置的端口ip和dhcp出现冲突,可根据配置判断是否要进行端口重分配
                                String nodeName = port.getElement().getName();
                                String ip = port.getIp();
                                String conflictIps = StringUtils.join(new HashSet<>(possibleDhcpIps), ",");
                                throw new MpRuntimeException(CloudErrorCode.IP_CONFLICT.format(nodeName, ip, conflictIps));
                            }
                        }
                    }
                } catch (MpRuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    log.error("reallocate ip error , network is {} ", JsonCommon.stringify(network), e);
                }
            }
        }
    }

    /**
     * getNewReallocateIp
     *
     * @param element
     * @param network
     * @param excloudIps
     * @return
     */
    private String getNewReallocateIp(CloudPluginSceneElementBO element, ConnectedNetworkBO network, List<String> excloudIps) {
        // 重新选ip
        if (element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH
                || element.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER) {
            List<String> newIps = IpUtils.newAllocateDefaultRouterIps(network.getNetwork().getCidr(), 1,
                    excloudIps,
                    network.getNetwork().getAllocationPools());
            if (CollectionUtils.isNotEmpty(newIps)) {
                return newIps.get(0);
            }
        } else {
            List<String> newIps = IpUtils.newRandomIps(network.getNetwork().getCidr(), 1,
                    excloudIps,
                    network.getNetwork().getAllocationPools(),
                    Boolean.TRUE.equals(network.getNetwork().getDhcp()));
            if (CollectionUtils.isNotEmpty(newIps)) {
                return newIps.get(0);
            }
        }
        return null;
    }

    /**
     * getPossibleDhcpIps
     *
     * @param gateway
     * @param allocationPools
     * @param cidr
     * @return
     */
    private List<String> getPossibleDhcpIps(String gateway, List<AllocationPool> allocationPools, String cidr) {
        List<String> possibleDhcpIps = this.getPossibleDhcpIps(gateway, cidr);
        if (CollectionUtils.isNotEmpty(allocationPools)) {
            allocationPools.forEach(p -> {
                if (StringUtils.isNotEmpty(p.getStart())) {
                    possibleDhcpIps.add(p.getStart());
                }
            });
        }
        return possibleDhcpIps;
    }

    /**
     * getPossibleDhcpIps
     *
     * @param gateway
     * @param cidr
     * @return
     */
    private List<String> getPossibleDhcpIps(String gateway, String cidr) {
        Set<String> possibleDhcpIps = new HashSet<>();
        if (StringUtils.isNotEmpty(cidr)) {
            String firstIp = IpUtils.beginIp(cidr);
            possibleDhcpIps.add(IpUtils.nextIp(firstIp));
        }
        if (gateway.endsWith(".254")) {
            int index = StringUtils.lastIndexOf(gateway, ".");
            String prefix = StringUtils.substring(gateway, 0, index);
            String ip = StringUtils.joinWith(".", prefix, "1");
            if (StringUtils.isNotEmpty(cidr) && IpUtils.newIsInCidr(ip, cidr)) {
                possibleDhcpIps.add(ip);
            }
        }
        possibleDhcpIps.add(IpUtils.nextIp(gateway));
        return new ArrayList<>(possibleDhcpIps);
    }

    /**
     * isInCidr
     *
     * @param cidr
     * @param ip
     * @return boolean
     */
    private boolean isInCidr(String cidr, String ip) {
        if (StringUtils.isEmpty(cidr) || StringUtils.isEmpty(ip)) {
            return false;
        }
        if (!cidr.contains(Ipv4Util.IP_MASK_SPLIT_MARK) && !cidr.contains(Ipv4Util.IP_SPLIT_MARK)) {
            return false;
        }
        return IpUtils.newIsInCidr(ip, cidr);
    }

    /**
     * setFwdnsInterfaceIps
     *
     * @param cloudElements
     */
    private void setFwdnsInterfaceIps(List<CloudPluginSceneElementBO> cloudElements) {
        for (CloudPluginSceneElementBO element : cloudElements) {
            Integer virtualType = element.getVirtualType();
            if (virtualType == null) {
                continue;
            }
            if (virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE) {
                List<ConnectedNetworkBO> allnets = new ArrayList<>();
                allnets.addAll(element.getAccessNetworks());
                allnets.addAll(element.getDeriveNetworks());
                for (ConnectedNetworkBO net : allnets) {
                    if (Boolean.TRUE.equals(net.getNetwork().getExternal()) || StringUtils.isEmpty(net.getNetwork().getCidr())) {
                        continue;
                    }
                    List<String> randomIps = IpUtils.newRandomIps(net.getNetwork().getCidr(), 1, new ArrayList<>(net.getNetwork().getExcloudIps()), new ArrayList<>(), true);
                    if (CollectionUtils.isNotEmpty(randomIps)) {
                        // 如果ip不够分了 只能走云平台默认逻辑
                        String distributedPortMappingIp = randomIps.get(0);
                        if (CollectionUtils.isNotEmpty(net.getFromPorts())) {
                            SceneElementPortBO srcPort = net.getFromPorts().get(0);
                            CloudPluginSceneElementPortBO cloudPort = getPluginHandler().getPort(srcPort.getId());
                            if (cloudPort != null) {
                                if (CollectionUtils.isNotEmpty(cloudPort.getAccessModes())) {
                                    for (CloudAccessMode accessMode : cloudPort.getAccessModes()) {
                                        // 除console协议的其他接入协议中,有需要端口映射的,那么需要分配一个分布式的端口映射ip
                                        if (!AccessMode.Protocol.CONSOLE.equals(accessMode.getProtocol()) && Boolean.TRUE.equals(accessMode.getProxy())) {
                                            cloudPort.setDistributedPortMappingIp(distributedPortMappingIp);
                                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), distributedPortMappingIp);
                                            net.getNetwork().getExcloudIps().add(distributedPortMappingIp);
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            getHandlerUtils().addFwdnsInterfaceIp(element.getSrcElement().getId(), net.getNetwork().getId(), distributedPortMappingIp);
                            net.getNetwork().getExcloudIps().add(distributedPortMappingIp);
                        }
                    }
                }
            }
        }
    }

    /**
     * loadInitaitePortsTasks
     *
     * @param cloudElements
     * @param initiateRouterElementPortsTasks
     * @param initiateOtherElementPortsTasks
     */
    private void loadInitaitePortsTasks(List<CloudPluginSceneElementBO> cloudElements, List<Runnable> initiateRouterElementPortsTasks, List<Runnable> initiateOtherElementPortsTasks) {
        for (CloudPluginSceneElementBO cloudElement : cloudElements) {
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType != null
                    && (virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_ROUTER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.CONTAINER)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_SWITCH)
                    || virtualType.equals(BaseCloudPluginSceneElementModel.VirtualType.DYNAMIPS))) {
                Runnable task = () -> initiateElementPortsProcess(cloudElement);
                Integer type = cloudElement.getSrcElement().getType();
                // 防火墙也看作路由
                if (type != null && (type.equals(BaseSceneElementModel.Type.ROUTER)
                        || type.equals(BaseSceneElementModel.Type.FIREWALL))) {
                    initiateRouterElementPortsTasks.add(task);
                } else {
                    initiateOtherElementPortsTasks.add(task);
                }
            } else if (Boolean.TRUE.equals(cloudElement.getSrcElement().getIsAuxiliary())) {
                // 辅助节点也需要处理下端口
                initiateOtherElementPortsTasks.add(() -> initiateVirtualPort(cloudElement));
            }
        }
    }

    /**
     * getOrderedAllCloudElements
     *
     * @return
     */
    private List<CloudPluginSceneElementBO> getOrderedAllCloudElements() {
        List<CloudPluginSceneElementBO> allCloudElements = new ArrayList<>();
        for (CloudPluginSceneElementBO cloudPluginSceneElementBO : new ArrayList<>(getPluginHandler().getElementMap().values())) {
            allCloudElements.add(cloudPluginSceneElementBO);
        }

        // 使用节点name排序一下,这样节点类型相同时可以有个相对固定的初始化顺序,节点名称相同的情况暂不处理
        // 使初始化结果不受数据库数据顺序的影响
        Collections.sort(allCloudElements, (a, b) -> {
            if (a.getSrcElement().getName() == null || b.getSrcElement().getName() == null) {
                return 0;
            }
            return a.getSrcElement().getName().compareTo(b.getSrcElement().getName());
        });

        allCloudElements.forEach(cloudElement -> this.initCoreType(cloudElement));
        // 根据节点的类型排序,这个顺序直接影响组网的结果
        Collections.sort(allCloudElements, Comparator.comparing(CloudPluginSceneElementBO::getCoreType));
        return allCloudElements;
    }

    /**
     * markIpSetedPorts
     */
    private void markIpSetedPorts() {
        if (getPluginHandler().getElementPortMap() != null) {
            getPluginHandler().getElementPortMap().values().forEach(p -> {
                if (p.getSrcPort() != null && StringUtils.isNotEmpty(p.getSrcPort().getIp())) {
                    getHandlerUtils().addManualConfigIpPort(p.getSrcPort());
                }
            });
        }
    }
}
