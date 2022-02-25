package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
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
