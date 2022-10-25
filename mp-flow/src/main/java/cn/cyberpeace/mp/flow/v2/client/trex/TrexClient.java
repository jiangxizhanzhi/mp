package cn.cyberpeace.mp.flow.v2.client.trex;

import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
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
    private static final double standardInterval = 6;

    private TrexHttpClient trexHttpClient = new TrexHttpClient("http://10.100.7.143:80", 6000, 10);

    public void initTrexServer(NetworkInfos networkInfos) {
        var reslut = trexHttpClient.trexPost(TrexUrlConstant.INITIALIZATION_TREX, networkInfos, null,
                new TypeReference<TrexResponse<InitInfoData>>() {
                });
        log.info("Initialization of trex virtual machine completed {}", reslut.getData());
    }

    public void uploadResource(List<FlowResourceData> resources) {
        resources.stream().forEach(resource -> {
            JSONObject object = new JSONObject();
            object.put("fileName", resource.getFileName());
            object.put("flow_id", resource.getFlowId());
            String pcapRsp = HttpUtils.postFile("http://10.100.7.143:80" + TrexUrlConstant.UPLOAD_PCAP, resource.getFile(), "file", object);
            JSONObject jsonObject = JSONObject.parseObject(pcapRsp);
            log.info("upload pcap file response:{}", jsonObject.toJSONString());
        });
    }

    public void startTask(List<ProtocolInfo> protocolInfos, List<LoadInfo> loadInfos) {

    }

    public List<LoadInfo> calculateLoad(int time, double total, String loadMode) {
        List<TrexClient.LoadInfo> loadInfos = new ArrayList<>();
        if (StringUtils.isNotEmpty(loadMode) && loadMode.equals("BINOMIAL_DISTRIBUTION")) {
            binomialDistributionLoad(loadInfos, time, 0.5, total);
        } else {
            normalDistributionLoad(loadInfos, time, total);
        }
        return loadInfos;
    }

    private void binomialDistributionLoad(List<LoadInfo> loadInfos, int time, double p, double maxLoad) {
        BinomialDistribution binomialDistribution = new BinomialDistribution(time, p);
        double proportion = maxLoad / binomialDistribution.probability(20);
        for (int i = 1; i <= time; i++) {
            LoadInfo loadInfo = new LoadInfo();
            loadInfo.setLoad((int) (binomialDistribution.probability(i) * proportion));
            loadInfo.setTime(i);
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
            loadInfo.setLoad((int) (normalDistribution.density(index) * proportion));
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
        private Integer load;
    }

    @Getter
    @Setter
    public static class FlowResource {
        private String flowId;
        private String pcapFile;
    }
}
