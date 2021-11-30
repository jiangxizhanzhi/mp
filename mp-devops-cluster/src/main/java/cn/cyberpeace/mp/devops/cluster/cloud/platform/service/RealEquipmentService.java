package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

/**
 * 实装管理接口
 */
public interface RealEquipmentService extends BaseCloudPlatformService {

    String ENTITY = "/entity";

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

    /**
     * 接口
     * @param snmp
     * @return
     */
    SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp);

    /**
     * CPU
     * @param snmp
     * @return
     */
    SnmpCloudPlatform snmpForCpu(SnmpParams snmp);

    /**
     * 内存
     * @param snmp
     * @return
     */
    SnmpCloudPlatform snmpForMem(SnmpParams snmp);

    /**
     * 磁盘
     * @param snmp
     * @return
     */
    SnmpCloudPlatform snmpForDisk(SnmpParams snmp);

    /**
     * 根据交换机名称查询
     * @param switchName
     * @return
     */
    RealEquipmentBySwitchNameCloudPlatform findBySwitchName(String switchName);

    /**
     * 根据串口查询
     * @param serialPort
     * @return
     */
    RealEquipmentBySerialPortCloudPlatform findBySerialPort(String serialPort);

    /**
     * 获取登录链接
     * @param deviceUuid
     * @return
     */
    RealEquipmentConnectionCloudPlatform connection(String deviceUuid);
}
