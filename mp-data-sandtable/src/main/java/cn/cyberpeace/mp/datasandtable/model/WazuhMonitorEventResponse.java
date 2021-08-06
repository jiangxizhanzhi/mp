package cn.cyberpeace.mp.datasandtable.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 监控指标返回结果
 * @Autho qiaokang
 * @Date：2021/6/22 17:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WazuhMonitorEventResponse {

    @ApiModelProperty("监控指标集合")
    private List<WazuhModel> monitorEventModelList;

    @ApiModelProperty("指标总数")
    private int total;

}
