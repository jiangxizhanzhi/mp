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
     * @return
     */
    public static ConcurrentHashMap<String, Integer> getCidrToContainerMap(
            CloudScenePluginHandler pluginHandler,
            List<CloudPluginSceneElementBO> allElements,
            CloudPluginSceneElementBO cloudElement,
            HashMap<String, List<VirtualNic>> virtualNicsMap) {
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
            Map<String, Integer> cidrContainerMap = cidrPools.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toMap(cidr -> cidr, cidr -> 0));
            if (cidrContainerMap.size() == 0) {
                String defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
                cidrPools.add(defultCidr);
                cidrContainerMap.put(defultCidr, 0);
            }
            addFirewallDefaultCIDRs(isFireWall, cidrPools, cidrContainerMap, cidrListByVEnt);
            return new ConcurrentHashMap<>(cidrContainerMap);
        }
        ;

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
                    .filter(id -> BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(pluginHandler.getElement(id).getImageType()))
                    .findFirst()
                    .isPresent();
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
            Map<String, Integer> cidrContainerMap = cidrPools.stream().collect(Collectors.toMap(cidr -> cidr, cidr -> 0));
//          处理子网容器委托
            cidrContainerMap.putAll(containerCidrs.stream().collect(Collectors.toMap(cidr -> cidr, cidr -> 1)));
            cidrPools.addAll(containerCidrs);

            addFirewallDefaultCIDRs(isFireWall, cidrPools, cidrContainerMap, cidrListByVEnt);


            cloudElement.setCidrPools(cidrPools);
            return new ConcurrentHashMap<>(cidrContainerMap);
        }

        List<String> nodeIds = Arrays.asList(cloudElement.getSrcElement().getId());

        autoAssignVirtualNicIps(virtualNicsMap, elementIpMap, nodeIds, defultCidr, ips);

        ConcurrentHashMap<String, Integer> cidrContainerMap = new ConcurrentHashMap<>();
//        未连VNet节点直接判断节点类型
        if (BaseCloudPluginSceneElementModel.ImageType.CONTAINER_REGISTRY.equals(cloudElement.getImageType())) {
            for (VirtualNic virtualNic : virtualNicsMap.get(cloudElement.getSrcElement().getId())) {
                cidrContainerMap.put(IpUtils.ip2Cidr24(virtualNic.getFixedIp()), 1);
            }
        } else if (BaseCloudPluginSceneElementModel.ImageType.LAAS_VM.equals(cloudElement.getImageType())) {
            for (VirtualNic virtualNic : virtualNicsMap.get(cloudElement.getSrcElement().getId())) {
                cidrContainerMap.put(IpUtils.ip2Cidr24(virtualNic.getFixedIp()), 0);
            }
        }
        return cidrContainerMap;
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
                                                Map<String, Integer> cidrContainerMap, Set<String> cidrListByVEnt) {

        if (isFireWall) {
//                防火墙需要两个子网 其中一个子网掩码/26
            String defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
            cidrPools.add(defultCidr);
            cidrListByVEnt.add(defultCidr);
            cidrContainerMap.put(defultCidr, 2);


            defultCidr = IpUtils.getNextDefaultCIDR(cidrListByVEnt);
            defultCidr = defultCidr.substring(0, defultCidr.length() - 1) + "6";
            cidrPools.add(defultCidr);
            cidrListByVEnt.add(defultCidr);
            cidrContainerMap.put(defultCidr, 3);

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

}
