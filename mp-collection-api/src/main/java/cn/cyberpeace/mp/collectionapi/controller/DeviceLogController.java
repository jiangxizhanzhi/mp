package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.constant.DeviceLogConstant;
import cn.cyberpeace.mp.collection.model.log.CommonDeviceLog;
import cn.cyberpeace.mp.collection.service.DeviceLogService;
import cn.cyberpeace.mp.collectionapi.bo.DeviceLogBO;
import cn.cyberpeace.mp.collectionapi.bo.LogReportBO;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/log/dev")
public class DeviceLogController {
    @Resource
    private DeviceLogService deviceLogService;

    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportDeviceLog(@RequestBody DeviceLogBO logData) {
        log.info("device log: {}", JsonCommon.stringify(logData));

        // TODO:根据日志类型进行数据格式转换
        CommonDeviceLog deviceLog = new CommonDeviceLog();
        BeanUtil.copyProperties(logData, deviceLog,"data");
        deviceLog.setRawData(logData.getMessage().toString());

        try {
            deviceLog = deviceLogService.saveDocument(deviceLog);
            return new ServiceResult<>(true).asResult();
        } catch (Exception e) {
            log.error("save device log error",e);
            return new ServiceResult<>(false).asResult();
        }
    }
}
