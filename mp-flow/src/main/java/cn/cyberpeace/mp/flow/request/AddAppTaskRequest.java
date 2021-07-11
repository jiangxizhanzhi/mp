package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAppTaskRequest {

    private String name;

    private String description;

    private Config config;

    @Data
    public static final class Config {
        private Specifics specifics;
        private NetworkStructure networkStructure;
    }

    @Data
    public static final class NetworkStructure {
        private NetworkConfig networkConfig;
    }

    @Data
    public static final class NetworkConfig {
        private List<Client> client;
        private List<Server> server;
    }

    @Data
    public static final class Specifics {
        private int duration;
        private String srcIpRange;
        private String dstIpRange;
        private List<CapList> capList;
        
        private String flowProtocol;
        
        private String content;
        
        private Integer mult;
    }

    @Data
    public static final class CapList {
        private String name;
        private int cps = 1;
    }

    @Data
    public static final class Client {
        private String ip;
        private String port;
        private String defaultGw;
        private String nodeName;
    }

    @Data
    public static final class Server {
        private String ip;
        private String port;
        private String defaultGw;
        private String nodeName;
    }

    public AddAppTaskRequest(FlowTaskModel flowTaskModel, FlowPortModel flowPortModel) {
        AddAppTaskRequest.CapList capList = new AddAppTaskRequest.CapList();
        capList.setName(flowTaskModel.getThirdFlowName());

        AddAppTaskRequest.Specifics specifics = new AddAppTaskRequest.Specifics();
        int duration = flowTaskModel.getCount() * flowTaskModel.getIntervalTime();
        specifics.setDuration(duration);
        if (StringUtils.isBlank(flowTaskModel.getOriginIps())) {
            flowTaskModel.setOriginIps("127.0.0.1-127.0.0.1");
        }
        String[] srcIps = flowTaskModel.getOriginIps().split(BaseConstants.SHORT_DASH);
        String srcIpRange;
        if (srcIps.length < 2) {
            srcIpRange = srcIps[0] + BaseConstants.SHORT_DASH + srcIps[0];
        } else {
            srcIpRange = flowTaskModel.getOriginIps();
        }
        specifics.setSrcIpRange(srcIpRange);
        String[] targetIps = flowTaskModel.getTargetIps().split(BaseConstants.SHORT_DASH);
        String dstIpRange;
        String targetIp = targetIps[0];
        if (targetIps.length < 2) {
            dstIpRange = targetIp + BaseConstants.SHORT_DASH + targetIp;
        } else {
            dstIpRange = flowTaskModel.getTargetIps();
        }
        specifics.setDstIpRange(dstIpRange);
        specifics.setCapList(Collections.singletonList(capList));
        specifics.setFlowProtocol(flowTaskModel.getFlowProtocol());
        specifics.setContent(flowTaskModel.getContent());
        specifics.setMult(Objects.isNull(flowTaskModel.getMult()) ? 1 : flowTaskModel.getMult());

        AddAppTaskRequest.Client client = new AddAppTaskRequest.Client();
        client.setIp(flowPortModel.getClientIp());
        client.setPort(flowPortModel.getClientMac());
        
        client.setDefaultGw(targetIp);
        client.setNodeName(flowTaskModel.getNodeId());

        AddAppTaskRequest.Server server = new AddAppTaskRequest.Server();
        server.setIp(flowPortModel.getServerIp());
        server.setPort(flowPortModel.getServerMac());
        
        server.setDefaultGw(targetIp);
        server.setNodeName(flowTaskModel.getNodeId());

        AddAppTaskRequest.NetworkConfig networkConfig = new AddAppTaskRequest.NetworkConfig();
        networkConfig.setClient(Collections.singletonList(client));
        networkConfig.setServer(Collections.singletonList(server));

        AddAppTaskRequest.NetworkStructure networkStructure = new AddAppTaskRequest.NetworkStructure();
        networkStructure.setNetworkConfig(networkConfig);

        AddAppTaskRequest.Config config = new AddAppTaskRequest.Config();
        config.setSpecifics(specifics);
        config.setNetworkStructure(networkStructure);

        
        this.name = flowTaskModel.getTitle() + System.currentTimeMillis();
        this.description = flowTaskModel.getTaskDesc();
        this.config = config;
    }

}
