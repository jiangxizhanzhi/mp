//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RealEquipmentService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.*;
//import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
//
///**
// * 实装管理接口
// * @author wanggang
// */
//@Service
//public class RealEquipmentServiceImpl extends BaseOpenStackServiceImpl implements RealEquipmentService {
//
//    public RealEquipmentServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig, objectMapper);
//    }
//
//    @Override
//    public DeviceCloudPlatformModel page(String getParams) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists" + getParams, DeviceCloudPlatformModel.class);
//    }
//
//    @Override
//    public Device add(Device device) {
//        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists", JsonUtils.toUnderlineJSONString(device),Device.class);
//    }
//
//    @Override
//    public Device update(String uuid,Device device) {
//        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
//    }
//
//    @Override
//    public RealEquipmentBySwitchNameCloudPlatform findBySwitchName(String switchName) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/switch_map?switch_name=" + switchName,RealEquipmentBySwitchNameCloudPlatform.class);
//    }
//
//    @Override
//    public RealEquipmentBySerialPortCloudPlatform findBySerialPort(String serialPort) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/ccm_map?ccm_server_name=" + serialPort,RealEquipmentBySerialPortCloudPlatform.class);
//    }
//
//    @Override
//    public RealEquipmentConnectionCloudPlatform connection(String deviceUuid){
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/connection?device_uuid=" + deviceUuid,RealEquipmentConnectionCloudPlatform.class);
//    }
//
//    @Override
//    public SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp) {
//        snmp.setDataType("interfaces");
//        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpInterfaceResourceCloudPlatform.class);
//    }
//
//    @Override
//    public SnmpCloudPlatform snmpForCpu(SnmpParams snmp) {
//        snmp.setDataType("cpu");
//        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
//    }
//
//    @Override
//    public SnmpCloudPlatform snmpForMem(SnmpParams snmp) {
//        snmp.setDataType("mem");
//        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
//    }
//
//    @Override
//    public SnmpCloudPlatform snmpForDisk(SnmpParams snmp) {
//        snmp.setDataType("disk");
//        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/snmp", JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
//    }
//
//
//}
