package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.base.util.http.HttpData;
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
            object.put("fileName", resource.getFileName());
            object.put("flow_id", resource.getFlowId());
            String pcapRsp = HttpUtils.postFile(server + TrexUrlConstant.UPLOAD_PCAP, resource.getFile(), "file", object);
            JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
            log.info("upload pcap file response:{}", jsonObject.toJSONString());
        });
    }

    public void startTask(StartInfoData startInfoData) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.START_TASK, startInfoData, null,
                new TypeReference<TrexResponse<StartInfoData>>() {
                });
        log.info("Initialization of trex virtual machine completed {}", reslut.getData());
    }

    public TaskStatusData queryTaskLastStatus(QueryTaskLastStatus queryTaskLastStatus) {
        //todo : 待优化，此处body参数为空即可，但是不能传null,后续修改一下
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.LAST_RECORDS, queryTaskLastStatus, null,
                new TypeReference<TrexResponse<TaskStatusData>>() {
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

    public List<LoadInfo> calculateLoad(int time, double maxLoad, String loadMode) {
        List<TrexClient.LoadInfo> loadInfos = new ArrayList<>();
        if (StringUtils.isNotEmpty(loadMode) && loadMode.equals(BINOMIAL)) {
            binomialDistributionLoad(loadInfos, time, 0.5, maxLoad);
        } else if (StringUtils.isNotEmpty(loadMode) && loadMode.equals(GAUSS)){
            normalDistributionLoad(loadInfos, time, maxLoad);
        }
        return loadInfos;
    }

    private void binomialDistributionLoad(List<LoadInfo> loadInfos, int time, double p, double maxLoad) {
        BinomialDistribution binomialDistribution = new BinomialDistribution(time, p);
        double proportion = maxLoad / binomialDistribution.probability(time / 2);
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setBandwidth((int) (binomialDistribution.probability(i) * proportion));
            loadInfo.setTime(i);
            if (loadInfo.getBandwidth() <= 0){
                loadInfo.setBandwidth(1);
            }
            loadInfos.add(loadInfo);
        }
    }

    private void normalDistributionLoad(List<LoadInfo> loadInfos, int time, double maxLoad) {
        NormalDistribution normalDistribution = new NormalDistribution();
        double interval = standardInterval / time;
        double index = -(standardInterval / 2);
        double proportion = maxLoad / normalDistribution.density(0);
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setTime(i);
            loadInfo.setBandwidth((int) (normalDistribution.density(index) * proportion));
            loadInfos.add(loadInfo);
            index += interval;
        }
    }

    public static class ProtocolInfo {

    }

    @Getter
    @Setter
    public static class LoadInfo {
        private Integer time;
        private Integer bandwidth;
    }

    @Getter
    @Setter
    public static class FlowResource {
        private String flowId;
        private String pcapFile;
    }
}
