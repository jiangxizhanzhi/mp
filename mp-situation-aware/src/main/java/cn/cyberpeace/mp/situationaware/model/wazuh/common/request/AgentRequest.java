package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BaseRequest;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
public class AgentRequest extends BaseRequest {

    private String name;

    public AgentRequest() {

    }

    public AgentRequest(String name) {
        this.name = name;
    }
}
