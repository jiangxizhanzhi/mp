package cn.cyberpeace.mp.flow.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 新增虚拟机请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddVmRequest {

    private String name;
    private String image;
    private int cpu = 2;
    private int ram = 4096;
    private int disk = 40;
    private String requestUser;
    private String host;
    private List<Networks> networks;

    @Data
    public static final class Networks {
        private String networkId;
        private String fixedIp;
        private QosRule qosRule;
        private boolean enableExternal;
        private List<AccessModes> accessModes;
    }

    @Data
    public static final class QosRule {
        private int maxKbps;
        private int maxBurstKbps;
        private String direction;
        private int lossPercent;
        private int delayMs;
        private int delayCorrelationMs;
    }

    @Data
    public static final class AccessModes {
        private String protocol;
        private String ipAddress;
        private int port;
        private String macAddress;
        private String username;
        private String password;
        private boolean enablePortMapping;
        private String portMappingProtocol;
    }

}
