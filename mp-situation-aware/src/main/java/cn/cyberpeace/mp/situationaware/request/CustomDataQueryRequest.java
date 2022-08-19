package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomDataQueryRequest extends BaseEventRequest{
    @ApiModelProperty(value = "探针id")
    private List<String> probeIds;
}
