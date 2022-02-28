package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 11:27
 */
@Data
public class Port {

    private Local local;

    private Remote remote;

    private Scan scan;

    private Integer inode;

    private Integer pid;

    @JSONField(name = "rx_queue")
    private Integer rxQueue;

    @JSONField(name = "tx_queue")
    private String txQueue;

    private String protocol;

    private String state;

    private String process;

    @JSONField(name = "agent_id")
    private String agentId;


    @Data
    public static class Local {

        private String ip;

        private Integer port;
    }

    @Data
    public static class Remote {

        private String ip;

        private Integer port;
    }


}
