package cn.cyberpeace.mp.collection.core.request.wazuh;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
public class CollectorBaseRequest extends BasePageRequest {

    private String agentName;

    public CollectorBaseRequest() {

    }

    public CollectorBaseRequest(String agentName) {
        this.agentName = agentName;
    }
}
