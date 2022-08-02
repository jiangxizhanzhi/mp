package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
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

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 初始化注册MP信息到云平台
 * @author wanggang
 */
@Service
@Slf4j
public class InitRegisterService extends BaseCpService {

    public InitRegisterService(DevopsConfig devopsConfig) {
        super(devopsConfig);
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
        BaseCloudPlatformResult pageResult = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/register_mp", JSONObject.toJSONString(map), BaseCloudPlatformResult.class);
        super.checkResult(pageResult);
        return pageResult;
    }

    public Integer initTarget() {
        var result = new CloudPlatformItemsPageResult();
        try {
            result = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/init", CloudPlatformItemsPageResult.class);
        } catch (Exception e) {
            log.error("get init target error: {}", ExceptionUtils.getStackTrace(e));
        }
        Integer res = NumberUtils.INTEGER_ZERO;
        List<Device> deviceList = JSONObject.parseArray(JSONObject.toJSONString(result.getItems()), Device.class);
        List<String> uuids = deviceList.stream().map(device -> device.getUuid()).collect(Collectors.toList());
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
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
            deviceList.forEach(device -> device.setIsHide(Boolean.FALSE));
            res += devopsTargetService.insert(deviceList);
        }
        if (CollectionUtils.isNotEmpty(existedList)) {
            existedList.forEach(e -> devopsTargetService.updateById(e));
            res += existedList.size();
        }
        return res;
    }
}
