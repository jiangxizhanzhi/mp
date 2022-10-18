package cn.cyberpeace.mp.collection.core.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 10:58
 */
@Data
public class Netiface {

    private X rx;

    private X tx;

    private Scan scan;

    private String type;

    private Integer mtu;

    private String state;

    private String mac;

    private String name;

    @JSONField(name = "agent_id")
    private String agentId;

    private String ip;

    /**
     * 接收或者发送的包
     */
    @Data
    public static class X {

        private Integer bytes;

        private Integer dropped;

        private String errors;

        private String packets;
    }


}
