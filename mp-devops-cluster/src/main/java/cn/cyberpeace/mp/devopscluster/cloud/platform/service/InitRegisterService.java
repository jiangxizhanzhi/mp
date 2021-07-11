package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
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
import java.util.stream.Collectors;


@Service
@Slf4j
public class InitRegisterService extends BaseCpService {

    public InitRegisterService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    @Resource
    private DevopsTargetService devopsTargetService;

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    
    @Async
    public BaseCloudPlatformResult register(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("ip_port", url);
        BaseCloudPlatformResult pageResult = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/register_mp", JSONObject.toJSONString(map), BaseCloudPlatformResult.class);
        super.checkResult(pageResult);
        return pageResult;
    }

    
    public Integer initTarget() {
        var result = new CloudPlatformItemsPageResult();
        try {
            result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION +
                    "/device/lists?limit=9999&is_internal=false", CloudPlatformItemsPageResult.class);
        } catch (Exception e) {
            log.error("get init target error: {}", ExceptionUtils.getStackTrace(e));
        }
        Integer res = NumberUtils.INTEGER_ZERO;
        if (CollectionUtils.isEmpty(result.getItems())) {
            return res;
        }
        List<Device> deviceList = JSON.parseArray(JSON.toJSONString(result.getItems()), Device.class);
        List<String> uuids = deviceList.stream().map(device -> device.getUuid()).collect(Collectors.toList());
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
        List<String> existedUuids = deviceTargetPOS.stream().map(p -> p.getUuid()).collect(Collectors.toList());
        List<Device> notExistedList = new ArrayList<>();
        List<Device> existedList = new ArrayList<>();
        if (CollectionUtils.isEmpty(deviceTargetPOS)) {
            notExistedList.addAll(deviceList);
        } else {
            notExistedList.addAll(deviceList.stream()
                    .filter(device -> !existedUuids.contains(device.getUuid()))
                    .collect(Collectors.toList()));
            existedList.addAll(deviceList.stream()
                    .filter(device -> existedUuids.contains(device.getUuid()))
                    .collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(notExistedList)) {
            notExistedList.forEach(device -> device.setIsHide(Boolean.FALSE));
            res += devopsTargetService.insert(notExistedList);
        }
        if (CollectionUtils.isNotEmpty(existedList)) {
            existedList.forEach(e -> devopsTargetService.updateById(e));
            res += existedList.size();
        }
        return res;
    }
}
