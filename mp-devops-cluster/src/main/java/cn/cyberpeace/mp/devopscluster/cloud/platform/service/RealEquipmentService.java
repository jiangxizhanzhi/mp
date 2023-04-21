package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
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
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.*;

/**
 * 实装管理接口
 * @author wanggang
 */
@Service
@Slf4j
public class RealEquipmentService extends BaseCpService {

    private static final String ENTITY = "/entity";

    private static final String SNMP = "/snmp";

    private static final String ENTITY_USB = "/entity_usbs";

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
        List<Device> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),Device.class);
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
        Device result = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/lists", JsonUtils.toUnderlineJSONString(device),Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 更新
     * @param uuid
     * @param device
     * @return
     */
    public Device update(String uuid,Device device){
        Device result = super.sendPut(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
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
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.toUnderlineJSONString(snmp), SnmpInterfaceResourceCloudPlatform.class);
    }


    /**
     * CPU
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForCpu(SnmpParams snmp){
        snmp.setDataType("cpu");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 内存
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForMem(SnmpParams snmp){
        snmp.setDataType("mem");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 磁盘
     * @param snmp
     * @return
     */
    public SnmpCloudPlatform snmpForDisk(SnmpParams snmp){
        snmp.setDataType("disk");
        return super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + SNMP, JsonUtils.toUnderlineJSONString(snmp), SnmpCloudPlatform.class);
    }

    /**
     * 根据交换机名称查询
     * @param switchName
     * @return
     */
    public CloudPlatformPageResult<RealEquipmentByParams> findBySwitchName(String switchName){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/switch_map?switch_name=" + switchName,CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<RealEquipmentByParams> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),RealEquipmentByParams.class);
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
        List<RealEquipmentByParams> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),RealEquipmentByParams.class);
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
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY + "/connection?device_uuid=" + deviceUuid,CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetConnection> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),TargetConnection.class);
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            list.forEach(l -> {
                if (StringUtils.isEmpty(config.getPublicNetProxyIp())) {
                    return;
                }
                var u = URLUtil.url(l.getUrl());
                l.setUrl(StringUtils.replaceOnce(l.getUrl(), u.getHost(), config.getPublicNetProxyIp()));
            });
        }
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }

    /**
     * 获取服务器列表
     * @return
     */
    public List<Hypervisor> hypervisorList() {
        log.info("RealEquipmentService.hypervisorList begin.");
        CloudPlatformResult result = super.sendGet(URL_VERSION + "/hypervisors", CloudPlatformResult.class);
        super.checkResult(result);
        List<Hypervisor> hypervisorList = JSONObject.parseArray(JSONObject.toJSONString(((JSONObject)result.getData()).getJSONArray("list")), Hypervisor.class);
        log.info("RealEquipmentService.hypervisorList end. hypervisorList:{}", hypervisorList);
        return hypervisorList;
    }

    /**
     * 根据hostName查询usb列表
     * @return
     */
    public ListObjServiceResult<EntityUsb> physicalUsbListByHost(String hostName) {
        log.info("RealEquipmentService.physicalUsbListByHost begin. hostName:{}", hostName);
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + ENTITY_USB + "?host=" + hostName, CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<EntityUsb> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResult) && CollectionUtils.isNotEmpty(pageResult.getItems())) {
            count = pageResult.getTotal();
            responseList = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), EntityUsb.class);
        }
        log.info("RealEquipmentService.physicalUsbListByHost end.");
        return new ListObjServiceResult(new ListObjResult<>(responseList, count)).asResult();
    }
}
