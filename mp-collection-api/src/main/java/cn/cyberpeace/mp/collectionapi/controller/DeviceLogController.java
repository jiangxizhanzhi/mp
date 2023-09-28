package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import cn.cyberpeace.mp.collection.service.DeviceLogService;
import cn.cyberpeace.mp.collectionapi.bo.DeviceLogBO;
import cn.cyberpeace.mp.collectionapi.bo.MetricBO;
import cn.cyberpeace.mp.collectionapi.vo.MetricVO;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/log/dev")
public class DeviceLogController {
    @Resource
    private DeviceLogService deviceLogService;

    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportDeviceLog(@RequestBody @NonNull DeviceLogBO logData) {
        if (log.isDebugEnabled()) {
            log.debug("device log: {}", JsonCommon.stringify(logData));
        }

        // TODO:根据日志类型进行数据格式转换
        DeviceLog deviceLog = new DeviceLog();
        BeanUtil.copyProperties(logData, deviceLog, "message");
        deviceLog.setMessage(JSON.toJSONString(logData.getMessage()));

        try {
            deviceLogService.saveDocument(deviceLog);
            return new ServiceResult<>(true).asResult();
        } catch (Exception e) {
            log.error("save device log error", e);
            return new ServiceResult<>(false).asResult();
        }
    }

    @GetMapping("/stat")
    @ApiOperation(value = "查询日志统计指标")
    public ServiceResult<MetricVO> getMetrics(@RequestParam("metric") @NonNull String metric, @RequestParam("device_type") @NonNull String deviceType) {
        String[] deviceTypes = Arrays.stream(deviceType.split(",")).map(String::trim).toArray(String[]::new);
        Map<String, Long> result = deviceLogService.statisticByLogType(metric, deviceTypes);
        List<MetricBO> metrics = result.entrySet().stream().map(entry -> MetricBO.builder().metric(metric).deviceType(entry.getKey()).value(entry.getValue().intValue()).build()).collect(Collectors.toList());

        return new ServiceResult<>(new MetricVO(metrics)).asResult();
    }
}
