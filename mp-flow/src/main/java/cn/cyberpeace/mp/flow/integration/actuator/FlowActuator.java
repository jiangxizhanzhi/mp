package cn.cyberpeace.mp.flow.integration.actuator;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.flow.bo.FlowTaskStart;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.enums.ResourceTypeEnum;
import cn.cyberpeace.mp.flow.enums.TaskActionEnum;
import cn.cyberpeace.mp.flow.enums.TaskStatusEnum;
import cn.cyberpeace.mp.flow.enums.TaskTaskStatusEnum;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
import cn.cyberpeace.mp.flow.request.BatchStartFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.StartFlowTaskRequest;
import cn.cyberpeace.mp.flow.service.impl.FlowTaskTaskService;
import cn.cyberpeace.mp.flow.util.FlowWebSocketClientUtil;
import cn.cyberpeace.mp.flow.util.ResponseUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class FlowActuator {

    private static final String INIT_NETWORK_URL = "/api/v1/task/network/init";

    private static final String START_TASK_URL = "/api/v1/task/v2/tasks/action";

    private static final String START_TASKS_URL = "/api/v1/task/v2/tasks/batch/action";

    private static final String UPLOAD_PCAP_URL = "/api/v1/resource/pcap/upload";

    private static final String PROTOCOL_TOP5 = "/api/v1/task/tasks/protocols/top5";

    private String actuatorHost;

    private FlowTaskTaskService flowTaskTaskService;

    private String actuatorIp;

    public FlowActuator(String actuatorHost, FlowTaskTaskService flowTaskTaskService) {
        this.actuatorHost = "http://" + actuatorHost;
        this.flowTaskTaskService = flowTaskTaskService;
        actuatorIp = actuatorHost.split(BaseConstants.COLON)[0];
    }

    public FlowActuator(String actuatorIp, Integer actuatorPort, FlowTaskTaskService flowTaskTaskService) {
        this.actuatorHost = "http://" + actuatorIp + ":" + actuatorPort;
        this.flowTaskTaskService = flowTaskTaskService;
        this.actuatorIp = actuatorIp;
    }

    public boolean startTask(String taskId, FlowTaskStart flowTaskStart) {
        String url = this.actuatorHost + START_TASK_URL;
        StartFlowTaskRequest startFlowTaskRequest = StartFlowTaskRequest.builder()
                .action(TaskActionEnum.START.toValue()).time(TimeUtils.localDateTimeToString(LocalDateTime.now()))
                .task(flowTaskStart).build();
        try {
            String startTaskRsp = HttpUtils.sendPost(url, JsonUtils.snakeStringify(startFlowTaskRequest));
            var rsp = JsonCommon.snakeParse(startTaskRsp, new TypeReference<TrexResponse<Object>>() {
            });
            if ("1000".equals(rsp.getCode())) {
                // 跟流量执行器建立ws连接，用于接收任务状态信息
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(this.actuatorIp);
                wsClient.sendMessage(taskId);
                // 更新任务状态为启动中
                flowTaskTaskService.updateTaskStatus(taskId, TaskTaskStatusEnum.STARTING.toValue());
                return true;
            } else if ("6008".equals(rsp.getCode())) {
                // 更新任务状态为运行中
                flowTaskTaskService.updateTaskStatus(taskId, TaskStatusEnum.RUNNING.toValue());
                return true;
            } else {
                log.error("start task failed for {}", taskId);
                return false;
            }
        } catch (Exception e) {
            log.error("start task error for {}", taskId, e);
            return false;
        }
    }

    public boolean startTasks(Integer runType, List<FlowTaskStart> flowTaskStartList, List<String> notRunningTaskIds) {
        String url = this.actuatorHost + START_TASKS_URL;
        try {
            BatchStartFlowTaskRequest batchStartFlowTaskRequest = BatchStartFlowTaskRequest.builder()
                    .action(TaskActionEnum.START.toValue()).time(TimeUtils.localDateTimeToString(LocalDateTime.now()))
                    .tasks(flowTaskStartList).runType(runType).build();

            String batchStartTaskRsp = HttpUtils.sendPost(url, JsonUtils.snakeStringify(batchStartFlowTaskRequest));
            JSONObject batchStartTaskObj = JSONObject.parseObject(batchStartTaskRsp);
            log.info("batch start task response:{}", batchStartTaskObj.toJSONString());
            if (ResponseUtils.checkResponse(batchStartTaskObj)) {
                log.info("batch start task success");

                // 跟流量执行器建立ws连接，用于接收任务状态信息
                FlowWebSocketClientUtil wsClient = FlowWebSocketClientUtil.getWebSocketClient(this.actuatorIp);
                wsClient.sendMessage(String.join(BaseConstants.COMMA, notRunningTaskIds));

                // 批量更新任务状态为运行中
                flowTaskTaskService.batchUpdateTaskStatus(notRunningTaskIds, TaskTaskStatusEnum.STARTING.toValue());
                return true;
            } else {
                log.error("batch start tasks failed");
                return true;
            }
        } catch (Exception e) {
            log.error("batch start tasks error: {}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public boolean uploadPcap(FlowInfoModel flowInfoModel, List<FlowProtocolModel> flowProtocolModelList) {
        byte[] fileByte;
        JSONObject object = new JSONObject();
        object.put("flowId", flowInfoModel.getId());
        object.put("flow_id", flowInfoModel.getId());
        if (ResourceTypeEnum.PACP.toValue() == flowInfoModel.getResourceType()) {
            fileByte = FsCommon.downloadRawContent(flowInfoModel.getPcapFile());
            String fileName = flowInfoModel.getTitle() + System.currentTimeMillis() + ".pcap";
            object.put("fileName", fileName);
            object.put("file_name", fileName);
            if (CollectionUtils.isNotEmpty(flowProtocolModelList)) {
                JSONObject protocolRate = new JSONObject();
                for (FlowProtocolModel flowProtocolModel : flowProtocolModelList) {
                    protocolRate.put(flowProtocolModel.getProtocolName(), flowProtocolModel.getRate());
                }
                object.put("protocolRate", protocolRate);
                object.put("protocol_rate", protocolRate);
            }
        } else {
            fileByte = FsCommon.downloadRawContent(flowInfoModel.getScriptFile());
            String fileName = flowInfoModel.getTitle() + System.currentTimeMillis() + "." + flowInfoModel.getScriptFile()
                    .split(BaseConstants.SPOT)[1];
            object.put("fileName", fileName);
            object.put("file_name", fileName);
        }

        try {
            String url = this.actuatorHost + UPLOAD_PCAP_URL;
            String pcapRsp = HttpUtils.postFileV2(url, fileByte, "file", object);
            var rsp = JsonCommon.snakeParse(pcapRsp, new TypeReference<TrexResponse<Object>>() {
            });
            if ("1000".equals(rsp.getCode())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public static class ProtocolRate {
        private String name;
        private BigDecimal rate;

        public ProtocolRate(String name, BigDecimal rate) {
            this.name = name;
            this.rate = rate;
        }
    }

    public byte[] changeCap2Pcap(byte[] fileByte) {
        String fileName = "/tmp/"+ TextUtils.uuid()+".pcap";
        try {
            FileUtils.writeByteArrayToFile(new File(fileName), fileByte);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String pythonCmd = String.format("python3 ./editcaptopcap.py %s", fileName);
        log.info("============python analyse cmd: {}", pythonCmd);
        try {
            Process process = Runtime.getRuntime().exec(pythonCmd);
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            return FileUtils.readFileToByteArray(new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean uploadPcapV2(byte[] fileByte, String flowId, String flowTile, String flowFileName, int resourceType, List<ProtocolRate> flowProtocolModelList) {
        // 确保文件格式是 pcap ，这样就能支持cap、pcapng
        fileByte = this.changeCap2Pcap(fileByte);
        JSONObject object = new JSONObject();
        object.put("flowId", flowId);
        object.put("flow_id", flowId);
        if (ResourceTypeEnum.PACP.toValue() == resourceType) {
            String fileName = flowTile + System.currentTimeMillis() + ".pcap";
            object.put("fileName", fileName);
            object.put("file_name", fileName);
            if (CollectionUtils.isNotEmpty(flowProtocolModelList)) {
                JSONObject protocolRate = new JSONObject();
                for (ProtocolRate flowProtocolModel : flowProtocolModelList) {
                    protocolRate.put(flowProtocolModel.getName(), flowProtocolModel.getRate());
                }
                object.put("protocolRate", protocolRate);
                object.put("protocol_rate", protocolRate);
            }
        } else {
            String fileName = flowTile + System.currentTimeMillis() + "." + flowFileName.split(BaseConstants.SPOT)[1];
            object.put("fileName", fileName);
            object.put("file_name", fileName);
        }

        try {
            String url = this.actuatorHost + UPLOAD_PCAP_URL;
            String pcapRsp = HttpUtils.postFileV2(url, fileByte, "file", object);
            var rsp = JsonCommon.snakeParse(pcapRsp, new TypeReference<TrexResponse<Object>>() {
            });
            if ("1000".equals(rsp.getCode())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean initNetwork(String targetMac, String targetIp, List<String> taskIds) {
        if (StringUtils.isEmpty(targetMac) || StringUtils.isEmpty(targetIp) || CollectionUtils.isEmpty(taskIds)) {
            return false;
        }
        List<InitNetworkInfo> networkInfos = new ArrayList<>();
        for (String taskId : taskIds) {
            InitNetworkInfo networkInfo = new InitNetworkInfo();
            networkInfo.setMac(targetMac);
            networkInfo.setTaskId(taskId);
            networkInfo.setTargetIp(targetIp);
            networkInfos.add(networkInfo);
        }
        String url = this.actuatorHost + INIT_NETWORK_URL;
        Map<String, Object> params = Map.of("network_infos", networkInfos);
        try {
            String initRsp = HttpUtils.sendPost(url, JsonCommon.snakeStringify(params));
            var rsp = JsonCommon.snakeParse(initRsp, new TypeReference<TrexResponse<Object>>() {
            });
            if ("1000".equals(rsp.getCode())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("init network error, {}", ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public ArrayNode top5Protocols() {
        ArrayNode protocols = JsonCommon.createArrayNode();
        try {
            String url = this.actuatorHost + PROTOCOL_TOP5;
            String rsp = HttpUtils.sendPost(url, "{}");
            JsonNode rspObj = JsonCommon.parseTree(rsp);
            if (ResponseUtils.checkResponse(rspObj)) {
                protocols.addAll((ArrayNode) rspObj.get(BaseConstants.STR_DATA).get("protocols"));
            } else {
                log.error("query top5 protocols failed");
            }
        } catch (Exception e) {
            log.error("query top5 protocols error, {}", ExceptionUtils.getStackTrace(e));
        }
        return protocols;
    }

    @Getter
    @Setter
    private static class InitNetworkInfo {
        private String mac;
        private String taskId;
        private String targetIp;
        private Integer total;
    }

    @Getter
    @Setter
    private static class TrexResponse<T> {
        private String code;

        private T data;
    }

}
