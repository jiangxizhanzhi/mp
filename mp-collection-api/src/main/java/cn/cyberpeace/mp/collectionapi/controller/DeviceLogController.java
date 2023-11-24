package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.collection.config.CollectionConfig;
import cn.cyberpeace.mp.collection.model.log.DeviceLog;
import cn.cyberpeace.mp.collection.model.log.PageableLogs;
import cn.cyberpeace.mp.collection.request.DeviceLogQueryRequest;
import cn.cyberpeace.mp.collection.service.DeviceLogService;
import cn.cyberpeace.mp.collectionapi.bo.DeviceLogBO;
import cn.cyberpeace.mp.collectionapi.bo.MetricBO;
import cn.cyberpeace.mp.collectionapi.vo.DeviceLogVO;
import cn.cyberpeace.mp.collectionapi.vo.MetricVO;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    @Resource
    private CollectionConfig config;

    private HttpClient client;

    /**
     * 测试环境业务AD client, 也需要推一下日志
     */
    private HttpClient testAdClient;

    @PostConstruct
    private void initHttpClient() {
        client = HttpClient.builder().configServer(config.getApp()).build();
        testAdClient = HttpClient.builder().configServer(config.getAdApp()).build();
    }



    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportDeviceLog(@RequestBody @NonNull DeviceLogBO logData) {
        if (FlowController.debugLogFlag) {
            log.debug("device log: {}", JsonCommon.stringify(logData));
        }

        // TODO:根据日志类型进行数据格式转换
        DeviceLog deviceLog = BeanUtil.copyProperties(logData, DeviceLog.class, "message");
        deviceLog.setTimestamp(System.currentTimeMillis());
        if (logData.getMessage() instanceof String) {
            deviceLog.setMessage(logData.getMessage());
        } else {
            deviceLog.setMessage(JSON.toJSONString(logData.getMessage()));
        }

        try {
            deviceLogService.saveDocument(deviceLog);
            forwardLog(logData);
            return new ServiceResult<>(true).asResult();
        } catch (Exception e) {
            log.error("save device log error", e);
            return new ServiceResult<>(false).asResult();
        }
    }

    // 把日志同时转发到上层应用，目前无性能要求所以不做异步处理
    private void forwardLog(DeviceLogBO logBO) {
        client.post("/modify/stat/log/", logBO, null, null, 10_000);
        testAdClient.post("/api/ct/web/bwm_race/situation/chain/get_log/?viewer_key=36af2bf41995c3207c62e39c1914b5ec", logBO, null, null, 10_000);
        // client.post("/api/mp/log/dev/mock", logBO, null, null, 0);
    }

    @PostMapping("/mock")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> mockForward(@RequestBody @NonNull DeviceLogBO logData) {
        if (FlowController.debugLogFlag) {
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
        if (FlowController.debugLogFlag) {
            log.debug("getMetrics metric = {}, deviceType = {}", metric, deviceType);
        }
        String[] deviceTypes = Arrays.stream(deviceType.split(",")).map(String::trim).toArray(String[]::new);
        Map<String, Long> result = deviceLogService.statisticByLogType(metric, deviceTypes);
        List<MetricBO> metrics = result.entrySet().stream().map(entry -> MetricBO.builder().metric(metric).deviceType(entry.getKey()).value(entry.getValue().intValue()).build()).collect(Collectors.toList());

        return new ServiceResult<>(new MetricVO(metrics)).asResult();
    }

    @GetMapping("")
    @ApiOperation(value = "查询设备上报的日志信息")
    // public DeviceLogVO queryLogs(@RequestBody @NonNull DeviceLogQueryRequest req) {
    public DeviceLogVO queryLogs(@RequestParam(value = "time_start", required = false) String timeStart,
                                 @RequestParam(value = "time_end", required = false) String timeEnd,
                                 @RequestParam(required = false) String ip,
                                 @RequestParam @NotBlank String type,
                                 @RequestParam(value = "log_type") @NotBlank String logType,
                                 @RequestParam(value = "page_size", defaultValue = "100") @Max(1000) @Min(1) Integer pageSize,
                                 @RequestParam(value = "page_index", defaultValue = "1") @Min(1) Integer pageIndex) {
        if (FlowController.debugLogFlag) {
            log.debug("queryLogs timeStart = {}, timeEnd = {}, ip = {}, type = {}, logType = {}, pageSize = {}, pageIndex = {}", timeStart, timeEnd, ip, type, logType, pageSize, pageIndex);
        }
        DeviceLogQueryRequest req = DeviceLogQueryRequest.builder()
                .timeEnd(timeEnd)
                .timeStart(timeStart)
                .ip(ip)
                .type(type)
                .logType(logType)
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
        if (pageSize < 1 || pageSize > 1000) {
            req.setPageSize(100);
        }
        if (pageIndex < 1) {
            req.setPageIndex(1);
        }
        PageableLogs logs = deviceLogService.searchLog(req);
        List<Object> rawLogs = logs.getData().stream().map(DeviceLog::getMessage).map(Object::toString).map(JSON::parse).collect(Collectors.toList());
        DeviceLogVO result = BeanUtil.copyProperties(logs, DeviceLogVO.class, "data");
        result.setData(rawLogs);
        return result;
    }
}
