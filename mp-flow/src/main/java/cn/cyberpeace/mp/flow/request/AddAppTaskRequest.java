package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowPortModel;
import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @Description: 新增app任务请求
 * @Author qiaokang
 * @Date：2021/8/18 14:13
 */
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
    }

    @Data
    public static final class Server {
        private String ip;
        private String port;
        private String defaultGw;
    }

    public AddAppTaskRequest(FlowTaskModel flowTaskModel, FlowPortModel flowPortModel) {
        AddAppTaskRequest.CapList capList = new AddAppTaskRequest.CapList();
        capList.setName(flowTaskModel.getThirdFlowName());

        AddAppTaskRequest.Specifics specifics = new AddAppTaskRequest.Specifics();
        int duration = flowTaskModel.getCount() * flowTaskModel.getIntervalTime();
        specifics.setDuration(duration);
        specifics.setSrcIpRange(flowTaskModel.getOriginIps());
        String[] targetIps = flowTaskModel.getTargetIps().split(BaseConstants.SHORT_DASH);
        String dstIpRange;
        if (targetIps.length < 2) {
            dstIpRange = targetIps[0] + BaseConstants.SHORT_DASH + targetIps[0];
        } else {
            dstIpRange = flowTaskModel.getTargetIps();
        }
        specifics.setDstIpRange(dstIpRange);
        specifics.setCapList(Collections.singletonList(capList));

        AddAppTaskRequest.Client client = new AddAppTaskRequest.Client();
        client.setIp(flowPortModel.getClientIp());
        client.setPort(flowPortModel.getClientMac());
        // 目标ip作为客户端网关
        String clientGw = targetIps[0];
        client.setDefaultGw(clientGw);

        AddAppTaskRequest.Server server = new AddAppTaskRequest.Server();
        server.setIp(flowPortModel.getServerIp());
        server.setPort(flowPortModel.getServerMac());
        // 目标ip作为服务端网关
//        String serverGw = flowTaskModel.getOriginIps().split(BaseConstants.SHORT_DASH)[0];
        server.setDefaultGw(clientGw);

        AddAppTaskRequest.NetworkConfig networkConfig = new AddAppTaskRequest.NetworkConfig();
        networkConfig.setClient(Collections.singletonList(client));
        networkConfig.setServer(Collections.singletonList(server));

        AddAppTaskRequest.NetworkStructure networkStructure = new AddAppTaskRequest.NetworkStructure();
        networkStructure.setNetworkConfig(networkConfig);

        AddAppTaskRequest.Config config = new AddAppTaskRequest.Config();
        config.setSpecifics(specifics);
        config.setNetworkStructure(networkStructure);

        // 名称加时间戳，防止重复
        this.name = flowTaskModel.getTitle() + System.currentTimeMillis();
        this.description = flowTaskModel.getTaskDesc();
        this.config = config;
    }

}
