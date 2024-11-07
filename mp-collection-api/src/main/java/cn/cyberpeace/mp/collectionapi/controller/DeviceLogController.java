package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.base.util.thread.ThreadPoolUtils;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/log/dev")
public class DeviceLogController {
    @Resource
    private DeviceLogService deviceLogService;

    @Resource
    private CollectionConfig config;

    /**
     * 日志分sched 调度 和 dmf 扰动也叫裁决  两种日志。
     * 业务主要需要扰动日志
     */
    private static final String DMF_LOG_TYPE = "dmf";
    private static final int DEVICE_DMF_MAX_SEQ = 10;

    private static final List<DeviceLog> DEVICE_BUFFER = new ArrayList<DeviceLog>();

    /**
     * 选手造成的扰动日志发送数量过大，直接推送给业务，导致业务报500，
     * 这里增加一个缓冲池。key是device_ip, 只缓存扰动日志。
     * 当同一个设备扰动日志1s内发送数量超过10条时，指挥发送10条给业务。
     */
    private static final Map<String, Integer> PUSH_LOG_COUNT_BUFFER = new ConcurrentHashMap<>(8);

    /**
     * 测试环境业务AD client, 也需要推一下日志
     */
    private HttpClient testAdClient;

    private static final Object DEFAULT_MESSAGE_RETURN = new Object();

    @PostConstruct
    private void init() {
        testAdClient = HttpClient.builder().configServer(config.getAdApp()).configMaxConnTotal(100).configMaxConnPerRoute(75).configTimeout(30_000).build();
        // 每两秒执行一次，或者size>100 保存日志
        ScheduledExecutorService deviceLogBufferScheduleExecutor = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService pushLogScheduleExecutor = Executors.newScheduledThreadPool(1);

        deviceLogBufferScheduleExecutor.scheduleAtFixedRate(() -> {
            synchronized (DEVICE_BUFFER) {
                if (!CollectionUtils.isEmpty(DEVICE_BUFFER)) {
                    deviceLogService.saveDocuments(DEVICE_BUFFER);
                    DEVICE_BUFFER.clear();
                }
            }
        }, 1, 2, TimeUnit.SECONDS);

        //每过1s清理一下PUSH_LOG_COUNT_BUFFER记录的数目。因为如果扰动日志1s内，某设备记录数超过10条就不再发送了。
        pushLogScheduleExecutor.scheduleAtFixedRate(() -> {
            synchronized (PUSH_LOG_COUNT_BUFFER) {
                if (!CollectionUtils.isEmpty(PUSH_LOG_COUNT_BUFFER)) {
                    PUSH_LOG_COUNT_BUFFER.clear();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

    }



    /**
     *  DEVICE_BUFFER 收集日志。
     *  如果缓存长度大于100或者等待2s后，就批量存储
     * @author
     * @date 16:46 2023/11/30
     * @param logData
     * @return cn.cyberpeace.mp.base.service.result.ServiceResult<java.lang.Boolean>
     **/
    @PostMapping("")
    @ApiOperation(value = "上报单条设备日志")
    public ServiceResult<Boolean> reportDeviceLog(@RequestBody @NonNull DeviceLogBO logData) {
        if (FlowController.debugLogFlag) {
            log.info("device log: {}", JsonCommon.stringify(logData));
        }

        // TODO:根据日志类型进行数据格式转换
        DeviceLog deviceLog = BeanUtil.copyProperties(logData, DeviceLog.class, "message");
        deviceLog.setTimestamp(System.currentTimeMillis());
        if (Objects.nonNull(logData.getMessage())) {
            if (logData.getMessage() instanceof String) {
                if (JsonUtils.isValidJson(logData.getMessage().toString())) {
                    deviceLog.setMessage(logData.getMessage());
                }
            } else {
                deviceLog.setMessage(JSON.toJSONString(logData.getMessage()));
            }
        }
        try {
            // 有几次设备日志发送量过大挤占了连接池，导致其他接口连接超时，这边由于接口都对接好了。正常设备上报日志应该ok
            synchronized (DEVICE_BUFFER) {
                DEVICE_BUFFER.add(deviceLog);
                if (DEVICE_BUFFER.size() > 100) {
                    deviceLogService.saveDocuments(DEVICE_BUFFER);
                    DEVICE_BUFFER.clear();
                }
            }
            CompletableFuture.runAsync(() -> forwardLog(logData), ThreadPoolUtils.DEVICE_PUSH_POOL)
                    .exceptionally(error -> {
                        log.error("推送日志异常", error);
                        return null;
                    });
            return new ServiceResult<>(true).asResult();
        } catch (Exception e) {
            log.error("save device log error");
            return new ServiceResult<>(false, BaseErrorCode.ERROR, e.getMessage()).asResult();
        }
    }

    // 把日志同时转发到上层应用，目前无性能要求所以不做异步处理
    private void forwardLog(DeviceLogBO logBO) {
        if (Objects.isNull(logBO)) {
            return;
        } else if (Objects.isNull(logBO.getIp())) {
            return;
        } else if (DMF_LOG_TYPE.equals(logBO.getLogType())) {
            synchronized (PUSH_LOG_COUNT_BUFFER) {
                if (Objects.isNull(PUSH_LOG_COUNT_BUFFER.get(logBO.getIp()))) {
                    PUSH_LOG_COUNT_BUFFER.put(logBO.getIp(), 1);
                } else {
                    PUSH_LOG_COUNT_BUFFER.put(logBO.getIp(), PUSH_LOG_COUNT_BUFFER.get(logBO.getIp()) + 1);
                }
                if (PUSH_LOG_COUNT_BUFFER.get(logBO.getIp()) > DEVICE_DMF_MAX_SEQ) {
                    return;
                }
            }
        }

        if (logBO.getMessage() instanceof String) {
            logBO.setMessage(JSON.parse(logBO.getMessage().toString()));
        }
        testAdClient.post("/api/ct/web/bwm_race/situation/chain/get_log/?viewer_key=dd42bacedfdef66b417333c47803d69c", logBO, null, null, 10_000);
        // client.post("/api/mp/log/dev/mock", logBO, null, null, 0);
    }

    @GetMapping("/stat")
    @ApiOperation(value = "查询日志统计指标")
    public ServiceResult<MetricVO> getMetrics(@RequestParam("metric") @NonNull String metric, @RequestParam("device_type") @NonNull String deviceType) {
        if (FlowController.debugLogFlag) {
            log.info("getMetrics metric = {}, deviceType = {}", metric, deviceType);
        }
        String[] deviceTypes = Arrays.stream(deviceType.split(",")).map(String::trim).toArray(String[]::new);
        Map<String, Long> result = deviceLogService.statisticByLogType(metric, deviceTypes);
        List<MetricBO> metrics = result.entrySet().stream().map(entry -> MetricBO.builder().metric(metric).deviceType(entry.getKey()).value(entry.getValue().intValue()).build()).collect(Collectors.toList());

        return new ServiceResult<>(new MetricVO(metrics)).asResult();
    }

    @GetMapping("")
    @ApiOperation(value = "查询设备上报的日志信息")
    public DeviceLogVO queryLogs(@RequestParam(value = "time_start", required = false) String timeStart,
                                 @RequestParam(value = "time_end", required = false) String timeEnd,
                                 @RequestParam(required = false) String ip,
                                 @RequestParam @NotBlank String type,
                                 @RequestParam(value = "log_type") @NotBlank String logType,
                                 @RequestParam(value = "page_size", defaultValue = "100") @Max(1000) @Min(1) Integer pageSize,
                                 @RequestParam(value = "page_index", defaultValue = "1") @Min(1) Integer pageIndex) {
        if (FlowController.debugLogFlag) {
            log.info("queryLogs timeStart = {}, timeEnd = {}, ip = {}, type = {}, logType = {}, pageSize = {}, pageIndex = {}", timeStart, timeEnd, ip, type, logType, pageSize, pageIndex);
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
        List<Object> rawLogs = logs.getData().stream().map(DeviceLog::getMessage).map(Object::toString).map(str -> {
            try {
                return JSON.parse(str);
            } catch (Exception e) {
                log.error(String.format("convert message to JSON Object fail: %s", str));
                return DEFAULT_MESSAGE_RETURN;
            }
        }).collect(Collectors.toList());
        DeviceLogVO result = BeanUtil.copyProperties(logs, DeviceLogVO.class, "data");
        result.setData(rawLogs);
        return result;
    }

    @GetMapping("/page")
    @ApiOperation(value = "查询设备上报的日志信息")
    public Object queryLogs(@RequestParam @NotBlank String type) {
        if (FlowController.debugLogFlag) {
            log.info("queryLogs  type = {}", type);
        }
        DeviceLogQueryRequest req = DeviceLogQueryRequest.builder()
                .type(type)
                .build();
            req.setPageSize(10);
            req.setPageIndex(1);

        PageableLogs logs = deviceLogService.searchLog(req);
        return logs.getData();
    }
}
