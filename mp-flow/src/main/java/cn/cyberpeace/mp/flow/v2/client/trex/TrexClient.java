package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/20
 */
@Slf4j
public class TrexClient {
    private String server;
    private TrexHttpClient trexHttpClient;
    private static final double standardInterval = 6;
    private static final String RANDOM = "random";
    private static final String BINOMIAL = "binomial";
    private static final String UNIFORMITY = "uniformity";
    private static final String INDEX = "index";
    private static final String GAUSS = "gauss";

    public TrexClient(String server) {
        this.server = server;
        this.trexHttpClient = new TrexHttpClient(server, 6000, 10);
    }
//    private TrexHttpClient trexHttpClient = new TrexHttpClient("http://10.100.7.143:80", 6000, 10);

    public void initTrexServer(NetworkInfos networkInfos) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.INITIALIZATION_TREX, networkInfos, null,
                new TypeReference<TrexResponse<NetworkInfos>>() {
                });
        log.info("Initialization of trex virtual machine response:{}", reslut.getData());
    }

    public void uploadResource(List<FlowResourceData> resources) {
        resources.stream().forEach(resource -> {
            JSONObject object = new JSONObject();
            object.put("fileName", resource.getFileName() + ".pcap");
            object.put("flow_id", resource.getFlowId());
            String pcapRsp = HttpUtils.postFile(server + TrexUrlConstant.UPLOAD_PCAP, resource.getFile(), "file", object);
            JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
            log.info("upload pcap file response:{}", jsonObject.toJSONString());
        });
    }

    public void startTask(StartTaskData startInfoData) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.TASK_ACTION, startInfoData, null,
                new TypeReference<TrexResponse<StartTaskData>>() {
                });
        log.info("Start traffic task action completed {}", reslut.getData());
        if (!reslut.isSuccess()) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, reslut.getMsg());
        }
    }

    public void doTaskAction(TaskActionData taskActionData) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.TASK_ACTION, taskActionData, null,
                new TypeReference<TrexResponse<TaskActionData>>() {
                });
        log.info("Traffic task action {} completed {}", taskActionData.getAction(), reslut.getData());
        if (!reslut.isSuccess()) {
            throw new MpRuntimeException(BaseErrorCode.ERROR, reslut.getMsg());
        }
    }

    public TaskLastStatusResponseData queryTaskLastStatus(QueryTaskLastStatusData queryTaskLastStatus) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.LAST_RECORDS, queryTaskLastStatus, null,
                new TypeReference<TrexResponse<TaskLastStatusResponseData>>() {
                });
        log.info("Update all tasks status response:{}", reslut.getData());
        return reslut.getData();
    }

    public List<TaskStatusResponseData> queryTaskStatus(QueryTaskStatusData queryTaskStatusData) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.TASK_STATUS, queryTaskStatusData, null,
                new TypeReference<TrexResponse<List<TaskStatusResponseData>>>() {
                });
        log.info("Query single task status response:{}", reslut.getData());
        return reslut.getData();
    }

    public static class ProtocolInfo {

    }

    @Getter
    @Setter
    public static class FlowResource {
        private String flowId;
        private String pcapFile;
    }
}
