package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.*;

/**
 * 实装管理接口
 * @author wanggang
 */
@Service
@Slf4j
public class RealEquipmentService extends BaseOpenStackServiceImpl {

    private static final String ENTITY = "/entity";

    public RealEquipmentService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public DeviceCloudPlatformModel page(String getParams){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    /**
     * 新增
     * @param device
     * @return
     */
    public Device add(Device device){
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists", JsonUtils.toUnderlineJSONString(device),Device.class);
    }

    /**
     * 更新
     * @param uuid
     * @param device
     * @return
     */
    public Device update(String uuid,Device device){
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
    }

    /**
     * 接口
     * @param snmp
     * @return
     */
    public SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp){
        snmp.setDataType("interfaces");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpInterfaceResourceCloudPlatform.class);
    }


    /**
     * CPU
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForCpu(SnmpParams snmp){
        snmp.setDataType("cpu");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 内存
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForMem(SnmpParams snmp){
        snmp.setDataType("mem");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 磁盘
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForDisk(SnmpParams snmp){
        snmp.setDataType("disk");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 根据交换机名称查询
     * @param switchName
     * @return
     */
    public RealEquipmentBySwitchNameCloudPlatform findBySwitchName(String switchName){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/switch_map?switch_name=" + switchName,RealEquipmentBySwitchNameCloudPlatform.class);
    }

    /**
     * 根据串口查询
     * @param serialPort
     * @return
     */
    public RealEquipmentBySerialPortCloudPlatform findBySerialPort(String serialPort){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/ccm_map?ccm_server_name=" + serialPort,RealEquipmentBySerialPortCloudPlatform.class);
    }

    /**
     * 获取登录链接
     * @param deviceUuid
     * @return
     */
    public RealEquipmentConnectionCloudPlatform connection(String deviceUuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/connection?device_uuid=" + deviceUuid,RealEquipmentConnectionCloudPlatform.class);
    }
}
