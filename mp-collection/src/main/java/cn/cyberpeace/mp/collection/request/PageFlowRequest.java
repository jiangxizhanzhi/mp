package cn.cyberpeace.mp.collection.request;

import cn.cyberpeace.mp.collection.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PageFlowRequest extends BasePageRequest {

    @ApiModelProperty(value = "战队id")
    private String teamId;

    @ApiModelProperty(value = "靶标ip")
    private List<String> agentIps;

    @ApiModelProperty(value = "源ip")
    private List<String> sourceIps;

    @ApiModelProperty(value = "目标ip")
    private List<String> targetIps;

    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "端口")
    private String port;

    @ApiModelProperty(value = "流量类型")
    private String protocol;
}
