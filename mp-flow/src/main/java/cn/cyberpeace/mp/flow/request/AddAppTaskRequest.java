package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 新增app任务请求
 * @Autho qiaokang
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

}
