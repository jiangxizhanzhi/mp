package cn.cyberpeace.mp.situationaware.request;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageQueryRequest extends BasePageRequest {
    @ApiModelProperty(value = "战队id")
    private String teamId;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    @ApiModelProperty(value = "主机ip")
    private List<String> agentIps;
    @ApiModelProperty(value = "agent名称")
    private List<String> agentNames = new ArrayList<>();
}
