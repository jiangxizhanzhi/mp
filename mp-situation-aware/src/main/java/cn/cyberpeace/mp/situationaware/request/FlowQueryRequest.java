package cn.cyberpeace.mp.situationaware.request;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class FlowQueryRequest {
    @ApiModelProperty(value = "一条流量记录的id")
    private String dataId;
}
