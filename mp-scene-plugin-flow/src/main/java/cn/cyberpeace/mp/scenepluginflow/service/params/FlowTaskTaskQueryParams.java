package cn.cyberpeace.mp.scenepluginflow.service.params;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowTaskTaskQueryParams extends QueryParams {
    private String actuatorId;
}
