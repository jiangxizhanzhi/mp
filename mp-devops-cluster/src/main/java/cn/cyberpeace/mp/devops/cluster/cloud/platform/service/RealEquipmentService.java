package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.SnmpParams;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.RealEquipmentByParams;
import cn.cyberpeace.mp.devops.cluster.model.SnmpCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.model.SnmpInterfaceResourceCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.*;

/**
 * 实装管理接口
 * @author wanggang
 */
@Service
@Slf4j
public class RealEquipmentService extends BaseOpenStackService {

    private static final String ENTITY = "/entity";

    private static final String SNMP = "/snmp";

    public RealEquipmentService(DevopsConfig devopsConfig) {
        super(devopsConfig);
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
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }
}
