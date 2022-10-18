package cn.cyberpeace.mp.collection.core.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 10:58
 */
@Data
public class NetAddr {

    private Scan scan;

    private String broadcast;

    private String proto;

    private String address;

    private String iface;

    private String netmask;

    @JSONField(name = "agent_id")
    private String agentId;


}
