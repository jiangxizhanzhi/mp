package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "系统告警分布")
public class AlarmDistributionVo {

    @ApiModelProperty("云平台服务告警数量")
    private Long cpAlarmCount;

    @ApiModelProperty("业务服务告警数量")
    private Long crAlarmCount;

    @ApiModelProperty("服务器告警数量")
    private Long serverAlarmCount;

}
