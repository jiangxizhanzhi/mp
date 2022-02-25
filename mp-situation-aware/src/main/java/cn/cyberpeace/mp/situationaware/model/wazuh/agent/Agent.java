package cn.cyberpeace.mp.situationaware.model.wazuh.agent;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 09:48
 */
@Data
public class Agent {

    private OS os;

    private String id;

    private List<String> group;

    private String ip;

    private String version;

    private Date lastKeepAlive;

    private String status;

    @JSONField(name = "node_name")
    private String nodeName;

    private Date dateAdd;

    private String name;

    private String manager;

    private String registerIP;

}
