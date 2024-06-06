package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化注册MP信息到云平台
 * @author wanggang
 */
@Service
@Slf4j
public class InitRegisterService extends BaseCpService {

    private static final String DEVICE_REGISTER = "/cloud/v1/device/register_mp";

    private static final String DEVICE_LIST_ALL = "/cloud/v1/device/virtual_device?limit=9999&is_internal=false";


    public InitRegisterService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    @Resource
    private DevopsTargetService devopsTargetService;

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    /**
     * 注册MP地址
     * @return
     */
    @Async
    public BaseCloudPlatformResult register(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("ip_port", url);
        BaseCloudPlatformResult pageResult = super.sendPost(DEVICE_REGISTER, JSONObject.toJSONString(map), BaseCloudPlatformResult.class);
        super.checkResult(pageResult);
        return pageResult;
    }

    /**
     * 同步云平台初始靶标
     * @return
     */
    public Integer initTarget() {
        CloudPlatformItemsPageResult result = new CloudPlatformItemsPageResult();
        try {
            result = super.sendGet(DEVICE_LIST_ALL, CloudPlatformItemsPageResult.class);
        } catch (Exception e) {
            log.error("get init target error: {}", ExceptionUtils.getStackTrace(e));
        }
        Integer res = NumberUtils.INTEGER_ZERO;
        if (CollectionUtils.isEmpty(result.getData().getItems())) {
            return res;
        }
        List<Device> deviceList = JSON.parseArray(JSON.toJSONString(result.getData().getItems()), Device.class);
        List<String> uuids = new ArrayList<>();
        for (Device device2 : deviceList) {
            String deviceUuid = device2.getUuid();
            uuids.add(deviceUuid);
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
        List<String> existedUuids = new ArrayList<>();
        for (DeviceTargetPO p : deviceTargetPOS) {
            String uuid = p.getUuid();
            existedUuids.add(uuid);
        }
        List<Device> notExistedList = new ArrayList<>();
        List<Device> existedList = new ArrayList<>();
        if (CollectionUtils.isEmpty(deviceTargetPOS)) {
            notExistedList.addAll(deviceList);
        } else {
            List<Device> list1 = new ArrayList<>();
            for (Device device1 : deviceList) {
                if (!existedUuids.contains(device1.getUuid())) {
                    list1.add(device1);
                }
            }
            notExistedList.addAll(list1);
            List<Device> list = new ArrayList<>();
            for (Device device : deviceList) {
                if (existedUuids.contains(device.getUuid())) {
                    list.add(device);
                }
            }
            existedList.addAll(list);
        }
        if (CollectionUtils.isNotEmpty(notExistedList)) {
            for (Device device : notExistedList) {
                device.setIsHide(Boolean.FALSE);
            }
            res += devopsTargetService.insert(notExistedList);
        }
        if (CollectionUtils.isNotEmpty(existedList)) {
            for (Device e : existedList) {
                devopsTargetService.updateById(e);
            }
            res += existedList.size();
        }
        return res;
    }
}
