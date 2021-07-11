package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "告警数量统计信息")
public class AlarmCountVO {

    @ApiModelProperty(value = "告警类型：0 云平台服务, 1 业务服务 2 服务器")
    private Integer alarmType;

    @ApiModelProperty(value = "告警数量")
    private int alarmCount;

}
