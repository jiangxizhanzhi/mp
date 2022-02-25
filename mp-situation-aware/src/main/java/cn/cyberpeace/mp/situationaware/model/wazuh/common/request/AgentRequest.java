package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
public class AgentRequest extends BasePageRequest {

    private String name;

    public AgentRequest() {

    }

    public AgentRequest(String name) {
        this.name = name;
    }
}
