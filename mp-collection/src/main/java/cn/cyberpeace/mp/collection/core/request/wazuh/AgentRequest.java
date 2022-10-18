package cn.cyberpeace.mp.collection.core.request.wazuh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentRequest extends BasePageRequest {

    private String name;

    private String ip;

    private String status;

}
