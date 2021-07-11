package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WazuhMonitorEventResponse {

    @ApiModelProperty("监控指标集合")
    private List<WazuhHostStateModel> monitorEventModelList;

    @ApiModelProperty("指标总数")
    private int total;

}
