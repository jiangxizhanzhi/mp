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

import cn.cyberpeace.mp.agent.snmp.model.SnmpAccessInfo;
import cn.cyberpeace.mp.agent.snmp.service.SnmpTaskServiceImpl;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        result.setOpenSnmp(device.getOpenSnmp());
        return result;
    }

    /**
     * 更新
     * @param uuid
     * @param device
     * @return
     */
    public Device update(String uuid,Device device){
        Device result = super.sendPut(CPCLOUD_URL + URL_VERSION + ENTITY + "/" + uuid,JsonUtils.snakeStringify(device),Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 接口
     * @param snmp
     * @return
     */
    public SnmpInterfaceResourceCloudPlatform interfacesResource(SnmpParams snmp){
        SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
        snmpAccessInfo.setCommunity(snmp.getCommunityName());
        snmpAccessInfo.setVersion("v2c");
        snmpAccessInfo.setHostIp(snmp.getIp());
        snmpAccessInfo.setPort("161");
        SnmpTaskServiceImpl snmpTaskService = BeanCommon.getBean(SnmpTaskServiceImpl.class);

        String interfaceNameOid = "1.3.6.1.2.1.2.2.1.2";
        String inOid = "1.3.6.1.2.1.2.2.1.10";
        String outOid = "1.3.6.1.2.1.2.2.1.16";

        var result = snmpTaskService.walkSnmpData(snmpAccessInfo, Arrays.asList(interfaceNameOid, inOid, outOid));
        Map<String, String> nameResult = new HashMap<>();
        Map<String, BigDecimal> inResult = new HashMap<>();
        Map<String, BigDecimal> outResult = new HashMap<>();

        for (Map.Entry<String, String> entry : result.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.startsWith(interfaceNameOid)) {
                nameResult.put(key.replace(interfaceNameOid, ""), value);
            } else if (key.startsWith(inOid)) {
                inResult.put(key.replace(inOid, ""), new BigDecimal(value));
            } else if (key.startsWith(outOid)) {
                outResult.put(key.replace(outOid, ""), new BigDecimal(value));
            }
        }

        SnmpInterfaceResourceCloudPlatform cloudPlatform = new SnmpInterfaceResourceCloudPlatform();
        cloudPlatform.setStatus(true);
        List<SnmpInterfaceResourceCloudPlatform.Interfaces> list = new ArrayList<>();
        cloudPlatform.setResult(list);
        for (String key : nameResult.keySet()) {
            String name = nameResult.get(key);
            BigDecimal in = inResult.get(key);
            BigDecimal out = outResult.get(key);
            if (name != null && in != null && out != null) {
                var i = new SnmpInterfaceResourceCloudPlatform.Interfaces();
                i.setInterfaceName(name);
                i.setInTraffic(in.longValue());
                i.setOutTraffic(out.longValue());
                list.add(i);
            }
        }
        return cloudPlatform;

//        snmp.setDataType("interfaces");
//        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.snakeStringify(snmp), SnmpInterfaceResourceCloudPlatform.class);
    }


    /**
     * CPU
     * @param snmp
     * @return
     */
    public BigDecimal snmpForCpu(SnmpParams snmp){
        try {
            SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
            snmpAccessInfo.setCommunity(snmp.getCommunityName());
            snmpAccessInfo.setVersion("v2c");
            snmpAccessInfo.setHostIp(snmp.getIp());
            snmpAccessInfo.setPort("161");

            SnmpTaskServiceImpl snmpTaskService = BeanCommon.getBean(SnmpTaskServiceImpl.class);
            if (StringUtils.isEmpty(snmp.getOid())) {
                String freeCpu = ".1.3.6.1.4.1.2021.11.11.0";
                Map<String, String> result = snmpTaskService.getSnmpData(snmpAccessInfo, Arrays.asList(freeCpu));
                BigDecimal totalMem = new BigDecimal(result.get(freeCpu));
                return new BigDecimal(100).subtract(totalMem);
            } else {
                Map<String, String> result = snmpTaskService.getSnmpData(snmpAccessInfo, Arrays.asList(snmp.getOid()));
                BigDecimal res = new BigDecimal(result.get(snmp.getOid()));
                return res;
            }
        } catch (Exception e) {
            log.error("使用snmp计算cpu使用率失败", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 内存
     * @param snmp
     * @return
     */
    public BigDecimal snmpForMem(SnmpParams snmp) {
        try {
            SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
            snmpAccessInfo.setCommunity(snmp.getCommunityName());
            snmpAccessInfo.setVersion("v2c");
            snmpAccessInfo.setHostIp(snmp.getIp());
            snmpAccessInfo.setPort("161");

            SnmpTaskServiceImpl snmpTaskService = BeanCommon.getBean(SnmpTaskServiceImpl.class);
            if (StringUtils.isEmpty(snmp.getOid())) {
                String totalMemOid = "1.3.6.1.4.1.2021.4.5.0";
                String freeMemOid = "1.3.6.1.4.1.2021.4.11.0";
                Map<String, String> result = snmpTaskService.getSnmpData(snmpAccessInfo, Arrays.asList(totalMemOid, freeMemOid));
                BigDecimal totalMem = new BigDecimal(result.get(totalMemOid));
                BigDecimal freeMem = new BigDecimal(result.get(freeMemOid));
                BigDecimal usedMem = totalMem.subtract(freeMem);
                return usedMem.divide(totalMem, 2, RoundingMode.DOWN).multiply(new BigDecimal(100));
            } else {
                Map<String, String> result = snmpTaskService.getSnmpData(snmpAccessInfo, Arrays.asList(snmp.getOid()));
                BigDecimal res = new BigDecimal(result.get(snmp.getOid()));
                return res;
            }
        } catch (Exception e) {
            log.error("使用snmp计算cpu使用率失败", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 磁盘
     * @param snmp
     * @return
     */
    public BigDecimal snmpForDisk(SnmpParams snmp) {
        try {
            SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
            snmpAccessInfo.setCommunity(snmp.getCommunityName());
            snmpAccessInfo.setVersion("v2c");
            snmpAccessInfo.setHostIp(snmp.getIp());
            snmpAccessInfo.setPort("161");

            SnmpTaskServiceImpl snmpTaskService = BeanCommon.getBean(SnmpTaskServiceImpl.class);
            if (StringUtils.isEmpty(snmp.getOid())) {
                String unitOid = "1.3.6.1.2.1.25.2.3.1.4";
                String totalOid = "1.3.6.1.2.1.25.2.3.1.5";
                String usedOid = "1.3.6.1.2.1.25.2.3.1.6";
                Map<String, String> result = snmpTaskService.walkSnmpData(snmpAccessInfo, Arrays.asList(unitOid, totalOid, usedOid));
                Map<String, BigDecimal> unitResult = new HashMap<>();
                Map<String, BigDecimal> totalResult = new HashMap<>();
                Map<String, BigDecimal> usedResult = new HashMap<>();
                for (Map.Entry<String, String> entry : result.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key.startsWith(unitOid)) {
                        unitResult.put(key.replace(unitOid, ""), new BigDecimal(value));
                    } else if (key.startsWith(totalOid)) {
                        totalResult.put(key.replace(totalOid, ""), new BigDecimal(value));
                    } else if (key.startsWith(usedOid)) {
                        usedResult.put(key.replace(usedOid, ""), new BigDecimal(value));
                    }
                }
                log.info("unitResult : {}", JsonCommon.stringify(unitResult));
                log.info("totalResult : {}", JsonCommon.stringify(totalResult));
                log.info("usedResult : {}", JsonCommon.stringify(usedResult));

                BigDecimal total = new BigDecimal(0);
                BigDecimal used = new BigDecimal(0);
                for (String key : unitResult.keySet()) {
                    var unit = unitResult.get(key);
                    var totalTmp = totalResult.get(key);
                    var usedTmp = usedResult.get(key);
                    if (totalTmp != null && usedTmp != null) {
                        total = total.add(totalTmp.multiply(unit));
                        used = used.add(usedTmp.multiply(unit));
                    }
                }

                return used.divide(total,2,RoundingMode.DOWN).multiply(new BigDecimal(100));
            } else {
                Map<String, String> result = snmpTaskService.getSnmpData(snmpAccessInfo, Arrays.asList(snmp.getOid()));
                BigDecimal res = new BigDecimal(result.get(snmp.getOid()));
                return res;
            }
        } catch (Exception e) {
            log.error("使用snmp计算cpu使用率失败", e);
            return BigDecimal.ZERO;
        }
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
