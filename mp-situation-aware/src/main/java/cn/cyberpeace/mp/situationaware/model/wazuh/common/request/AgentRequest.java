package cn.cyberpeace.mp.situationaware.model.wazuh.common.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/24 16:12
 */
@Data
@ApiModel("wazuh代理请求")
public class AgentRequest extends BasePageRequest {

    @ApiModelProperty(value = "名称")
    private String name;

    public AgentRequest() {

    }

    public AgentRequest(String name) {
        this.name = name;
    }
}
