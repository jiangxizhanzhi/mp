 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentService.java.
 // File size 9485.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.EntityUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SnmpParams;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.RealEquipmentByParams;
import cn.cyberpeace.mp.devopscluster.model.SnmpCloudPlatform;
import cn.cyberpeace.mp.devopscluster.model.SnmpInterfaceResourceCloudPlatform;
import cn.cyberpeace.mp.devopscluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.*;

/**
 * @ClassName RealEquipmentService
 * @Description 实装管理接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class RealEquipmentService extends BaseCpService {

    private static final String ENTITY = "/entity";

    private static final String SNMP = "/snmp";

    private static final String ENTITY_USB = "/entity_usbs";

    private static final String ENTITY_ADD_URL = "/cloud/v1/entity/lists";

    private static final String SWITCH_CONNECT_ENTITY_DETAIL = "/cloud/v1/switch/switch_map";

    @Autowired
    private DeviceTargetMapper deviceTargetMapper;

    /**
     * RealEquipmentService
     *
     * @param devopsClusterConfig
     */
    public RealEquipmentService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Device> page(String getParams){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists" + getParams, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()),Device.class);
        POLogoUtils.changeLogoPathToUrl(list);
        CloudPlatformPageResult<Device> model = new CloudPlatformPageResult<>();
        model.setList(list);
        model.setTotal(pageResult.getTotal());
        return model;
    }

    /**
     * 新增
     * @param device
     * @return
     */
    public Device add(Device device){
        Device result = super.sendPost(ENTITY_ADD_URL, JsonUtils.snakeStringify(device),Device.class);
        super.checkResult(result);
        result.setOpenSyslog(device.getOpenSyslog());
        result.setOpenKafka(device.getOpenKafka());
        result.setOpenRestapi(device.getOpenRestapi());
        result.setOpenSoap(device.getOpenSoap());
        return result;
    }

    /**
     * 更新
     * @param uuid
     * @param device
     * @return
     */
    public Device update(String uuid,Device device){
        Device result = super.sendPut(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/" + uuid,JsonUtils.snakeStringify(device),Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 接口
     * @param snmp
     * @return
     */
    public SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp){
        snmp.setDataType("interfaces");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.snakeStringify(snmp), SnmpInterfaceResourceCloudPlatform.class);
    }


    /**
     * CPU
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForCpu(SnmpParams snmp){
        snmp.setDataType("cpu");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.snakeStringify(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 内存
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForMem(SnmpParams snmp){
        snmp.setDataType("mem");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.snakeStringify(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 磁盘
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForDisk(SnmpParams snmp){
        snmp.setDataType("disk");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.snakeStringify(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 根据交换机名称查询
     * @param switchName
     * @return
     */
    public CloudPlatformPageResult<RealEquipmentByParams> findBySwitchName(String switchName){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(SWITCH_CONNECT_ENTITY_DETAIL+"?switch_name=" + switchName,CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<RealEquipmentByParams> list = new ArrayList<>();
        if (null != pageResult.getData()){
            list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()),RealEquipmentByParams.class);
        }
        POLogoUtils.changeLogoPathToUrl(list);
        CloudPlatformPageResult<RealEquipmentByParams> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    /**
     * 根据串口查询
     * @param serialPort
     * @return
     */
    public CloudPlatformPageResult<RealEquipmentByParams> findBySerialPort(String serialPort){
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/ccm_map?ccm_server_name=" + serialPort,CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<RealEquipmentByParams> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()),RealEquipmentByParams.class);
        POLogoUtils.changeLogoPathToUrl(list);
        CloudPlatformPageResult<RealEquipmentByParams> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    /**
     * 获取登录链接
     * @param deviceUuid
     * @return
     */
    public CloudPlatformPageResult<TargetConnection> connection(String deviceUuid){
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(Collections.emptyList());
        return model;
    }

    /**
     * 获取服务器列表
     * @return
     */
    public List<Hypervisor> hypervisorList() {
        log.debug("RealEquipmentService.hypervisorList begin.");
        CloudPlatformResult result = super.sendGet(URL_VERSION + "/hypervisors", CloudPlatformResult.class);
        super.checkResult(result);
        List<Hypervisor> hypervisorList = JSONObject.parseArray(JSONObject.toJSONString(((JSONObject)result.getData()).getJSONArray("list")), Hypervisor.class);
        List<Hypervisor> rtnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(hypervisorList)) {
            for (Hypervisor hypervisor : hypervisorList) {
                if (!"disabled".equals(hypervisor.getStatus())) {
                    rtnList.add(hypervisor);
                }
            }
        }
        log.debug("RealEquipmentService.hypervisorList end. rtnList:{}", rtnList);
        return rtnList;
    }

    /**
     * 根据hostName查询usb列表
     * @return
     */
    public ListObjServiceResult<EntityUsb> physicalUsbListByHost(String hostName) {
        log.debug("RealEquipmentService.physicalUsbListByHost begin. hostName:{}", hostName);
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY_USB + "?host=" + hostName, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<EntityUsb> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResult) && CollectionUtils.isNotEmpty(pageResult.getData().getItems())) {
            count = pageResult.getTotal();
            responseList = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getData().getItems()), EntityUsb.class);
        }
        log.debug("RealEquipmentService.physicalUsbListByHost end.");
        return new ListObjServiceResult(new ListObjResult<>(responseList, count)).asResult();
    }

    public DeviceTargetPO getTargetBo(String id) {
        return deviceTargetMapper.findByUuid(id);
    }
}
