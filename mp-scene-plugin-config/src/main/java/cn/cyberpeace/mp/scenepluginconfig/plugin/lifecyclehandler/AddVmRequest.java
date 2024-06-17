package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
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
public class AddVmRequest implements SnakeHttpData {

    private String name;
    private String templateId;
    private int cpu = 2;
    private int ram = 4096;
    private int disk = 40;
    private String requestUser;
    private String host;
    private List<Networks> network;

    @Data
    public static final class Networks {
        private String networkId;
        private String fixedIp;
        private String cidr;
        private String gateway;
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
