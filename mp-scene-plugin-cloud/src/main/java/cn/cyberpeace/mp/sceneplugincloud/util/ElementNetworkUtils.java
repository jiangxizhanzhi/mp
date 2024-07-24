package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 用于节点网络操作
 */
public class ElementNetworkUtils {

    /**
     * 筛选出所有cidr是否连接容器
     *
     * @param allElements
     * @param cloudElement
     * @param virtualNicsMap
     * @param elementCidrContainerMap
     * @return
     */
    public static ConcurrentHashMap<String, CidrType> getCidrToContainerMap(
            CloudScenePluginHandler pluginHandler,
            List<CloudPluginSceneElementBO> allElements,
            CloudPluginSceneElementBO cloudElement,
            HashMap<String, List<VirtualNic>> virtualNicsMap, Boolean isManage) {
        //        判断是否有防火墙节点
        boolean isFireWall = cloudElement.getDeriveNetworks().stream()
                .flatMap(connectedNetworkBO -> connectedNetworkBO.getToPorts().stream())
                .map(SceneElementPortBO::getElement)
                .filter(element -> BaseCloudPluginSceneElementModel.ImageType.FIRE_WALL.equals(pluginHandler.getElement(element.getId()).getImageType()))
                .findAny()
                .isPresent();

        Set<String> cidrListByVEnt = allElements
                .stream()
                .map(CloudPluginSceneElementBO::getCidrPools)
                .flatMap(cidrPools -> cidrPools.stream())
                .collect(Collectors.toSet());

        if (virtualNicsMap.size() == 0) {
            List<String> cidrPools = cloudElement.getCidrPools();
            Map<String, CidrType> cidrContainerMap =
                    cidrPools.stream()
                            .filter(StringUtils::isNotEmpty)
                            .collect(Collectors.toMap(cidr -> cidr, cidr -> CidrType.VM));
            if (cidrContainerMap.size() == 0) {
                addCidrToResources(cidrListByVEnt, cidrPools, cidrContainerMap, CidrType.VM);
            }
            addFirewallDefaultCIDRs(isFireWall, cidrPools, cidrContainerMap, cidrListByVEnt);

            addManagementCidrsToResources(cidrListByVEnt, cidrPools, cidrContainerMap,isManage);
            return new ConcurrentHashMap<>(cidrContainerMap);
        }

        Map<String, List<String>> elementIpMap = pluginHandler.getScene().getSrcScene().getElementIpMap();

//        查目前所有cidr（包括ip所属cidr）
        List<String> cidrListByIp = virtualNicsMap
                .values()
                .stream()
                .flatMap(virtualNics -> virtualNics.stream())
                .map(VirtualNic::getFixedIp)
                .filter(Objects::nonNull)
                .map(IpUtils::ip2Cidr24)
                .collect(Collectors.toList());
        cidrListByVEnt.addAll(cidrListByIp);
        String defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
        cidrListByVEnt.add(defultCidr);
        HashSet<String> ips = new HashSet<>();

//        VNet创建所有子网
        if (BaseCloudPluginSceneElementModel.ImageType.VNET.equals(cloudElement.getImageType())) {
            List<String> nodeIds = cloudElement.getDeriveNetworks().stream()
                    .flatMap(connectedNetworkBO -> connectedNetworkBO.getToPorts().stream())
                    .map(SceneElementPortBO::getElement)
                    .map(SceneElementBO::getId)
                    .distinct()
                    .collect(Collectors.toList());

            boolean present = nodeIds.stream()
                    .anyMatch(id -> BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(pluginHandler.getElement(id).getImageType()));
            String defultContainerCidr = null;
            ArrayList<String> containerCidrs = new ArrayList<>();
            if (present) {
                defultContainerCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
                cidrListByVEnt.add(defultContainerCidr);
                containerCidrs.add(defultContainerCidr);
            }

            autoAssignVirtualNicIps(virtualNicsMap, elementIpMap, nodeIds, defultCidr, ips);

            List<String> cidrPools = cloudElement.getCidrPools();
//            先处理VM cidr
            Set<String> vmFixedIps = nodeIds.stream()
                    .filter(id -> BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(pluginHandler.getElement(id).getImageType()))
                    .map(virtualNicsMap::get)
                    .flatMap(List::stream)
                    .map(VirtualNic::getFixedIp)
                    .collect(Collectors.toSet());

//                                更新cidr
            List<String> cidrList = vmFixedIps.stream().map(ip -> IpUtils.ip2Cidr24(ip)).collect(Collectors.toList());
            cidrPools.addAll(cidrList);


            for (String id : nodeIds) {
                if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(pluginHandler.getElement(id).getImageType())) {
                    List<VirtualNic> virtualNics = virtualNicsMap.get(id);
                    for (VirtualNic virtualNic : virtualNics) {
                        String ip = IpUtils.generateRandomIpNotInList(defultContainerCidr, ips);
                        ips.add(ip);
                        virtualNic.setFixedIp(ip);
                    }
                    virtualNicsMap.put(id, virtualNics);

                    pluginHandler.getElement(id).setIsPublicIp(false);
                }
            }

            cidrPools = cidrPools.stream().filter(cidr -> StringUtils.isNotEmpty(cidr)).distinct().collect(Collectors.toList());
            Map<String, CidrType> cidrContainerMap = cidrPools.stream().collect(Collectors.toMap(cidr -> cidr, cidr -> CidrType.VM));
//          处理子网容器委托
            cidrContainerMap.putAll(containerCidrs.stream().collect(Collectors.toMap(cidr -> cidr, cidr -> CidrType.CONTAINER)));
            cidrPools.addAll(containerCidrs);

            addFirewallDefaultCIDRs(isFireWall, cidrPools, cidrContainerMap, cidrListByVEnt);
            addManagementCidrsToResources(cidrListByVEnt, cidrPools, cidrContainerMap, isManage);

            cloudElement.setCidrPools(cidrPools);
            return new ConcurrentHashMap<>(cidrContainerMap);
        }

        List<String> nodeIds = Arrays.asList(cloudElement.getSrcElement().getId());

        autoAssignVirtualNicIps(virtualNicsMap, elementIpMap, nodeIds, defultCidr, ips);

        ConcurrentHashMap<String, CidrType> cidrContainerMap = new ConcurrentHashMap<>();
//        未连VNet节点直接判断节点类型
        if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(cloudElement.getImageType())) {
            for (VirtualNic virtualNic : virtualNicsMap.get(cloudElement.getSrcElement().getId())) {
                cidrContainerMap.put(IpUtils.ip2Cidr24(virtualNic.getFixedIp()), CidrType.VM);
            }
        } else if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(cloudElement.getImageType())) {
            for (VirtualNic virtualNic : virtualNicsMap.get(cloudElement.getSrcElement().getId())) {
                cidrContainerMap.put(IpUtils.ip2Cidr24(virtualNic.getFixedIp()), CidrType.CONTAINER);
            }
        }
        addManagementCidrsToResources(cidrListByVEnt, null, cidrContainerMap, isManage);
        return cidrContainerMap;
    }

    /**
     * 将指定的CIDR列表作为管理类型CIDR资源添加到CIDR池和容器映射中。
     *
     * @param cidrListByVEnt   包含待添加的CIDR条目的集合。
     * @param cidrPools        CIDR池列表，将从cidrListByVEnt中添加CIDR条目。
     * @param cidrContainerMap CIDR容器映射，用于跟踪已分配的CIDR资源及其类型。
     * @param isManage 是否需要建管理网
     */
    private static void addManagementCidrsToResources(Set<String> cidrListByVEnt, List<String> cidrPools,
                                                      Map<String, CidrType> cidrContainerMap, Boolean isManage) {
        if(isManage){
            addCidrToResources(cidrListByVEnt, cidrPools, cidrContainerMap, CidrType.MANAGEMENT);
        }
    }

    /**
     * 提取CIDR信息并添加到相应的集合和映射中。
     * 此方法用于处理CIDR集合，通过获取下一个默认CIDR，将其添加到CIDR池和CIDR列表中，并在映射中记录其类型。
     *
     * @param cidrListByVEnt   CIDR列表，用于存储和管理CIDR信息。
     * @param cidrPools        CIDR池，用于存储可用的CIDR。
     * @param cidrContainerMap CIDR映射，用于映射CIDR字符串到其对应的CIDR类型。
     * @param cidrType         CIDR的类型，用于标记和区分不同的CIDR。
     */
    private static void addCidrToResources(Set<String> cidrListByVEnt, List<String> cidrPools, Map<String, CidrType> cidrContainerMap, CidrType cidrType) {
        String defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
        if (null != cidrPools) {
            cidrPools.add(defultCidr);
        }
        cidrListByVEnt.add(defultCidr);
        cidrContainerMap.put(defultCidr, cidrType);
    }

    /**
     * 在防火墙配置情况下，生成并添加两个默认的CIDR子网。
     * 第一个子网使用默认的CIDR池获取，第二个子网基于第一个进行修改。
     * 更新CIDR池列表和CIDR容器映射。
     *
     * @param isFireWall       是否为防火墙配置。
     * @param cidrPools        CIDR池列表，用于存储生成的CIDR子网。
     * @param cidrContainerMap CIDR容器映射，用于存储CIDR及其对应的标识。
     * @param cidrListByVEnt
     */
    private static void addFirewallDefaultCIDRs(boolean isFireWall, List<String> cidrPools,
                                                Map<String, CidrType> cidrContainerMap, Set<String> cidrListByVEnt) {

        if (isFireWall) {
//                防火墙需要两个子网 其中一个子网掩码/26
            addCidrToResources(cidrListByVEnt, cidrPools, cidrContainerMap, CidrType.FIRE_WALL_MANAGEMENT);
            String defultCidr;


            defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
            defultCidr = defultCidr.substring(0, defultCidr.length() - 1) + "6";
            cidrPools.add(defultCidr);
            cidrListByVEnt.add(defultCidr);
            cidrContainerMap.put(defultCidr, CidrType.FIRE_WALL);

        }
    }

    /**
     * 为指定节点的虚拟网卡自动分配默认子网内的IP地址。
     * <p>
     * 此方法遍历虚拟网卡映射表，针对特定节点列表中的节点，如果其虚拟网卡的固定IP不在元素IP映射表中或元素IP映射表不存在，
     * 则生成一个新的、未被使用的IP地址，并将其设置为虚拟网卡的固定IP。
     *
     * @param virtualNicsMap 节点ID与虚拟网卡列表的映射关系。
     * @param elementIpMap   节点ID与已分配IP地址列表的映射关系。
     * @param nodeIds        需要自动分配IP地址的节点ID列表。
     * @param defultCidr     默认的子网CIDR，用于生成新的IP地址。
     * @param ips            已分配的IP地址集合，用于记录新分配的IP地址。
     */
    private static void autoAssignVirtualNicIps(HashMap<String, List<VirtualNic>> virtualNicsMap, Map<String, List<String>> elementIpMap, List<String> nodeIds, String defultCidr, HashSet<String> ips) {
        virtualNicsMap.forEach((id, virtualNics) -> {
            List<String> elementIps = elementIpMap.get(id);
            virtualNics.forEach(virtualNic -> {
                String fixedIp = virtualNic.getFixedIp();
//                    自动分配ip需更换为默认子网内ip
                if (nodeIds.contains(id) && (elementIps == null || !elementIps.contains(fixedIp))) {
                    String ip = IpUtils.generateRandomIpNotInList(defultCidr, ips);
                    ips.add(ip);
                    virtualNic.setFixedIp(ip);
                }
            });
        });
    }

    private static void checkIfElementIsContainerById(List<CloudPluginSceneElementBO> allElements, AtomicReference<Boolean> isContainer, String id) {
        // 根据id 从allElements取出对应的element
        CloudPluginSceneElementBO cloudElement1 = allElements
                .stream()
                .filter(cloudElement2 -> cloudElement2.getSrcElement().getId().equals(id))
                .findFirst()
                .get();
        if (null != cloudElement1) {
            if ((BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(cloudElement1.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == cloudElement1.getVirtualType())
                    || (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(cloudElement1.getImageType()) && BaseCloudPluginSceneElementModel.VirtualType.CONTAINER == cloudElement1.getVirtualType())) {
                isContainer.set(true);
            }
        }
    }

    //    创建枚举类区分子网用途
    public enum CidrType {
        /**
         * 虚拟机
         */
        VM,
        /**
         * 容器
         */
        CONTAINER,
        /**
         * 虚拟防火墙
         */
        FIRE_WALL,
        /**
         * 虚拟防火墙管理网
         */
        FIRE_WALL_MANAGEMENT,
        /**
         * 管理网
         */
        MANAGEMENT;
    }


}
