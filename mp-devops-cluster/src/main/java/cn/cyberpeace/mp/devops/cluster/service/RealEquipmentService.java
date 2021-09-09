package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

/**
 * 实装管理接口
 */
public interface RealEquipmentService extends BaseCloudPlatformService {

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    DeviceCloudPlatformModel page(String getParams);

    /**
     * 新增
     * @param device
     * @return
     */
    Device add(Device device);

    /**
     * 更新
     * @param uuid
     * @param device
     * @return
     */
    Device update(String uuid,Device device);

    SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp);

    SnmpCloudPlatform snmpForCpu(SnmpParams snmp);

    SnmpCloudPlatform snmpForMem(SnmpParams snmp);

    SnmpCloudPlatform snmpForDisk(SnmpParams snmp);
}
